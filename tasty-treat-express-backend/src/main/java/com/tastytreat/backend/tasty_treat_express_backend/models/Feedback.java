package com.tastytreat.backend.tasty_treat_express_backend.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tbl_feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    // @JsonBackReference("feedbackUserReference")
    @JsonIgnoreProperties("feedbacks")
    private User user;

    
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "orderId", nullable = false)
    // @JsonBackReference("feedbackOrderReference")
    @JsonIgnoreProperties("feedbacks")
    private Order orders;

    //@ManyToOne
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", referencedColumnName = "restaurantId", nullable = false)
    // @JsonBackReference("feedbackRestaurantReference")
    @JsonIgnoreProperties("feedbacks")
    private Restaurant restaurant;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", referencedColumnName = "menu_id", nullable = true)
    // @JsonBackReference("feedbackMenuItemReference")
    @JsonIgnoreProperties("feedbacks")
    private MenuItem menuItem;

    private int rating;

    @Column(length = 500)
    private String comments;

    private LocalDateTime feedbackDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Order getOrders() {
        return orders;
    }

    public void setOrders(Order order) {
        this.orders = order;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public MenuItem getMenuItems() {
        return menuItem;
    }

    public void setMenuItems(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(LocalDateTime feedbackDate) {
        this.feedbackDate = feedbackDate;
    }

    public Feedback(User user, Order order, Restaurant restaurant, MenuItem menuItem, int rating, String comments) {
        this.user = user;
        this.orders = order;
        this.restaurant = restaurant;
        this.menuItem = menuItem;
        this.rating = rating;
        this.comments = comments;
    }

    public Feedback() {
    }

    public Feedback(Long feedbackId, User user, Order order, Restaurant restaurant, MenuItem menuItem, int rating,
            String comments, LocalDateTime feedbackDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.feedbackId = feedbackId;
        this.user = user;
        this.orders = order;
        this.restaurant = restaurant;
        this.menuItem = menuItem;
        this.rating = rating;
        this.comments = comments;
        this.feedbackDate = feedbackDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Feedback(Long feedbackId, int rating, String comments, Order order, Restaurant restaurant, MenuItem menuItem,
            User user) {
        this.feedbackId = feedbackId;
        this.rating = rating;
        this.comments = comments;
        this.orders = order;
        this.restaurant = restaurant;
        this.menuItem = menuItem;
        this.user = user;
    }

}
