package com.nova.haodf.service.task;

import com.nova.haodf.config.ProxyConfig;
import com.nova.haodf.entity.DoctorOfflineReview;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class DownloadDoctorOfflineReviewContentTask implements Runnable {
    private static final String REVIEW_CONTENT_URL_TEMPLATE = "https://www.haodf.com/doctor/%d/pingjia-zhenliao/%d.html";
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadDoctorOfflineReviewContentTask.class);
    private final DoctorOfflineReview doctorOfflineReview;
    private final ProxyConfig proxyConfig;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final CountDownLatch countDownLatch;
    private final Map<String, String> resultMap;
    private final List<DoctorOfflineReview> successDoctorOfflineReviewList;
    private final List<DoctorOfflineReview> deletedDoctorOfflineReviewList;


    public DownloadDoctorOfflineReviewContentTask(
            DoctorOfflineReview doctorOfflineReview, ProxyConfig proxyConfig, ThreadPoolTaskExecutor taskExecutor, CountDownLatch countDownLatch,
            Map<String, String> resultMap, List<DoctorOfflineReview> successDoctorOfflineReviewList, List<DoctorOfflineReview> deletedDoctorOfflineReviewList) {
        this.doctorOfflineReview = doctorOfflineReview;
        this.proxyConfig = proxyConfig;
        this.taskExecutor = taskExecutor;
        this.countDownLatch = countDownLatch;
        this.resultMap = resultMap;
        this.successDoctorOfflineReviewList = successDoctorOfflineReviewList;
        this.deletedDoctorOfflineReviewList = deletedDoctorOfflineReviewList;
    }

    @Override
    public void run() {
        Long doctorId = doctorOfflineReview.getDoctorId();
        Long reviewId = doctorOfflineReview.getReviewId();
        try {
            Future<String> future = taskExecutor.submit(() -> downloadDoctorOfflineReviewContent(doctorId, reviewId));
            String content = future.get(5, TimeUnit.SECONDS);
            if (content != null) {
                String filename = String.format("doctor-offline-review-content/%d", reviewId);
                resultMap.put(filename, content);
                LOGGER.info("Downloaded doctor offline review content, reviewId = {}", reviewId);
            }
        } catch (Exception exception) {
            LOGGER.warn("Failed to download doctor offline review content, reviewId = {}", reviewId);
        } finally {
            countDownLatch.countDown();
        }
    }

    private String downloadDoctorOfflineReviewContent(Long doctorId, Long reviewId) throws IOException, ParseException {
        HttpGet httpGet = new HttpGet(REVIEW_CONTENT_URL_TEMPLATE.formatted(doctorId, reviewId));
        httpGet.setConfig(proxyConfig.getRequestConfig());
        httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "text/html");
        httpGet.setHeader(HttpHeaders.USER_AGENT, proxyConfig.getUserAgent());
        httpGet.setHeader(HttpHeaders.ACCEPT_CHARSET, "utf-8");
        httpGet.setHeader(HttpHeaders.CONTENT_ENCODING, "gzip");
        httpGet.setHeader(HttpHeaders.CONNECTION, "close");

        String content = null;
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(proxyConfig.getCredentialsProvider())
                .build()) {
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity httpEntity = response.getEntity();
                if (httpEntity != null) {
                    String rawString = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
                    int start = rawString.indexOf("window.__INITIAL_STATE__");
                    if (start != -1) {
                        start = rawString.indexOf("{", start);
                        int end = rawString.lastIndexOf("};");
                        content = rawString.substring(start, end + 1);
                        doctorOfflineReview.setStatus(9);
                        successDoctorOfflineReviewList.add(doctorOfflineReview);
                        LOGGER.info("Found doctor offline review content, reviewId = {}", reviewId);
                    } else if (rawString.contains("PAGE_DATA")) {
                        LOGGER.warn("Doctor offline review content might be inaccessible, reviewId = {}", reviewId);
                    } else {
                        doctorOfflineReview.setStatus(1023);
                        deletedDoctorOfflineReviewList.add(doctorOfflineReview);
                        LOGGER.warn("Doctor offline review content might be deleted, reviewId = {}", reviewId);
                    }
                }
            }
        }
        return content;
    }
}
