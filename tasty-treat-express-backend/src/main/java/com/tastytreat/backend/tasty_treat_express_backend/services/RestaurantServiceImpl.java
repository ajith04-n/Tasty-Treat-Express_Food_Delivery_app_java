package com.tastytreat.backend.tasty_treat_express_backend.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;
import com.tastytreat.backend.tasty_treat_express_backend.models.Report;
import com.tastytreat.backend.tasty_treat_express_backend.models.Restaurant;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.MenuItemRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.RestaurantRepository;

@Service
public class RestaurantServiceImpl implements RestaurantService {
	@Autowired
	RestaurantRepository restaurantRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;
	
    public Restaurant saveRestaurant(Restaurant restaurant) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(restaurant.getPassword());
        restaurant.setPassword(hashedPassword); 
        return restaurantRepository.save(restaurant);
    }

    public boolean authenticateRestaurant(String email, String password) {
        Restaurant restaurant = restaurantRepository.findByEmail(email);
        if (restaurant != null) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            return encoder.matches(password, restaurant.getPassword()); 
        }
        return false;
    }

    public boolean existsByEmail(String email) {
        return restaurantRepository.existsByEmail(email);
    }

    public Restaurant findByEmail(String email) {
        return restaurantRepository.findByEmail(email);
    }

    public Restaurant updateRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    public Restaurant getRestaurantById(String restaurantId) {
        return restaurantRepository.findById(restaurantId).orElse(null);
    }
    @Override
    public void deleteRestaurant(String restaurantId) {
        restaurantRepository.deleteById(restaurantId);
    }

    // methods i updated later
    public List<MenuItem> getRestaurantMenu(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);
        if (restaurant!= null) {
            return restaurant.getMenu();
        } else {
            throw new ObjectNotFoundException("Restaurant not found", Restaurant.class);
        }
    }

    @Override
    public List<Restaurant> findRestaurantsByLocation(String location) {
            return restaurantRepository.findByLocation(location);
    }

    public List<MenuItem> addMenuItem(String restaurantId, MenuItem menuItem) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);
        if (restaurant != null) {
            List<MenuItem> menu = restaurant.getMenu();
            menu.add(menuItem);
            restaurant.setMenu(menu);
            restaurantRepository.save(restaurant);
            return menu;
        } else {
            throw new ObjectNotFoundException("Restaurant not found", Restaurant.class);
        }
    }

    public List<Feedback> getRestaurantFeedback(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);
        if (restaurant!= null) {
            return restaurant.getFeedbacks();
        } else {
            throw new ObjectNotFoundException("Restaurant not found", Restaurant.class);
        }
    }


    public List<Feedback> addFeedback(String restaurantId, Feedback feedback) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);
        if (restaurant != null) {
            List<Feedback> feedbacks = restaurant.getFeedbacks();
            feedbacks.add(feedback);
            restaurant.setFeedbacks(feedbacks);
            restaurantRepository.save(restaurant);
            return feedbacks;
        } else {
            throw new ObjectNotFoundException("Restaurant not found", Restaurant.class);
        }
    }

    public double calculateAverageRating(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + restaurantId));
        List<Feedback> feedbacks = restaurant.getFeedbacks();

        return feedbacks.stream()
                .mapToDouble(value -> Math.floor(value.getRating()))
                .average()
                .orElse(0.0);
    }

    public List<Feedback> getRestaurantFeedbacks(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + restaurantId));
        return restaurant.getFeedbacks();
    }

    public List<Restaurant> findRestaurantsNearby(double userLat, double userLon, double radiusKm) {
        return restaurantRepository.findAll().stream()
                .filter(restaurant -> {
                    double dist = calculateDistance(userLat, userLon,
                            restaurant.getLatitude(), restaurant.getLongitude());
                    return dist <= radiusKm;
                })
                .collect(Collectors.toList());
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371; 
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    @Override
    public List<Order> getRestaurantOrders(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + restaurantId));
        return restaurant.getOrders();
        }

    @Override
    public List<Report> getRestaurantReport(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + restaurantId));
        return restaurant.getReports();
        }

    @Override
    public boolean existsById(String restaurantId) {
        return restaurantRepository.existsById(restaurantId);
    }

}