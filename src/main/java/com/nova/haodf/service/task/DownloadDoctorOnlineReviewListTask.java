package com.nova.haodf.service.task;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.nova.haodf.entity.Doctor;
import com.nova.haodf.util.EntityStatus;
import com.nova.haodf.config.ProxyConfig;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DownloadDoctorOnlineReviewListTask implements Runnable {
    private static final int PAGE_SIZE = 100;
    private static final int MAX_PAGE_COUNT = 1024;
    private static final int RETRY_COUNT = 5;
    private static final String REVIEW_LIST_URL = "https://www.haodf.com/ndoctor/comment/AjaxCommentList";
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadDoctorOnlineReviewListTask.class);
    private final Doctor doctor;
    private final ProxyConfig proxyConfig;
    private final CountDownLatch countDownLatch;
    private final Map<String, String> resultMap;
    private final List<Doctor> successDoctorList;
    private final List<Doctor> failDoctorList;

    public DownloadDoctorOnlineReviewListTask(
            Doctor doctor, ProxyConfig proxyConfig, CountDownLatch countDownLatch,
            Map<String, String> resultMap, List<Doctor> successDoctorList, List<Doctor> failDoctorList
    ) {
        this.doctor = doctor;
        this.proxyConfig = proxyConfig;
        this.countDownLatch = countDownLatch;
        this.resultMap = resultMap;
        this.successDoctorList = successDoctorList;
        this.failDoctorList = failDoctorList;
    }

    @Override
    public void run() {
        Map<String, String> currentMap = new HashMap<>();
        Long doctorId = doctor.getDoctorId();
        int pageCount = 1;
        boolean hasNext = true; // 标记是否还有下一页
        while (pageCount <= MAX_PAGE_COUNT && hasNext) {
            int retryCount = 0;
            while (retryCount < RETRY_COUNT) {
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                try {
                    int finalPageCount = pageCount;
                    Future<Optional<String>> future = executorService.submit(() -> {
                        Thread.sleep(500);
                        return downloadDoctorOnlineReviewByDoctor(doctorId, finalPageCount, PAGE_SIZE);
                    });
                    Optional<String> optionalString = future.get(5, TimeUnit.SECONDS);
                    if (optionalString.isPresent()) {
                        String filename = String.format("doctor-online-review/%d/%d", doctorId, pageCount);
                        currentMap.put(filename, optionalString.get());
                        LOGGER.info(
                                "Downloaded doctor online review list, doctorId = {}, pageCount = {}, retry = {}",
                                doctorId, pageCount, retryCount
                        ); // 本页获取信息正常，未到最后一页
                    } else {
                        hasNext = false; // 本页获取信息正常，已到最后一页
                    }
                    break;
                } catch (InterruptedException | ExecutionException | TimeoutException exception) {
                    // 本页获取信息异常，进行重试
                    LOGGER.warn(
                            "Exception occurred when downloading doctor online review list, doctorId = {}, pageCount = {}, retry = {}",
                            doctorId, pageCount, retryCount, exception
                    );
                } finally {
                    executorService.shutdown();
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
            doctor.setOnlineStatus(EntityStatus.ERROR);
            failDoctorList.add(doctor);
            LOGGER.warn("Fail to download doctor online review list, doctorId = {}, pageCount = {}", doctorId, pageCount);
        } else {
            doctor.setOnlineStatus(EntityStatus.OK);
            successDoctorList.add(doctor);
            resultMap.putAll(currentMap);
            LOGGER.info("Downloaded doctor online review list, doctorId = {}, pageCount = {}", doctorId, pageCount);
        }
        countDownLatch.countDown();
    }

    private Optional<String> downloadDoctorOnlineReviewByDoctor(Long doctorId, int page, int pageSize) throws InterruptedException, IOException, ParseException {
        ParamsConfig paramsConfig = new ParamsConfig(doctorId, page, pageSize);
        String paramsString = JSON.toJSONString(paramsConfig);
        HttpPost httpPost = new HttpPost(REVIEW_LIST_URL);
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
                            .getJSONObject("data")
                            .getJSONArray("data")
                            .isEmpty();
                    if (!isEmpty) {
                        entityString = Optional.of(rawString);
                    }
                    LOGGER.info("Downloaded doctor online review list, doctorId = {}, page = {}", doctorId, page);
                }
            }
        }
        return entityString;
    }

    private record ParamsConfig(Long doctorId, Integer nowPage, Integer pageSize) {
    }
}
