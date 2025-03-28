package com.tastyTreatExpress.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReportDTO {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime generatedAt;
    private Integer totalOrders;
    private Integer completedOrders;
    private Integer pendingOrders;
    private Integer cancelledOrders;
    private Double totalOrderValue;
    private Double averageOrderValue;
    private LocalDateTime latestOrderDate;
    private Integer uniqueCustomers;
    private Double repeatCustomerRate;
    private Integer newCustomers;
    private Double averageFeedbackRating;
    private Integer positiveFeedbackCount;
    private Integer negativeFeedbackCount;
    private String bestSellingItem;
    private Double averageDeliveryTime;
    private Double estimatedDeliveryTime;
    private Double delayRate;
    private Integer outOfStockCount;
    private Long userId; // Reference to the user
    private String restaurantId; // Reference to the restaurant

    public ReportDTO(Long id, LocalDate startDate, LocalDate endDate, LocalDateTime generatedAt, Integer totalOrders,
            Integer completedOrders, Integer pendingOrders, Integer cancelledOrders, Double totalOrderValue,
            Double averageOrderValue, LocalDateTime latestOrderDate, Integer uniqueCustomers,
            Double repeatCustomerRate, Integer newCustomers, Double averageFeedbackRating,
            Integer positiveFeedbackCount, Integer negativeFeedbackCount, String bestSellingItem,
            Double averageDeliveryTime, Double estimatedDeliveryTime, Double delayRate, Integer outOfStockCount,
            Long userId, String restaurantId) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedAt = generatedAt;
        this.totalOrders = totalOrders;
        this.completedOrders = completedOrders;
        this.pendingOrders = pendingOrders;
        this.cancelledOrders = cancelledOrders;
        this.totalOrderValue = totalOrderValue;
        this.averageOrderValue = averageOrderValue;
        this.latestOrderDate = latestOrderDate;
        this.uniqueCustomers = uniqueCustomers;
        this.repeatCustomerRate = repeatCustomerRate;
        this.newCustomers = newCustomers;
        this.averageFeedbackRating = averageFeedbackRating;
        this.positiveFeedbackCount = positiveFeedbackCount;
        this.negativeFeedbackCount = negativeFeedbackCount;
        this.bestSellingItem = bestSellingItem;
        this.averageDeliveryTime = averageDeliveryTime;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.delayRate = delayRate;
        this.outOfStockCount = outOfStockCount;
        this.userId = userId;
        this.restaurantId = restaurantId;
    }

    public ReportDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Integer getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(Integer completedOrders) {
        this.completedOrders = completedOrders;
    }

    public Integer getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(Integer pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public Integer getCancelledOrders() {
        return cancelledOrders;
    }

    public void setCancelledOrders(Integer cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }

    public Double getTotalOrderValue() {
        return totalOrderValue;
    }

    public void setTotalOrderValue(Double totalOrderValue) {
        this.totalOrderValue = totalOrderValue;
    }

    public Double getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(Double averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }

    public LocalDateTime getLatestOrderDate() {
        return latestOrderDate;
    }

    public void setLatestOrderDate(LocalDateTime latestOrderDate) {
        this.latestOrderDate = latestOrderDate;
    }

    public Integer getUniqueCustomers() {
        return uniqueCustomers;
    }

    public void setUniqueCustomers(Integer uniqueCustomers) {
        this.uniqueCustomers = uniqueCustomers;
    }

    public Double getRepeatCustomerRate() {
        return repeatCustomerRate;
    }

    public void setRepeatCustomerRate(Double repeatCustomerRate) {
        this.repeatCustomerRate = repeatCustomerRate;
    }

    public Integer getNewCustomers() {
        return newCustomers;
    }

    public void setNewCustomers(Integer newCustomers) {
        this.newCustomers = newCustomers;
    }

    public Double getAverageFeedbackRating() {
        return averageFeedbackRating;
    }

    public void setAverageFeedbackRating(Double averageFeedbackRating) {
        this.averageFeedbackRating = averageFeedbackRating;
    }

    public Integer getPositiveFeedbackCount() {
        return positiveFeedbackCount;
    }

    public void setPositiveFeedbackCount(Integer positiveFeedbackCount) {
        this.positiveFeedbackCount = positiveFeedbackCount;
    }

    public Integer getNegativeFeedbackCount() {
        return negativeFeedbackCount;
    }

    public void setNegativeFeedbackCount(Integer negativeFeedbackCount) {
        this.negativeFeedbackCount = negativeFeedbackCount;
    }

    public String getBestSellingItem() {
        return bestSellingItem;
    }

    public void setBestSellingItem(String bestSellingItem) {
        this.bestSellingItem = bestSellingItem;
    }

    public Double getAverageDeliveryTime() {
        return averageDeliveryTime;
    }

    public void setAverageDeliveryTime(Double averageDeliveryTime) {
        this.averageDeliveryTime = averageDeliveryTime;
    }

    public Double getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(Double estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public Double getDelayRate() {
        return delayRate;
    }

    public void setDelayRate(Double delayRate) {
        this.delayRate = delayRate;
    }

    public Integer getOutOfStockCount() {
        return outOfStockCount;
    }

    public void setOutOfStockCount(Integer outOfStockCount) {
        this.outOfStockCount = outOfStockCount;
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
    
    
}
