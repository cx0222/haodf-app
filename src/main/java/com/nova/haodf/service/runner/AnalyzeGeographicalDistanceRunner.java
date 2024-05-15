package com.nova.haodf.service.runner;

import com.nova.haodf.entity.DoctorOfflineReview;
import com.nova.haodf.service.AmapService;
import com.nova.haodf.service.DoctorOfflineReviewService;
import com.nova.haodf.util.EntityStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AnalyzeGeographicalDistanceRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadDoctorAndHospitalIdRunner.class);
    private final AmapService amapService;
    private final DoctorOfflineReviewService doctorOfflineReviewService;
    @Value("${options.runner.analyze-geographical-distance}")
    private Boolean taskOn;

    @Autowired
    public AnalyzeGeographicalDistanceRunner(AmapService amapService, DoctorOfflineReviewService doctorOfflineReviewService) {
        this.amapService = amapService;
        this.doctorOfflineReviewService = doctorOfflineReviewService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!taskOn) {
            LOGGER.warn("Load doctor and hospital id runner is not started.");
            return;
        }
        List<DoctorOfflineReview> doctorOfflineReviewList = doctorOfflineReviewService
                .getDoctorOfflineReviewsByStatusAndLimit(999, 10000);
        while (!doctorOfflineReviewList.isEmpty()) {
            int count = doctorOfflineReviewList.size();
            LOGGER.info("Found {} analyze geographical distance tasks", count);
            for (int i = 0; i < count; ++i) {
                DoctorOfflineReview doctorOfflineReview = doctorOfflineReviewList.get(i);
                Long reviewId = doctorOfflineReview.getReviewId();
                try {
                    String city = doctorOfflineReview.getPatientCity();
                    String geocode = amapService.getGeocode(city, level -> level.equals("省") || level.equals("市"));
                    if (geocode == null) {
                        LOGGER.warn("Failed to get city geocode, trying province geocode");
                        String province = doctorOfflineReview.getPatientProvince();
                        geocode = amapService.getGeocode(province, level -> level.equals("省") || level.equals("市"));
                    }
                    LOGGER.info("The final decided geocode = {}", geocode);
                    doctorOfflineReview.setLocation(geocode);
                    doctorOfflineReview.setStatus(6);
                } catch (Exception exception) {
                    LOGGER.warn("Failed to get geocode for doctor offline review id = {}", reviewId, exception);
                }
                if (i % 1000 == 999) {
                    LOGGER.info("Current progress {}/{}", i + 1, count);
                }
            }
            doctorOfflineReviewService.updateLocationByReviewIds(doctorOfflineReviewList);
            doctorOfflineReviewList = doctorOfflineReviewService
                    .getDoctorOfflineReviewsByStatusAndLimit(EntityStatus.COMPLETED, 10000);
        }
    }
}
