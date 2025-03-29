package com.tastytreat.backend.tasty_treat_express_backend.models;



import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name = "tbl_reports")
@Data
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ElementCollection
    @CollectionTable(name = "feedback_rating_distribution", joinColumns = @JoinColumn(name = "report_id"))
    @MapKeyColumn(name = "rating")
    @Column(name = "count")
    private Map<Integer, Long> feedbackRatingDistribution;

    
    @ElementCollection
    @CollectionTable(name = "menu_item_popularity", joinColumns = @JoinColumn(name = "report_id"))
    @MapKeyColumn(name = "menu_item_name")
    @Column(name = "frequency")
    private Map<String, Long> menuItemPopularity;
    private String bestSellingItem;

    
    private Double averageDeliveryTime;
    private Double estimatedDeliveryTime;
    private Double delayRate;

   
    @ElementCollection
    @CollectionTable(name = "peak_hours", joinColumns = @JoinColumn(name = "report_id"))
    @MapKeyColumn(name = "hour_range")
    @Column(name = "order_count")
    private Map<String, Long> peakHours;
    private Integer outOfStockCount;
   
    
    //@ManyToOne(cascade = CascadeType.ALL)
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
   // @JsonBackReference("reportUserReference")
   @JsonIgnoreProperties("reports")
    private User user;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
   // @JsonBackReference("restaurantReportReference")
    @JsonIgnoreProperties("reports")
    private Restaurant restaurant;

    private String reportType;

    @CreationTimestamp
    private LocalDateTime createdAt;

  
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

    public LocalDateTime getLatestOrderDate() {
        return latestOrderDate;
    }
    public void setLatestOrderDate(LocalDateTime latestOrderDate) {
        this.latestOrderDate = latestOrderDate;
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

    public Map<Integer, Long> getFeedbackRatingDistribution() {
        return feedbackRatingDistribution;
    }

    public void setFeedbackRatingDistribution(Map<Integer, Long> feedbackRatingDistribution) {
        this.feedbackRatingDistribution = feedbackRatingDistribution;
    }

    public Map<String, Long> getMenuItemPopularity() {
        return menuItemPopularity;
    }

    public void setMenuItemPopularity(Map<String, Long> menuItemPopularity) {
        this.menuItemPopularity = menuItemPopularity;
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

    public Map<String, Long> getPeakHours() {
        return peakHours;
    }

    public void setPeakHours(Map<String, Long> peakHours) {
        this.peakHours = peakHours;
    }

    public Integer getOutOfStockCount() {
        return outOfStockCount;
    }

    public void setOutOfStockCount(Integer outOfStockCount) {
        this.outOfStockCount = outOfStockCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Report(LocalDate startDate, LocalDate endDate, LocalDateTime generatedAt, Integer totalOrders,
            Integer completedOrders, Integer pendingOrders, Integer cancelledOrders, Double totalOrderValue,
            Double averageOrderValue, Integer uniqueCustomers, Double repeatCustomerRate, Integer newCustomers,
            Double averageFeedbackRating, Integer positiveFeedbackCount, Integer negativeFeedbackCount,
            Map<Integer, Long> feedbackRatingDistribution, Map<String, Long> menuItemPopularity, String bestSellingItem,
            Double averageDeliveryTime, Double estimatedDeliveryTime, Double delayRate,
            Map<String, Long> peakHours, String reportType, User user, Restaurant restaurant) {

        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedAt = generatedAt;
        this.totalOrders = totalOrders;
        this.completedOrders = completedOrders;
        this.pendingOrders = pendingOrders;
        this.cancelledOrders = cancelledOrders;
        this.totalOrderValue = totalOrderValue;
        this.averageOrderValue = averageOrderValue;
        this.uniqueCustomers = uniqueCustomers;
        this.repeatCustomerRate = repeatCustomerRate;
        this.newCustomers = newCustomers;
        this.averageFeedbackRating = averageFeedbackRating;
        this.positiveFeedbackCount = positiveFeedbackCount;
        this.negativeFeedbackCount = negativeFeedbackCount;
        this.feedbackRatingDistribution = feedbackRatingDistribution;
        this.menuItemPopularity = menuItemPopularity;
        this.bestSellingItem = bestSellingItem;
        this.averageDeliveryTime = averageDeliveryTime;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.delayRate = delayRate;
        this.peakHours = peakHours;
        this.reportType = reportType;
        this.user = user;
        this.restaurant = restaurant;
    }

    public Report(User user, Integer totalOrders, Double totalOrderValue, LocalDateTime latestOrderDate) {
        this.user = user;
        this.totalOrders = totalOrders;
        this.totalOrderValue = totalOrderValue;
        this.latestOrderDate = latestOrderDate;
    }

    public Report() {
        //TODO Auto-generated constructor stub
    }

   
}
