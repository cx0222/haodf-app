package com.nova.haodf.controller;

import com.nova.haodf.entity.DoctorOfflineReview;
import com.nova.haodf.service.DoctorOfflineReviewService;
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
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin
@Validated
@RequestMapping(path = "api/v3/doctor-offline-review")
public class DoctorOfflineReviewController {
    private final DoctorOfflineReviewService doctorOfflineReviewService;

    @Autowired
    public DoctorOfflineReviewController(DoctorOfflineReviewService doctorOfflineReviewService) {
        this.doctorOfflineReviewService = doctorOfflineReviewService;
    }

    @GetMapping(value = "review-id/{review-id}")
    public ResponseEntity<Response<DoctorOfflineReview>> getDoctorOfflineReviewByReviewId(
            @PathVariable(value = "review-id") Long reviewId
    ) {
        DoctorOfflineReview doctorOfflineReview = doctorOfflineReviewService.getDoctorOfflineReviewByReviewId(reviewId);
        Response<DoctorOfflineReview> response = new Response<>(
                "Doctor offline review with review id = %d found".formatted(reviewId),
                HttpStatus.OK, doctorOfflineReview
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "doctor-id/{doctor-id}/{limit}/{offset}")
    public ResponseEntity<Response<DoctorOfflineReview>> getDoctorOfflineReviewsByDoctorId(
            @PathVariable(value = "doctor-id") Long doctorId,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset
    ) {
        List<DoctorOfflineReview> doctorOfflineReviewList = doctorOfflineReviewService
                .getDoctorOfflineReviewsByDoctorId(doctorId, limit, offset);
        Response<DoctorOfflineReview> response = new Response<>(
                "Doctor offline reviews with doctor id = %d found".formatted(doctorId),
                HttpStatus.OK, doctorOfflineReviewList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "type-description/{type-description}/{limit}/{offset}")
    public ResponseEntity<Response<DoctorOfflineReview>> getDoctorOfflineReviewsByTypeDescription(
            @PathVariable(value = "type-description") String typeDescription,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset
    ) {
        List<DoctorOfflineReview> doctorOfflineReviewList = doctorOfflineReviewService
                .getDoctorOfflineReviewsByTypeDescription(typeDescription, limit, offset);
        Response<DoctorOfflineReview> response = new Response<>(
                "Doctor offline reviews with type description = %s found".formatted(typeDescription),
                HttpStatus.OK, doctorOfflineReviewList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "effect/{effect}/{limit}/{offset}")
    public ResponseEntity<Response<DoctorOfflineReview>> getDoctorOfflineReviewsByEffect(
            @PathVariable(value = "effect") String effect,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset
    ) {
        List<DoctorOfflineReview> doctorOfflineReviewList = doctorOfflineReviewService
                .getDoctorOfflineReviewsByEffect(effect, limit, offset);
        Response<DoctorOfflineReview> response = new Response<>(
                "Doctor offline reviews with effect = %s found".formatted(effect),
                HttpStatus.OK, doctorOfflineReviewList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "attitude/{attitude}/{limit}/{offset}")
    public ResponseEntity<Response<DoctorOfflineReview>> getDoctorOfflineReviewsByAttitude(
            @PathVariable(value = "attitude") String attitude,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset
    ) {
        List<DoctorOfflineReview> doctorOfflineReviewList = doctorOfflineReviewService
                .getDoctorOfflineReviewsByAttitude(attitude, limit, offset);
        Response<DoctorOfflineReview> response = new Response<>(
                "Doctor offline reviews with attitude = %s found".formatted(attitude),
                HttpStatus.OK, doctorOfflineReviewList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "skill/{skill}/{limit}/{offset}")
    public ResponseEntity<Response<DoctorOfflineReview>> getDoctorOfflineReviewsBySkill(
            @PathVariable(value = "skill") String skill,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset
    ) {
        List<DoctorOfflineReview> doctorOfflineReviewList = doctorOfflineReviewService
                .getDoctorOfflineReviewsBySkill(skill, limit, offset);
        Response<DoctorOfflineReview> response = new Response<>(
                "Doctor offline reviews with skill = %s found".formatted(skill),
                HttpStatus.OK, doctorOfflineReviewList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "real-time/{real-time-low}/{real-time-high}/{limit}/{offset}/{order}")
    public ResponseEntity<Response<DoctorOfflineReview>> getDoctorOfflineReviewsByRealTime(
            @PathVariable(value = "real-time-low") LocalDateTime realTimeLow,
            @PathVariable(value = "real-time-high") LocalDateTime realTimeHigh,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset,
            @PathVariable(value = "order") Boolean order
    ) {
        List<DoctorOfflineReview> doctorOfflineReviewList = doctorOfflineReviewService
                .getDoctorOfflineReviewsByRealTime(realTimeLow, realTimeHigh, limit, offset, order);
        Response<DoctorOfflineReview> response = new Response<>(
                "Doctor offline reviews with real time found", HttpStatus.OK, doctorOfflineReviewList
        );
        return ResponseEntity.ok(response);
    }
}
