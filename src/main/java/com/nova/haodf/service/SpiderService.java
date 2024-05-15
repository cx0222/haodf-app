package com.nova.haodf.service;

import com.nova.haodf.config.DownloaderExecutorConfig;
import com.nova.haodf.config.DownloaderTaskSwitch;
import com.nova.haodf.config.MainExecutorConfig;
import com.nova.haodf.config.ProxyConfig;
import com.nova.haodf.entity.Doctor;
import com.nova.haodf.entity.DoctorOfflineReview;
import com.nova.haodf.entity.Hospital;
import com.nova.haodf.service.task.DownloadDoctorConsultationListTask;
import com.nova.haodf.service.task.DownloadDoctorListTask;
import com.nova.haodf.service.task.DownloadDoctorOfflineReviewContentTask;
import com.nova.haodf.service.task.DownloadDoctorOfflineReviewListTask;
import com.nova.haodf.service.task.DownloadDoctorOnlineReviewListTask;
import com.nova.haodf.service.task.DownloadDoctorStatisticsTask;
import com.nova.haodf.service.task.DownloadHospitalInfoTask;
import com.nova.haodf.util.EntityStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class SpiderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpiderService.class);
    private final DownloaderTaskSwitch downloaderTaskSwitch;
    private final HospitalService hospitalService;
    private final DoctorService doctorService;
    private final DoctorOfflineReviewService doctorOfflineReviewService;
    private final FileStorageService fileStorageService;
    private final ProxyConfig proxyConfig;
    private final MainExecutorConfig mainExecutorConfig;
    private final DownloaderExecutorConfig downloaderExecutorConfig;
    @Value("${options.downloader.batch-size}")
    private int batchSize;
    @Value("${options.downloader.await-termination-seconds}")
    private int awaitTerminationSeconds;

    @Autowired
    public SpiderService(
            DownloaderTaskSwitch downloaderTaskSwitch, HospitalService hospitalService, DoctorService doctorService,
            DoctorOfflineReviewService doctorOfflineReviewService, FileStorageService fileStorageService,
            ProxyConfig proxyConfig, MainExecutorConfig mainExecutorConfig, DownloaderExecutorConfig downloaderExecutorConfig
    ) {
        this.downloaderTaskSwitch = downloaderTaskSwitch;
        this.hospitalService = hospitalService;
        this.doctorService = doctorService;
        this.doctorOfflineReviewService = doctorOfflineReviewService;
        this.fileStorageService = fileStorageService;
        this.proxyConfig = proxyConfig;
        this.mainExecutorConfig = mainExecutorConfig;
        this.downloaderExecutorConfig = downloaderExecutorConfig;
    }

    @Scheduled(fixedRate = 3 * 60 * 1000)
    public void getDoctorListInBatch() throws IOException, InterruptedException {
        if (!downloaderTaskSwitch.getGetDoctorListTaskOn()) {
            LOGGER.warn("Get doctor list task is not started.");
            return;
        }
        List<Hospital> hospitalList = hospitalService.getHospitalListByStatusAndLimit(EntityStatus.EMPTY, batchSize);
        ThreadPoolTaskExecutor downloaderServiceExecutor = mainExecutorConfig.getMainExecutor();
        Map<String, String> batchResultMap = new ConcurrentHashMap<>();
        int count = hospitalList.size();
        LOGGER.info("Downloading {} doctor lists", count);
        List<Hospital> successHospitalList = new CopyOnWriteArrayList<>();
        List<Hospital> failHospitalList = new CopyOnWriteArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(count);
        for (Hospital hospital : hospitalList) {
            DownloadDoctorListTask downloadDoctorListTask = new DownloadDoctorListTask(
                    hospital, proxyConfig, countDownLatch, batchResultMap, successHospitalList, failHospitalList
            );
            downloaderServiceExecutor.execute(downloadDoctorListTask);
        }
        boolean latchStatus = countDownLatch.await(awaitTerminationSeconds, TimeUnit.SECONDS);
        fileStorageService.saveToFile(batchResultMap);
        hospitalService.saveHospitalList(successHospitalList);
        hospitalService.saveHospitalList(failHospitalList);
        LOGGER.info("Finished {} tasks, success count = {}, fail count = {}, count down latch status = {}",
                count, successHospitalList.size(), failHospitalList.size(), latchStatus
        );
        downloaderServiceExecutor.shutdown();
    }

    @Scheduled(fixedDelay = 1000)
    public void getDoctorOnlineReviewListInBatch() throws IOException, InterruptedException {
        if (!downloaderTaskSwitch.getGetDoctorOnlineReviewListTaskOn()) {
            LOGGER.warn("Get doctor online review list task is not started.");
            return;
        }
        List<Doctor> doctorList = doctorService.getDoctorListByOnlineStatusAndLimit(EntityStatus.EMPTY, batchSize);
        ThreadPoolTaskExecutor downloaderServiceExecutor = mainExecutorConfig.getMainExecutor();
        Map<String, String> batchResultMap = new ConcurrentHashMap<>();
        int count = doctorList.size();
        LOGGER.info("Downloading {} doctor online review lists", count);
        List<Doctor> successDoctorList = new CopyOnWriteArrayList<>();
        List<Doctor> failDoctorList = new CopyOnWriteArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(count);
        for (Doctor doctor : doctorList) {
            DownloadDoctorOnlineReviewListTask downloadDoctorOnlineReviewListTask = new DownloadDoctorOnlineReviewListTask(
                    doctor, proxyConfig, countDownLatch, batchResultMap, successDoctorList, failDoctorList
            );
            downloaderServiceExecutor.execute(downloadDoctorOnlineReviewListTask);
        }
        boolean latchStatus = countDownLatch.await(awaitTerminationSeconds, TimeUnit.SECONDS);
        fileStorageService.saveToFile(batchResultMap);
        doctorService.saveDoctorList(successDoctorList);
        doctorService.saveDoctorList(failDoctorList);
        LOGGER.info("Finished {} tasks, success count = {}, fail count = {}, count down latch status = {}",
                count, successDoctorList.size(), failDoctorList.size(), latchStatus
        );
        downloaderServiceExecutor.shutdown();
    }

    @Scheduled(fixedDelay = 1000)
    public void getDoctorOfflineReviewListInBatch() throws IOException, InterruptedException {
        if (!downloaderTaskSwitch.getGetDoctorOfflineReviewListTaskOn()) {
            LOGGER.warn("Get doctor offline review list task is not started.");
            return;
        }
        List<Doctor> doctorList = doctorService.getDoctorListByOfflineStatusAndLimit(EntityStatus.EMPTY, batchSize);
        ThreadPoolTaskExecutor downloaderServiceExecutor = mainExecutorConfig.getMainExecutor();
        Map<String, String> batchResultMap = new ConcurrentHashMap<>();
        int count = doctorList.size();
        LOGGER.info("Downloading {} doctor offline review lists", count);
        List<Doctor> successDoctorList = new CopyOnWriteArrayList<>();
        List<Doctor> failDoctorList = new CopyOnWriteArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(count);
        for (Doctor doctor : doctorList) {
            DownloadDoctorOfflineReviewListTask downloadDoctorOfflineReviewListTask = new DownloadDoctorOfflineReviewListTask(
                    doctor, proxyConfig, countDownLatch, batchResultMap, successDoctorList, failDoctorList
            );
            downloaderServiceExecutor.execute(downloadDoctorOfflineReviewListTask);
        }
        boolean latchStatus = countDownLatch.await(awaitTerminationSeconds, TimeUnit.SECONDS);
        fileStorageService.saveToFile(batchResultMap);
        doctorService.saveDoctorList(successDoctorList);
        doctorService.saveDoctorList(failDoctorList);
        LOGGER.info("Finished {} tasks, success count = {}, fail count = {}, count down latch status = {}",
                count, successDoctorList.size(), failDoctorList.size(), latchStatus
        );
        downloaderServiceExecutor.shutdown();
    }

    @Scheduled(fixedDelay = 1000)
    public void getDoctorConsultationListInBatch() throws IOException, InterruptedException {
        if (!downloaderTaskSwitch.getGetDoctorConsultationListTaskOn()) {
            LOGGER.warn("Get doctor consultation list task is not started.");
            return;
        }
        List<Doctor> doctorList = doctorService.getDoctorListByConsultationStatusAndLimit(EntityStatus.EMPTY, batchSize);
        ThreadPoolTaskExecutor mainExecutor = mainExecutorConfig.getMainExecutor();
        ThreadPoolTaskExecutor downloaderExecutor = downloaderExecutorConfig.getDownloaderExecutor();
        Map<String, String> batchResultMap = new ConcurrentHashMap<>();
        int count = doctorList.size();
        LOGGER.info("Downloading {} doctor consultation lists", count);
        List<Doctor> successDoctorList = new CopyOnWriteArrayList<>();
        List<Doctor> failDoctorList = new CopyOnWriteArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(count);
        for (Doctor doctor : doctorList) {
            DownloadDoctorConsultationListTask downloadDoctorConsultationListTask = new DownloadDoctorConsultationListTask(
                    doctor, proxyConfig, downloaderExecutor, countDownLatch, batchResultMap, successDoctorList, failDoctorList
            );
            mainExecutor.execute(downloadDoctorConsultationListTask);
        }
        boolean latchStatus = countDownLatch.await(awaitTerminationSeconds, TimeUnit.SECONDS);
        fileStorageService.saveToFile(batchResultMap);
        doctorService.saveDoctorList(successDoctorList);
        doctorService.saveDoctorList(failDoctorList);
        LOGGER.info("Finished {} tasks, success count = {}, fail count = {}, count down latch status = {}",
                count, successDoctorList.size(), failDoctorList.size(), latchStatus
        );
        downloaderExecutor.shutdown();
        mainExecutor.shutdown();
    }

    @Scheduled(fixedDelay = 1000)
    public void getHospitalInfoInBatch() throws IOException, InterruptedException {
        if (!downloaderTaskSwitch.getGetHospitalInfoTaskOn()) {
            LOGGER.warn("Get hospital info task is not started.");
            return;
        }
        List<Hospital> hospitalList = hospitalService
                .getHospitalInfoListByHospitalInfoStatusAndLimit(EntityStatus.EMPTY, batchSize);
        ThreadPoolTaskExecutor mainExecutor = mainExecutorConfig.getMainExecutor();
        Map<String, String> batchResultMap = new ConcurrentHashMap<>();
        int count = hospitalList.size();
        LOGGER.info("Downloading {} hospital info pages", count);
        List<Hospital> successHospitalList = new CopyOnWriteArrayList<>();
        List<Hospital> failHospitalList = new CopyOnWriteArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(count);
        for (Hospital hospital : hospitalList) {
            DownloadHospitalInfoTask downloadHospitalInfoTask = new DownloadHospitalInfoTask(
                    hospital, proxyConfig, countDownLatch, batchResultMap, successHospitalList, failHospitalList
            );
            mainExecutor.execute(downloadHospitalInfoTask);
        }
        boolean latchStatus = countDownLatch.await(awaitTerminationSeconds, TimeUnit.SECONDS);
        fileStorageService.saveToFile(batchResultMap);
        hospitalService.saveHospitalList(successHospitalList);
        hospitalService.saveHospitalList(failHospitalList);
        LOGGER.info("Finished {} tasks, success count = {}, fail count = {}, count down latch status = {}",
                count, successHospitalList.size(), failHospitalList.size(), latchStatus
        );
        mainExecutor.shutdown();
    }

    @Scheduled(fixedDelay = 1000)
    public void getDoctorStatisticsInBatch() throws IOException, InterruptedException {
        if (!downloaderTaskSwitch.getGetDoctorStatisticsTaskOn()) {
            LOGGER.warn("Get doctor statistics task is not started.");
            return;
        }
        List<Doctor> doctorList = doctorService.getDoctorListByStatisticsStatusAndLimit(EntityStatus.EMPTY, batchSize);
        ThreadPoolTaskExecutor mainExecutor = mainExecutorConfig.getMainExecutor();
        ThreadPoolTaskExecutor downloaderExecutor = downloaderExecutorConfig.getDownloaderExecutor();
        Map<String, String> batchResultMap = new ConcurrentHashMap<>();
        int count = doctorList.size();
        LOGGER.info("Downloading {} doctor statistics pages", count);
        List<Doctor> successDoctorList = new CopyOnWriteArrayList<>();
        List<Doctor> failDoctorList = new CopyOnWriteArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(count);
        for (Doctor doctor : doctorList) {
            DownloadDoctorStatisticsTask downloadDoctorStatisticsTask = new DownloadDoctorStatisticsTask(
                    doctor, proxyConfig, downloaderExecutor, countDownLatch, batchResultMap, successDoctorList, failDoctorList
            );
            mainExecutor.execute(downloadDoctorStatisticsTask);
        }
        boolean latchStatus = countDownLatch.await(awaitTerminationSeconds, TimeUnit.SECONDS);
        fileStorageService.saveToFile(batchResultMap);
        doctorService.saveDoctorList(successDoctorList);
        doctorService.saveDoctorList(failDoctorList);
        LOGGER.info("Finished {} tasks, success count = {}, fail count = {}, count down latch status = {}",
                count, successDoctorList.size(), failDoctorList.size(), latchStatus
        );
        downloaderExecutor.shutdown();
        mainExecutor.shutdown();
    }

    @Scheduled(fixedDelay = 1000)
    public void getDoctorOfflineReviewContentInBatch() throws IOException, InterruptedException {
        if (!downloaderTaskSwitch.getGetDoctorOfflineReviewContentTaskOn()) {
            LOGGER.warn("Get doctor offline review content task is not started.");
            return;
        }
        List<DoctorOfflineReview> doctorOfflineReviewList = doctorOfflineReviewService
                .getDoctorOfflineReviewsByStatusAndLimit(6, batchSize);
        ThreadPoolTaskExecutor mainExecutor = mainExecutorConfig.getMainExecutor();
        ThreadPoolTaskExecutor downloaderExecutor = downloaderExecutorConfig.getDownloaderExecutor();
        Map<String, String> batchResultMap = new ConcurrentHashMap<>();
        int count = doctorOfflineReviewList.size();
        LOGGER.info("Downloading {} doctor offline review content pages", count);
        List<DoctorOfflineReview> successDoctorOfflineReviewList = new CopyOnWriteArrayList<>();
        List<DoctorOfflineReview> deletedDoctorOfflineReviewList = new CopyOnWriteArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(count);
        for (DoctorOfflineReview doctorOfflineReview : doctorOfflineReviewList) {
            DownloadDoctorOfflineReviewContentTask downloadDoctorOfflineReviewContentTask = new DownloadDoctorOfflineReviewContentTask(
                    doctorOfflineReview, proxyConfig, downloaderExecutor, countDownLatch, batchResultMap, successDoctorOfflineReviewList, deletedDoctorOfflineReviewList
            );
            mainExecutor.execute(downloadDoctorOfflineReviewContentTask);
        }
        boolean latchStatus = countDownLatch.await(awaitTerminationSeconds, TimeUnit.SECONDS);
        fileStorageService.saveToFile(batchResultMap);
        doctorOfflineReviewService.saveDoctorOfflineReviewList(successDoctorOfflineReviewList);
        doctorOfflineReviewService.saveDoctorOfflineReviewList(deletedDoctorOfflineReviewList);
        int successCount = successDoctorOfflineReviewList.size();
        int deletedCount = deletedDoctorOfflineReviewList.size();
        LOGGER.info("Finished {} tasks, success count = {}, deleted count = {}, fail count = {}, count down latch status = {}",
                count, successCount, deletedCount, batchSize - successCount - deletedCount, latchStatus
        );
        downloaderExecutor.shutdown();
        mainExecutor.shutdown();
    }
}
