package com.nova.haodf.service;

import com.nova.haodf.entity.DoctorOfflineReview;
import com.nova.haodf.exception.DoctorOfflineReviewNotFoundException;
import com.nova.haodf.repository.DoctorOfflineReviewRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DoctorOfflineReviewService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorOfflineReviewService.class);
    private final DoctorOfflineReviewRepository doctorOfflineReviewRepository;
    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    public DoctorOfflineReviewService(DoctorOfflineReviewRepository doctorOfflineReviewRepository, EntityManager entityManager) {
        this.doctorOfflineReviewRepository = doctorOfflineReviewRepository;
        this.entityManager = entityManager;
    }

    public List<DoctorOfflineReview> getDoctorOfflineReviewsByStatusAndLimit(int status, int limit) {
        List<DoctorOfflineReview> doctorOfflineReviewList = doctorOfflineReviewRepository.findDoctorOfflineReviewsByStatus(status, limit);
        LOGGER.info("Found {} doctor offline reviews with status {}", doctorOfflineReviewList.size(), status);
        return doctorOfflineReviewList;
    }

    @Transactional
    public void updateLocationByReviewIds(List<DoctorOfflineReview> doctorOfflineReviewList) {
        int count = doctorOfflineReviewList.size();
        LOGGER.debug("Updating location by review ids, count = {}", count);
        for (DoctorOfflineReview doctorOfflineReview : doctorOfflineReviewList) {
            entityManager.merge(doctorOfflineReview);
        }
        entityManager.flush();
        entityManager.clear();
        LOGGER.info("Updated location by review ids, count = {}", count);
    }

    public void saveDoctorOfflineReview(DoctorOfflineReview doctorOfflineReview) {
        doctorOfflineReviewRepository.save(doctorOfflineReview);
        LOGGER.info("Updated doctor offline review, review id = {}", doctorOfflineReview.getReviewId());
    }

    public void saveDoctorOfflineReviewList(List<DoctorOfflineReview> doctorOfflineReviewList) {
        doctorOfflineReviewList = doctorOfflineReviewRepository.saveAll(doctorOfflineReviewList);
        LOGGER.info("Saved {} doctor offline reviews", doctorOfflineReviewList.size());
    }

    @Cacheable(value = "getDoctorOfflineReviewByReviewId")
    public DoctorOfflineReview getDoctorOfflineReviewByReviewId(Long reviewId) {
        LOGGER.debug("Getting doctor offline review by review id, review id = {}", reviewId);
        return doctorOfflineReviewRepository.findById(reviewId)
                .orElseThrow(() -> new DoctorOfflineReviewNotFoundException(reviewId));
    }

    @Cacheable(value = "getDoctorOfflineReviewsByDoctorId")
    public List<DoctorOfflineReview> getDoctorOfflineReviewsByDoctorId(
            Long doctorId, int limit, int offset
    ) {
        LOGGER.debug("Getting doctor offline reviews by doctor id, doctor id = {}, limit = {}, offset = {}",
                doctorId, limit, offset
        );
        return doctorOfflineReviewRepository.findDoctorOfflineReviewsByDoctorId(
                doctorId, limit, offset
        );
    }

    @Cacheable(value = "getDoctorOfflineReviewsByTypeDescription")
    public List<DoctorOfflineReview> getDoctorOfflineReviewsByTypeDescription(
            String typeDescription, int limit, int offset
    ) {
        LOGGER.debug("Getting doctor offline reviews by type description, type description = {}, limit = {}, offset = {}",
                typeDescription, limit, offset
        );
        return doctorOfflineReviewRepository.findDoctorOfflineReviewsByTypeDescription(
                typeDescription, limit, offset
        );
    }

    @Cacheable(value = "getDoctorOfflineReviewsByEffect")
    public List<DoctorOfflineReview> getDoctorOfflineReviewsByEffect(
            String effect, int limit, int offset
    ) {
        LOGGER.debug("Getting doctor offline reviews by effect, effect = {}, limit = {}, offset = {}",
                effect, limit, offset
        );
        return doctorOfflineReviewRepository.findDoctorOfflineReviewsByEffect(
                effect, limit, offset
        );
    }

    @Cacheable(value = "getDoctorOfflineReviewsByAttitude")
    public List<DoctorOfflineReview> getDoctorOfflineReviewsByAttitude(
            String attitude, int limit, int offset
    ) {
        LOGGER.debug("Getting doctor offline reviews by attitude, attitude = {}, limit = {}, offset = {}",
                attitude, limit, offset
        );
        return doctorOfflineReviewRepository.findDoctorOfflineReviewsByAttitude(
                attitude, limit, offset
        );
    }

    @Cacheable(value = "getDoctorOfflineReviewsBySkill")
    public List<DoctorOfflineReview> getDoctorOfflineReviewsBySkill(
            String skill, int limit, int offset
    ) {
        LOGGER.debug("Getting doctor offline reviews by skill, skill = {}, limit = {}, offset = {}",
                skill, limit, offset
        );
        return doctorOfflineReviewRepository.findDoctorOfflineReviewsBySkill(
                skill, limit, offset
        );
    }

    public List<DoctorOfflineReview> getDoctorOfflineReviewsByRealTime(
            LocalDateTime realTimeLow, LocalDateTime realTimeHigh, int limit, int offset, boolean order
    ) {
        LOGGER.debug("Getting doctor offline reviews by real time, " +
                        "real time = {} - {}, limit = {}, offset = {}, order = {}",
                realTimeLow, realTimeHigh, limit, offset, order
        );
        return order ? doctorOfflineReviewRepository.findDoctorOfflineReviewsByRealTimeAsc(
                realTimeLow, realTimeHigh, limit, offset
        ) : doctorOfflineReviewRepository.findDoctorOfflineReviewsByRealTimeDesc(
                realTimeLow, realTimeHigh, limit, offset
        );
    }
}
