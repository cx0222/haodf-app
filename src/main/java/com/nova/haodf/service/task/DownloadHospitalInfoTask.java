package com.nova.haodf.service.task;

import com.nova.haodf.util.EntityStatus;
import com.nova.haodf.config.ProxyConfig;
import com.nova.haodf.entity.Hospital;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

public class DownloadHospitalInfoTask implements Runnable {
    private static final int RETRY_COUNT = 5;
    private static final String HOSPITAL_INFO_URL_TEMPLATE = "https://www.haodf.com/hospital/%d.html";
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadHospitalInfoTask.class);
    private final Hospital hospital;
    private final ProxyConfig proxyConfig;
    private final CountDownLatch countDownLatch;
    private final Map<String, String> resultMap;
    private final List<Hospital> successHospitalList;
    private final List<Hospital> failHospitalList;

    public DownloadHospitalInfoTask(
            Hospital hospital, ProxyConfig proxyConfig, CountDownLatch countDownLatch,
            Map<String, String> resultMap, List<Hospital> successHospitalList, List<Hospital> failHospitalList
    ) {
        this.hospital = hospital;
        this.proxyConfig = proxyConfig;
        this.countDownLatch = countDownLatch;
        this.resultMap = resultMap;
        this.successHospitalList = successHospitalList;
        this.failHospitalList = failHospitalList;
    }

    @Override
    public void run() {
        Map<String, String> currentMap = new HashMap<>();
        Long hospitalId = hospital.getHospitalId();
        int retryCount = 0;
        boolean cleanStop = false;
        while (retryCount < RETRY_COUNT && !cleanStop) {
            try {
                Optional<String> optionalString = downloadHospitalInfoByHospitalId(hospitalId);
                if (optionalString.isPresent()) {
                    currentMap.put("hospital-info/" + hospitalId, optionalString.get());
                    LOGGER.info("Downloaded hospital info, hospitalId = {}, retry = {}", hospitalId, retryCount); // 本页获取信息正常，未到最后一页
                    cleanStop = true; // 获取信息正常，结尾标志
                }
            } catch (InterruptedException | IOException | ParseException exception) {
                // 本页获取信息异常，进行重试
                LOGGER.warn("Failed to download hospital info, hospitalId = {}, retry = {}", hospitalId, retryCount, exception);
            } finally {
                ++retryCount; // 重新尝试，更新重试次数
            }
        }
        if (cleanStop) {
            hospital.setHospitalInfoStatus(EntityStatus.OK);
            successHospitalList.add(hospital);
            resultMap.putAll(currentMap);
            LOGGER.info("Downloaded hospital info, hospitalId = {}", hospitalId);
        } else {
            hospital.setHospitalInfoStatus(EntityStatus.ERROR);
            failHospitalList.add(hospital);
            LOGGER.warn("Fail to download hospital info, hospitalId = {}", hospitalId);
        }
        countDownLatch.countDown();
    }

    private Optional<String> downloadHospitalInfoByHospitalId(Long hospitalId) throws InterruptedException, IOException, ParseException {
        Thread.sleep(400);
        HttpGet httpGet = new HttpGet(HOSPITAL_INFO_URL_TEMPLATE.formatted(hospitalId));
        httpGet.setConfig(proxyConfig.getRequestConfig());
        httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "text/html");
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
                    LOGGER.info("Downloaded hospital info, hospitalId = {}", hospitalId);
                }
            }
        }
        return entityString;
    }
}
