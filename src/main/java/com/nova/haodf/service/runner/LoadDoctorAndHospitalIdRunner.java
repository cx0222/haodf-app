package com.nova.haodf.service.runner;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.nova.haodf.config.MainExecutorConfig;
import com.nova.haodf.service.DoctorService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class LoadDoctorAndHospitalIdRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadDoctorAndHospitalIdRunner.class);
    private final MainExecutorConfig mainExecutorConfig;
    private final DoctorService doctorService;
    @Value("${options.runner.load-doctor-and-hospital-id}")
    private Boolean taskOn;
    @Value("${options.resources.doctor-detail-dir}")
    private String mainDirectory;
    @Value("${options.spider.await-termination-seconds}")
    private int awaitTerminationSeconds;

    @Autowired
    public LoadDoctorAndHospitalIdRunner(MainExecutorConfig mainExecutorConfig, DoctorService doctorService) {
        this.mainExecutorConfig = mainExecutorConfig;
        this.doctorService = doctorService;
    }

    private void extractDoctorId(Long hospitalId, JSONObject jsonObject, Map<Long, Long> doctorHospitalIdMap) {
        try {
            Objects.requireNonNull(jsonObject);
        } catch (Exception exception) {
            LOGGER.warn("Could not parse json", exception);
        }
        Long doctorId = jsonObject.getLong("doctorId");
        doctorHospitalIdMap.put(doctorId, hospitalId);
    }

    @Override
    public void run(String... args) throws Exception {
        if (!taskOn) {
            LOGGER.warn("Load doctor and hospital id runner is not started.");
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
            DoctorHospitalIdSetterRunnerTask doctorHospitalIdSetterRunnerTask = new DoctorHospitalIdSetterRunnerTask(
                    file, countDownLatch, mainExecutor
            );
            mainExecutor.submit(doctorHospitalIdSetterRunnerTask);
        }
        boolean latchStatus = countDownLatch.await(awaitTerminationSeconds, TimeUnit.SECONDS);
        mainExecutor.shutdown();
        LOGGER.info("Load doctor and hospital id runner run completed, latch status = {}", latchStatus);
    }

    private class DoctorHospitalIdSetterRunnerTask implements Runnable {
        private final File file;
        private final CountDownLatch countDownLatch;
        private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

        public DoctorHospitalIdSetterRunnerTask(File file, CountDownLatch countDownLatch, ThreadPoolTaskExecutor threadPoolTaskExecutor) {
            this.file = file;
            this.countDownLatch = countDownLatch;
            this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        }

        @Override
        public void run() {
            Map<Long, Long> doctorHospitalIdMap = new HashMap<>();
            String filePath = file.getPath();
            try {
                LOGGER.info("Processing file {}", filePath);
                String[] splits = file.getPath().split("/");
                Long hospitalId = Long.parseLong(splits[splits.length - 2].substring(12));
                StringBuilder builder = new StringBuilder();
                List<String> stringList = FileUtils.readLines(file, StandardCharsets.UTF_8);
                stringList.forEach(builder::append);
                String jsonString = builder.toString();
                JSONArray jsonArray = JSONObject.parseObject(jsonString)
                        .getJSONArray("data");
                int count = jsonArray.size();
                for (int index = 0; index < count; ++index) {
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    extractDoctorId(hospitalId, jsonObject, doctorHospitalIdMap);
                }
                LOGGER.info("File {} parsed", filePath);
            } catch (Exception exception) {
                LOGGER.error("Fail to parse file {}", filePath, exception);
            }
            doctorService.updateHospitalIdMap(doctorHospitalIdMap);
            countDownLatch.countDown();
            LOGGER.info("There are approximately {} tasks left", threadPoolTaskExecutor.getQueueSize());
        }
    }
}
