package com.nova.haodf.service.runner;

import com.nova.haodf.util.EntityStatus;
import com.nova.haodf.config.MainExecutorConfig;
import com.nova.haodf.entity.DoctorOnlineReview;
import com.nova.haodf.service.DoctorOnlineReviewService;
import com.nova.haodf.util.HtmlInstruction;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
public class ParseDoctorOnlineReviewRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParseDoctorOnlineReviewRunner.class);
    private static final Map<String, HtmlInstruction> HTML_INSTRUCTION_MAP = new HashMap<>();

    static {
        HTML_INSTRUCTION_MAP.put("setPatientName", new HtmlInstruction(String.class, element -> element.getElementsByClass("patient-name").text().trim()));
        HTML_INSTRUCTION_MAP.put("setDiseaseTag", new HtmlInstruction(String.class, element -> element.getElementsByClass("disease-tag").text().trim()));
        HTML_INSTRUCTION_MAP.put("setEvaluateDate", new HtmlInstruction(LocalDateTime.class, element -> {
            String evaluateDateString = element.getElementsByClass("evaluate-date").text().trim();
            return LocalDateTime.parse(evaluateDateString.replaceAll(" ", "T"));
        }));
        HTML_INSTRUCTION_MAP.put("setTrait", new HtmlInstruction(String.class, element -> element.getElementsByClass("trait").text().trim()));
        HTML_INSTRUCTION_MAP.put("setMoreDetailUrl", new HtmlInstruction(String.class, element -> element.getElementsByTag("a").attr("href")));
        HTML_INSTRUCTION_MAP.put("setDetail", new HtmlInstruction(String.class, element -> element.getElementsByClass("eva-detail").text().trim()));
    }

    private final MainExecutorConfig mainExecutorConfig;
    private final DoctorOnlineReviewService doctorOnlineReviewService;
    @Value("${options.runner.parse-online-review}")
    private Boolean taskOn;
    @Value("${options.resources.doctor-online-review-dir}")
    private String mainDirectory;
    @Value("${options.spider.await-termination-seconds}")
    private int awaitTerminationSeconds;

    @Autowired
    public ParseDoctorOnlineReviewRunner(MainExecutorConfig mainExecutorConfig, DoctorOnlineReviewService doctorOnlineReviewService) {
        this.mainExecutorConfig = mainExecutorConfig;
        this.doctorOnlineReviewService = doctorOnlineReviewService;
    }

    private DoctorOnlineReview parseOnlineReview(Element element) {
        DoctorOnlineReview doctorOnlineReview = new DoctorOnlineReview();
        try {
            Objects.requireNonNull(element);
        } catch (NullPointerException nullPointerException) {
            LOGGER.warn("Could not parse html", nullPointerException);
            return null;
        }
        for (Map.Entry<String, HtmlInstruction> entry : HTML_INSTRUCTION_MAP.entrySet()) {
            String methodName = entry.getKey();
            HtmlInstruction htmlInstruction = entry.getValue();
            Object fieldData = htmlInstruction.applyParseAndTransform(element);
            try {
                DoctorOnlineReview.class
                        .getDeclaredMethod(methodName, htmlInstruction.getClazz())
                        .invoke(doctorOnlineReview, fieldData);
            } catch (Exception exception) {
                LOGGER.warn("Exception occurred, method name = {}", methodName, exception);
            }
        }
        doctorOnlineReview.setStatus(EntityStatus.OK);
        return doctorOnlineReview;
    }

    private void addOnlineReviewConsultationId(DoctorOnlineReview doctorOnlineReview) {
        String moreDetailUrl = doctorOnlineReview.getMoreDetailUrl();
        if (moreDetailUrl == null || moreDetailUrl.isEmpty()) {
            LOGGER.warn("Consultation id could not be deduced by more detail url");
            return;
        }
        String[] splits = moreDetailUrl.split("/");
        String consultationIdString = splits[splits.length - 1];
        if (!consultationIdString.endsWith(".html")) {
            LOGGER.warn("Consultation id could not be deduced by more detail url");
            return;
        }
        try {
            int length = consultationIdString.length();
            Long consultationId = Long.parseLong(consultationIdString.substring(0, length - 5));
            doctorOnlineReview.setConsultationId(consultationId);
        } catch (Exception exception) {
            LOGGER.warn("Could not obtain consultation id", exception);
        }
    }

    @Override
    public void run(String... args) throws Exception {
        if (!taskOn) {
            LOGGER.warn("Parse doctor online review runner is not started.");
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
            DoctorOnlineReviewParserRunnerTask doctorOnlineReviewParserRunnerTask = new DoctorOnlineReviewParserRunnerTask(
                    file, countDownLatch, mainExecutor
            );
            mainExecutor.submit(doctorOnlineReviewParserRunnerTask);
        }
        boolean latchStatus = countDownLatch.await(awaitTerminationSeconds, TimeUnit.SECONDS);
        mainExecutor.shutdown();
        LOGGER.info("Parse doctor online review runner run completed, latch status = {}", latchStatus);
    }

    private class DoctorOnlineReviewParserRunnerTask implements Runnable {
        private final File file;
        private final CountDownLatch countDownLatch;
        private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

        public DoctorOnlineReviewParserRunnerTask(File file, CountDownLatch countDownLatch, ThreadPoolTaskExecutor threadPoolTaskExecutor) {
            this.file = file;
            this.countDownLatch = countDownLatch;
            this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        }

        @Override
        public void run() {
            List<DoctorOnlineReview> doctorOnlineReviewList = new ArrayList<>();
            String filePath = file.getPath();
            String[] splits = filePath.split("/");
            Long doctorId = Long.parseLong(splits[splits.length - 2]);
            try {
                LOGGER.info("Processing file {}", filePath);
                StringBuilder builder = new StringBuilder();
                List<String> stringList = FileUtils.readLines(file, StandardCharsets.UTF_8);
                stringList.forEach(builder::append);
                String htmlString = builder.toString();
                Elements elements = Jsoup.parse(htmlString)
                        .getElementsByClass("patient-eva");
                for (Element element : elements) {
                    DoctorOnlineReview doctorOnlineReview = parseOnlineReview(element);
                    if (doctorOnlineReview == null) {
                        continue;
                    }
                    doctorOnlineReview.setDoctorId(doctorId);
                    addOnlineReviewConsultationId(doctorOnlineReview);
                    doctorOnlineReviewList.add(doctorOnlineReview);
                }
                LOGGER.info("File {} parsed", filePath);
            } catch (Exception exception) {
                LOGGER.error("Fail to parse file {}", filePath, exception);
            }
            doctorOnlineReviewService.saveDoctorOnlineReviewList(doctorOnlineReviewList);
            countDownLatch.countDown();
            LOGGER.info("There are approximately {} tasks left", threadPoolTaskExecutor.getQueueSize());
        }
    }
}
