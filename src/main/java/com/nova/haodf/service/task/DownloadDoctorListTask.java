package com.nova.haodf.service.task;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.nova.haodf.entity.Hospital;
import com.nova.haodf.config.ProxyConfig;
import com.nova.haodf.util.EntityStatus;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

public class DownloadDoctorListTask implements Runnable {
    private static final int MAX_PAGE_COUNT = 1024;
    private static final int RETRY_COUNT = 5;
    private static final String DOCTOR_LIST_URL = "https://www.haodf.com/nhospital/pc/hospital/ajaxHosTuijianDocList";
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadDoctorListTask.class);
    private final Hospital hospital;
    private final ProxyConfig proxyConfig;
    private final CountDownLatch countDownLatch;
    private final Map<String, String> resultMap;
    private final List<Hospital> successHospitalList;
    private final List<Hospital> failHospitalList;

    public DownloadDoctorListTask(
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
        int pageCount = 1;
        boolean hasNext = true; // 标记是否还有下一页
        while (pageCount <= MAX_PAGE_COUNT && hasNext) {
            int retryCount = 0;
            while (retryCount < RETRY_COUNT) {
                try {
                    Optional<String> optionalString = downloadDoctorListByHospital(hospitalId, pageCount);
                    if (optionalString.isPresent()) {
                        currentMap.put(hospitalId + "/" + pageCount, optionalString.get());
                        LOGGER.info(
                                "Downloaded doctor list, hospitalId = {}, pageCount = {}, retry = {}",
                                hospitalId, pageCount, retryCount
                        ); // 本页获取信息正常，未到最后一页
                    } else {
                        hasNext = false; // 本页获取信息正常，已到最后一页
                    }
                    break;
                } catch (InterruptedException | IOException | ParseException exception) {
                    // 本页获取信息异常，进行重试
                    LOGGER.warn(
                            "Failed to download doctor list, hospitalId = {}, pageCount = {}, retry = {}",
                            hospitalId, pageCount, retryCount, exception
                    );
                } finally {
                    ++retryCount;
                }
            }
            if (retryCount == RETRY_COUNT) {
                break; // 在获取本页信息时，发生多次异常，重试次数达到最大值
                // 此时，hasNext == true，表示可能还有下一页，本次任务执行异常
            }
            ++pageCount;
        }
        if (hasNext) {
            hospital.setStatus(EntityStatus.ERROR);
            failHospitalList.add(hospital);
            LOGGER.warn("Fail to download doctor list, hospitalId = {}, pageCount = {}", hospitalId, pageCount);
        } else {
            hospital.setStatus(EntityStatus.OK);
            successHospitalList.add(hospital);
            resultMap.putAll(currentMap);
            LOGGER.info("Downloaded doctor list, hospitalId = {}, pageCount = {}", hospitalId, pageCount);
        }
        countDownLatch.countDown();
    }

    private Optional<String> downloadDoctorListByHospital(Long hospitalId, int page) throws InterruptedException, IOException, ParseException {
        Thread.sleep(800);
        ParamsConfig paramsConfig = new ParamsConfig(hospitalId, page);
        String paramsString = JSON.toJSONString(paramsConfig);
        HttpPost httpPost = new HttpPost(DOCTOR_LIST_URL);
        httpPost.setConfig(proxyConfig.getRequestConfig());
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8");
        httpPost.setHeader(HttpHeaders.USER_AGENT, proxyConfig.getUserAgent());
        httpPost.setHeader(HttpHeaders.ACCEPT_CHARSET, "utf-8");
        httpPost.setEntity(new StringEntity(paramsString, StandardCharsets.UTF_8));

        Optional<String> entityString = Optional.empty();
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(proxyConfig.getCredentialsProvider())
                .build()) {
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity httpEntity = response.getEntity();
                if (httpEntity != null) {
                    String rawString = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
                    boolean isEmpty = JSONObject.parseObject(rawString)
                            .getJSONArray("data")
                            .isEmpty();
                    if (!isEmpty) {
                        entityString = Optional.of(rawString);
                    }
                    LOGGER.info("Downloaded doctor list, hospitalId = {}, page = {}", hospitalId, page);
                }
            }
        }
        return entityString;
    }

    private record ParamsConfig(Long hospitalId, Integer nowPage) {
    }
}
