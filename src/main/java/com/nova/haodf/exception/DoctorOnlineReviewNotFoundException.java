package com.nova.haodf.exception;

import java.util.UUID;

public class DoctorOnlineReviewNotFoundException extends IllegalStateException {
    public DoctorOnlineReviewNotFoundException(UUID onlineReviewId) {
        super(String.format("Doctor online review with id %s not found", onlineReviewId));
    }

    public DoctorOnlineReviewNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
