package com.tastytreat.frontend.tasty_treat_express_frontend.models;


import java.util.List;

public class RestaurantDTO {
    private String restaurantId;
    private String name;
    private String address;
    private String description;
    private String email;
    private String phoneNumber;
    private double latitude;
    private double longitude;
    private double rating;
    private List<Long> menuItemIds;
    private List<Long> orderIds;
    private List<Long> feedbackIds;
    private List<Long> reportIds;
    private String password;

    public RestaurantDTO(String restaurantId, String name, String address, String description, String email,
            String phoneNumber, double latitude, double longitude, double rating, List<Long> menuItemIds,
            List<Long> orderIds, List<Long> feedbackIds, List<Long> reportIds) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.address = address;
        this.description = description;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.menuItemIds = menuItemIds;
        this.orderIds = orderIds;
        this.feedbackIds = feedbackIds;
        this.reportIds = reportIds;
    }

    public RestaurantDTO() {
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<Long> getMenuItemIds() {
        return menuItemIds;
    }

    public void setMenuItemIds(List<Long> menuItemIds) {
        this.menuItemIds = menuItemIds;
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

    public List<Long> getReportIds() {
        return reportIds;
    }

    public void setReportIds(List<Long> reportIds) {
        this.reportIds = reportIds;
    }

}
