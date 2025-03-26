package com.tastytreat.backend.tasty_treat_express_backend.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tastytreat.backend.tasty_treat_express_backend.exceptions.BadRequestException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.FeedbackNotFoundException;
import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.services.FeedbackService;


@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    // Add feedback for a restaurant
    @PostMapping("/{orderId}/{restaurantId}")
    public ResponseEntity<Feedback> addFeedback(
            @PathVariable Long orderId,
            @PathVariable String restaurantId,
            @RequestBody Feedback feedback) {
        try {
            Feedback addedFeedback = feedbackService.addFeedback(orderId, restaurantId, feedback);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedFeedback);
        } catch (RuntimeException e) {
            throw new BadRequestException("Failed to add feedback: " + e.getMessage());
        }
    }

    // Add feedback for a menu item
    @PostMapping("/{orderId}/menu-item/{menuItemId}")
    public ResponseEntity<Feedback> addMenuItemFeedback(
            @PathVariable Long orderId,
            @PathVariable Long menuItemId,
            @RequestBody Feedback feedback) {
        try {
            Feedback addedFeedback = feedbackService.addMenuItemFeedback(orderId, menuItemId, feedback);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedFeedback);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Update feedback
    @PutMapping("/{feedbackId}")
    public ResponseEntity<Feedback> updateFeedback(
            @PathVariable Long feedbackId,
            @RequestBody Feedback feedback) {
        try {
            Feedback updatedFeedback = feedbackService.updateFeedback(feedbackId, feedback);
            return ResponseEntity.ok(updatedFeedback);
        } catch (RuntimeException e) {
            throw new FeedbackNotFoundException("Feedback with ID " + feedbackId + " not found.");
        }
    }

    // Delete feedback
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<String> deleteFeedback(@PathVariable Long feedbackId) {
        try {
            feedbackService.deleteFeedback(feedbackId);
            return ResponseEntity.ok("Feedback deleted successfully.");
        } catch (RuntimeException e) {
            throw new FeedbackNotFoundException("Feedback with ID " + feedbackId + " not found.");
        }
    }

    // Get feedback for an order
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Feedback>> getFeedbackForOrder(@PathVariable Long orderId) {
        List<Feedback> feedbacks = feedbackService.getFeedbackForOrder(orderId);
        if (feedbacks == null || feedbacks.isEmpty()) {
            throw new FeedbackNotFoundException("No feedback found for order ID " + orderId);
        }
        return ResponseEntity.ok(feedbacks);
    }

    // Get feedback for a restaurant
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Feedback>> getFeedbackForRestaurant(@PathVariable String restaurantId) {
        List<Feedback> feedbacks = feedbackService.getFeedbackForRestaurant(restaurantId);
        if (feedbacks == null || feedbacks.isEmpty()) {
            throw new FeedbackNotFoundException("No feedback found for restaurant ID " + restaurantId);
        }
        return ResponseEntity.ok(feedbacks);
    }
    
    // Get feedback for a menu item
    @GetMapping("/menu-item/{menuItemId}")
    public ResponseEntity<List<Feedback>> getFeedbackForMenuItem(@PathVariable Long menuItemId) {
        List<Feedback> feedbacks = feedbackService.getFeedbackForMenuItem(menuItemId);
        return ResponseEntity.ok(feedbacks);
    }

    // Get feedback for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Feedback>> getFeedbackForUser(@PathVariable Long userId) {
        List<Feedback> feedbacks = feedbackService.getFeedbackForUser(userId);
        return ResponseEntity.ok(feedbacks);
    }

    // Get average rating for a restaurant
    @GetMapping("/restaurant/{restaurantId}/average-rating")
    public ResponseEntity<Double> calculateAverageRatingForRestaurant(@PathVariable String restaurantId) {
        double averageRating = feedbackService.calculateAverageRatingForRestaurant(restaurantId);
        return ResponseEntity.ok(averageRating);
    }

    // Get average rating for a menu item
    @GetMapping("/menu-item/{menuItemId}/average-rating")
    public ResponseEntity<Double> calculateAverageRatingForMenuItem(@PathVariable Long menuItemId) {
        double averageRating = feedbackService.calculateAverageRatingForMenuItem(menuItemId);
        return ResponseEntity.ok(averageRating);
    }

    // Get top feedbacks for a restaurant
    @GetMapping("/restaurant/{restaurantId}/top-feedbacks")
    public ResponseEntity<List<Feedback>> getTopFeedbacksForRestaurant(
            @PathVariable String restaurantId,
            @RequestParam int limit) {
        List<Feedback> topFeedbacks = feedbackService.getTopFeedbacksForRestaurant(restaurantId, limit);
        return ResponseEntity.ok(topFeedbacks);
    }

    // Filter feedback by rating threshold
    @GetMapping("/filter-by-rating")
    public ResponseEntity<List<Feedback>> getFeedbackByRatingThreshold(@RequestParam int minRating) {
        List<Feedback> feedbacks = feedbackService.getFeedbackByRatingThreshold(minRating);
        return ResponseEntity.ok(feedbacks);
    }

    // Filter feedback within date range
    @GetMapping("/filter-by-date")
    public ResponseEntity<List<Feedback>> getFeedbackWithinDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        List<Feedback> feedbacks = feedbackService.getFeedbackWithinDateRange(start, end);
        return ResponseEntity.ok(feedbacks);
    }

    // Search feedback by keyword
    @GetMapping("/search")
    public ResponseEntity<List<Feedback>> searchFeedbackByKeyword(@RequestParam String keyword) {
        List<Feedback> feedbacks = feedbackService.searchFeedbackByKeyword(keyword);
        return ResponseEntity.ok(feedbacks);
    }

    // Get feedback summary for a restaurant
    @GetMapping("/restaurant/{restaurantId}/summary")
    public ResponseEntity<Map<String, Object>> getFeedbackSummaryForRestaurant(@PathVariable String restaurantId) {
        Map<String, Object> summary = feedbackService.getFeedbackSummaryForRestaurant(restaurantId);
        return ResponseEntity.ok(summary);
    }


    // Get personalized feedback dashboard for user
    @GetMapping("/dashboard/user/{userId}")
    public ResponseEntity<List<Feedback>> getPersonalizedFeedbackDashboard(@PathVariable Long userId) {
        List<Feedback> feedbacks = feedbackService.getPersonalizedFeedbackDashboard(userId);
        return ResponseEntity.ok(feedbacks);
    }

    // Notify restaurant on low rating
    @PutMapping("/notify-restaurant")
    public ResponseEntity<String> notifyRestaurantOnLowRating(@RequestBody Feedback feedback) {
        feedbackService.notifyRestaurantOnLowRating(feedback);
        return ResponseEntity.ok("Notification sent to restaurant.");
    }

    // Thank user for positive feedback
    @PutMapping("/thank-user")
    public ResponseEntity<String> thankUserForPositiveFeedback(@RequestBody Feedback feedback) {
        feedbackService.thankUserForPositiveFeedback(feedback);
        return ResponseEntity.ok("Thank you message sent to user.");
    }

}
