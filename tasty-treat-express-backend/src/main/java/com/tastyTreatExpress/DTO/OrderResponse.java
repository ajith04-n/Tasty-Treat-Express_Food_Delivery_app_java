package com.tastyTreatExpress.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
//@AllArgsConstructor
public class OrderResponse {
    private Long orderId; //  order ID
    private String orderName;
    private Double totalAmount;

    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    public OrderResponse(Long id, String orderName, Double totalAmount, LocalDateTime orderDate, String status, String restaurantName) {
        this.orderId = id;
        this.orderName = orderName;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.status = status;
        this.restaurantName = restaurantName;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    private LocalDateTime orderDate;
    private String status;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    private String restaurantName; // Restaurant name

}
