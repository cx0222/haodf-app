package com.nova.haodf.service;

import com.nova.haodf.entity.DoctorConsultation;
import com.nova.haodf.exception.DoctorConsultationNotFoundException;
import com.nova.haodf.repository.DoctorConsultationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DoctorConsultationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorConsultationService.class);
    private final DoctorConsultationRepository doctorConsultationRepository;

    @Autowired
    public DoctorConsultationService(DoctorConsultationRepository doctorConsultationRepository) {
        this.doctorConsultationRepository = doctorConsultationRepository;
    }

    public void saveDoctorConsultationList(List<DoctorConsultation> doctorConsultationList) {
        doctorConsultationList = doctorConsultationRepository.saveAll(doctorConsultationList);
        LOGGER.info("Saved {} doctor consultation records", doctorConsultationList.size());
    }

    @Cacheable(value = "getDoctorConsultationByConsultationId")
    public DoctorConsultation getDoctorConsultationByConsultationId(Long consultationId) {
        LOGGER.debug("Getting doctor consultation by consultation id, consultation id = {}", consultationId);
        return doctorConsultationRepository.findById(consultationId)
                .orElseThrow(() -> new DoctorConsultationNotFoundException(consultationId));
    }

    @Cacheable(value = "getDoctorConsultationsByDoctorId")
    public List<DoctorConsultation> getDoctorConsultationsByDoctorId(
            Long doctorId, int limit, int offset
    ) {
        LOGGER.debug("Getting doctor consultation by doctor id, doctor id = {}, limit = {}, offset = {}",
                doctorId, limit, offset
        );
        return doctorConsultationRepository.findDoctorConsultationByDoctorId(
                doctorId, limit, offset
        );
    }

    @Cacheable(value = "getDoctorConsultationsByPatientId")
    public List<DoctorConsultation> getDoctorConsultationsByPatientId(
            Long patientId, int limit, int offset
    ) {
        LOGGER.debug("Getting doctor consultation by patient id, patient id = {}, limit = {}, offset = {}",
                patientId, limit, offset
        );
        return doctorConsultationRepository.findDoctorConsultationByPatientId(
                patientId, limit, offset
        );
    }

    @Cacheable(value = "getDoctorConsultationsByBusinessType")
    public List<DoctorConsultation> getDoctorConsultationsByBusinessType(
            String businessType, int limit, int offset
    ) {
        LOGGER.debug("Getting doctor consultation by business type, business type = {}, limit = {}, offset = {}",
                businessType, limit, offset
        );
        return doctorConsultationRepository.findDoctorConsultationByBusinessType(
                businessType, limit, offset
        );
    }

    public List<DoctorConsultation> getDoctorConsultationsByMessageCount(
            int messageCountLow, int messageCountHigh, int limit, int offset, boolean order
    ) {
        LOGGER.debug("Getting doctor consultation by message count, " +
                        "message count = {} - {}, limit = {}, offset = {}, order = {}",
                messageCountLow, messageCountHigh, limit, offset, order
        );
        return order ? doctorConsultationRepository.findDoctorConsultationOrderByMessageCountAsc(
                messageCountLow, messageCountHigh, limit, offset
        ) : doctorConsultationRepository.findDoctorConsultationOrderByMessageCountDesc(
                messageCountLow, messageCountHigh, limit, offset
        );
    }

    public List<DoctorConsultation> getDoctorConsultationsByDoctorMessageCount(
            int doctorMessageCountLow, int doctorMessageCountHigh, int limit, int offset, boolean order
    ) {
        LOGGER.debug("Getting doctor consultation by doctor message count, " +
                        "doctor message count = {} - {}, limit = {}, offset = {}, order = {}",
                doctorMessageCountLow, doctorMessageCountHigh, limit, offset, order
        );
        return order ? doctorConsultationRepository.findDoctorConsultationOrderByDoctorMessageCountAsc(
                doctorMessageCountLow, doctorMessageCountHigh, limit, offset
        ) : doctorConsultationRepository.findDoctorConsultationOrderByDoctorMessageCountDesc(
                doctorMessageCountLow, doctorMessageCountHigh, limit, offset
        );
    }

    public List<DoctorConsultation> getDoctorConsultationsByPatient(
            int patientAgeLow, int patientAgeHigh, String patientGender,
            int limit, int offset, boolean order
    ) {
        LOGGER.debug("Getting doctor consultation by patient, " +
                        "patient age = {} - {}, patient gender = {}, limit = {}, offset = {}, order = {}",
                patientAgeLow, patientAgeHigh, patientGender, limit, offset, order
        );
        return order ? doctorConsultationRepository.findDoctorConsultationOrderByPatientAgeAscAndGender(
                patientAgeLow, patientAgeHigh, patientGender, limit, offset
        ) : doctorConsultationRepository.findDoctorConsultationOrderByPatientAgeDescAndGender(
                patientAgeLow, patientAgeHigh, patientGender, limit, offset
        );
    }

    public List<DoctorConsultation> getDoctorConsultationsByStartDate(
            LocalDate startDateLow, LocalDate startDateHigh, int limit, int offset, boolean order
    ) {
        LOGGER.debug("Getting doctor consultation by start date, " +
                        "start date = {} - {}, limit = {}, offset = {}, order = {}",
                startDateLow, startDateHigh, limit, offset, order
        );
        return order ? doctorConsultationRepository.findDoctorConsultationOrderByStartDateAsc(
                startDateLow, startDateHigh, limit, offset
        ) : doctorConsultationRepository.findDoctorConsultationOrderByStartDateDesc(
                startDateLow, startDateHigh, limit, offset
        );
    }
}
