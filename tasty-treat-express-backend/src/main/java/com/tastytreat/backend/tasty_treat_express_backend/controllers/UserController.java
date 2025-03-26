package com.tastytreat.backend.tasty_treat_express_backend.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.User;
import com.tastytreat.backend.tasty_treat_express_backend.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    
    @PostMapping(value="/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        if (userService.existsByEmail(user.getEmail())) {
            return new ResponseEntity<>("User with this email already exists!", HttpStatus.BAD_REQUEST);
        }
        userService.saveUser(user);
        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }

    
    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session) {
        if (userService.authenticateUser(email, password)) {
            User user = userService.findUserByEmail(email);
            session.setAttribute("user", user);
            session.setAttribute("authenticatedUser", true);
            return new ResponseEntity<>("User authenticated successfully!", HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid email or password!", HttpStatus.UNAUTHORIZED);
    }

    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return new ResponseEntity<>("Logged out successfully!", HttpStatus.NO_CONTENT);
    }

    // Fetch all users
    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Fetch user by ID
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Update user details
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(
            @PathVariable long userId,
            @Valid @RequestBody User user) {
        if (userId != user.getId()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User updatedUser = userService.updateUser(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    // Update user address
    @PutMapping("/{userId}/address")
    public ResponseEntity<String> updateUserAddress(
            @PathVariable long userId,
            @RequestParam String newAddress) {
        try {
            userService.updateUserAddress(userId, newAddress);
            return new ResponseEntity<>("Address updated successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable to update address: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Update user password
    @PutMapping("/{userId}/password")
    public ResponseEntity<String> updateUserPassword(
            @PathVariable long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        try {
            userService.updateUserPassword(userId, oldPassword, newPassword);
            return new ResponseEntity<>("Password updated successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable to update password: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Delete a user
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable long userId) {
        try {
            userService.deleteUser(userId);
            return new ResponseEntity<>("User deleted successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable to delete user: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Generate user order summary report
    @GetMapping("/{userId}/report")
    public ResponseEntity<Map<String, Object>> generateUserOrderReport(@PathVariable long userId) {
        Map<String, Object> report = userService.generateUserOrderSummaryReport(userId);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    // Add feedback for a restaurant
    @PostMapping("/{userId}/feedback/{restaurantId}")
    public ResponseEntity<String> addFeedback(
            @PathVariable long userId,
            @PathVariable String restaurantId,
            @Valid @RequestBody Feedback feedback) {
        try {
            userService.addFeedback(userId, restaurantId, feedback);
            return new ResponseEntity<>("Feedback added successfully!", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable to add feedback: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Fetch all feedback for a user
    @GetMapping("/{userId}/feedback")
    public ResponseEntity<List<Feedback>> getUserFeedback(@PathVariable long userId) {
        List<Feedback> feedback = userService.getUserFeedback(userId);
        return new ResponseEntity<>(feedback, HttpStatus.OK);
    }
}
