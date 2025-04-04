package com.tastytreat.frontend.tasty_treat_express_frontend.models;

import java.util.List;

public class UserDTO {
    private long id;
    private String email;
    private String name;
    private String address;
    private String phoneNumber;
    private String password;
    private List<Long> orderIds;
    private List<Long> feedbackIds;

    public UserDTO(long id, String email, String name, String address, String phoneNumber, List<Long> orderIds,
            List<Long> feedbackIds) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.orderIds = orderIds;
        this.feedbackIds = feedbackIds;
    }


    public UserDTO() {
    }

    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
        }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Long> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<Long> orderIds) {
        this.orderIds = orderIds;
    }

    public List<Long> getFeedbackIds() {
        return feedbackIds;
    }

    public void setFeedbackIds(List<Long> feedbackIds) {
        this.feedbackIds = feedbackIds;
    }

}
