package com.example.Tasty.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "order_table")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double totalPrice;
    private String status;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime orderDate;
    private String paymentMethod;

    // Constructors
    public Order() {
    }

    public Order(double totalPrice, String status, LocalDateTime estimatedDeliveryTime, LocalDateTime orderDate, String paymentMethod) {
        this.totalPrice = totalPrice;
        this.status = status;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.orderDate = orderDate;
        this.paymentMethod = paymentMethod;
    }

    // Getters & Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(LocalDateTime estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                ", estimatedDeliveryTime=" + estimatedDeliveryTime +
                ", orderDate=" + orderDate +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}

    // Utility method to calculate total price from order items
   /* public double calculateTotalPrice() {
        return orderItems.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
    }
*/
   
