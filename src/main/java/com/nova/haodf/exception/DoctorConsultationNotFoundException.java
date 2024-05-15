package com.nova.haodf.exception;

public class DoctorConsultationNotFoundException extends IllegalStateException {
    public DoctorConsultationNotFoundException(Long consultationId) {
        super(String.format("Doctor consultation with id %d not found", consultationId));
    }

    public DoctorConsultationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
