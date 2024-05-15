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
import org.jsoup.Jsoup;
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

public class DownloadDoctorConsultationListTask implements Runnable {
    private static final int MAX_PAGE_COUNT = 100;
    private static final int RETRY_COUNT = 5;
    private static final int TIMEOUT_SECOND = 15;
    private static final String CONSULTATION_LIST_URL_TEMPLATE = "https://www.haodf.com/doctor/%d/bingcheng.html?p=%d&type=all";
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadDoctorConsultationListTask.class);
    private final Doctor doctor;
    private final ProxyConfig proxyConfig;
    private final ThreadPoolTaskExecutor downloaderExecutor;
    private final CountDownLatch countDownLatch;
    private final Map<String, String> resultMap;
    private final List<Doctor> successDoctorList;
    private final List<Doctor> failDoctorList;

    public DownloadDoctorConsultationListTask(
            Doctor doctor, ProxyConfig proxyConfig, ThreadPoolTaskExecutor downloaderExecutor,
            CountDownLatch countDownLatch, Map<String, String> resultMap, List<Doctor> successDoctorList, List<Doctor> failDoctorList
    ) {
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
        Long doctorId = doctor.getDoctorId();
        int pageCount = 1;
        boolean hasNext = true; // 标记是否还有下一页
        while (pageCount <= MAX_PAGE_COUNT && hasNext) {
            int retryCount = 0;
            while (retryCount < RETRY_COUNT) {
                try {
                    int finalPageCount = pageCount;
                    Future<Optional<String>> future = downloaderExecutor.submit(() -> {
                        Thread.sleep(500);
                        return downloadDoctorConsultationByDoctor(doctorId, finalPageCount);
                    });
                    Optional<String> optionalString = future.get(TIMEOUT_SECOND, TimeUnit.SECONDS);
                    if (optionalString.isPresent()) {
                        String filename = String.format("doctor-consultation/%d/%d", doctorId, pageCount);
                        currentMap.put(filename, optionalString.get());
                        LOGGER.info(
                                "Downloaded doctor consultation list, doctorId = {}, pageCount = {}, retry = {}",
                                doctorId, pageCount, retryCount
                        ); // 本页获取信息正常，未到最后一页
                    } else {
                        hasNext = false; // 本页获取信息正常，已到最后一页
                    }
                    break;
                } catch (InterruptedException | ExecutionException | TimeoutException exception) {
                    // 本页获取信息异常，进行重试
                    LOGGER.warn(
                            "Exception occurred when downloading doctor consultation list, doctorId = {}, pageCount = {}, retry = {}",
                            doctorId, pageCount, retryCount, exception
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
        if (pageCount > MAX_PAGE_COUNT) {
            doctor.setConsultationStatus(EntityStatus.STOPPED);
            successDoctorList.add(doctor);
            LOGGER.warn("Downloaded doctor consultation list, but might be incomplete, doctorId = {}, pageCount = {}", doctorId, pageCount);
        } else if (hasNext) {
            doctor.setConsultationStatus(EntityStatus.ERROR);
            failDoctorList.add(doctor);
            LOGGER.warn("Fail to download doctor consultation list, doctorId = {}, pageCount = {}", doctorId, pageCount);
        } else {
            doctor.setConsultationStatus(EntityStatus.OK);
            successDoctorList.add(doctor);
            resultMap.putAll(currentMap);
            LOGGER.info("Downloaded doctor consultation list, doctorId = {}, pageCount = {}", doctorId, pageCount);
        }
        countDownLatch.countDown();
    }

    private Optional<String> downloadDoctorConsultationByDoctor(Long doctorId, int page) throws InterruptedException, IOException, ParseException {
        HttpGet httpGet = new HttpGet(CONSULTATION_LIST_URL_TEMPLATE.formatted(doctorId, page));
        httpGet.setConfig(proxyConfig.getRequestConfig());
        httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "text/html");
        httpGet.setHeader(HttpHeaders.USER_AGENT, proxyConfig.getUserAgent());
        httpGet.setHeader(HttpHeaders.ACCEPT_CHARSET, "utf-8");
        httpGet.setHeader(HttpHeaders.CONTENT_ENCODING, "gzip");
        Optional<String> entityString = Optional.empty();
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(proxyConfig.getCredentialsProvider())
                .build()) {
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity httpEntity = response.getEntity();
                if (httpEntity != null) {
                    String rawString = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
                    int recordCount = Jsoup.parse(rawString)
                            .getElementsByClass("disease")
                            .size();
                    if (recordCount != 0) {
                        entityString = Optional.of(rawString);
                    }
                    LOGGER.info("Downloaded doctor consultation list, doctorId = {}, page = {}", doctorId, page);
                }
            }
        }
        return entityString;
    }
}
