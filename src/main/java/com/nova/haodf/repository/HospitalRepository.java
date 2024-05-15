package com.nova.haodf.repository;

import com.nova.haodf.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    @Query(value = "select * from hospital " +
            "where status = ?1 limit ?2", nativeQuery = true)
    List<Hospital> findHospitalsByStatus(int status, int limit);

    @Query(value = "select * from hospital " +
            "where hospital_info_status = ?1 limit ?2", nativeQuery = true)
    List<Hospital> findHospitalsByHospitalInfoStatus(int hospitalInfoStatus, int limit);

    @Query(value = "select * from hospital " +
            "where country_rank between ?1 and ?2 " +
            "order by country_rank limit ?3 offset ?4", nativeQuery = true)
    List<Hospital> findHospitalsByCountryRankBetween(
            int countryRankLow, int countryRankHigh, int limit, int offset
    );

    @Query(value = "select * from hospital " +
            "where province_name = ?1 and province_rank between ?2 and ?3 " +
            "order by province_rank limit ?4 offset ?5", nativeQuery = true)
    List<Hospital> findHospitalsByProvinceNameAndProvinceRankBetween(
            String provinceName, int provinceRankLow, int provinceRankHigh, int limit, int offset
    );

    @Query(value = "select * from hospital " +
            "where category = ?1 limit ?2 offset ?3", nativeQuery = true)
    List<Hospital> findHospitalsByCategory(
            int category, int limit, int offset
    );

    @Query(value = "select * from hospital " +
            "where grade = ?1 limit ?2 offset ?3", nativeQuery = true)
    List<Hospital> findHospitalsByGrade(
            int grade, int limit, int offset
    );

    @Query(value = "select * from hospital " +
            "where property = ?1 limit ?2 offset ?3", nativeQuery = true)
    List<Hospital> findHospitalsByProperty(
            int property, int limit, int offset
    );

    @Query(value = "select * from hospital " +
            "where area_code rlike ?1 limit ?2 offset ?3", nativeQuery = true)
    List<Hospital> findHospitalsByAreaCodeRlike(
            String areaCodeRlike, int limit, int offset
    );

    @Query(value = "select * from hospital " +
            "where total_space_hits between ?1 and ?2 " +
            "and service_patient_count between ?3 and ?4 " +
            "and total_comment_count between ?5 and ?6 " +
            "and total_disease_count between ?7 and ?8 " +
            "and total_doctor_count between ?9 and ?10 " +
            "and total_faculty_count between ?11 and ?12 " +
            "limit ?13 offset ?14", nativeQuery = true)
    List<Hospital> findHospitalsByTotalStatistics(
            int totalSpaceHitsLow, int totalSpaceHitsHigh,
            int servicePatientCountLow, int servicePatientCountHigh,
            int totalCommentCountLow, int totalCommentCountHigh,
            int totalDiseaseCountLow, int totalDiseaseCountHigh,
            int totalDoctorCountLow, int totalDoctorCountHigh,
            int totalFacultyCountLow, int totalFacultyCountHigh,
            int limit, int offset
    );

    @Query(value = "select * from hospital " +
            "where up_vote_count2years between ?1 and ?2 " +
            "and down_vote_count2years between ?3 and ?4 " +
            "and year_haodf_count between ?5 and ?6 " +
            "order by year_haodf_count desc " +
            "limit ?7 offset ?8", nativeQuery = true)
    List<Hospital> findHospitalsByUpVoteAndYearHaodfCount(
            int upVoteCount2YearsLow, int upVoteCount2YearsHigh,
            int downVoteCount2YearsLow, int downVoteCount2YearsHigh,
            int yearHaodfCountLow, int yearHaodfCountHigh,
            int limit, int offset
    );

    @Query(value = "select * from hospital " +
            "where article_count between ?1 and ?2 " +
            "and live_count between ?3 and ?4 " +
            "order by (article_count + live_count) desc " +
            "limit ?5 offset ?6", nativeQuery = true)
    List<Hospital> findHospitalsByArticleCountAndLiveCount(
            int articleCountLow, int articleCountHigh,
            int liveCountLow, int liveCountHigh,
            int limit, int offset
    );
}
