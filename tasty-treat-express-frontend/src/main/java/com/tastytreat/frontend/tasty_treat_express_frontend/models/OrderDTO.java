package com.tastytreat.frontend.tasty_treat_express_frontend.models;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {
    private Long orderId;
    private Long userId;
    private String restaurantId;
    private List<Long> menuItemIds;
    private List<Long> feedbackIds;
    private Double totalAmount;
    private String status;
    private String deliveryAddress;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryTime;
    private Double currentLatitude;
    private Double currentLongitude;
    private String transactionId;

    public OrderDTO(Long orderId, Long userId, String restaurantId, List<Long> menuItemIds, List<Long> feedbackIds,
            Double totalAmount, String status, String deliveryAddress, String paymentStatus, String paymentMethod,
            LocalDateTime orderDate, LocalDateTime deliveryTime, Double currentLatitude, Double currentLongitude,
            String transactionId) {
        this.orderId = orderId;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.menuItemIds = menuItemIds;
        this.feedbackIds = feedbackIds;
        this.totalAmount = totalAmount;
        this.status = status;
        this.deliveryAddress = deliveryAddress;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.orderDate = orderDate;
        this.deliveryTime = deliveryTime;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
        this.transactionId = transactionId;
    }

    public OrderDTO() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public List<Long> getMenuItemIds() {
        return menuItemIds;
    }

    public void setMenuItemIds(List<Long> menuItemIds) {
        this.menuItemIds = menuItemIds;
    }

    public List<Long> getFeedbackIds() {
        return feedbackIds;
    }

    public void setFeedbackIds(List<Long> feedbackIds) {
        this.feedbackIds = feedbackIds;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public Double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

}
