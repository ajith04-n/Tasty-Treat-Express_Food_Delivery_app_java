package com.example.frontend.model;

import java.io.Serializable;


import java.util.ArrayList;
import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String name;

    @Column(nullable = false)
    private String password;

    private String address;

    private String phoneNumber;

   
    
    

	// Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public User() {
    }

    public User(long id, String email, String name, String password, String address, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
    

    

    @Override
    public String toString() {
        return "User [id=" + id + ", email=" + email + ", name=" + name + ", password=" + password + ", address="
                + address + ", phoneNumber=" + phoneNumber + "]";
    }
}

    /*public List<Order> getOrders() {
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
    public List<Report> getReports() {
		return reports;
	}

	public void setReports(List<Report> reports) {
		this.reports = reports;
	}
*/
/* @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonIgnoreProperties("user") // Prevents infinite recursion during serialization
private List<Order> orders = new ArrayList<>();


@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonIgnoreProperties("user")
private List<Feedback> feedbacks= new ArrayList<>(); ; // One-to-many relationship with Feedback

@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonIgnoreProperties("user") // Prevents infinite recursion during serialization
private List<Report>  reports= new ArrayList<>();

*/
   