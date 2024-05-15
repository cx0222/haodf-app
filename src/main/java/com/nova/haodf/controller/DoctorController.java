package com.nova.haodf.controller;

import com.nova.haodf.entity.Doctor;
import com.nova.haodf.service.DoctorService;
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
@RequestMapping(path = "api/v3/doctor")
public class DoctorController {
    private final DoctorService doctorService;
    private final IndexService indexService;

    @Autowired
    public DoctorController(DoctorService doctorService, IndexService indexService) {
        this.doctorService = doctorService;
        this.indexService = indexService;
    }

    @GetMapping(value = "doctor-id/{doctor-id}")
    public ResponseEntity<Response<Doctor>> getDoctorByDoctorId(
            @PathVariable(value = "doctor-id") Long doctorId
    ) {
        Doctor doctor = doctorService.getDoctorByDoctorId(doctorId);
        Response<Doctor> response = new Response<>(
                "Doctor with doctor id = %d found".formatted(doctorId), HttpStatus.OK, doctor
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "recommend-index/{limit}/{offset}/{order}")
    public ResponseEntity<Response<Doctor>> getDoctorsByRecommendIndex(
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset,
            @PathVariable(value = "order") Boolean order
    ) {
        List<Doctor> doctorList = doctorService.getDoctorsByRecommendIndex(limit, offset, order);
        Response<Doctor> response = new Response<>(
                "Doctors with recommend index found", HttpStatus.OK, doctorList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "article-count/{limit}/{offset}/{order}")
    public ResponseEntity<Response<Doctor>> getDoctorsByArticleCount(
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset,
            @PathVariable(value = "order") Boolean order
    ) {
        List<Doctor> doctorList = doctorService.getDoctorsByArticleCount(limit, offset, order);
        Response<Doctor> response = new Response<>(
                "Doctors with article count found", HttpStatus.OK, doctorList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "vote-count/{limit}/{offset}/{order}")
    public ResponseEntity<Response<Doctor>> getDoctorsByVoteCount(
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset,
            @PathVariable(value = "order") Boolean order
    ) {
        List<Doctor> doctorList = doctorService.getDoctorsByVoteCount(limit, offset, order);
        Response<Doctor> response = new Response<>(
                "Doctors with vote count found", HttpStatus.OK, doctorList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "open-space-time/{limit}/{offset}/{order}")
    public ResponseEntity<Response<Doctor>> getDoctorsByOpenSpaceTime(
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset,
            @PathVariable(value = "order") Boolean order
    ) {
        List<Doctor> doctorList = doctorService.getDoctorsByOpenSpaceTime(limit, offset, order);
        Response<Doctor> response = new Response<>(
                "Doctors with open space time found", HttpStatus.OK, doctorList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "present-count/{limit}/{offset}/{order}")
    public ResponseEntity<Response<Doctor>> getDoctorsByPresentCount(
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset,
            @PathVariable(value = "order") Boolean order
    ) {
        List<Doctor> doctorList = doctorService.getDoctorsByPresentCount(limit, offset, order);
        Response<Doctor> response = new Response<>(
                "Doctors with present count found", HttpStatus.OK, doctorList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "space-replied-count/{limit}/{offset}/{order}")
    public ResponseEntity<Response<Doctor>> getDoctorsBySpaceRepliedCount(
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset,
            @PathVariable(value = "order") Boolean order
    ) {
        List<Doctor> doctorList = doctorService.getDoctorsBySpaceRepliedCount(limit, offset, order);
        Response<Doctor> response = new Response<>(
                "Doctors with space replied count found", HttpStatus.OK, doctorList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "total-hit/{limit}/{offset}/{order}")
    public ResponseEntity<Response<Doctor>> getDoctorsByTotalHit(
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset,
            @PathVariable(value = "order") Boolean order
    ) {
        List<Doctor> doctorList = doctorService.getDoctorsByTotalHit(limit, offset, order);
        Response<Doctor> response = new Response<>(
                "Doctors with total hit found", HttpStatus.OK, doctorList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "total-sign-in-count/{limit}/{offset}/{order}")
    public ResponseEntity<Response<Doctor>> getDoctorsByTotalSignInCount(
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset,
            @PathVariable(value = "order") Boolean order
    ) {
        List<Doctor> doctorList = doctorService.getDoctorsByTotalSignInCount(limit, offset, order);
        Response<Doctor> response = new Response<>(
                "Doctors with total sign in count found", HttpStatus.OK, doctorList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "vote-in2years/{limit}/{offset}/{order}")
    public ResponseEntity<Response<Doctor>> getDoctorsByVoteIn2Years(
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset,
            @PathVariable(value = "order") Boolean order
    ) {
        List<Doctor> doctorList = doctorService.getDoctorsByVoteIn2Years(limit, offset, order);
        Response<Doctor> response = new Response<>(
                "Doctors with vote in 2 years found", HttpStatus.OK, doctorList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "area-code/{area-code-rlike}/{limit}/{offset}")
    public ResponseEntity<Response<Doctor>> getDoctorsByAreaCode(
            @PathVariable(value = "area-code-rlike") String areaCodeRlike,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset
    ) {
        List<Doctor> doctorList = doctorService.getDoctorsByAreaCode(areaCodeRlike, limit, offset);
        Response<Doctor> response = new Response<>(
                "Doctors with area code = %s found".formatted(areaCodeRlike),
                HttpStatus.OK, doctorList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "retrieve")
    public ResponseEntity<List<Map<String, String>>> retrieveDoctors(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "limit", required = false, defaultValue = "15")
            @ValuesAllowed(propertyName = "limit", values = {"1", "15", "30", "60", "90", "120", "150", "180"}) Integer limit
    ) {
        List<Map<String, String>> mapList = indexService.searchAllDoctors(query, limit);
        return ResponseEntity.ok(mapList);
    }
}
