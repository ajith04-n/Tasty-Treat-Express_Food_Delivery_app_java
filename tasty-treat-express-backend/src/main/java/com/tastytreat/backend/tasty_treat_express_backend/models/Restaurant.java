package com.tastytreat.backend.tasty_treat_express_backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name="tbl_restaurant")
public class Restaurant {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID) 
	private String restaurantId;

	
    @Size(min = 3, max = 100, message = "Restaurant name must be between 3 and 100 characters")
	@Column(name = "restaurant_name", nullable = false, length = 100) 
	private String name;

	
    @Size(min = 10, max = 250, message = "Restaurant address must be between 10 and 250 characters")
	@Column(name = "restaurant_address", nullable = false, length = 250) 
	private String address;

	@Size(max = 500, message = "Description must not exceed 500 characters")
	@Column(name = "restaurant_description", length = 500) 
	private String description;

	
	@Email(message = "Email should be valid")
	@Size(max = 100, message = "Email must not exceed 100 characters")
	@Column(name = "email", nullable = false, unique = true, length = 100)
	private String email; 

	
    @Size(min = 6, message = "Password must be at least 6 characters long")
	@Column(name = "password", length = 100)
	//@JsonIgnore()
	private String password;

	 
	 @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
	 @Column(name = "phone_number", nullable = false, length = 10) 
	 private String phoneNumber;

	 												// mappedBy - used in the parent entity
	 @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
	// @JsonManagedReference("menuItemRestaurantReference") // It's typically used on the "one" side of a @OneToMany relationship to avoid infinite loops between related entities.
	@JsonIgnoreProperties("restaurant")
	private List<MenuItem> menu = new ArrayList<>();

	@OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL,orphanRemoval = true)
	// @JsonManagedReference("orderRestaurantReference") 
	@JsonIgnoreProperties("restaurant")
	private List<Order> orders = new ArrayList<>();

	@OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
	//@JsonManagedReference("feedbackRestaurantReference")
	@JsonIgnoreProperties("restaurant")
	private List<Feedback> feedbacks = new ArrayList<>();

	@OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
	//@JsonManagedReference("restaurantReportReference")
	@JsonIgnoreProperties("restaurant")
	private List<Report> reports = new ArrayList<>();

	private String location;

    public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	@Min(-90)
    @Max(90)
	private double latitude;
	
	@Min(-180)
	@Max(180)
	private double longitude;

	@CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

	@Column(nullable = false, columnDefinition = "double default 0")
	private double rating;


	
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
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public List<Order> getOrders() {
		return orders;
	}
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
	public List<Feedback> getFeedbacks() {
		return feedbacks;
	}
	public void setFeedbacks(List<Feedback> feedbacks) {
		this.feedbacks = feedbacks;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	


	public Restaurant() {
	}
	public List<MenuItem> getMenu() {
		return menu;
	}
	public void setMenu(List<MenuItem> menu) {
		this.menu = menu;
	}
	public Restaurant(String name, String address, String description, String email, String password,String phoneNum) {
		this.name = name;
		this.address = address;
		this.description = description;
		this.email = email;
		this.password = password;
		this.phoneNumber= phoneNum;
		}
	public Restaurant(
			 String name,
			 String address,
			 String description,
			 String email,
		     String password,
			 String phoneNumber,
			List<MenuItem> menu) {
		super();
		this.name = name;
		this.address = address;
		this.description = description;
		this.email = email;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.menu = menu;
	}
	public String getRestaurantId() {
		return restaurantId;
	}
	public void setRestaurantId(String restaurantId) {
		this.restaurantId = restaurantId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}


	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public List<Report> getReports() {
		return reports;
	}
	public void setReports(List<Report> reports) {
		this.reports = reports;
	}
    public Restaurant orElseThrow(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'orElseThrow'");
    }
	
}

