package com.tastytreat.backend.tasty_treat_express_backend.services;

import java.util.List;

import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;
import com.tastytreat.backend.tasty_treat_express_backend.models.Report;
import com.tastytreat.backend.tasty_treat_express_backend.models.Restaurant;

import jakarta.validation.Valid;

public interface RestaurantService {
	public Restaurant saveRestaurant(Restaurant restaurant);
	public boolean authenticateRestaurant(String email, String password);
	public Restaurant findByEmail(String email);
	public boolean existsByEmail(String email);
	public List<Restaurant> findAll();
	public Restaurant getRestaurantById(String restaurantId);
	public Restaurant updateRestaurant(@Valid Restaurant restaurant);  
	public Restaurant updateRestaurant(String restaurantId,@Valid Restaurant restaurant);

	
	 // - newAddional methods -
	public void deleteRestaurant(String restaurantId);
	public List<MenuItem> getRestaurantMenu(String restaurantId);
	public List<Restaurant> findRestaurantsByLocation(String location);
	public List<Feedback> getRestaurantFeedback(String restaurantId);
	public List<Order> getRestaurantOrders(String restaurantId); 
	
	public List<Report> getRestaurantReport(String restaurantId);
	public double calculateAverageRating(String restaurantId);
	public List<Feedback> getRestaurantFeedbacks(String restaurantId);
	public List<Feedback> addFeedback(String restaurantId, Feedback feedback);
	public List<MenuItem> addMenuItem(String restaurantId, MenuItem menuItem);
	public List<Restaurant> findRestaurantsNearby(double userLat, double userLon, double radiusKm);
    public boolean existsById(String restaurantId);
   

	
	
	
}
