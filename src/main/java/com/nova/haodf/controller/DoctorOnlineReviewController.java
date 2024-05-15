package com.nova.haodf.controller;

import com.nova.haodf.entity.DoctorOnlineReview;
import com.nova.haodf.service.DoctorOnlineReviewService;
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
import java.util.UUID;

@RestController
@CrossOrigin
@Validated
@RequestMapping(path = "api/v3/doctor-online-review")
public class DoctorOnlineReviewController {
    private final DoctorOnlineReviewService doctorOnlineReviewService;

    @Autowired
    public DoctorOnlineReviewController(DoctorOnlineReviewService doctorOnlineReviewService) {
        this.doctorOnlineReviewService = doctorOnlineReviewService;
    }

    @GetMapping(value = "review-id/{online-review-id}")
    public ResponseEntity<Response<DoctorOnlineReview>> getDoctorOnlineReviewByReviewId(
            @PathVariable(value = "online-review-id") UUID onlineReviewId
    ) {
        DoctorOnlineReview doctorOnlineReview = doctorOnlineReviewService
                .getDoctorOnlineReviewByReviewId(onlineReviewId);
        Response<DoctorOnlineReview> response = new Response<>(
                "Doctor online review with review id = %s found".formatted(onlineReviewId),
                HttpStatus.OK, doctorOnlineReview
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "consultation-id/{consultation-id}/{limit}/{offset}")
    public ResponseEntity<Response<DoctorOnlineReview>> getDoctorOnlineReviewsByConsultationId(
            @PathVariable(value = "consultation-id") Long consultationId,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset
    ) {
        List<DoctorOnlineReview> doctorOnlineReviewList = doctorOnlineReviewService
                .getDoctorOnlineReviewsByConsultationId(consultationId, limit, offset);
        Response<DoctorOnlineReview> response = new Response<>(
                "Doctor online reviews with consultation id = %d found".formatted(consultationId),
                HttpStatus.OK, doctorOnlineReviewList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "doctor-id/{doctor-id}/{limit}/{offset}")
    public ResponseEntity<Response<DoctorOnlineReview>> getDoctorOnlineReviewsByDoctorId(
            @PathVariable(value = "doctor-id") Long doctorId,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset
    ) {
        List<DoctorOnlineReview> doctorOnlineReviewList = doctorOnlineReviewService
                .getDoctorOnlineReviewsByDoctorId(doctorId, limit, offset);
        Response<DoctorOnlineReview> response = new Response<>(
                "Doctor online reviews with doctor id = %d found".formatted(doctorId),
                HttpStatus.OK, doctorOnlineReviewList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "evaluate-date/{evaluate-date-low}/{evaluate-date-high}/{limit}/{offset}/{order}")
    public ResponseEntity<Response<DoctorOnlineReview>> getDoctorOnlineReviewsByEvaluateDate(
            @PathVariable(value = "evaluate-date-low") LocalDateTime evaluateDateLow,
            @PathVariable(value = "evaluate-date-high") LocalDateTime evaluateDateHigh,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset,
            @PathVariable(value = "order") Boolean order
    ) {
        List<DoctorOnlineReview> doctorOnlineReviewList = doctorOnlineReviewService
                .getDoctorOnlineReviewsByEvaluateDate(evaluateDateLow, evaluateDateHigh, limit, offset, order);
        Response<DoctorOnlineReview> response = new Response<>(
                "Doctor online reviews with evaluate date found", HttpStatus.OK, doctorOnlineReviewList
        );
        return ResponseEntity.ok(response);
    }
}
