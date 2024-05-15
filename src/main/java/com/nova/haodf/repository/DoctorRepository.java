package com.nova.haodf.repository;

import com.nova.haodf.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    @Query(value = "select * from doctor " +
            "where online_status = ?1 limit ?2", nativeQuery = true)
    List<Doctor> findDoctorsByOnlineStatus(int status, int limit);

    @Query(value = "select * from doctor " +
            "where offline_status = ?1 limit ?2", nativeQuery = true)
    List<Doctor> findDoctorsByOfflineStatus(int status, int limit);

    @Query(value = "select * from doctor " +
            "where consultation_status = ?1 limit ?2", nativeQuery = true)
    List<Doctor> findDoctorsByConsultationStatus(int status, int limit);

    @Query(value = "select * from doctor " +
            "where doctor_statistics_status = ?1 and space_id > 0 limit ?2 ", nativeQuery = true)
    List<Doctor> findDoctorsByDoctorStatisticsStatus(int status, int limit);

    @Query(value = "select * from doctor where recommend_index is not null " +
            "order by recommend_index limit ?1 offset ?2", nativeQuery = true)
    List<Doctor> findDoctorsOrderByRecommendIndexAsc(
            int limit, int offset
    );

    @Query(value = "select * from doctor where recommend_index is not null " +
            "order by recommend_index desc limit ?1 offset ?2", nativeQuery = true)
    List<Doctor> findDoctorsOrderByRecommendIndexDesc(
            int limit, int offset
    );

    @Query(value = "select * from doctor where article_count is not null " +
            "order by article_count limit ?1 offset ?2", nativeQuery = true)
    List<Doctor> findDoctorsOrderByArticleCountAsc(
            int limit, int offset
    );

    @Query(value = "select * from doctor where article_count is not null " +
            "order by article_count desc limit ?1 offset ?2", nativeQuery = true)
    List<Doctor> findDoctorsOrderByArticleCountDesc(
            int limit, int offset
    );

    @Query(value = "select * from doctor where doctor_vote_count is not null " +
            "order by doctor_vote_count limit ?1 offset ?2", nativeQuery = true)
    List<Doctor> findDoctorsOrderByDoctorVoteCountAsc(
            int limit, int offset
    );

    @Query(value = "select * from doctor where doctor_vote_count is not null " +
            "order by doctor_vote_count desc limit ?1 offset ?2", nativeQuery = true)
    List<Doctor> findDoctorsOrderByDoctorVoteCountDesc(
            int limit, int offset
    );

    @Query(value = "select * from doctor where open_space_time is not null " +
            "order by open_space_time limit ?1 offset ?2", nativeQuery = true)
    List<Doctor> findDoctorsOrderByOpenSpaceTimeAsc(
            int limit, int offset
    );

    @Query(value = "select * from doctor where open_space_time is not null " +
            "order by open_space_time desc limit ?1 offset ?2", nativeQuery = true)
    List<Doctor> findDoctorsOrderByOpenSpaceTimeDesc(
            int limit, int offset
    );

    @Query(value = "select * from doctor where present_count is not null " +
            "order by present_count limit ?1 offset ?2", nativeQuery = true)
    List<Doctor> findDoctorsOrderByPresentCountAsc(
            int limit, int offset
    );

    @Query(value = "select * from doctor where present_count is not null " +
            "order by present_count desc limit ?1 offset ?2", nativeQuery = true)
    List<Doctor> findDoctorsOrderByPresentCountDesc(
            int limit, int offset
    );

    @Query(value = "select * from doctor where space_replied_count is not null " +
            "order by space_replied_count limit ?1 offset ?2", nativeQuery = true)
    List<Doctor> findDoctorsOrderBySpaceRepliedCountAsc(
            int limit, int offset
    );

    @Query(value = "select * from doctor where space_replied_count is not null " +
            "order by space_replied_count desc limit ?1 offset ?2", nativeQuery = true)
    List<Doctor> findDoctorsOrderBySpaceRepliedCountDesc(
            int limit, int offset
    );

    @Query(value = "select * from doctor where total_hit is not null " +
            "order by total_hit limit ?1 offset ?2", nativeQuery = true)
    List<Doctor> findDoctorsOrderByTotalHitAsc(
            int limit, int offset
    );

    @Query(value = "select * from doctor where total_hit is not null " +
            "order by total_hit desc limit ?1 offset ?2", nativeQuery = true)
    List<Doctor> findDoctorsOrderByTotalHitDesc(
            int limit, int offset
    );

    @Query(value = "select * from doctor where total_sign_in_count is not null " +
            "order by total_sign_in_count limit ?1 offset ?2", nativeQuery = true)
    List<Doctor> findDoctorsOrderByTotalSignInCountAsc(
            int limit, int offset
    );

    @Query(value = "select * from doctor where total_sign_in_count is not null " +
            "order by total_sign_in_count desc limit ?1 offset ?2", nativeQuery = true)
    List<Doctor> findDoctorsOrderByTotalSignInCountDesc(
            int limit, int offset
    );

    @Query(value = "select * from doctor where vote_in2years is not null " +
            "order by vote_in2years limit ?1 offset ?2", nativeQuery = true)
    List<Doctor> findDoctorsOrderByVoteIn2YearsAsc(
            int limit, int offset
    );

    @Query(value = "select * from doctor where vote_in2years is not null " +
            "order by vote_in2years desc limit ?1 offset ?2", nativeQuery = true)
    List<Doctor> findDoctorsOrderByVoteIn2YearsDesc(
            int limit, int offset
    );

    @Query(value = "select * from doctor " +
            "where area_code rlike ?1 limit ?2 offset ?3", nativeQuery = true)
    List<Doctor> findDoctorsByAreaCodeRlike(
            String areaCodeRlike, int limit, int offset
    );

    @Transactional
    @Query(value = "update doctor set hospital_id = ?2 where doctor_id = ?1", nativeQuery = true)
    @Modifying(flushAutomatically = true)
    void updateHospitalId(Long doctorId, Long hospitalId);
}
