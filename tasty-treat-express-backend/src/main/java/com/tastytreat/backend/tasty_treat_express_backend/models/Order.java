package com.tastytreat.backend.tasty_treat_express_backend.models;

import java.sql.Driver;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_orders")
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "orderId")

public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	 @JsonProperty("id")
	private Long orderId;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id", referencedColumnName = "user_id")
	@JsonBackReference("orderUserReference")
	private User customer;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "restaurant_id", referencedColumnName = "restaurantId")
	@JsonBackReference("orderRestaurantReference")
	private Restaurant restaurant;
/*
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name = "menu_order_map", joinColumns = @JoinColumn(name = "order_id", 
	referencedColumnName = "orderId"), inverseJoinColumns = @JoinColumn(name = "menu_id", referencedColumnName = "menu_id"))
	//@JsonManagedReference("order-menu")
	// @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "orderId")
	@JsonIgnoreProperties()
	private List<MenuItem> menuItems;
*/
	
	@OneToMany(mappedBy = "orders", cascade = CascadeType.ALL,orphanRemoval = true)
	//@JsonManagedReference("orderMenuReference") 
	private List<MenuItem> menuItems;
	
	// Add One-to-Many relationship with Feedback
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Feedback> feedbacks = new ArrayList<>();

	private Double totalAmount;

	private String status; // Pending, Confirmed, Preparing, Delivered

	private String deliveryAddress;

	private String paymentStatus; // Paid, Pending

	private String paymentMethod; // Credit Card, Cash on Delivery

	private LocalDateTime orderDate;

	private LocalDateTime deliveryTime;
	private String mostOrderedItem;

	private Double currentLatitude;
	private Double currentLongitude;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	private String transactionId;

	// @ManyToOne
	// @JoinColumn(name = "driver_id", referencedColumnName = "driverId")
	// private Driver driver;

	
	public String getTransactionId() {
		return transactionId;
	}


	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}



	public Double getCurrentLatitude() {
		return currentLatitude;
	}



	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public void setCurrentLatitude(Double currentLatitude) {
		this.currentLatitude = currentLatitude;
	}

	public Double getCurrentLongitude() {
		return currentLongitude;
	}

	public void setCurrentLongitude(Double currentLongitude) {
		this.currentLongitude = currentLongitude;
	}

	public String getMostOrderedItem() {
		return mostOrderedItem;
	}

	public void setMostOrderedItem(String mostOrderedItem) {
		this.mostOrderedItem = mostOrderedItem;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public User getCustomer() {
		return customer;
	}

	public void setCustomer(User customer) {
		this.customer = customer;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	public List<MenuItem> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(List<MenuItem> menuItems) {
		this.menuItems = menuItems;
	}

	// Add getter and setter for feedbacks
	public List<Feedback> getFeedbacks() {
		return feedbacks;
	}

	public void setFeedbacks(List<Feedback> feedbacks) {
		this.feedbacks = feedbacks;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
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

	@Override
	public String toString() {
		return "Order [orderId=" + orderId + ", customer=" + customer + ", restaurant=" + restaurant + ", menuItems="
				+ menuItems + ", totalAmount=" + totalAmount + ", status=" + status + ", deliveryAddress="
				+ deliveryAddress + ", paymentStatus=" + paymentStatus + ", paymentMethod=" + paymentMethod
				+ ", orderDate=" + orderDate + ", deliveryTime=" + deliveryTime + "]";
	}

	public Order(User customer, Restaurant restaurant, List<MenuItem> menuItems, Double totalAmount,
			String status,
			String deliveryAddress, String paymentStatus, String paymentMethod, LocalDateTime orderDate,
			LocalDateTime deliveryTime) {
		super();
		this.customer = customer;
		this.restaurant = restaurant;
		this.menuItems = menuItems;
		this.totalAmount = totalAmount;
		this.status = status;
		this.deliveryAddress = deliveryAddress;
		this.paymentStatus = paymentStatus;
		this.paymentMethod = paymentMethod;
		this.orderDate = orderDate;
		this.deliveryTime = deliveryTime;
	}

	public Order() {
		// TODO Auto-generated constructor stub
	}
}