package com.nova.haodf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DownloaderTaskSwitch {
    @Value("${options.downloader.doctor-list}")
    private Boolean getDoctorListTaskOn;
    @Value("${options.downloader.doctor-online-review-list}")
    private Boolean getDoctorOnlineReviewListTaskOn;
    @Value("${options.downloader.doctor-offline-review-list}")
    private Boolean getDoctorOfflineReviewListTaskOn;
    @Value("${options.downloader.doctor-consultation-list}")
    private Boolean getDoctorConsultationListTaskOn;
    @Value("${options.downloader.hospital-info}")
    private Boolean getHospitalInfoTaskOn;
    @Value("${options.downloader.doctor-statistics}")
    private Boolean getDoctorStatisticsTaskOn;
    @Value("${options.downloader.doctor-offline-review-content}")
    private Boolean getDoctorOfflineReviewContentTaskOn;

    public Boolean getGetDoctorListTaskOn() {
        return getDoctorListTaskOn;
    }

    public Boolean getGetDoctorOnlineReviewListTaskOn() {
        return getDoctorOnlineReviewListTaskOn;
    }

    public Boolean getGetDoctorOfflineReviewListTaskOn() {
        return getDoctorOfflineReviewListTaskOn;
    }

    public Boolean getGetDoctorConsultationListTaskOn() {
        return getDoctorConsultationListTaskOn;
    }

    public Boolean getGetHospitalInfoTaskOn() {
        return getHospitalInfoTaskOn;
    }

    public Boolean getGetDoctorStatisticsTaskOn() {
        return getDoctorStatisticsTaskOn;
    }

    public Boolean getGetDoctorOfflineReviewContentTaskOn() {
        return getDoctorOfflineReviewContentTaskOn;
    }
}
