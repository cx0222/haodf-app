package com.nova.haodf.service.runner;

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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class LoadDoctorStatisticsRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadDoctorStatisticsRunner.class);
    private static final Map<String, JsonInstruction> JSON_INSTRUCTION_MAP = new HashMap<>();

    static {
        JSON_INSTRUCTION_MAP.put("setArticleCount", new JsonInstruction("articleNum", Integer.class));
        JSON_INSTRUCTION_MAP.put("setDoctorVoteCount", new JsonInstruction("doctorVoteCnt", Integer.class));
        JSON_INSTRUCTION_MAP.put("setOpenSpaceTime", new JsonInstruction("openSpaceTime", LocalDateTime.class, JsonInstruction.STRING_TO_LOCAL_DATETIME_ADAPTER_FUNCTION));
        JSON_INSTRUCTION_MAP.put("setPresentCount", new JsonInstruction("presentCnt", Integer.class));
        JSON_INSTRUCTION_MAP.put("setSpaceRepliedCount", new JsonInstruction("spaceRepliedCount", Integer.class));
        JSON_INSTRUCTION_MAP.put("setTotalHit", new JsonInstruction("totalHit", Integer.class));
        JSON_INSTRUCTION_MAP.put("setTotalSignInCount", new JsonInstruction("totalSignInCount", Integer.class));
        JSON_INSTRUCTION_MAP.put("setVoteIn2Years", new JsonInstruction("voteIn2Years", Integer.class));
    }

    private final MainExecutorConfig mainExecutorConfig;
    private final DoctorService doctorService;
    @Value("${options.runner.load-doctor-statistics}")
    private Boolean taskOn;
    @Value("${options.resources.doctor-statistics-dir}")
    private String mainDirectory;
    @Value("${options.spider.await-termination-seconds}")
    private int awaitTerminationSeconds;

    @Autowired
    public LoadDoctorStatisticsRunner(MainExecutorConfig mainExecutorConfig, DoctorService doctorService) {
        this.mainExecutorConfig = mainExecutorConfig;
        this.doctorService = doctorService;
    }

    private Doctor parseStatistics(JSONObject jsonObject) {
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
        doctor.setDoctorStatisticsStatus(EntityStatus.OK);
        return doctor;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!taskOn) {
            LOGGER.warn("Load doctor statistics runner is not started.");
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
            DoctorStatisticsLoaderRunnerTask doctorStatisticsLoaderRunnerTask = new DoctorStatisticsLoaderRunnerTask(
                    file, countDownLatch, mainExecutor
            );
            mainExecutor.submit(doctorStatisticsLoaderRunnerTask);
        }
        boolean latchStatus = countDownLatch.await(awaitTerminationSeconds, TimeUnit.SECONDS);
        mainExecutor.shutdown();
        LOGGER.info("Load doctor statistics runner run completed, latch status = {}", latchStatus);
    }

    private class DoctorStatisticsLoaderRunnerTask implements Runnable {
        private final File file;
        private final CountDownLatch countDownLatch;
        private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

        public DoctorStatisticsLoaderRunnerTask(File file, CountDownLatch countDownLatch, ThreadPoolTaskExecutor threadPoolTaskExecutor) {
            this.file = file;
            this.countDownLatch = countDownLatch;
            this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        }

        @Override
        public void run() {
            List<Doctor> doctorList = new ArrayList<>();
            String filePath = file.getPath();
            String filename = file.getName().replaceAll("\\D", "");
            Long doctorId = Long.parseLong(filename);
            try {
                LOGGER.info("Processing file {}", filePath);
                StringBuilder builder = new StringBuilder();
                List<String> stringList = FileUtils.readLines(file, StandardCharsets.UTF_8);
                stringList.forEach(builder::append);
                String jsonString = builder.toString();
                JSONObject jsonObject = JSONObject.parseObject(jsonString)
                        .getJSONObject("data");
                Doctor doctor = parseStatistics(jsonObject);
                if (doctor != null) {
                    doctor.setDoctorId(doctorId);
                    doctorList.add(doctor);
                }
                LOGGER.info("File {} parsed", filePath);
            } catch (Exception exception) {
                LOGGER.error("Fail to parse file {}", filePath, exception);
            }
            doctorService.saveDoctorStatisticsList(doctorList);
            countDownLatch.countDown();
            LOGGER.info("There are approximately {} tasks left", threadPoolTaskExecutor.getQueueSize());
        }
    }
}
