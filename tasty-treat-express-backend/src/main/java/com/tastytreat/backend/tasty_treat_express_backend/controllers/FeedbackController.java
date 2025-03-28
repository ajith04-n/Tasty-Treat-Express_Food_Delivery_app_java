package com.tastytreat.backend.tasty_treat_express_backend.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tastyTreatExpress.DTO.FeedbackDTO;
import com.tastyTreatExpress.DTO.FeedbackMapper;
import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.services.FeedbackService;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    // Add feedback for a restaurant
    @PostMapping("/{orderId}/{restaurantId}")
    public ResponseEntity<FeedbackDTO> addFeedback(
            @PathVariable Long orderId,
            @PathVariable String restaurantId,
            @RequestBody Feedback feedback) {
        try {
            Feedback addedFeedback = feedbackService.addFeedback(orderId, restaurantId, feedback);
            FeedbackDTO feedbackDTO = FeedbackMapper.toFeedbackDTO(addedFeedback);
            return ResponseEntity.status(HttpStatus.CREATED).body(feedbackDTO);
        } catch (RuntimeException e) {
        	System.out.println("Err"+ e.getMessage().toString() );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Add feedback for a menu item
    @PostMapping("/{orderId}/menu-item/{menuItemId}")
    public ResponseEntity<FeedbackDTO> addMenuItemFeedback(
            @PathVariable Long orderId,
            @PathVariable Long menuItemId,
            @RequestBody Feedback feedback) {
        try {
            Feedback addedFeedback = feedbackService.addMenuItemFeedback(orderId, menuItemId, feedback);
            FeedbackDTO feedbackDTO = FeedbackMapper.toFeedbackDTO(addedFeedback);
            return ResponseEntity.status(HttpStatus.CREATED).body(feedbackDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Update feedback
    @PutMapping("/{feedbackId}")
    public ResponseEntity<FeedbackDTO> updateFeedback(
            @PathVariable Long feedbackId,
            @RequestBody Feedback feedback) {
        try {
            Feedback updatedFeedback = feedbackService.updateFeedback(feedbackId, feedback);
            FeedbackDTO feedbackDTO = FeedbackMapper.toFeedbackDTO(updatedFeedback);
            return ResponseEntity.ok(feedbackDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Delete feedback
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<String> deleteFeedback(@PathVariable Long feedbackId) {
        try {
            feedbackService.deleteFeedback(feedbackId);
            return ResponseEntity.ok("Feedback deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feedback not found.");
        }
    }

    // Get feedback for an order
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbackForOrder(@PathVariable Long orderId) {
        List<Feedback> feedbacks = feedbackService.getFeedbackForOrder(orderId);
        List<FeedbackDTO> feedbackDTOs = feedbacks.stream()
                .map(FeedbackMapper::toFeedbackDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(feedbackDTOs);
    }

    // Get feedback for a restaurant
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbackForRestaurant(@PathVariable String restaurantId) {
        List<Feedback> feedbacks = feedbackService.getFeedbackForRestaurant(restaurantId);
        List<FeedbackDTO> feedbackDTOs = feedbacks.stream()
                .map(FeedbackMapper::toFeedbackDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(feedbackDTOs);
    }

    // Get feedback for a menu item
    @GetMapping("/menu-item/{menuItemId}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbackForMenuItem(@PathVariable Long menuItemId) {
        List<Feedback> feedbacks = feedbackService.getFeedbackForMenuItem(menuItemId);
        List<FeedbackDTO> feedbackDTOs = feedbacks.stream()
                .map(FeedbackMapper::toFeedbackDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(feedbackDTOs);
    }

    // Get feedback for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbackForUser(@PathVariable Long userId) {
        List<Feedback> feedbacks = feedbackService.getFeedbackForUser(userId);
        List<FeedbackDTO> feedbackDTOs = feedbacks.stream()
                .map(FeedbackMapper::toFeedbackDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(feedbackDTOs);
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
    public ResponseEntity<List<FeedbackDTO>> getTopFeedbacksForRestaurant(
            @PathVariable String restaurantId,
            @RequestParam int limit) {
        List<Feedback> topFeedbacks = feedbackService.getTopFeedbacksForRestaurant(restaurantId, limit);
        List<FeedbackDTO> feedbackDTOs = topFeedbacks.stream()
                .map(FeedbackMapper::toFeedbackDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(feedbackDTOs);
    }

    // Filter feedback by rating threshold
    @GetMapping("/filter-by-rating")
    public ResponseEntity<List<FeedbackDTO>> getFeedbackByRatingThreshold(@RequestParam int minRating) {
        List<Feedback> feedbacks = feedbackService.getFeedbackByRatingThreshold(minRating);
        List<FeedbackDTO> feedbackDTOs = feedbacks.stream()
                .map(FeedbackMapper::toFeedbackDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(feedbackDTOs);
    }

    // Filter feedback within date range
    @GetMapping("/filter-by-date")
    public ResponseEntity<List<FeedbackDTO>> getFeedbackWithinDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        List<Feedback> feedbacks = feedbackService.getFeedbackWithinDateRange(start, end);
        List<FeedbackDTO> feedbackDTOs = feedbacks.stream()
                .map(FeedbackMapper::toFeedbackDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(feedbackDTOs);
    }

    // Search feedback by keyword
    @GetMapping("/search")
    public ResponseEntity<List<FeedbackDTO>> searchFeedbackByKeyword(@RequestParam String keyword) {
        List<Feedback> feedbacks = feedbackService.searchFeedbackByKeyword(keyword);
        List<FeedbackDTO> feedbackDTOs = feedbacks.stream()
                .map(FeedbackMapper::toFeedbackDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(feedbackDTOs);
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
