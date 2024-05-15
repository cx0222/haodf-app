package com.nova.haodf.exception;

public class DoctorNotFoundException extends IllegalStateException {
    public DoctorNotFoundException(Long doctorId) {
        super(String.format("Doctor with id %d not found", doctorId));
    }

    public DoctorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
