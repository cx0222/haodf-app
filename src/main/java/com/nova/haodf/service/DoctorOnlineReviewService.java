package com.nova.haodf.service;

import com.nova.haodf.entity.DoctorOnlineReview;
import com.nova.haodf.exception.DoctorOnlineReviewNotFoundException;
import com.nova.haodf.repository.DoctorOnlineReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DoctorOnlineReviewService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorOnlineReviewService.class);
    private final DoctorOnlineReviewRepository doctorOnlineReviewRepository;

    @Autowired
    public DoctorOnlineReviewService(DoctorOnlineReviewRepository doctorOnlineReviewRepository) {
        this.doctorOnlineReviewRepository = doctorOnlineReviewRepository;
    }

    public void saveDoctorOnlineReviewList(List<DoctorOnlineReview> doctorOnlineReviewList) {
        doctorOnlineReviewList = doctorOnlineReviewRepository.saveAll(doctorOnlineReviewList);
        LOGGER.info("Saved {} doctor online reviews", doctorOnlineReviewList.size());
    }

    public void updateTraitSatisfactionByOnlineReviewIds(Map<UUID, Integer> traitSatisfactionMap) {
        LOGGER.debug("Updating trait satisfaction by trait satisfaction map, count = {}", traitSatisfactionMap.size());
        traitSatisfactionMap.forEach(doctorOnlineReviewRepository::updateTraitSatisfactionByOnlineReviewId);
    }

    public List<DoctorOnlineReview> getDoctorOnlineReviewsByStatusAndLimit(int status, int limit) {
        List<DoctorOnlineReview> doctorOnlineReviewList = doctorOnlineReviewRepository.findDoctorOnlineReviewsByStatus(status, limit);
        LOGGER.info("Found {} doctor online reviews with status {}", doctorOnlineReviewList.size(), status);
        return doctorOnlineReviewList;
    }

    @Cacheable(value = "getDoctorOnlineReviewByReviewId")
    public DoctorOnlineReview getDoctorOnlineReviewByReviewId(UUID onlineReviewId) {
        LOGGER.debug("Getting doctor online review by review id, online review id = {}", onlineReviewId);
        return doctorOnlineReviewRepository.findById(onlineReviewId)
                .orElseThrow(() -> new DoctorOnlineReviewNotFoundException(onlineReviewId));
    }

    @Cacheable(value = "getDoctorOnlineReviewsByConsultationId")
    public List<DoctorOnlineReview> getDoctorOnlineReviewsByConsultationId(
            Long consultationId, int limit, int offset
    ) {
        LOGGER.debug("Getting doctor online reviews by consultation id, consultation id = {}, limit = {}, offset = {}",
                consultationId, limit, offset
        );
        return doctorOnlineReviewRepository.findDoctorOnlineReviewsByConsultationId(
                consultationId, limit, offset
        );
    }

    @Cacheable(value = "getDoctorOnlineReviewsByDoctorId")
    public List<DoctorOnlineReview> getDoctorOnlineReviewsByDoctorId(
            Long doctorId, int limit, int offset
    ) {
        LOGGER.debug("Getting doctor online reviews by doctor id, doctor id = {}, limit = {}, offset = {}",
                doctorId, limit, offset
        );
        return doctorOnlineReviewRepository.findDoctorOnlineReviewsByDoctorId(
                doctorId, limit, offset
        );
    }

    public List<DoctorOnlineReview> getDoctorOnlineReviewsByEvaluateDate(
            LocalDateTime evaluateDateLow, LocalDateTime evaluateDateHigh, int limit, int offset, boolean order
    ) {
        LOGGER.debug("Getting doctor online reviews by evaluate date, " +
                        "evaluate date = {} - {}, limit = {}, offset = {}, order = {}",
                evaluateDateLow, evaluateDateHigh, limit, offset, order
        );
        return order ? doctorOnlineReviewRepository.findDoctorOnlineReviewsOrderByEvaluateDateAsc(
                evaluateDateLow, evaluateDateHigh, limit, offset
        ) : doctorOnlineReviewRepository.findDoctorOnlineReviewsOrderByEvaluateDateDesc(
                evaluateDateLow, evaluateDateHigh, limit, offset
        );
    }
}
