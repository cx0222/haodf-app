package com.nova.haodf.controller;

import com.nova.haodf.entity.DoctorConsultation;
import com.nova.haodf.service.DoctorConsultationService;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin
@Validated
@RequestMapping(path = "api/v3/doctor-consultation")
public class DoctorConsultationController {
    private final DoctorConsultationService doctorConsultationService;

    @Autowired
    public DoctorConsultationController(DoctorConsultationService doctorConsultationService) {
        this.doctorConsultationService = doctorConsultationService;
    }

    @GetMapping(value = "consultation-id/{consultation-id}")
    public ResponseEntity<Response<DoctorConsultation>> getDoctorConsultationByConsultationId(
            @PathVariable(value = "consultation-id") Long consultationId
    ) {
        DoctorConsultation doctorConsultation = doctorConsultationService
                .getDoctorConsultationByConsultationId(consultationId);
        Response<DoctorConsultation> response = new Response<>(
                "Doctor consultation with consultation id = %d found".formatted(consultationId),
                HttpStatus.OK, doctorConsultation
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "doctor-id/{doctor-id}/{limit}/{offset}")
    public ResponseEntity<Response<DoctorConsultation>> getDoctorConsultationsByDoctorId(
            @PathVariable(value = "doctor-id") Long doctorId,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset
    ) {
        List<DoctorConsultation> doctorConsultationList = doctorConsultationService
                .getDoctorConsultationsByDoctorId(doctorId, limit, offset);
        Response<DoctorConsultation> response = new Response<>(
                "Doctor consultation with doctor id = %s found".formatted(doctorId),
                HttpStatus.OK, doctorConsultationList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "patient-id/{patient-id}/{limit}/{offset}")
    public ResponseEntity<Response<DoctorConsultation>> getDoctorConsultationsByPatientId(
            @PathVariable(value = "patient-id") Long patientId,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset
    ) {
        List<DoctorConsultation> doctorConsultationList = doctorConsultationService
                .getDoctorConsultationsByPatientId(patientId, limit, offset);
        Response<DoctorConsultation> response = new Response<>(
                "Doctor consultation with patient id = %s found".formatted(patientId),
                HttpStatus.OK, doctorConsultationList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "business-type/{business-type}/{limit}/{offset}")
    public ResponseEntity<Response<DoctorConsultation>> getDoctorConsultationsByBusinessType(
            @PathVariable(value = "business-type") String businessType,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset
    ) {
        List<DoctorConsultation> doctorConsultationList = doctorConsultationService
                .getDoctorConsultationsByBusinessType(businessType, limit, offset);
        Response<DoctorConsultation> response = new Response<>(
                "Doctor consultation with business type = %s found".formatted(businessType),
                HttpStatus.OK, doctorConsultationList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "message-count/{message-count-low}/{message-count-high}/{limit}/{offset}/{order}")
    public ResponseEntity<Response<DoctorConsultation>> getDoctorConsultationsByMessageCount(
            @PathVariable(value = "message-count-low") Integer messageCountLow,
            @PathVariable(value = "message-count-high") Integer messageCountHigh,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset,
            @PathVariable(value = "order") Boolean order
    ) {
        List<DoctorConsultation> doctorConsultationList = doctorConsultationService
                .getDoctorConsultationsByMessageCount(messageCountLow, messageCountHigh, limit, offset, order);
        Response<DoctorConsultation> response = new Response<>(
                "Doctor consultation with message count found", HttpStatus.OK, doctorConsultationList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "doctor-message-count/{doctor-message-count-low}/{doctor-message-count-high}/{limit}/{offset}/{order}")
    public ResponseEntity<Response<DoctorConsultation>> getDoctorConsultationsByDoctorMessageCount(
            @PathVariable(value = "doctor-message-count-low") Integer doctorMessageCountLow,
            @PathVariable(value = "doctor-message-count-high") Integer doctorMessageCountHigh,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset,
            @PathVariable(value = "order") Boolean order
    ) {
        List<DoctorConsultation> doctorConsultationList = doctorConsultationService
                .getDoctorConsultationsByDoctorMessageCount(doctorMessageCountLow, doctorMessageCountHigh, limit, offset, order);
        Response<DoctorConsultation> response = new Response<>(
                "Doctor consultation with doctor message count found", HttpStatus.OK, doctorConsultationList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "patient/{patient-age-low}/{patient-age-high}/{patient-gender}/{limit}/{offset}/{order}")
    public ResponseEntity<Response<DoctorConsultation>> getDoctorConsultationsByPatient(
            @PathVariable(value = "patient-age-low") Integer patientAgeLow,
            @PathVariable(value = "patient-age-high") Integer patientAgeHigh,
            @PathVariable(value = "patient-gender") String patientGender,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset,
            @PathVariable(value = "order") Boolean order
    ) {
        List<DoctorConsultation> doctorConsultationList = doctorConsultationService
                .getDoctorConsultationsByPatient(patientAgeLow, patientAgeHigh, patientGender, limit, offset, order);
        Response<DoctorConsultation> response = new Response<>(
                "Doctor consultation with patient found", HttpStatus.OK, doctorConsultationList
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "start-date/{start-date-low}/{start-date-high}/{limit}/{offset}/{order}")
    public ResponseEntity<Response<DoctorConsultation>> getDoctorConsultationsByStartDate(
            @PathVariable(value = "start-date-low") LocalDate startDateLow,
            @PathVariable(value = "start-date-high") LocalDate startDateHigh,
            @PathVariable(value = "limit") @ValuesAllowed(propertyName = "limit", values = {"1", "5", "10", "20", "50", "100", "200"}) Integer limit,
            @PathVariable(value = "offset") @Max(value = 10000, message = "The query offset must not exceed 10000") Integer offset,
            @PathVariable(value = "order") Boolean order
    ) {
        List<DoctorConsultation> doctorConsultationList = doctorConsultationService
                .getDoctorConsultationsByStartDate(startDateLow, startDateHigh, limit, offset, order);
        Response<DoctorConsultation> response = new Response<>(
                "Doctor consultation with start date found", HttpStatus.OK, doctorConsultationList
        );
        return ResponseEntity.ok(response);
    }
}
