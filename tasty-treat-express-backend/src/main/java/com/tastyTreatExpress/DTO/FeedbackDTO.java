package com.tastyTreatExpress.DTO;

import java.time.LocalDateTime;

public class FeedbackDTO {
    private Long feedbackId;
    private Long userId;
    private String userName;
    private Long orderId;
    private String restaurantId;
    private Long menuItemId;
    private int rating;
    private String comments;
    private LocalDateTime feedbackDate;

    public FeedbackDTO(Long feedbackId, Long userId, String userName, Long orderId, String restaurantId,
            Long menuItemId, int rating,
            String comments, LocalDateTime feedbackDate) {
        this.feedbackId = feedbackId;
        this.userId = userId;
        this.userName = userName;
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.menuItemId = menuItemId;
        this.rating = rating;
        this.comments = comments;
        this.feedbackDate = feedbackDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public FeedbackDTO() {
    }

    public Long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
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

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(LocalDateTime feedbackDate) {
        this.feedbackDate = feedbackDate;
    }

}

// package com.tastyTreatExpress.DTO;

// import java.time.LocalDateTime;

// public class FeedbackDTO {
// private Long feedbackId;
// private Long userId;
// private Long orderId;
// private String restaurantId;
// private Long menuItemId;
// private int rating;
// private String comments;
// private LocalDateTime feedbackDate;

// public FeedbackDTO(Long feedbackId, Long userId, Long orderId, String
// restaurantId, Long menuItemId, int rating,
// String comments, LocalDateTime feedbackDate) {
// this.feedbackId = feedbackId;
// this.userId = userId;
// this.orderId = orderId;
// this.restaurantId = restaurantId;
// this.menuItemId = menuItemId;
// this.rating = rating;
// this.comments = comments;
// this.feedbackDate = feedbackDate;
// }

// public FeedbackDTO() {
// }

// public Long getFeedbackId() {
// return feedbackId;
// }

// public void setFeedbackId(Long feedbackId) {
// this.feedbackId = feedbackId;
// }

// public Long getUserId() {
// return userId;
// }

// public void setUserId(Long userId) {
// this.userId = userId;
// }

// public Long getOrderId() {
// return orderId;
// }

// public void setOrderId(Long orderId) {
// this.orderId = orderId;
// }

// public String getRestaurantId() {
// return restaurantId;
// }

// public void setRestaurantId(String restaurantId) {
// this.restaurantId = restaurantId;
// }

// public Long getMenuItemId() {
// return menuItemId;
// }

// public void setMenuItemId(Long menuItemId) {
// this.menuItemId = menuItemId;
// }

// public int getRating() {
// return rating;
// }

// public void setRating(int rating) {
// this.rating = rating;
// }

// public String getComments() {
// return comments;
// }

// public void setComments(String comments) {
// this.comments = comments;
// }

// public LocalDateTime getFeedbackDate() {
// return feedbackDate;
// }

// public void setFeedbackDate(LocalDateTime feedbackDate) {
// this.feedbackDate = feedbackDate;
// }

// }
