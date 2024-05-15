package com.nova.haodf.service;

import com.nova.haodf.entity.Hospital;
import com.nova.haodf.exception.HospitalNotFoundException;
import com.nova.haodf.repository.HospitalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospitalService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HospitalService.class);
    private final HospitalRepository hospitalRepository;

    @Autowired
    public HospitalService(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    public void saveHospitalList(List<Hospital> hospitalList) {
        hospitalList = hospitalRepository.saveAll(hospitalList);
        LOGGER.info("Saved {} hospitals", hospitalList.size());
    }

    public void saveHospitalDetailList(List<Hospital> hospitalWithDetailList) {
        for (Hospital hospital : hospitalWithDetailList) {
            Long hospitalId = hospital.getHospitalId();
            try {
                Hospital previousInstance = hospitalRepository.findById(hospitalId)
                        .orElseThrow(() -> new HospitalNotFoundException(hospitalId));
                hospital.setStatus(previousInstance.getStatus());
                hospital.setHospitalInfoStatus(previousInstance.getHospitalInfoStatus());
            } catch (HospitalNotFoundException hospitalNotFoundException) {
                LOGGER.warn("Hospital with id = {} not found", hospitalId, hospitalNotFoundException);
            }
        }
        hospitalWithDetailList = hospitalRepository.saveAll(hospitalWithDetailList);
        LOGGER.info("Saved {} hospitals with detail", hospitalWithDetailList.size());
    }

    public List<Hospital> getHospitalListByStatusAndLimit(int status, int limit) {
        List<Hospital> hospitalList = hospitalRepository.findHospitalsByStatus(status, limit);
        LOGGER.info("Found {} hospitals with status {}", hospitalList.size(), status);
        return hospitalList;
    }

    public List<Hospital> getHospitalInfoListByHospitalInfoStatusAndLimit(int hospitalInfoStatus, int limit) {
        List<Hospital> hospitalList = hospitalRepository.findHospitalsByHospitalInfoStatus(hospitalInfoStatus, limit);
        LOGGER.info("Found {} hospitals with hospital info status {}", hospitalList.size(), hospitalInfoStatus);
        return hospitalList;
    }

    @Cacheable(value = "getHospitalByHospitalId")
    public Hospital getHospitalByHospitalId(Long hospitalId) {
        LOGGER.debug("Getting hospital by hospital id, hospital id = {}", hospitalId);
        return hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new HospitalNotFoundException(hospitalId));
    }

    public List<Hospital> getHospitalsByCountryRank(
            int countryRankLow, int countryRankHigh, int limit, int offset
    ) {
        LOGGER.debug("Getting hospitals by country rank, " +
                        "country rank low = {}, country rank high = {}, limit = {}, offset = {}",
                countryRankLow, countryRankHigh, limit, offset
        );
        return hospitalRepository.findHospitalsByCountryRankBetween(
                countryRankLow, countryRankHigh, limit, offset
        );
    }

    public List<Hospital> getHospitalsByProvinceRank(
            String provinceName, int provinceRankLow, int provinceRankHigh, int limit, int offset
    ) {
        LOGGER.debug("Getting hospitals by province rank, " +
                        "province name = {}, province rank low = {}, province rank high = {}, " +
                        "limit = {}, offset = {}",
                provinceName, provinceRankLow, provinceRankHigh, limit, offset
        );
        return hospitalRepository.findHospitalsByProvinceNameAndProvinceRankBetween(
                provinceName, provinceRankLow, provinceRankHigh, limit, offset
        );
    }

    @Cacheable(value = "getHospitalsByCategory")
    public List<Hospital> getHospitalsByCategory(
            int category, int limit, int offset
    ) {
        LOGGER.debug("Getting hospitals by category, category = {}, limit = {}, offset = {}",
                category, limit, offset
        );
        return hospitalRepository.findHospitalsByCategory(
                category, limit, offset
        );
    }

    @Cacheable(value = "getHospitalsByGrade")
    public List<Hospital> getHospitalsByGrade(
            int grade, int limit, int offset
    ) {
        LOGGER.debug("Getting hospitals by grade, grade = {}, limit = {}, offset = {}",
                grade, limit, offset
        );
        return hospitalRepository.findHospitalsByGrade(
                grade, limit, offset
        );
    }

    @Cacheable(value = "getHospitalsByProperty")
    public List<Hospital> getHospitalsByProperty(
            int property, int limit, int offset
    ) {
        LOGGER.debug("Getting hospitals by property, property = {}, limit = {}, offset = {}",
                property, limit, offset
        );
        return hospitalRepository.findHospitalsByProperty(
                property, limit, offset
        );
    }

    public List<Hospital> getHospitalsByAreaCode(
            String areaCodeRlike, int limit, int offset
    ) {
        LOGGER.debug("Getting hospitals by area code, area code rlike = {}, limit = {}, offset = {}",
                areaCodeRlike, limit, offset
        );
        return hospitalRepository.findHospitalsByAreaCodeRlike(
                areaCodeRlike, limit, offset
        );
    }

    public List<Hospital> getHospitalsByStatistics(
            int totalSpaceHitsLow, int totalSpaceHitsHigh,
            int servicePatientCountLow, int servicePatientCountHigh,
            int totalCommentCountLow, int totalCommentCountHigh,
            int totalDiseaseCountLow, int totalDiseaseCountHigh,
            int totalDoctorCountLow, int totalDoctorCountHigh,
            int totalFacultyCountLow, int totalFacultyCountHigh,
            int limit, int offset
    ) {
        LOGGER.debug("Getting hospitals by statistics, " +
                        "total space hits = {} - {}, " +
                        "service patient count = {} - {}, " +
                        "total comment count = {} - {}, " +
                        "total disease count = {} - {}, " +
                        "total doctor count = {} - {}, " +
                        "total faculty count = {} - {}, " +
                        "limit = {}, offset = {}",
                totalSpaceHitsLow, totalSpaceHitsHigh,
                servicePatientCountLow, servicePatientCountHigh,
                totalCommentCountLow, totalCommentCountHigh,
                totalDiseaseCountLow, totalDiseaseCountHigh,
                totalDoctorCountLow, totalDoctorCountHigh,
                totalFacultyCountLow, totalFacultyCountHigh,
                limit, offset
        );
        return hospitalRepository.findHospitalsByTotalStatistics(
                totalSpaceHitsLow, totalSpaceHitsHigh,
                servicePatientCountLow, servicePatientCountHigh,
                totalCommentCountLow, totalCommentCountHigh,
                totalDiseaseCountLow, totalDiseaseCountHigh,
                totalDoctorCountLow, totalDoctorCountHigh,
                totalFacultyCountLow, totalFacultyCountHigh,
                limit, offset
        );
    }

    public List<Hospital> getHospitalsByVoteAndHaodfCount(
            int upVoteCount2YearsLow, int upVoteCount2YearsHigh,
            int downVoteCount2YearsLow, int downVoteCount2YearsHigh,
            int yearHaodfCountLow, int yearHaodfCountHigh,
            int limit, int offset
    ) {
        LOGGER.debug("Getting hospitals by votes and haodf count, " +
                        "up vote count 2 years = {} - {}, " +
                        "up vote count 2 years = {} - {}, " +
                        "year haodf count = {} - {}, " +
                        "limit = {}, offset = {}",
                upVoteCount2YearsLow, upVoteCount2YearsHigh,
                downVoteCount2YearsLow, downVoteCount2YearsHigh,
                yearHaodfCountLow, yearHaodfCountHigh,
                limit, offset
        );
        return hospitalRepository.findHospitalsByUpVoteAndYearHaodfCount(
                upVoteCount2YearsLow, upVoteCount2YearsHigh,
                downVoteCount2YearsLow, downVoteCount2YearsHigh,
                yearHaodfCountLow, yearHaodfCountHigh,
                limit, offset
        );
    }

    public List<Hospital> getHospitalsByArticleAndLiveCount(
            int articleCountLow, int articleCountHigh,
            int liveCountLow, int liveCountHigh,
            int limit, int offset
    ) {
        LOGGER.debug("Getting hospitals by article and live count, " +
                        "article count = {} - {}, " +
                        "live count = {} - {}, " +
                        "limit = {}, offset = {}",
                articleCountLow, articleCountHigh,
                liveCountLow, liveCountHigh,
                limit, offset
        );
        return hospitalRepository.findHospitalsByArticleCountAndLiveCount(
                articleCountLow, articleCountHigh,
                liveCountLow, liveCountHigh,
                limit, offset
        );
    }
}
