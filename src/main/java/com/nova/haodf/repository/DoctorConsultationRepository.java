package com.nova.haodf.repository;

import com.nova.haodf.entity.DoctorConsultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DoctorConsultationRepository extends JpaRepository<DoctorConsultation, Long> {
    @Query(value = "select * from doctor_consultation " +
            "where doctor_id = ?1 limit ?2 offset ?3", nativeQuery = true)
    List<DoctorConsultation> findDoctorConsultationByDoctorId(
            Long doctorId, int limit, int offset
    );

    @Query(value = "select * from doctor_consultation " +
            "where patient_id = ?1 limit ?2 offset ?3", nativeQuery = true)
    List<DoctorConsultation> findDoctorConsultationByPatientId(
            Long patientId, int limit, int offset
    );

    @Query(value = "select * from doctor_consultation " +
            "where business_type = ?1 limit ?2 offset ?3", nativeQuery = true)
    List<DoctorConsultation> findDoctorConsultationByBusinessType(
            String businessType, int limit, int offset
    );

    @Query(value = "select * from doctor_consultation " +
            "where message_count between ?1 and ?2 " +
            "order by message_count limit ?3 offset ?4", nativeQuery = true)
    List<DoctorConsultation> findDoctorConsultationOrderByMessageCountAsc(
            int messageCountLow, int messageCountHigh, int limit, int offset
    );

    @Query(value = "select * from doctor_consultation " +
            "where message_count between ?1 and ?2 " +
            "order by message_count desc limit ?3 offset ?4", nativeQuery = true)
    List<DoctorConsultation> findDoctorConsultationOrderByMessageCountDesc(
            int messageCountLow, int messageCountHigh, int limit, int offset
    );

    @Query(value = "select * from doctor_consultation " +
            "where doctor_message_count between ?1 and ?2 " +
            "order by doctor_message_count limit ?3 offset ?4", nativeQuery = true)
    List<DoctorConsultation> findDoctorConsultationOrderByDoctorMessageCountAsc(
            int doctorMessageCountLow, int doctorMessageCountHigh, int limit, int offset
    );

    @Query(value = "select * from doctor_consultation " +
            "where doctor_message_count between ?1 and ?2 " +
            "order by doctor_message_count desc limit ?3 offset ?4", nativeQuery = true)
    List<DoctorConsultation> findDoctorConsultationOrderByDoctorMessageCountDesc(
            int doctorMessageCountLow, int doctorMessageCountHigh, int limit, int offset
    );

    @Query(value = "select * from doctor_consultation " +
            "where patient_age between ?1 and ?2 and patient_gender = ?3 " +
            "order by patient_age limit ?4 offset ?5", nativeQuery = true)
    List<DoctorConsultation> findDoctorConsultationOrderByPatientAgeAscAndGender(
            int patientAgeLow, int patientAgeHigh, String patientGender, int limit, int offset
    );

    @Query(value = "select * from doctor_consultation " +
            "where patient_age between ?1 and ?2 and patient_gender = ?3 " +
            "order by patient_age desc limit ?4 offset ?5", nativeQuery = true)
    List<DoctorConsultation> findDoctorConsultationOrderByPatientAgeDescAndGender(
            int patientAgeLow, int patientAgeHigh, String patientGender, int limit, int offset
    );

    @Query(value = "select * from doctor_consultation " +
            "where start_date between ?1 and ?2 " +
            "order by start_date limit ?3 offset ?4", nativeQuery = true)
    List<DoctorConsultation> findDoctorConsultationOrderByStartDateAsc(
            LocalDate startDateLow, LocalDate startDateHigh, int limit, int offset
    );

    @Query(value = "select * from doctor_consultation " +
            "where start_date between ?1 and ?2 " +
            "order by start_date desc limit ?3 offset ?4", nativeQuery = true)
    List<DoctorConsultation> findDoctorConsultationOrderByStartDateDesc(
            LocalDate startDateLow, LocalDate startDateHigh, int limit, int offset
    );
}
