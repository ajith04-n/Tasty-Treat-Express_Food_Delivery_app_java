package com.tastytreat.backend.tasty_treat_express_backend.exceptions;

import java.time.LocalDateTime;

public class SuccessResponse {
    private String message;
    private LocalDateTime timestamp;
    private String status = "success";

    public SuccessResponse(String message, LocalDateTime timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public SuccessResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public SuccessResponse() {
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public SuccessResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
