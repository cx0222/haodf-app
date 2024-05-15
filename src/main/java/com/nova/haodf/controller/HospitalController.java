package com.nova.haodf.controller;

import com.nova.haodf.entity.Hospital;
import com.nova.haodf.service.HospitalService;
import com.nova.haodf.service.IndexService;
import com.nova.haodf.util.Response;
import com.nova.haodf.util.ValuesAllowed;
import jakarta.validation.constraints.Max;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@Validated
@RequestMapping(path = "api/v3/hospital")
public class HospitalController {
    private final HospitalService hospitalService;
    private final IndexService indexService;

    @Autowired
    public HospitalController(HospitalService hospitalService, IndexService indexService) {
        this.hospitalService = hospitalService;
        this.indexService = indexService;
    }

    @GetMapping(value = "hospital-id/{hospital-id}")
    public ResponseEntity<Response<Hospital>> getHospitalByHospitalId(
            @PathVariable(value = "hospital-id") Long hospitalId
    ) {
        Hospital hospital = hospitalService.getHospitalByHospitalId(hospitalId);
        Response<Hospital> response = new Response<>(
                "Hospital with hospital id = %d found".formatted(hospitalId),
                HttpStatus.OK, hospital
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "country-rank/{country-rank-low}/{country-rank-high}/{limit}/{offset}")
    public ResponseEntity<Response<Hospital>> getHospitalsByCountryRank(
            @PathVariable(value = "country-rank-low") Integer countryRankLow,
            @PathVariable(value = "country-rank-high") Integer countryRankHigh,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset
    ) {
        List<Hospital> hospitalList = hospitalService.getHospitalsByCountryRank(
                countryRankLow, countryRankHigh, limit, offset
        );
        Response<Hospital> response = new Response<>(
                "Hospitals with country rank between %d and %d found".formatted(countryRankLow, countryRankHigh),
                HttpStatus.OK, hospitalList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "province-rank/{province-name}/{province-rank-low}/{province-rank-high}/{limit}/{offset}")
    public ResponseEntity<Response<Hospital>> getHospitalsByProvinceRank(
            @PathVariable(value = "province-name") String provinceName,
            @PathVariable(value = "province-rank-low") Integer provinceRankLow,
            @PathVariable(value = "province-rank-high") Integer provinceRankHigh,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset
    ) {
        List<Hospital> hospitalList = hospitalService.getHospitalsByProvinceRank(
                provinceName, provinceRankLow, provinceRankHigh, limit, offset
        );
        Response<Hospital> response = new Response<>(
                "Hospitals with province %s rank between %d and %d found".formatted(provinceName, provinceRankLow, provinceRankHigh),
                HttpStatus.OK, hospitalList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "category/{category}/{limit}/{offset}")
    public ResponseEntity<Response<Hospital>> getHospitalsByCategory(
            @PathVariable(value = "category") Integer category,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset
    ) {
        List<Hospital> hospitalList = hospitalService.getHospitalsByCategory(
                category, limit, offset
        );
        Response<Hospital> response = new Response<>(
                "Hospitals with category = %s found".formatted(category),
                HttpStatus.OK, hospitalList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "grade/{grade}/{limit}/{offset}")
    public ResponseEntity<Response<Hospital>> getHospitalsByGrade(
            @PathVariable(value = "grade") Integer grade,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset
    ) {
        List<Hospital> hospitalList = hospitalService.getHospitalsByGrade(
                grade, limit, offset
        );
        Response<Hospital> response = new Response<>(
                "Hospitals with grade = %s found".formatted(grade),
                HttpStatus.OK, hospitalList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "property/{property}/{limit}/{offset}")
    public ResponseEntity<Response<Hospital>> getHospitalsByProperty(
            @PathVariable(value = "property") Integer property,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset
    ) {
        List<Hospital> hospitalList = hospitalService.getHospitalsByProperty(
                property, limit, offset
        );
        Response<Hospital> response = new Response<>(
                "Hospitals with property = %s found".formatted(property),
                HttpStatus.OK, hospitalList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "area-code/{area-code-rlike}/{limit}/{offset}")
    public ResponseEntity<Response<Hospital>> getHospitalsByAreaCode(
            @PathVariable(value = "area-code-rlike") String areaCodeRlike,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset
    ) {
        List<Hospital> hospitalList = hospitalService.getHospitalsByAreaCode(
                areaCodeRlike, limit, offset
        );
        Response<Hospital> response = new Response<>(
                "Hospitals with area code = %s found".formatted(areaCodeRlike),
                HttpStatus.OK, hospitalList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "statistics/{total-space-hits-low}/{total-space-hits-high}/"
            + "{service-patient-count-low}/{service-patient-count-high}/"
            + "{total-comment-count-low}/{total-comment-count-high}/"
            + "{total-disease-count-low}/{total-disease-count-high}/"
            + "{total-doctor-count-low}/{total-doctor-count-high}/"
            + "{total-faculty-count-low}/{total-faculty-count-high}/{limit}/{offset}")
    public ResponseEntity<Response<Hospital>> getHospitalsByStatistics(
            @PathVariable(value = "total-space-hits-low") Integer totalSpaceHitsLow,
            @PathVariable(value = "total-space-hits-high") Integer totalSpaceHitsHigh,
            @PathVariable(value = "service-patient-count-low") Integer servicePatientCountLow,
            @PathVariable(value = "service-patient-count-high") Integer servicePatientCountHigh,
            @PathVariable(value = "total-comment-count-low") Integer totalCommentCountLow,
            @PathVariable(value = "total-comment-count-high") Integer totalCommentCountHigh,
            @PathVariable(value = "total-disease-count-low") Integer totalDiseaseCountLow,
            @PathVariable(value = "total-disease-count-high") Integer totalDiseaseCountHigh,
            @PathVariable(value = "total-doctor-count-low") Integer totalDoctorCountLow,
            @PathVariable(value = "total-doctor-count-high") Integer totalDoctorCountHigh,
            @PathVariable(value = "total-faculty-count-low") Integer totalFacultyCountLow,
            @PathVariable(value = "total-faculty-count-high") Integer totalFacultyCountHigh,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset
    ) {
        List<Hospital> hospitalList = hospitalService.getHospitalsByStatistics(
                totalSpaceHitsLow, totalSpaceHitsHigh,
                servicePatientCountLow, servicePatientCountHigh,
                totalCommentCountLow, totalCommentCountHigh,
                totalDiseaseCountLow, totalDiseaseCountHigh,
                totalDoctorCountLow, totalDoctorCountHigh,
                totalFacultyCountLow, totalFacultyCountHigh,
                limit, offset
        );
        Response<Hospital> response = new Response<>(
                "Hospitals with statistics found", HttpStatus.OK, hospitalList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "votes/{up-vote-count2years-low}/{up-vote-count2years-high}/"
            + "{down-vote-count2years-low}/{down-vote-count2years-high}/"
            + "{year-haodf-count-low}/{year-haodf-count-high}/{limit}/{offset}")
    public ResponseEntity<Response<Hospital>> getHospitalsByVoteAndHaodfCount(
            @PathVariable(value = "up-vote-count2years-low") Integer upVoteCount2YearsLow,
            @PathVariable(value = "up-vote-count2years-high") Integer upVoteCount2YearsHigh,
            @PathVariable(value = "down-vote-count2years-low") Integer downVoteCount2YearsLow,
            @PathVariable(value = "down-vote-count2years-high") Integer downVoteCount2YearsHigh,
            @PathVariable(value = "year-haodf-count-low") Integer yearHaodfCountLow,
            @PathVariable(value = "year-haodf-count-high") Integer yearHaodfCountHigh,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset
    ) {
        List<Hospital> hospitalList = hospitalService.getHospitalsByVoteAndHaodfCount(
                upVoteCount2YearsLow, upVoteCount2YearsHigh,
                downVoteCount2YearsLow, downVoteCount2YearsHigh,
                yearHaodfCountLow, yearHaodfCountHigh,
                limit, offset
        );
        Response<Hospital> response = new Response<>(
                "Hospitals with votes and haodf count found", HttpStatus.OK, hospitalList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "article-live-count/{article-count-low}/{article-count-high}/"
            + "{live-count-low}/{live-count-high}/{limit}/{offset}")
    public ResponseEntity<Response<Hospital>> getHospitalsByArticleAndLiveCount(
            @PathVariable(value = "article-count-low") Integer articleCountLow,
            @PathVariable(value = "article-count-high") Integer articleCountHigh,
            @PathVariable(value = "live-count-low") Integer liveCountLow,
            @PathVariable(value = "live-count-high") Integer liveCountHigh,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset
    ) {
        List<Hospital> hospitalList = hospitalService.getHospitalsByArticleAndLiveCount(
                articleCountLow, articleCountHigh,
                liveCountLow, liveCountHigh,
                limit, offset
        );
        Response<Hospital> response = new Response<>(
                "Hospitals with article and live count found", HttpStatus.OK, hospitalList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "retrieve")
    public ResponseEntity<List<Map<String, String>>> retrieveHospitals(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "limit", required = false, defaultValue = "15")
            @ValuesAllowed(propertyName = "limit", values = {"1", "15", "30", "60", "90", "120", "150", "180"}) Integer limit
    ) {
        List<Map<String, String>> mapList = indexService.searchAllHospitals(query, limit);
        return ResponseEntity.ok(mapList);
    }
}
