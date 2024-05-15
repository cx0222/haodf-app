package com.nova.haodf.service.runner;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.nova.haodf.util.EntityStatus;
import com.nova.haodf.config.MainExecutorConfig;
import com.nova.haodf.entity.Doctor;
import com.nova.haodf.service.DoctorService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class LoadDoctorDetailRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadDoctorDetailRunner.class);
    private static final Map<String, JsonInstruction> JSON_INSTRUCTION_MAP = new HashMap<>();

    static {
        JSON_INSTRUCTION_MAP.put("setDoctorId", new JsonInstruction("doctorId", Long.class, JsonInstruction.INTEGER_TO_LONG_ADAPTER_FUNCTION));
        JSON_INSTRUCTION_MAP.put("setGrade", new JsonInstruction("baseDoctorInfo:grade", String.class));
        JSON_INSTRUCTION_MAP.put("setName", new JsonInstruction("baseDoctorInfo:name", String.class));
        JSON_INSTRUCTION_MAP.put("setEducationGrade", new JsonInstruction("baseDoctorInfo:educateGrade", String.class));
        JSON_INSTRUCTION_MAP.put("setTitle", new JsonInstruction("baseDoctorInfo:title", String.class));
        JSON_INSTRUCTION_MAP.put("setSpecialization", new JsonInstruction("baseDoctorInfo:specialize", String.class));
        JSON_INSTRUCTION_MAP.put("setProvince", new JsonInstruction("baseDoctorInfo:province", String.class));
        JSON_INSTRUCTION_MAP.put("setCity", new JsonInstruction("baseDoctorInfo:city", String.class));
        JSON_INSTRUCTION_MAP.put("setDistrict", new JsonInstruction("baseDoctorInfo:district", String.class));
        JSON_INSTRUCTION_MAP.put("setAreaCode", new JsonInstruction("baseDoctorInfo:areaCode", String.class));
        JSON_INSTRUCTION_MAP.put("setFacultyId", new JsonInstruction("baseDoctorInfo:facultyId", Long.class, JsonInstruction.INTEGER_TO_LONG_ADAPTER_FUNCTION));
        JSON_INSTRUCTION_MAP.put("setFacultyName", new JsonInstruction("baseDoctorInfo:facultyName", String.class));
        JSON_INSTRUCTION_MAP.put("setHospitalFacultyId", new JsonInstruction("baseDoctorInfo:hospitalFacultyId", Long.class, JsonInstruction.INTEGER_TO_LONG_ADAPTER_FUNCTION));
        JSON_INSTRUCTION_MAP.put("setHospitalFacultyName", new JsonInstruction("baseDoctorInfo:hospitalFacultyName", String.class));
        JSON_INSTRUCTION_MAP.put("setSpaceId", new JsonInstruction("baseDoctorInfo:spaceId", Long.class, JsonInstruction.INTEGER_TO_LONG_ADAPTER_FUNCTION));
        JSON_INSTRUCTION_MAP.put("setDoctorIntroduction", new JsonInstruction("baseDoctorInfo:doctorIntro", String.class));
        JSON_INSTRUCTION_MAP.put("setHeadImage", new JsonInstruction("baseDoctorInfo:headImage", String.class));
        JSON_INSTRUCTION_MAP.put("setHeadImageThumbnail", new JsonInstruction("baseDoctorInfo:headImageThumbnail", String.class));
        JSON_INSTRUCTION_MAP.put("setIsHidden", new JsonInstruction("baseDoctorInfo:isHidden", Integer.class));
        JSON_INSTRUCTION_MAP.put("setSchedule", new JsonInstruction("baseDoctorInfo:scheduleStr", String.class));
        JSON_INSTRUCTION_MAP.put("setMedalHonorYear", new JsonInstruction("baseDoctorInfo:medalHonorYear", String.class));
        JSON_INSTRUCTION_MAP.put("setMedalHonorUrl", new JsonInstruction("baseDoctorInfo:medalhonorUrl", String.class));
        JSON_INSTRUCTION_MAP.put("setMedalHonorUrlForNew", new JsonInstruction("baseDoctorInfo:medalhonorUrlForNew", String.class));
        JSON_INSTRUCTION_MAP.put("setHospitalCharacter", new JsonInstruction("baseDoctorInfo:hospitalCharacter", String.class));
        JSON_INSTRUCTION_MAP.put("setPrimaryHospitalCharacter", new JsonInstruction("baseDoctorInfo:primaryHospitalCharacter", String.class));
        JSON_INSTRUCTION_MAP.put("setCounterpartsRank", new JsonInstruction("baseDoctorInfo:counterpartsRank", Integer.class));
        JSON_INSTRUCTION_MAP.put("setHotRank", new JsonInstruction("commentRankInfo:hotRank", Double.class, JsonInstruction.BIG_DECIMAL_TO_DOUBLE_ADAPTER_FUNCTION));
        JSON_INSTRUCTION_MAP.put("setComplexRank", new JsonInstruction("commentRankInfo:complexRank", Integer.class));
        JSON_INSTRUCTION_MAP.put("setRecommendStatus", new JsonInstruction("commentRankInfo:recommendStatus", Integer.class));
        JSON_INSTRUCTION_MAP.put("setRecommendIndex", new JsonInstruction("commentRankInfo:recommendIndex", Double.class, JsonInstruction.BIG_DECIMAL_TO_DOUBLE_ADAPTER_FUNCTION));
        JSON_INSTRUCTION_MAP.put("setIsRegisterOpened", new JsonInstruction("serviceInfo:isRegisterOpened", Integer.class));
        JSON_INSTRUCTION_MAP.put("setRegisterDescription", new JsonInstruction("serviceInfo:registerDesc", String.class));
        JSON_INSTRUCTION_MAP.put("setIsOnlineClinicOpened", new JsonInstruction("serviceInfo:isOnlineClinicOpened", Integer.class));
        JSON_INSTRUCTION_MAP.put("setOnlineClinicDescription", new JsonInstruction("serviceInfo:onlineClinicDesc", String.class));
        JSON_INSTRUCTION_MAP.put("setIsCaseOpened", new JsonInstruction("serviceInfo:isCaseOpened", Integer.class));
        JSON_INSTRUCTION_MAP.put("setIsPhoneOpened", new JsonInstruction("serviceInfo:isPhoneOpened", Integer.class));
        JSON_INSTRUCTION_MAP.put("setIsBookingOpened", new JsonInstruction("serviceInfo:isBookingOpened", Integer.class));
        JSON_INSTRUCTION_MAP.put("setIsServiceStar", new JsonInstruction("serviceInfo:isServiceStar", Integer.class));
        JSON_INSTRUCTION_MAP.put("setIsOpenRemoteClinic", new JsonInstruction("serviceInfo:isOpenRemoteClinic", Integer.class));
        JSON_INSTRUCTION_MAP.put("setIsOpenSurgery", new JsonInstruction("serviceInfo:isOpenSurgery", Integer.class));
        JSON_INSTRUCTION_MAP.put("setIsOpenFamilyDoctor", new JsonInstruction("serviceInfo:isOpenFamilyDoctor", Integer.class));
        JSON_INSTRUCTION_MAP.put("setIsOpenCosmeToLogVideo", new JsonInstruction("serviceInfo:isOpenCosmeToLogVideo", Integer.class));
        JSON_INSTRUCTION_MAP.put("setSkill", new JsonInstruction("otherDoctorInfo:skill", Integer.class));
        JSON_INSTRUCTION_MAP.put("setAttitude", new JsonInstruction("otherDoctorInfo:attitude", Integer.class));
    }

    private final MainExecutorConfig mainExecutorConfig;
    private final DoctorService doctorService;
    @Value("${options.runner.load-doctor-detail}")
    private Boolean taskOn;
    @Value("${options.resources.doctor-detail-dir}")
    private String mainDirectory;
    @Value("${options.spider.await-termination-seconds}")
    private int awaitTerminationSeconds;

    @Autowired
    public LoadDoctorDetailRunner(MainExecutorConfig mainExecutorConfig, DoctorService doctorService) {
        this.mainExecutorConfig = mainExecutorConfig;
        this.doctorService = doctorService;
    }

    private Doctor parseDetail(JSONObject jsonObject) {
        try {
            Objects.requireNonNull(jsonObject);
        } catch (Exception exception) {
            LOGGER.warn("Could not parse json", exception);
            return null;
        }
        Doctor doctor = new Doctor();
        for (Map.Entry<String, JsonInstruction> entry : JSON_INSTRUCTION_MAP.entrySet()) {
            String methodName = entry.getKey();
            JsonInstruction jsonInstruction = entry.getValue();
            Object fieldData = JsonInstruction.getFieldDataFromJson(jsonObject, jsonInstruction.getPath());
            fieldData = jsonInstruction.applyTransform(fieldData);
            try {
                Doctor.class
                        .getDeclaredMethod(methodName, jsonInstruction.getClazz())
                        .invoke(doctor, fieldData);
            } catch (Exception exception) {
                LOGGER.warn("Exception occurred, method name = {}", methodName, exception);
            }
        }
        doctor.setDoctorDetailStatus(EntityStatus.OK);
        return doctor;
    }

    private void addDoctorConsultationInfo(Doctor doctor, JSONObject jsonObject) {
        try {
            JSONArray jsonArray = jsonObject.getJSONObject("serviceInfo")
                    .getJSONArray("productList");
            int productionCount = jsonArray.size(); // 统计该医生的问诊产品的数目
            for (int i = 0; i < productionCount; ++i) {
                JSONObject current = jsonArray.getJSONObject(0);
                String description = current.getString("desc"); // 获取医生问诊产品描述
                Integer price = current.getInteger("price"); // 获取医生问诊产品单价
                Integer isOpened = current.getBoolean("isOpen") ? 1 : 0; // 获取医生问诊产品状态
                if (description.contains("图文")) {
                    doctor.setTextBasedConsultationPrice(price);
                    doctor.setIsTextBasedConsultationOpened(isOpened);
                    doctor.setTextBasedConsultationDescription(description);
                } else if (description.contains("电话")) {
                    doctor.setPhoneBasedConsultationPrice(price);
                    doctor.setIsPhoneBasedConsultationOpened(isOpened);
                    doctor.setPhoneBasedConsultationDescription(description);
                } else {
                    LOGGER.warn("Doctor consultation type unrecognized, description = {}", description); // 未匹配目标类型
                }
            }
        } catch (Exception exception) {
            LOGGER.warn("Doctor consultation product undefined");
        }
    }

    @Override
    public void run(String... args) throws Exception {
        if (!taskOn) {
            LOGGER.warn("Load doctor detail runner is not started.");
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
            DoctorDetailParserRunnerTask doctorDetailParserRunnerTask = new DoctorDetailParserRunnerTask(
                    file, countDownLatch, mainExecutor
            );
            mainExecutor.submit(doctorDetailParserRunnerTask);
        }
        boolean latchStatus = countDownLatch.await(awaitTerminationSeconds, TimeUnit.SECONDS);
        mainExecutor.shutdown();
        LOGGER.info("Load doctor detail runner run completed, latch status = {}", latchStatus);
    }

    private class DoctorDetailParserRunnerTask implements Runnable {
        private final File file;
        private final CountDownLatch countDownLatch;
        private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

        public DoctorDetailParserRunnerTask(File file, CountDownLatch countDownLatch, ThreadPoolTaskExecutor threadPoolTaskExecutor) {
            this.file = file;
            this.countDownLatch = countDownLatch;
            this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        }

        @Override
        public void run() {
            List<Doctor> doctorList = new ArrayList<>();
            String filePath = file.getPath();
            try {
                LOGGER.info("Processing file {}", filePath);
                StringBuilder builder = new StringBuilder();
                List<String> stringList = FileUtils.readLines(file, StandardCharsets.UTF_8);
                stringList.forEach(builder::append);
                String jsonString = builder.toString();
                JSONArray jsonArray = JSONObject.parseObject(jsonString)
                        .getJSONArray("data");
                int count = jsonArray.size();
                for (int index = 0; index < count; ++index) {
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    Doctor doctor = parseDetail(jsonObject);
                    if (doctor == null) {
                        continue;
                    }
                    addDoctorConsultationInfo(doctor, jsonObject);
                    doctorList.add(doctor);
                }
                LOGGER.info("File {} parsed", filePath);
            } catch (Exception exception) {
                LOGGER.error("Fail to parse file {}", filePath, exception);
            }
            doctorService.saveDoctorDetailList(doctorList);
            countDownLatch.countDown();
            LOGGER.info("There are approximately {} tasks left", threadPoolTaskExecutor.getQueueSize());
        }
    }
}
