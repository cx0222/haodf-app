package com.nova.haodf.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<ApiException> handleDoctorNotFoundException(DoctorNotFoundException doctorNotFoundException) {
        ApiException apiException = new ApiException(
                doctorNotFoundException.getMessage(),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HospitalNotFoundException.class)
    public ResponseEntity<ApiException> handleHospitalNotFoundException(HospitalNotFoundException hospitalNotFoundException) {
        ApiException apiException = new ApiException(
                hospitalNotFoundException.getMessage(),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DoctorOfflineReviewNotFoundException.class)
    public ResponseEntity<ApiException> handleDoctorOfflineReviewNotFoundException(DoctorOfflineReviewNotFoundException doctorOfflineReviewNotFoundException) {
        ApiException apiException = new ApiException(
                doctorOfflineReviewNotFoundException.getMessage(),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DoctorOnlineReviewNotFoundException.class)
    public ResponseEntity<ApiException> handleDoctorOnlineReviewNotFoundException(DoctorOnlineReviewNotFoundException doctorOnlineReviewNotFoundException) {
        ApiException apiException = new ApiException(
                doctorOnlineReviewNotFoundException.getMessage(),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DoctorConsultationNotFoundException.class)
    public ResponseEntity<ApiException> handleDoctorConsultationNotFoundException(DoctorConsultationNotFoundException doctorConsultationNotFoundException) {
        ApiException apiException = new ApiException(
                doctorConsultationNotFoundException.getMessage(),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(QueryException.class)
    public ResponseEntity<ApiException> handleQueryException(QueryException queryException) {
        ApiException apiException = new ApiException(
                queryException.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiException, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiException> handleConstraintViolationException(ConstraintViolationException constraintViolationException) {
        ApiException apiException = new ApiException(
                constraintViolationException.getMessage(),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }
}
