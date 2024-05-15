package com.nova.haodf.exception;

public class QueryException extends IllegalStateException {
    public QueryException(Throwable cause) {
        super("Exception occurred when retrieving", cause);
    }

    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
