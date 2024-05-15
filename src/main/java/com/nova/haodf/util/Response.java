package com.nova.haodf.util;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Response<T> {
    private final String message;
    private final HttpStatus httpStatus;
    private final LocalDateTime responseTime;
    private final List<T> dataList;

    public Response(String message, HttpStatus httpStatus, List<T> dataList) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.responseTime = LocalDateTime.now();
        this.dataList = dataList;
    }

    public Response(String message, HttpStatus httpStatus, T dataItem) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.responseTime = LocalDateTime.now();
        this.dataList = Collections.singletonList(dataItem);
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public LocalDateTime getResponseTime() {
        return responseTime;
    }

    public List<T> getDataList() {
        return dataList;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Response<?> response = (Response<?>) object;
        return Objects.equals(message, response.message)
                && httpStatus == response.httpStatus
                && Objects.equals(responseTime, response.responseTime)
                && Objects.equals(dataList, response.dataList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, httpStatus, responseTime, dataList);
    }

    @Override
    public String
    toString() {
        return "Response {" +
                "message='" + message + '\'' +
                ", httpStatus=" + httpStatus +
                ", responseTime=" + responseTime +
                ", dataList=" + dataList +
                '}';
    }
}
