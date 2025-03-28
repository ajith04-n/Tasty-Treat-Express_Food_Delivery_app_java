package com.tastyTreatExpress.DTO;

import java.util.List;

public class MenuItemDTO {
    private Long menuId;
    private String name;
    private String description;
    private Double price;
    private String category;
    private String imageUrl;
    private Integer quantity;
    private Boolean isAvailable;
    private String status;
    private String restaurantId; 
    private List<Long> orderIds; 
    private List<Long> feedbackIds; 

    public MenuItemDTO(Long menuId, String name, String description, Double price, String category, String imageUrl,
            Integer quantity, Boolean isAvailable, String status, String restaurantId, List<Long> orderIds,
            List<Long> feedbackIds) {
        this.menuId = menuId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.isAvailable = isAvailable;
        this.status = status;
        this.restaurantId = restaurantId;
        this.orderIds = orderIds;
        this.feedbackIds = feedbackIds;
    }

    public MenuItemDTO() {
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
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
