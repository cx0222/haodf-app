package com.nova.haodf.repository;

import com.nova.haodf.entity.DoctorOnlineReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DoctorOnlineReviewRepository extends JpaRepository<DoctorOnlineReview, UUID> {
    @Query(value = "select * from doctor_online_review " +
            "where status = ?1 limit ?2", nativeQuery = true)
    List<DoctorOnlineReview> findDoctorOnlineReviewsByStatus(int status, int limit);

    @Query(value = "select * from doctor_online_review " +
            "where consultation_id = ?1 order by evaluate_date desc limit ?2 offset ?3", nativeQuery = true)
    List<DoctorOnlineReview> findDoctorOnlineReviewsByConsultationId(
            Long consultationId, int limit, int offset
    );

    @Query(value = "select * from doctor_online_review " +
            "where doctor_id = ?1 order by evaluate_date desc limit ?2 offset ?3", nativeQuery = true)
    List<DoctorOnlineReview> findDoctorOnlineReviewsByDoctorId(
            Long doctorId, int limit, int offset
    );

    @Query(value = "select * from doctor_online_review " +
            "where evaluate_date between ?1 and ?2 " +
            "order by evaluate_date limit ?3 offset ?4", nativeQuery = true)
    List<DoctorOnlineReview> findDoctorOnlineReviewsOrderByEvaluateDateAsc(
            LocalDateTime evaluateDateLow, LocalDateTime evaluateDateHigh, int limit, int offset
    );

    @Query(value = "select * from doctor_online_review " +
            "where evaluate_date between ?1 and ?2 " +
            "order by evaluate_date desc limit ?3 offset ?4", nativeQuery = true)
    List<DoctorOnlineReview> findDoctorOnlineReviewsOrderByEvaluateDateDesc(
            LocalDateTime evaluateDateLow, LocalDateTime evaluateDateHigh, int limit, int offset
    );

    @Transactional
    @Query(value = "update doctor_online_review set trait_satisfaction = ?2 " +
            "where online_review_id = ?1", nativeQuery = true)
    @Modifying(flushAutomatically = true)
    void updateTraitSatisfactionByOnlineReviewId(UUID onlineReviewId, Integer traitSatisfaction);
}
