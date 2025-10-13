package com.tastytreat.backend.tasty_treat_express_backend.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_menu")
public class MenuItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "menu_id")
	private Long menuId;

	@Size(min = 1, max = 100, message = "Name should be between 1 and 100 characters")
	private String name;

	@Size(max = 255, message = "Description cannot exceed 255 characters")
	private String description;

	@DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
	private Double price;

	@Size(max = 50, message = "Category cannot exceed 50 characters")
	private String category;

	private String imageUrl;

	@Min(value = 0, message = "Quantity cannot be negative")
	private Integer quantity;

	private Boolean isAvailable = true;

	@Pattern(regexp = "^(Popular|Regular|LowDemand)$", message = "Status must be one of Popular, Regular, or LowDemand")
	private String status;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinColumn( // we use @JoinColumn to define or customize foreign key columns in the child
					// entity.
			name = "restaurant_id", // The name of the column in the current entity (child table) that stores the
									// foreign key.
			referencedColumnName = "restaurantId", // The column name in the referenced entity (parent table) that the
													// foreign key points to.
			nullable = false // It specifies whether the foreign key column can accept NULL values.
	)
	// @JsonBackReference("menuItemRestaurantReference") // It's typically used on
	// the "many" side of a @OneToMany
	// relationship to avoid infinite loops between related
	@JsonIgnoreProperties("menu") // entities.
	private Restaurant restaurant;

	// @ManyToMany(mappedBy = "menuItems", fetch = FetchType.LAZY, cascade = {
	// CascadeType.PERSIST, CascadeType.MERGE })
	@ManyToMany(mappedBy = "menuItems", cascade = CascadeType.REMOVE)
	// @JsonBackReference("order-menu")
	// @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
	// property = "id")
	@JsonIgnoreProperties("menuItems")
	private List<Order> orders;

	// Add relationship with Feedback
	@OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JsonIgnoreProperties("menuItem")
	private List<Feedback> feedbacks = new ArrayList<>();

	public Long getId() {
		return menuId;
	}

	public void setId(Long id) {
		this.menuId = id;
	}

	public Long getMenuId() {
		return menuId;
	}

	public void setMenuId(Long menuId) {
		this.menuId = menuId;
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

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
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

	// Add getter and setter for status
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	// Add getter and setter for feedbacks
	public List<Feedback> getFeedbacks() {
		return feedbacks;
	}

	public void setFeedbacks(List<Feedback> feedbacks) {
		this.feedbacks = feedbacks;
	}

	public MenuItem(
			String name,
			String description,
			Double price,
			String category, String imageUrl,
			Integer quantity) {

		this.name = name;
		this.description = description;
		this.price = price;
		this.category = category;
		this.imageUrl = imageUrl;
		this.quantity = quantity;
	}

	public MenuItem(
			@Size(min = 1, max = 100, message = "Name should be between 1 and 100 characters") String name,
			@Size(max = 255, message = "Description cannot exceed 255 characters") String description,
			Double price,
			String category, String imageUrl,
			@Min(value = 0, message = "Quantity cannot be negative") Integer quantity,
			Boolean isAvailable, Restaurant restaurant) {
		super();
		this.name = name;
		this.description = description;
		this.price = price;
		this.category = category;
		this.imageUrl = imageUrl;
		this.quantity = quantity;
		this.isAvailable = isAvailable;
		this.restaurant = restaurant;
	}

	public MenuItem() {
		super();
	}

	public MenuItem(Long menuId, String name, Double price) {
		this.menuId = menuId;
		this.name = name;
		this.price = price;
	}

}