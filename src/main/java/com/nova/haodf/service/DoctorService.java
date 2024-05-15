package com.nova.haodf.service;

import com.nova.haodf.entity.Doctor;
import com.nova.haodf.exception.DoctorNotFoundException;
import com.nova.haodf.repository.DoctorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DoctorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorService.class);
    private final DoctorRepository doctorRepository;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public void saveDoctorList(List<Doctor> doctorList) {
        doctorList = doctorRepository.saveAll(doctorList);
        LOGGER.info("Saved {} doctors", doctorList.size());
    }

    public void saveDoctorDetailList(List<Doctor> doctorWithDetailList) {
        for (Doctor doctor : doctorWithDetailList) {
            Long doctorId = doctor.getDoctorId();
            try {
                Doctor previousInstance = doctorRepository.findById(doctorId)
                        .orElseThrow(() -> new DoctorNotFoundException(doctorId));
                doctor.setOfflineStatus(previousInstance.getOfflineStatus());
                doctor.setOnlineStatus(previousInstance.getOnlineStatus());
                doctor.setConsultationStatus(previousInstance.getConsultationStatus());
            } catch (DoctorNotFoundException doctorNotFoundException) {
                LOGGER.warn("Doctor with id = {} not found", doctorId, doctorNotFoundException);
            }
        }
        doctorWithDetailList = doctorRepository.saveAll(doctorWithDetailList);
        LOGGER.info("Saved {} doctors with detail", doctorWithDetailList.size());
    }

    public void saveDoctorStatisticsList(List<Doctor> doctorStatisticsList) {
        List<Doctor> doctorWithStatisticsList = new ArrayList<>();
        for (Doctor doctor : doctorStatisticsList) {
            Long doctorId = doctor.getDoctorId();
            try {
                Doctor newInstance = doctorRepository.findById(doctorId)
                        .orElseThrow(() -> new DoctorNotFoundException(doctorId));
                newInstance.setArticleCount(doctor.getArticleCount());
                newInstance.setDoctorVoteCount(doctor.getDoctorVoteCount());
                newInstance.setOpenSpaceTime(doctor.getOpenSpaceTime());
                newInstance.setPresentCount(doctor.getPresentCount());
                newInstance.setSpaceRepliedCount(doctor.getSpaceRepliedCount());
                newInstance.setTotalHit(doctor.getTotalHit());
                newInstance.setTotalSignInCount(doctor.getTotalSignInCount());
                newInstance.setVoteIn2Years(doctor.getVoteIn2Years());
                doctorWithStatisticsList.add(newInstance);
            } catch (DoctorNotFoundException doctorNotFoundException) {
                LOGGER.warn("Doctor with id = {} not found", doctorId, doctorNotFoundException);
            }
        }
        doctorWithStatisticsList = doctorRepository.saveAll(doctorWithStatisticsList);
        LOGGER.info("Saved {} doctors with statistics", doctorWithStatisticsList.size());
    }

    public List<Doctor> getDoctorListByOnlineStatusAndLimit(int onlineStatus, int limit) {
        List<Doctor> doctorList = doctorRepository.findDoctorsByOnlineStatus(onlineStatus, limit);
        LOGGER.info("Found {} doctors with online status {}", doctorList.size(), onlineStatus);
        return doctorList;
    }

    public List<Doctor> getDoctorListByOfflineStatusAndLimit(int offlineStatus, int limit) {
        List<Doctor> doctorList = doctorRepository.findDoctorsByOfflineStatus(offlineStatus, limit);
        LOGGER.info("Found {} doctors with offline status {}", doctorList.size(), offlineStatus);
        return doctorList;
    }

    public List<Doctor> getDoctorListByConsultationStatusAndLimit(int consultationStatus, int limit) {
        List<Doctor> doctorList = doctorRepository.findDoctorsByConsultationStatus(consultationStatus, limit);
        LOGGER.info("Found {} doctors with consultation status {}", doctorList.size(), consultationStatus);
        return doctorList;
    }

    public List<Doctor> getDoctorListByStatisticsStatusAndLimit(int statisticsStatus, int limit) {
        List<Doctor> doctorList = doctorRepository.findDoctorsByDoctorStatisticsStatus(statisticsStatus, limit);
        LOGGER.info("Found {} doctors with statistics status {}", doctorList.size(), statisticsStatus);
        return doctorList;
    }

    @Cacheable(value = "getDoctorByDoctorId")
    public Doctor getDoctorByDoctorId(Long doctorId) {
        LOGGER.debug("Getting doctor by doctor id, doctor id = {}", doctorId);
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));
    }

    @Cacheable(value = "getDoctorsByRecommendIndex")
    public List<Doctor> getDoctorsByRecommendIndex(int limit, int offset, boolean order) {
        LOGGER.debug("Getting doctors by recommend index, limit = {}, offset = {}, order = {}",
                limit, offset, order
        );
        return order ? doctorRepository.findDoctorsOrderByRecommendIndexAsc(limit, offset)
                : doctorRepository.findDoctorsOrderByRecommendIndexDesc(limit, offset);
    }

    @Cacheable(value = "getDoctorsByArticleCount")
    public List<Doctor> getDoctorsByArticleCount(int limit, int offset, boolean order) {
        LOGGER.debug("Getting doctors by article count, limit = {}, offset = {}, order = {}",
                limit, offset, order
        );
        return order ? doctorRepository.findDoctorsOrderByArticleCountAsc(limit, offset)
                : doctorRepository.findDoctorsOrderByArticleCountDesc(limit, offset);
    }

    @Cacheable(value = "getDoctorsByVoteCount")
    public List<Doctor> getDoctorsByVoteCount(int limit, int offset, boolean order) {
        LOGGER.debug("Getting doctors by vote count, limit = {}, offset = {}, order = {}",
                limit, offset, order
        );
        return order ? doctorRepository.findDoctorsOrderByDoctorVoteCountAsc(limit, offset)
                : doctorRepository.findDoctorsOrderByDoctorVoteCountDesc(limit, offset);
    }

    @Cacheable(value = "getDoctorsByOpenSpaceTime")
    public List<Doctor> getDoctorsByOpenSpaceTime(int limit, int offset, boolean order) {
        LOGGER.debug("Getting doctors by open space time, limit = {}, offset = {}, order = {}",
                limit, offset, order
        );
        return order ? doctorRepository.findDoctorsOrderByOpenSpaceTimeAsc(limit, offset)
                : doctorRepository.findDoctorsOrderByOpenSpaceTimeDesc(limit, offset);
    }

    @Cacheable(value = "getDoctorsByPresentCount")
    public List<Doctor> getDoctorsByPresentCount(int limit, int offset, boolean order) {
        LOGGER.debug("Getting doctors by present count, limit = {}, offset = {}, order = {}",
                limit, offset, order
        );
        return order ? doctorRepository.findDoctorsOrderByPresentCountAsc(limit, offset)
                : doctorRepository.findDoctorsOrderByPresentCountDesc(limit, offset);
    }

    @Cacheable(value = "getDoctorsBySpaceRepliedCount")
    public List<Doctor> getDoctorsBySpaceRepliedCount(int limit, int offset, boolean order) {
        LOGGER.debug("Getting doctors by space replied count, limit = {}, offset = {}, order = {}",
                limit, offset, order
        );
        return order ? doctorRepository.findDoctorsOrderBySpaceRepliedCountAsc(limit, offset)
                : doctorRepository.findDoctorsOrderBySpaceRepliedCountDesc(limit, offset);
    }

    @Cacheable(value = "getDoctorsByTotalHit")
    public List<Doctor> getDoctorsByTotalHit(int limit, int offset, boolean order) {
        LOGGER.debug("Getting doctors by total hit, limit = {}, offset = {}, order = {}",
                limit, offset, order
        );
        return order ? doctorRepository.findDoctorsOrderByTotalHitAsc(limit, offset)
                : doctorRepository.findDoctorsOrderByTotalHitDesc(limit, offset);
    }

    @Cacheable(value = "getDoctorsByTotalSignInCount")
    public List<Doctor> getDoctorsByTotalSignInCount(int limit, int offset, boolean order) {
        LOGGER.debug("Getting doctors by total sign in count, limit = {}, offset = {}, order = {}",
                limit, offset, order
        );
        return order ? doctorRepository.findDoctorsOrderByTotalSignInCountAsc(limit, offset)
                : doctorRepository.findDoctorsOrderByTotalSignInCountDesc(limit, offset);
    }

    @Cacheable(value = "getDoctorsByVoteIn2Years")
    public List<Doctor> getDoctorsByVoteIn2Years(int limit, int offset, boolean order) {
        LOGGER.debug("Getting doctors by vote in 2 years, limit = {}, offset = {}, order = {}",
                limit, offset, order
        );
        return order ? doctorRepository.findDoctorsOrderByVoteIn2YearsAsc(limit, offset)
                : doctorRepository.findDoctorsOrderByVoteIn2YearsDesc(limit, offset);
    }

    public List<Doctor> getDoctorsByAreaCode(String areaCodeRlike, int limit, int offset) {
        LOGGER.debug("Getting doctors by area code, area code rlike = {}, limit = {}, offset = {}",
                areaCodeRlike, limit, offset
        );
        return doctorRepository.findDoctorsByAreaCodeRlike(
                areaCodeRlike, limit, offset
        );
    }

    public void updateHospitalIdMap(Map<Long, Long> doctorHospitalIdMap) {
        doctorHospitalIdMap.forEach(doctorRepository::updateHospitalId);
    }
}
