package com.nova.haodf.service.task;

import com.nova.haodf.util.EntityStatus;
import com.nova.haodf.config.ProxyConfig;
import com.nova.haodf.entity.Doctor;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DownloadDoctorStatisticsTask implements Runnable {
    private static final int RETRY_COUNT = 5;
    private static final int TIMEOUT_SECOND = 15;
    private static final String DOCTOR_STATISTICS_URL_TEMPLATE = "https://www.haodf.com/ndoctor/ajaxGetDoctorData?spaceId=%d";
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadDoctorStatisticsTask.class);
    private final Doctor doctor;
    private final ProxyConfig proxyConfig;
    private final ThreadPoolTaskExecutor downloaderExecutor;
    private final CountDownLatch countDownLatch;
    private final Map<String, String> resultMap;
    private final List<Doctor> successDoctorList;
    private final List<Doctor> failDoctorList;

    public DownloadDoctorStatisticsTask(
            Doctor doctor, ProxyConfig proxyConfig, ThreadPoolTaskExecutor downloaderExecutor, CountDownLatch countDownLatch,
            Map<String, String> resultMap, List<Doctor> successDoctorList, List<Doctor> failDoctorList) {
        this.doctor = doctor;
        this.proxyConfig = proxyConfig;
        this.downloaderExecutor = downloaderExecutor;
        this.countDownLatch = countDownLatch;
        this.resultMap = resultMap;
        this.successDoctorList = successDoctorList;
        this.failDoctorList = failDoctorList;
    }

    @Override
    public void run() {
        Map<String, String> currentMap = new HashMap<>();
        Long spaceId = doctor.getSpaceId(), doctorId = doctor.getDoctorId();
        int retryCount = 0;
        boolean cleanStop = false;
        while (retryCount < RETRY_COUNT && !cleanStop) {
            try {
                Future<Optional<String>> future = downloaderExecutor.submit(() -> {
                    Thread.sleep(300);
                    return downloadDoctorStatisticsBySpaceId(spaceId);
                });
                Optional<String> optionalString = future.get(TIMEOUT_SECOND, TimeUnit.SECONDS);
                if (optionalString.isPresent()) {
                    currentMap.put("doctor-statistics/" + doctorId, optionalString.get());
                    LOGGER.info("Downloaded doctor statistics, spaceId = {}, retry = {}", spaceId, retryCount); // 本页获取信息正常，未到最后一页
                    cleanStop = true; // 获取信息正常，结尾标志
                }
            } catch (InterruptedException | ExecutionException | TimeoutException exception) {
                // 本页获取信息异常，进行重试
                LOGGER.warn("Failed to download doctor statistics, spaceId = {}, retry = {}", spaceId, retryCount, exception);
            } finally {
                ++retryCount; // 重新尝试，更新重试次数
            }
        }
        if (cleanStop) {
            doctor.setDoctorStatisticsStatus(EntityStatus.OK);
            successDoctorList.add(doctor);
            resultMap.putAll(currentMap);
            LOGGER.info("Downloaded doctor statistics, spaceId = {}", spaceId);
        } else {
            doctor.setDoctorStatisticsStatus(EntityStatus.ERROR);
            failDoctorList.add(doctor);
            LOGGER.warn("Fail to download doctor statistics, spaceId = {}", spaceId);
        }
        countDownLatch.countDown();
    }

    private Optional<String> downloadDoctorStatisticsBySpaceId(Long spaceId) throws InterruptedException, IOException, ParseException {
        HttpGet httpGet = new HttpGet(DOCTOR_STATISTICS_URL_TEMPLATE.formatted(spaceId));
        httpGet.setConfig(proxyConfig.getRequestConfig());
        httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        httpGet.setHeader(HttpHeaders.USER_AGENT, proxyConfig.getUserAgent());
        httpGet.setHeader(HttpHeaders.ACCEPT_CHARSET, "utf-8");

        Optional<String> entityString = Optional.empty();
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(proxyConfig.getCredentialsProvider())
                .build()) {
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity httpEntity = response.getEntity();
                if (httpEntity != null) {
                    String rawString = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
                    if (!rawString.isEmpty()) {
                        entityString = Optional.of(rawString);
                    }
                    LOGGER.info("Downloaded doctor statistics, spaceId = {}", spaceId);
                }
            }
        }
        return entityString;
    }
}
