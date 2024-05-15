package com.nova.haodf.exception;

public class DoctorOfflineReviewNotFoundException extends IllegalStateException {
    public DoctorOfflineReviewNotFoundException(Long reviewId) {
        super(String.format("Doctor offline review with id %d not found", reviewId));
    }

    public DoctorOfflineReviewNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
