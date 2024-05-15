package com.nova.haodf.exception;

public class HospitalNotFoundException extends IllegalStateException {
    public HospitalNotFoundException(Long hospitalId) {
        super(String.format("Hospital with id %d not found", hospitalId));
    }

    public HospitalNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
