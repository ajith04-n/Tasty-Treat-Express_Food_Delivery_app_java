package com.tastytreat.frontend.tasty_treat_express_frontend.models;

import java.time.LocalDateTime;

public class SuccessResponse {
    private String message;
    private LocalDateTime timestamp;
    private String status = "success";
    private UserDTO data;
    private RestaurantDTO resData;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public RestaurantDTO getResData() {
        return resData;
    }

    public void setResData(RestaurantDTO resData) {
        this.resData = resData;
    }

    public UserDTO getData() {
        return data;
    }

    public void setData(UserDTO data) {
        this.data = data;
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
