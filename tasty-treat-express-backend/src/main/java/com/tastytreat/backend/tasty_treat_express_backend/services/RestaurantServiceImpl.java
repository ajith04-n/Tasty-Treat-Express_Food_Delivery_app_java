package com.tastytreat.backend.tasty_treat_express_backend.services;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tastyTreatExpress.DTO.PasswordUpdateRequest;
import com.tastyTreatExpress.DTO.RestaurantDTO;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.DatabaseOperationException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.InvalidEmailException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.InvalidInputException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.InvalidPasswordException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.RestaurantNotFoundException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.UserNotFoundException;
import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;
import com.tastytreat.backend.tasty_treat_express_backend.models.Report;
import com.tastytreat.backend.tasty_treat_express_backend.models.Restaurant;
import com.tastytreat.backend.tasty_treat_express_backend.models.User;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.MenuItemRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.RestaurantRepository;

import jakarta.validation.Valid;

@Service
public class RestaurantServiceImpl implements RestaurantService {
    @Autowired
    RestaurantRepository restaurantRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private EmailService emailService;

    public Restaurant saveRestaurant(Restaurant restaurant) {
        if (restaurant.getPassword() == null || restaurant.getPassword().isEmpty()) {
            throw new InvalidInputException("Password cannot be empty.");
        }
        if (restaurant.getName() == null || restaurant.getName().isEmpty()) {
            throw new InvalidInputException("Name cannot be empty.");
        }

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
        Restaurant existingRestaurant = restaurantRepository.findById(restaurant.getRestaurantId()).orElse(null);
        if (existingRestaurant == null) {
            throw new RestaurantNotFoundException("Restaurant with ID " + restaurant.getRestaurantId() + " not found");
        }
        existingRestaurant.setName(restaurant.getName());
        existingRestaurant.setEmail(restaurant.getEmail());
        existingRestaurant.setPassword(restaurant.getPassword());
        return restaurantRepository.save(existingRestaurant);
    }

    public Restaurant updateRestaurant(String restaurantId, Restaurant restaurantDTO) {
        Restaurant existingRestaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(
                        () -> new RestaurantNotFoundException("Restaurant with ID " + restaurantId + " not found"));

        if (restaurantDTO.getName() != null)
            existingRestaurant.setName(restaurantDTO.getName());
        if (restaurantDTO.getEmail() != null)
            existingRestaurant.setEmail(restaurantDTO.getEmail());
        if (restaurantDTO.getAddress() != null)
            existingRestaurant.setAddress(restaurantDTO.getAddress());
        if (restaurantDTO.getDescription() != null)
            existingRestaurant.setDescription(restaurantDTO.getDescription());
        if (restaurantDTO.getPhoneNumber() != null) {
            existingRestaurant.setPhoneNumber(restaurantDTO.getPhoneNumber());
        }
        if (restaurantDTO.getRating() != 0) {
            existingRestaurant.setRating(restaurantDTO.getRating());
        }
        if (restaurantDTO.getLatitude() != 0) {
            existingRestaurant.setLatitude(restaurantDTO.getLatitude());
        }
        if (restaurantDTO.getLongitude() != 0) {
            existingRestaurant.setLongitude(restaurantDTO.getLongitude());
        }
        try {
            return restaurantRepository.save(existingRestaurant);
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to update restaurant details.");
        }
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

    public List<MenuItem> getRestaurantMenu(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);
        if (restaurant != null) {
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
        if (restaurant != null) {
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

    public void forgotPassword(String email) {
        if (!restaurantRepository.existsByEmail(email)) {
            throw new InvalidEmailException("Email not found in the system.");
        }
        Restaurant user = restaurantRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("Restaurant not found with email: " + email);
        }
        String newPassword = generateSecurePassword();
        System.out.println(newPassword);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(newPassword));
        restaurantRepository.save(user);

        String emailContent = buildPasswordResetEmail(newPassword);
        emailService.sendHtmlMessage(email, "Reset-Password", emailContent);
    }

    private String generateSecurePassword() {
        SecureRandom secureRandom = new SecureRandom();
        int password = 10000000 + secureRandom.nextInt(90000000);
        return String.valueOf(password);
    }

    private String buildPasswordResetEmail(String newPassword) {
        return "<html><body>" +
                "<h3>Password Reset Request</h3>" +
                "<p>Your new temporary password is: <strong>" + newPassword + "</strong></p>" +
                "<p>Please use this password to log in, and change it as soon as possible after logging in.</p>" +
                "<p>If you did not request a password reset, please ignore this email.</p>" +
                "<p>Thank you,<br/>Tasty Treat Team</p>" +
                "</body></html>";
    }

    public void updateUserPassword(String restaurantId, PasswordUpdateRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new UserNotFoundException("Restaurant with ID " + restaurantId + " not found"));

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(request.getOldPassword(), restaurant.getPassword())) {
            throw new InvalidPasswordException("Old password is incorrect!");
        }

        if (request.getNewPassword().length() < 8) {
            throw new InvalidPasswordException("New password must be at least 8 characters long.");
        }

        restaurant.setPassword(passwordEncoder.encode(request.getNewPassword()));
        restaurantRepository.save(restaurant);
    }
}