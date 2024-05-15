package com.nova.haodf.repository;

import com.nova.haodf.entity.DoctorOfflineReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DoctorOfflineReviewRepository extends JpaRepository<DoctorOfflineReview, Long> {
    @Query(value = "select * from doctor_offline_review " +
            "where status = ?1 order by review_id desc limit ?2", nativeQuery = true)
    List<DoctorOfflineReview> findDoctorOfflineReviewsByStatus(int status, int limit);

    @Query(value = "select * from doctor_offline_review " +
            "order by review_id desc limit ?1", nativeQuery = true)
    List<DoctorOfflineReview> findDoctorOfflineReviews(int limit);

    @Query(value = "select * from doctor_offline_review " +
            "where doctor_id = ?1 order by real_time desc limit ?2 offset ?3", nativeQuery = true)
    List<DoctorOfflineReview> findDoctorOfflineReviewsByDoctorId(
            Long doctorId, int limit, int offset
    );

    @Query(value = "select * from doctor_offline_review " +
            "where type_description = ?1 order by real_time desc limit ?2 offset ?3", nativeQuery = true)
    List<DoctorOfflineReview> findDoctorOfflineReviewsByTypeDescription(
            String typeDescription, int limit, int offset
    );

    @Query(value = "select * from doctor_offline_review " +
            "where effect = ?1 order by real_time desc limit ?2 offset ?3", nativeQuery = true)
    List<DoctorOfflineReview> findDoctorOfflineReviewsByEffect(
            String effect, int limit, int offset
    );

    @Query(value = "select * from doctor_offline_review " +
            "where attitude = ?1 order by real_time desc limit ?2 offset ?3", nativeQuery = true)
    List<DoctorOfflineReview> findDoctorOfflineReviewsByAttitude(
            String attitude, int limit, int offset
    );

    @Query(value = "select * from doctor_offline_review " +
            "where skill = ?1 order by real_time desc limit ?2 offset ?3", nativeQuery = true)
    List<DoctorOfflineReview> findDoctorOfflineReviewsBySkill(
            String skill, int limit, int offset
    );

    @Query(value = "select * from doctor_offline_review " +
            "where real_time between ?1 and ?2 " +
            "order by real_time limit ?3 offset ?4", nativeQuery = true)
    List<DoctorOfflineReview> findDoctorOfflineReviewsByRealTimeAsc(
            LocalDateTime realTimeLow, LocalDateTime realTimeHigh, int limit, int offset
    );

    @Query(value = "select * from doctor_offline_review " +
            "where real_time between ?1 and ?2 " +
            "order by real_time desc limit ?3 offset ?4", nativeQuery = true)
    List<DoctorOfflineReview> findDoctorOfflineReviewsByRealTimeDesc(
            LocalDateTime realTimeLow, LocalDateTime realTimeHigh, int limit, int offset
    );

    @Transactional
    @Query(value = "update doctor_offline_review set location = ?2 " +
            "where review_id = ?1", nativeQuery = true)
    @Modifying(flushAutomatically = true)
    void updateLocationByReviewId(Long reviewId, String location);

    @Transactional
    @Query(value = "update doctor_offline_review set status = ?2 " +
            "where review_id = ?1", nativeQuery = true)
    @Modifying(flushAutomatically = true)
    void updateStatusByReviewId(Long reviewId, int status);
}
