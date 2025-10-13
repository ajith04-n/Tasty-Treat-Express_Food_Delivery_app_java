package com.tastytreat.backend.tasty_treat_express_backend.exceptions;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String error;
    private String message;
    private int statusCode;
    private LocalDateTime timestamp;



    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }
    public ErrorResponse(String error, String message, int statusCode, LocalDateTime timestamp) {
        this.error = error;
        this.message = message;
        this.statusCode = statusCode;
        this.timestamp = timestamp;
    }

    public ErrorResponse(int statusCode, String message, LocalDateTime time) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp=time;
    }

    public int getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
