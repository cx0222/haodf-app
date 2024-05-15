package com.nova.haodf.service.runner;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.nova.haodf.config.MainExecutorConfig;
import com.nova.haodf.entity.DoctorOfflineReview;
import com.nova.haodf.service.DoctorOfflineReviewService;
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
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class ParseDoctorOfflineReviewTextRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParseDoctorOfflineReviewTextRunner.class);
    private final MainExecutorConfig mainExecutorConfig;
    private final DoctorOfflineReviewService doctorOfflineReviewService;
    @Value("${options.runner.parse-doctor-offline-review-text}")
    private Boolean taskOn;
    @Value("${options.resources.doctor-offline-review-text-dir}")
    private String mainDirectory;
    @Value("${options.spider.await-termination-seconds}")
    private int awaitTerminationSeconds;

    @Autowired
    public ParseDoctorOfflineReviewTextRunner(MainExecutorConfig mainExecutorConfig, DoctorOfflineReviewService doctorOfflineReviewService) {
        this.mainExecutorConfig = mainExecutorConfig;
        this.doctorOfflineReviewService = doctorOfflineReviewService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!taskOn) {
            LOGGER.warn("Parse doctor offline review text runner is not started.");
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
        LOGGER.info("Parse doctor offline review text runner run completed, latch status = {}", latchStatus);
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

        private static String getMainDescription(Long reviewId, JSONObject jsonObject) {
            StringBuilder mainDescription = new StringBuilder();
            try {
                JSONArray array = jsonObject.getJSONArray("contentWithTitles");
                int count = array.size();
                for (int i = 0; i < count; ++i) {
                    JSONObject entry = array.getJSONObject(i);
                    String title = entry.getString("title");
                    if (!title.startsWith("病情描述")) {
                        String content = entry.getString("content");
                        mainDescription.append(content);
                    }
                }
            } catch (Exception exception) {
                LOGGER.warn("Fail to get main description, review id = {}", reviewId, exception);
            }
            return mainDescription.toString();
        }

        @Override
        public void run() {
            String filePath = file.getPath();
            try {
                LOGGER.info("Processing file {}", filePath);
                String jsonString = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                JSONObject jsonObject = JSONObject.parseObject(jsonString)
                        .getJSONObject("commentDetail4pc")
                        .getJSONObject("commentData");
                Long reviewId = jsonObject.getLong("id");
                DoctorOfflineReview doctorOfflineReview = doctorOfflineReviewService.getDoctorOfflineReviewByReviewId(reviewId);
                String attitudeDescription = jsonObject.getString("attitudeDesc");
                String mainDescription = getMainDescription(reviewId, jsonObject);
                doctorOfflineReview.setReviewId(reviewId);
                doctorOfflineReview.setAttitudeDescription(attitudeDescription);
                doctorOfflineReview.setMainDescription(mainDescription);
                doctorOfflineReview.setStatus(12);
                doctorOfflineReviewService.saveDoctorOfflineReview(doctorOfflineReview);
                LOGGER.info("File {} parsed", filePath);
            } catch (Exception exception) {
                LOGGER.error("Fail to parse file {}", filePath, exception);
            }
            countDownLatch.countDown();
            LOGGER.info("There are approximately {} tasks left", threadPoolTaskExecutor.getQueueSize());
        }
    }
}
