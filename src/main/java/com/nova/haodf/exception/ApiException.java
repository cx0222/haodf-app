package com.nova.haodf.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Objects;

public class ApiException {
    private final String message;
    private final HttpStatus httpStatus;
    private final LocalDateTime createdTime;

    public ApiException(String message, HttpStatus httpStatus, LocalDateTime createdTime) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.createdTime = createdTime;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ApiException that = (ApiException) object;
        return Objects.equals(message, that.message)
                && httpStatus == that.httpStatus
                && Objects.equals(createdTime, that.createdTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, httpStatus, createdTime);
    }

    @Override
    public String
    toString() {
        return "ApiException {" +
                "message='" + message + '\'' +
                ", httpStatus=" + httpStatus +
                ", createdTime=" + createdTime +
                '}';
    }
}
