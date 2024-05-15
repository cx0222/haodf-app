package com.nova.haodf.service.runner;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.nova.haodf.util.EntityStatus;
import com.nova.haodf.config.MainExecutorConfig;
import com.nova.haodf.entity.DoctorConsultation;
import com.nova.haodf.service.DoctorConsultationService;
import com.nova.haodf.util.JsonInstruction;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class ParseDoctorConsultationRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParseDoctorConsultationRunner.class);
    private static final Map<String, JsonInstruction> JSON_INSTRUCTION_MAP = new HashMap<>();

    static {
        JSON_INSTRUCTION_MAP.put("setConsultationId", new JsonInstruction("medicalRecordId", Long.class));
        JSON_INSTRUCTION_MAP.put("setPatientId", new JsonInstruction("medicalRecordInfo:patientId", Long.class,
                JsonInstruction.STRING_TO_LONG_ADAPTER_FUNCTION
        ));
        JSON_INSTRUCTION_MAP.put("setTitle", new JsonInstruction("medicalRecordInfo:title", String.class));
        JSON_INSTRUCTION_MAP.put("setDiseaseName", new JsonInstruction("medicalRecordInfo:diseaseName", String.class));
        JSON_INSTRUCTION_MAP.put("setBusinessType", new JsonInstruction("medicalRecordInfo:businessType", String.class));
        JSON_INSTRUCTION_MAP.put("setMessageCount", new JsonInstruction("medicalRecordInfo:msgCount", Integer.class));
        JSON_INSTRUCTION_MAP.put("setDoctorMessageCount", new JsonInstruction("medicalRecordInfo:doctorMsgCount", Integer.class));
        JSON_INSTRUCTION_MAP.put("setHasPrescription", new JsonInstruction("medicalRecordInfo:hasRecipel", Integer.class));
        JSON_INSTRUCTION_MAP.put("setHasMedicalSummary", new JsonInstruction("medicalRecordInfo:hasMedicalSummary", Integer.class));
        JSON_INSTRUCTION_MAP.put("setIsGoodConsultation", new JsonInstruction("isGoodMedical", Integer.class, val -> (boolean) val ? 1 : 0));
        JSON_INSTRUCTION_MAP.put("setStartDate", new JsonInstruction("medicalRecordInfo:mrStartTime", LocalDate.class,
                JsonInstruction.STRING_TO_LOCAL_DATE_ADAPTER_FUNCTION
        ));
        JSON_INSTRUCTION_MAP.put("setMessageText", new JsonInstruction("doctorRes4MrInfo:messageText", String.class));
        JSON_INSTRUCTION_MAP.put("setDiseaseId", new JsonInstruction("diseaseTagInfoVO:diseaseId", Long.class,
                JsonInstruction.INTEGER_TO_LONG_ADAPTER_FUNCTION
        ));
        JSON_INSTRUCTION_MAP.put("setTermName", new JsonInstruction("diseaseTagInfoVO:termName", String.class));
        JSON_INSTRUCTION_MAP.put("setPatientGender", new JsonInstruction("patientInfo:sexVal", String.class));
        JSON_INSTRUCTION_MAP.put("setPatientAge", new JsonInstruction("patientInfo:ageVal", Integer.class,
                val -> Integer.parseInt(val.toString().replace("岁", ""))
        ));
    }

    private final MainExecutorConfig mainExecutorConfig;
    private final DoctorConsultationService doctorConsultationService;
    @Value("${options.runner.parse-doctor-consultation}")
    private Boolean taskOn;
    @Value("${options.resources.doctor-consultation-dir}")
    private String mainDirectory;
    @Value("${options.spider.await-termination-seconds}")
    private int awaitTerminationSeconds;

    @Autowired
    public ParseDoctorConsultationRunner(MainExecutorConfig mainExecutorConfig, DoctorConsultationService doctorConsultationService) {
        this.mainExecutorConfig = mainExecutorConfig;
        this.doctorConsultationService = doctorConsultationService;
    }

    private DoctorConsultation parseConsultation(JSONObject jsonObject) {
        try {
            Objects.requireNonNull(jsonObject);
        } catch (Exception exception) {
            LOGGER.warn("Could not parse json", exception);
            return null;
        }
        DoctorConsultation doctorConsultation = new DoctorConsultation();
        for (Map.Entry<String, JsonInstruction> entry : JSON_INSTRUCTION_MAP.entrySet()) {
            String methodName = entry.getKey();
            JsonInstruction jsonInstruction = entry.getValue();
            Object fieldData = JsonInstruction.getFieldDataFromJson(jsonObject, jsonInstruction.getPath());
            fieldData = jsonInstruction.applyTransform(fieldData);
            try {
                DoctorConsultation.class
                        .getDeclaredMethod(methodName, jsonInstruction.getClazz())
                        .invoke(doctorConsultation, fieldData);
            } catch (Exception exception) {
                LOGGER.warn("Exception occurred, method name = {}", methodName, exception);
            }
        }
        doctorConsultation.setStatus(EntityStatus.OK);
        return doctorConsultation;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!taskOn) {
            LOGGER.warn("Parse doctor consultation runner is not started.");
            return;
        }
        File directory = new File(mainDirectory);
        List<File> fileList = FileUtils.listFiles(directory, new String[]{"json"}, true)
                .stream()
                .toList();
        ThreadPoolTaskExecutor mainExecutor = mainExecutorConfig.getMainExecutor();
        int fileCount = fileList.size();
        CountDownLatch countDownLatch = new CountDownLatch(fileCount);
        for (int i = 0; i < fileCount; ++i) {
            File file = fileList.get(i);
            DoctorConsultationParserRunnerTask doctorConsultationParserRunnerTask = new DoctorConsultationParserRunnerTask(
                    file, countDownLatch, mainExecutor
            );
            mainExecutor.submit(doctorConsultationParserRunnerTask);
        }
        boolean latchStatus = countDownLatch.await(awaitTerminationSeconds, TimeUnit.SECONDS);
        mainExecutor.shutdown();
        LOGGER.info("Parse doctor consultation runner run completed, latch status = {}", latchStatus);
    }

    private class DoctorConsultationParserRunnerTask implements Runnable {
        private final File file;
        private final CountDownLatch countDownLatch;
        private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

        public DoctorConsultationParserRunnerTask(File file, CountDownLatch countDownLatch, ThreadPoolTaskExecutor threadPoolTaskExecutor) {
            this.file = file;
            this.countDownLatch = countDownLatch;
            this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        }

        @Override
        public void run() {
            // 读取 html 文件
            String filePath = file.getPath();
            String htmlString = null;
            try {
                LOGGER.info("Processing file {}", filePath);
                StringBuilder builder = new StringBuilder();
                List<String> stringList = FileUtils.readLines(file, StandardCharsets.UTF_8);
                stringList.forEach(builder::append);
                htmlString = builder.toString();
            } catch (IOException ioException) {
                LOGGER.warn("Failed to load doctor consultation file", ioException);
                countDownLatch.countDown();
                return;
            }
            // 获取 script 元素的文本
            String scriptText = null;
            try {
                Objects.requireNonNull(htmlString, "Html string is null");
                Element scriptElement = Jsoup.parse(htmlString)
                        .body()
                        .getElementsByTag("script")
                        .first();
                Objects.requireNonNull(scriptElement, "Script element is null");
                scriptText = scriptElement.html();
            } catch (Exception exception) {
                LOGGER.warn("Failed to parse html", exception);
                countDownLatch.countDown();
                return;
            }
            // 获取 doctorId 以及问诊记录的 Json 数组
            Long doctorId = null;
            JSONArray consultationJsonArray = null;
            try {
                Objects.requireNonNull(scriptText, "Script text is null");
                int jsonStartIndex = scriptText.indexOf("{");
                int jsonEndIndex = scriptText.lastIndexOf("};") + 1;
                String jsonString = scriptText.substring(jsonStartIndex, jsonEndIndex);
                JSONObject jsonObject = JSONObject.parseObject(jsonString)
                        .getJSONObject("bingcheng");
                doctorId = jsonObject.getJSONObject("doctorInfo")
                        .getLong("id");
                consultationJsonArray = jsonObject.getJSONObject("consultInfo")
                        .getJSONArray("data");
            } catch (Exception exception) {
                LOGGER.warn("Failed to obtain json array", exception);
                countDownLatch.countDown();
                return;
            }
            // 解析问诊记录的 Json 数组，提取相应信息
            List<DoctorConsultation> doctorConsultationList = new ArrayList<>();
            try {
                Objects.requireNonNull(consultationJsonArray, "Consultation json array is null");
                int count = consultationJsonArray.size();
                for (int index = 0; index < count; ++index) {
                    JSONObject jsonObject = consultationJsonArray.getJSONObject(index);
                    DoctorConsultation doctorConsultation = parseConsultation(jsonObject);
                    if (doctorConsultation == null) {
                        continue;
                    }
                    doctorConsultation.setDoctorId(doctorId);
                    doctorConsultationList.add(doctorConsultation);
                }
            } catch (Exception exception) {
                LOGGER.warn("Failed to parse json data", exception);
                countDownLatch.countDown();
                return;
            }
            // 将问诊记录存入数据库
            try {
                doctorConsultationService.saveDoctorConsultationList(doctorConsultationList);
            } catch (Exception exception) {
                LOGGER.warn("Failed to save consultation records");
            } finally {
                LOGGER.info("File {} done", filePath);
                countDownLatch.countDown();
                LOGGER.info("There are approximately {} tasks left", threadPoolTaskExecutor.getQueueSize());
            }
        }
    }
}
