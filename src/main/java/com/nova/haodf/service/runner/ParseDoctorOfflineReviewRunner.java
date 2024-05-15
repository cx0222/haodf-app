package com.nova.haodf.service.runner;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.nova.haodf.util.EntityStatus;
import com.nova.haodf.config.MainExecutorConfig;
import com.nova.haodf.entity.DoctorOfflineReview;
import com.nova.haodf.service.DoctorOfflineReviewService;
import com.nova.haodf.util.JsonInstruction;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class ParseDoctorOfflineReviewRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParseDoctorOfflineReviewRunner.class);
    private static final Map<String, JsonInstruction> JSON_INSTRUCTION_MAP = new HashMap<>();

    static {
        JSON_INSTRUCTION_MAP.put("setReviewId", new JsonInstruction("id", Long.class, JsonInstruction.STRING_TO_LONG_ADAPTER_FUNCTION));
        JSON_INSTRUCTION_MAP.put("setReason", new JsonInstruction("reason", String.class));
        JSON_INSTRUCTION_MAP.put("setDisease", new JsonInstruction("disease", String.class));
        JSON_INSTRUCTION_MAP.put("setTypeDescription", new JsonInstruction("typeDesc", String.class));
        JSON_INSTRUCTION_MAP.put("setContent", new JsonInstruction("content", String.class));
        JSON_INSTRUCTION_MAP.put("setEffect", new JsonInstruction("effect", String.class));
        JSON_INSTRUCTION_MAP.put("setName", new JsonInstruction("name", String.class));
        JSON_INSTRUCTION_MAP.put("setRealTime", new JsonInstruction("realTime", LocalDateTime.class, JsonInstruction.STRING_TO_LOCAL_DATETIME_ADAPTER_FUNCTION));
        JSON_INSTRUCTION_MAP.put("setAttitude", new JsonInstruction("attitude", String.class));
        JSON_INSTRUCTION_MAP.put("setSkill", new JsonInstruction("skill", String.class));
        JSON_INSTRUCTION_MAP.put("setCost", new JsonInstruction("cost", Integer.class, JsonInstruction.STRING_TO_INTEGER_ADAPTER_FUNCTION));
        JSON_INSTRUCTION_MAP.put("setAgree", new JsonInstruction("agree", Integer.class, JsonInstruction.STRING_TO_INTEGER_ADAPTER_FUNCTION));
        JSON_INSTRUCTION_MAP.put("setRemedy", new JsonInstruction("remedy", String.class));
        JSON_INSTRUCTION_MAP.put("setIllCondition", new JsonInstruction("illCondition", String.class));
        JSON_INSTRUCTION_MAP.put("setTag", new JsonInstruction("tag", String.class));
        JSON_INSTRUCTION_MAP.put("setPatientProvince", new JsonInstruction("patientProvince", String.class));
        JSON_INSTRUCTION_MAP.put("setPatientCity", new JsonInstruction("patientCity", String.class));
    }

    private final MainExecutorConfig mainExecutorConfig;
    private final DoctorOfflineReviewService doctorOfflineReviewService;
    @Value("${options.runner.parse-offline-review}")
    private Boolean taskOn;
    @Value("${options.resources.doctor-offline-review-dir}")
    private String mainDirectory;
    @Value("${options.spider.await-termination-seconds}")
    private int awaitTerminationSeconds;

    @Autowired
    public ParseDoctorOfflineReviewRunner(MainExecutorConfig mainExecutorConfig, DoctorOfflineReviewService doctorOfflineReviewService) {
        this.mainExecutorConfig = mainExecutorConfig;
        this.doctorOfflineReviewService = doctorOfflineReviewService;
    }

    private DoctorOfflineReview parseOfflineReview(JSONObject jsonObject) {
        try {
            Objects.requireNonNull(jsonObject);
        } catch (Exception exception) {
            LOGGER.warn("Could not parse json", exception);
            return null;
        }
        DoctorOfflineReview doctorOfflineReview = new DoctorOfflineReview();
        for (Map.Entry<String, JsonInstruction> entry : JSON_INSTRUCTION_MAP.entrySet()) {
            String methodName = entry.getKey();
            JsonInstruction jsonInstruction = entry.getValue();
            Object fieldData = JsonInstruction.getFieldDataFromJson(jsonObject, jsonInstruction.getPath());
            fieldData = jsonInstruction.applyTransform(fieldData);
            try {
                DoctorOfflineReview.class
                        .getDeclaredMethod(methodName, jsonInstruction.getClazz())
                        .invoke(doctorOfflineReview, fieldData);
            } catch (Exception exception) {
                LOGGER.warn("Exception occurred, method name = {}", methodName, exception);
            }
        }
        doctorOfflineReview.setStatus(EntityStatus.OK);
        return doctorOfflineReview;
    }

    private void addOfflineReviewLocation(DoctorOfflineReview doctorOfflineReview) {
        String name = doctorOfflineReview.getName();
        int start = name.indexOf("["), end = name.indexOf("]");
        if (start == -1 || end == -1 || start >= end) {
            LOGGER.warn("Patient province or city could not be deduced by name");
            return;
        }
        try {
            String location = name.substring(start + 1, end);
            location = location.replaceAll("\\d", "");
            String[] splits = location.split(" ");
            int length = splits.length;
            if (length == 2) {
                doctorOfflineReview.setPatientProvince(splits[0]);
                doctorOfflineReview.setPatientCity(splits[1]);
            } else if (length == 1) {
                doctorOfflineReview.setPatientProvince(splits[0]);
            }
        } catch (Exception exception) {
            LOGGER.warn("Could not obtain the province and city of patient", exception);
        }
    }

    @Override
    public void run(String... args) throws Exception {
        if (!taskOn) {
            LOGGER.warn("Parse doctor offline review runner is not started.");
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
            DoctorOfflineReviewParserRunnerTask doctorOfflineReviewParserRunnerTask = new DoctorOfflineReviewParserRunnerTask(
                    file, countDownLatch, mainExecutor
            );
            mainExecutor.submit(doctorOfflineReviewParserRunnerTask);
        }
        boolean latchStatus = countDownLatch.await(awaitTerminationSeconds, TimeUnit.SECONDS);
        mainExecutor.shutdown();
        LOGGER.info("Parse doctor offline review runner run completed, latch status = {}", latchStatus);
    }

    private class DoctorOfflineReviewParserRunnerTask implements Runnable {
        private final File file;
        private final CountDownLatch countDownLatch;
        private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

        public DoctorOfflineReviewParserRunnerTask(File file, CountDownLatch countDownLatch, ThreadPoolTaskExecutor threadPoolTaskExecutor) {
            this.file = file;
            this.countDownLatch = countDownLatch;
            this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        }

        @Override
        public void run() {
            List<DoctorOfflineReview> doctorOfflineReviewList = new ArrayList<>();
            String filePath = file.getPath();
            String[] splits = filePath.split("/");
            Long doctorId = Long.parseLong(splits[splits.length - 2]);
            try {
                LOGGER.info("Processing file {}", filePath);
                StringBuilder builder = new StringBuilder();
                List<String> stringList = FileUtils.readLines(file, StandardCharsets.UTF_8);
                stringList.forEach(builder::append);
                String jsonString = builder.toString();
                JSONArray jsonArray = JSONObject.parseObject(jsonString)
                        .getJSONObject("data")
                        .getJSONArray("data");
                int count = jsonArray.size();
                for (int index = 0; index < count; ++index) {
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    DoctorOfflineReview doctorOfflineReview = parseOfflineReview(jsonObject);
                    if (doctorOfflineReview == null) {
                        continue;
                    }
                    doctorOfflineReview.setDoctorId(doctorId);
                    addOfflineReviewLocation(doctorOfflineReview);
                    doctorOfflineReviewList.add(doctorOfflineReview);
                }
                LOGGER.info("File {} parsed", filePath);
            } catch (Exception exception) {
                LOGGER.error("Fail to parse file {}", filePath, exception);
            }
            doctorOfflineReviewService.saveDoctorOfflineReviewList(doctorOfflineReviewList);
            countDownLatch.countDown();
            LOGGER.info("There are approximately {} tasks left", threadPoolTaskExecutor.getQueueSize());
        }
    }
}
