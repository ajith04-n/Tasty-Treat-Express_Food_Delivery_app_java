package com.tastytreat.backend.tasty_treat_express_backend.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tastyTreatExpress.DTO.PasswordUpdateRequest;
import com.tastyTreatExpress.DTO.UserDTO;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.*;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.UserNotFoundException;
import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;
import com.tastytreat.backend.tasty_treat_express_backend.models.Report;
import com.tastytreat.backend.tasty_treat_express_backend.models.Restaurant;
import com.tastytreat.backend.tasty_treat_express_backend.models.User;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.FeedbackRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.OrderRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.ReportRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.RestaurantRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.UserRepository;

import jakarta.validation.Valid;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private FeedbackRepository feedbackRepo;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private EmailService emailService;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    // 1. authenticate user
    public boolean authenticateUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user != null && passwordEncoder.matches(password, user.get().getPassword())) {
            return true;
        }
        return false;
    }

    public User findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        System.out.println("User found by email: " + user);
        return user.orElseThrow(() -> new UserNotFoundException("Email not found."));
    }

    public User saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("User with this email already exists!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to save user, please try again later.");
        }
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User updateUser(Long userId, @Valid User userDTO) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        if (userDTO.getName() != null)
            existingUser.setName(userDTO.getName());
        if (userDTO.getEmail() != null)
            existingUser.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() != null)
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        if (userDTO.getPhoneNumber() != null)
            existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        if (userDTO.getAddress() != null)
            existingUser.setAddress(userDTO.getAddress());
        try {
            return userRepository.save(existingUser);
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to update user, please try again later.");
        }
    }

    public void updateUserAddress(long userId, String newAddress) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        userRepository.updateUserAddress(userId, newAddress);
        logger.info("Updated address for User ID: {}", userId);
    }

    public void updateUserPassword(long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Incorrect old password");
        }
        if (newPassword.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters long.");
        }

        // if
        // (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{6,}$"))
        // {
        // throw new RuntimeException(
        // "Password must be at least 6 characters long, include one uppercase letter,
        // one lowercase letter, and one special character.");
        // }
        if (oldPassword.equals(newPassword)) {
            throw new RuntimeException("New password must be different from the old password.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        // userRepository.updateUserPassword(userId, newPassword);
        logger.info("Updated password for User ID: {}", userId);
    }

    public void deleteUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        try {
            userRepository.delete(user);
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to delete user, please try again later.");
        }
    }

    @Override
    public Map<String, Object> generateUserOrderSummaryReport(long userId) {
        List<Order> userOrders = orderRepository.findByUser_Id(userId);

        if (userOrders.isEmpty()) {
            Map<String, Object> report = new HashMap<>();
            report.put("userId", userId);
            report.put("totalOrders", 0);
            report.put("completedOrders", 0);
            report.put("pendingOrders", 0);
            report.put("totalRevenue", 0.0);
            report.put("latestOrderDate", null);
            report.put("averageOrderValue", 0.0);
            report.put("orders", Collections.emptyList());
            return report;
        }
        int totalOrders = userOrders.size();

        int completedOrders = (int) userOrders.stream()
                .filter(order -> "DELIVERED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus()))
                .count();
        int pendingOrders = (int) userOrders.stream()
                .filter(order -> "PLACED".equals(order.getStatus()))
                .count();

        double totalRevenue = userOrders.stream()
                .filter(order -> "DELIVERED".equals(order.getStatus()) || "PLACED".equals(order.getStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();

        LocalDateTime latestOrderDate = userOrders.stream()
                .map(Order::getOrderDate)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        double averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0.0;

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("totalOrders", totalOrders);
        response.put("completedOrders", completedOrders);
        response.put("pendingOrders", pendingOrders);
        response.put("totalRevenue", totalRevenue);
        response.put("latestOrderDate", latestOrderDate);
        response.put("averageOrderValue", averageOrderValue);
        response.put("orders", userOrders);

        User user = userOrders.get(0).getUser();
        List<Report> existingReport = reportRepository.findByUserId(userId);
        if (existingReport.isEmpty()) {
            // Report report = new Report(user, totalOrders, totalRevenue, latestOrderDate);
            Report report = new Report();
            report.setUser(user);
            report.setTotalOrders(totalOrders);
            report.setTotalOrderValue(totalRevenue);
            report.setCreatedAt(latestOrderDate);
            report.setAverageOrderValue(averageOrderValue);
            reportRepository.save(report);
        }

        userOrders.forEach(order -> logger.info("Order ID: {}, Status: {}", order.getOrderId(), order.getStatus()));

        return response;
    }

    public Feedback addFeedback(long userId, String restaurantId, Feedback feedback) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + restaurantId));

        feedback.setUser(user);
        feedback.setRestaurant(restaurant);
        Feedback savedFeedback = feedbackRepo.save(feedback);
        logger.info("Added feedback by User ID: {} for Restaurant ID: {}", userId, restaurantId);
        return savedFeedback;
    }

    @Override
    public List<Feedback> getUserFeedback(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        return user.getFeedbacks();
    }

    @Override
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    public void updateUserPassword(long userId, PasswordUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Old password is incorrect!");
        }

        if (request.getNewPassword().length() < 8) {
            throw new InvalidPasswordException("New password must be at least 8 characters long.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public boolean updateUserPassword2(long userId, PasswordUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Old password is incorrect!");
        }

        if (request.getNewPassword().length() < 8) {
            throw new InvalidPasswordException("New password must be at least 8 characters long.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidPasswordException("New password and confirm password do not match.");
        }

        try {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to update password. Please try again later.");
        }
    }

    // forgot password
    public void forgotPassword(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new InvalidEmailException("Email not found in the system.");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));

        String newPassword = generateSecurePassword();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

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

}
