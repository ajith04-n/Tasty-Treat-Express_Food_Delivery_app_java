package com.tastytreat.backend.tasty_treat_express_backend.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tastyTreatExpress.DTO.FeedbackDTO;
import com.tastyTreatExpress.DTO.FeedbackMapper;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.InvalidInputException;
import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.services.FeedbackService;
import com.tastytreat.backend.tasty_treat_express_backend.services.FeedbackServiceImpl;
import com.tastytreat.backend.tasty_treat_express_backend.services.MenuItemService;
import com.tastytreat.backend.tasty_treat_express_backend.services.RestaurantServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin(origins = "http://localhost:9213")
@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackServiceImpl feedbackService;

    @Autowired
    private RestaurantServiceImpl restaurantService;

    @Autowired
    private MenuItemService menuItemService;

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
            System.out.println("Err" + e.getMessage().toString());
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
        if (restaurantId == null || restaurantId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else if (!restaurantService.existsById(restaurantId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        double averageRating = feedbackService.calculateAverageRatingForRestaurant(restaurantId);
        return ResponseEntity.ok(averageRating);
    }

    // Get average rating for a menu item
    @GetMapping("/menu-item/{menuItemId}/average-rating")
    public ResponseEntity<Double> calculateAverageRatingForMenuItem(@PathVariable Long menuItemId) {
        if (!menuItemService.existsById(menuItemId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        double averageRating = feedbackService.calculateAverageRatingForMenuItem(menuItemId);
        return ResponseEntity.ok(averageRating);
    }

    // Get top feedbacks for a restaurant
    @GetMapping("/restaurant/{restaurantId}/top-feedbacks/{limit}")
    public ResponseEntity<List<FeedbackDTO>> getTopFeedbacksForRestaurant(
            @PathVariable String restaurantId,
            @PathVariable int limit) {

        if (limit <= 0) {
            throw new InvalidInputException("Limit must be greater than zero.");
        }
        if (restaurantId == null || restaurantId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else if (!restaurantService.existsById(restaurantId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        List<Feedback> topFeedbacks = feedbackService.getTopFeedbacksForRestaurant(restaurantId, limit);

        if (topFeedbacks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<FeedbackDTO> feedbackDTOs = topFeedbacks.stream()
                .map(FeedbackMapper::toFeedbackDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(feedbackDTOs);
    }

    // Filter feedback by rating threshold
    @GetMapping("/filter-by-rating/{minRating}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbackByRatingThreshold(@PathVariable int minRating) {
        if (minRating < 1 || minRating > 5) {
            throw new InvalidInputException("Rating threshold must be between 1 and 5.");
        }

        List<Feedback> feedbacks = feedbackService.getFeedbackByRatingThreshold(minRating);

        if (feedbacks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<FeedbackDTO> feedbackDTOs = feedbacks.stream()
                .map(FeedbackMapper::toFeedbackDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(feedbackDTOs);
    }

    // Filter feedback within date range
    @GetMapping("/filter-by-date/{startDate}/{endDate}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbackWithinDateRange(
            @PathVariable String startDate,
            @PathVariable String endDate) {

        try {
            LocalDateTime start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            if (start.isAfter(end)) {
                throw new InvalidInputException("Start date cannot be after end date.");
            }

            List<Feedback> feedbacks = feedbackService.getFeedbackWithinDateRange(start, end);

            if (feedbacks.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<FeedbackDTO> feedbackDTOs = feedbacks.stream()
                    .map(FeedbackMapper::toFeedbackDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(feedbackDTOs);

        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Invalid date format. Use ISO format: YYYY-MM-DDTHH:MM:SS.");
        }
    }

    // Search feedback by keyword
    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<FeedbackDTO>> searchFeedbackByKeyword(@PathVariable String keyword) {
        if (keyword.trim().isEmpty()) {
            throw new InvalidInputException("Search keyword cannot be empty.");
        }
        List<Feedback> feedbacks = feedbackService.searchFeedbackByKeyword(keyword);

        if (feedbacks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

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
    public ResponseEntity<Map<String, String>> thankUserForPositiveFeedback(@RequestBody Feedback feedback) {

        feedbackService.thankUserForPositiveFeedback(feedback);
        Map<String, String> response = new HashMap<>();
        System.out.println("message sent...." + feedback.getFeedbackId());
        response.put("message", "Thank you message sent to user.");
        return ResponseEntity.ok(response);
    }

    // get all feedbacks
    @GetMapping("/all")
    public ResponseEntity<List<FeedbackDTO>> getAllFeedbacks() {
        List<Feedback> feedbacks = feedbackService.getAllFeedbacks();
        List<FeedbackDTO> feedbackDTOs = feedbacks.stream()
                .map(FeedbackMapper::toFeedbackDTO)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(feedbackDTOs);
    }

}

// ** This is before restaurant integration code: **

// package com.tastytreat.backend.tasty_treat_express_backend.controllers;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.time.format.DateTimeParseException;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.tastyTreatExpress.DTO.FeedbackDTO;
// import com.tastyTreatExpress.DTO.FeedbackMapper;
// import
// com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.InvalidInputException;
// import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
// import
// com.tastytreat.backend.tasty_treat_express_backend.services.FeedbackService;
// import
// com.tastytreat.backend.tasty_treat_express_backend.services.FeedbackServiceImpl;
// import
// com.tastytreat.backend.tasty_treat_express_backend.services.MenuItemService;
// import
// com.tastytreat.backend.tasty_treat_express_backend.services.RestaurantServiceImpl;

// @RestController
// @RequestMapping("/api/feedbacks")
// public class FeedbackController {

// @Autowired
// private FeedbackServiceImpl feedbackService;

// @Autowired
// private RestaurantServiceImpl restaurantService;

// @Autowired
// private MenuItemService menuItemService;

// // Add feedback for a restaurant
// @PostMapping("/{orderId}/{restaurantId}")
// public ResponseEntity<FeedbackDTO> addFeedback(
// @PathVariable Long orderId,
// @PathVariable String restaurantId,
// @RequestBody Feedback feedback) {
// try {
// Feedback addedFeedback = feedbackService.addFeedback(orderId, restaurantId,
// feedback);
// FeedbackDTO feedbackDTO = FeedbackMapper.toFeedbackDTO(addedFeedback);
// return ResponseEntity.status(HttpStatus.CREATED).body(feedbackDTO);
// } catch (RuntimeException e) {
// System.out.println("Err" + e.getMessage().toString());
// return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
// }
// }

// // Add feedback for a menu item
// @PostMapping("/{orderId}/menu-item/{menuItemId}")
// public ResponseEntity<FeedbackDTO> addMenuItemFeedback(
// @PathVariable Long orderId,
// @PathVariable Long menuItemId,
// @RequestBody Feedback feedback) {
// try {
// Feedback addedFeedback = feedbackService.addMenuItemFeedback(orderId,
// menuItemId, feedback);
// FeedbackDTO feedbackDTO = FeedbackMapper.toFeedbackDTO(addedFeedback);
// return ResponseEntity.status(HttpStatus.CREATED).body(feedbackDTO);
// } catch (RuntimeException e) {
// return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
// }
// }

// // Update feedback
// @PutMapping("/{feedbackId}")
// public ResponseEntity<FeedbackDTO> updateFeedback(
// @PathVariable Long feedbackId,
// @RequestBody Feedback feedback) {
// try {
// Feedback updatedFeedback = feedbackService.updateFeedback(feedbackId,
// feedback);
// FeedbackDTO feedbackDTO = FeedbackMapper.toFeedbackDTO(updatedFeedback);
// return ResponseEntity.ok(feedbackDTO);
// } catch (RuntimeException e) {
// return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
// }
// }

// // Delete feedback
// @DeleteMapping("/{feedbackId}")
// public ResponseEntity<String> deleteFeedback(@PathVariable Long feedbackId) {
// try {
// feedbackService.deleteFeedback(feedbackId);
// return ResponseEntity.ok("Feedback deleted successfully.");
// } catch (RuntimeException e) {
// return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feedback not
// found.");
// }
// }

// // Get feedback for an order
// @GetMapping("/order/{orderId}")
// public ResponseEntity<List<FeedbackDTO>> getFeedbackForOrder(@PathVariable
// Long orderId) {
// List<Feedback> feedbacks = feedbackService.getFeedbackForOrder(orderId);
// List<FeedbackDTO> feedbackDTOs = feedbacks.stream()
// .map(FeedbackMapper::toFeedbackDTO)
// .collect(Collectors.toList());
// return ResponseEntity.ok(feedbackDTOs);
// }

// // Get feedback for a restaurant
// @GetMapping("/restaurant/{restaurantId}")
// public ResponseEntity<List<FeedbackDTO>>
// getFeedbackForRestaurant(@PathVariable String restaurantId) {
// List<Feedback> feedbacks =
// feedbackService.getFeedbackForRestaurant(restaurantId);
// List<FeedbackDTO> feedbackDTOs = feedbacks.stream()
// .map(FeedbackMapper::toFeedbackDTO)
// .collect(Collectors.toList());
// return ResponseEntity.ok(feedbackDTOs);
// }

// // Get feedback for a menu item
// @GetMapping("/menu-item/{menuItemId}")
// public ResponseEntity<List<FeedbackDTO>> getFeedbackForMenuItem(@PathVariable
// Long menuItemId) {
// List<Feedback> feedbacks =
// feedbackService.getFeedbackForMenuItem(menuItemId);
// List<FeedbackDTO> feedbackDTOs = feedbacks.stream()
// .map(FeedbackMapper::toFeedbackDTO)
// .collect(Collectors.toList());
// return ResponseEntity.ok(feedbackDTOs);
// }

// // Get feedback for a user
// @GetMapping("/user/{userId}")
// public ResponseEntity<List<FeedbackDTO>> getFeedbackForUser(@PathVariable
// Long userId) {
// List<Feedback> feedbacks = feedbackService.getFeedbackForUser(userId);
// List<FeedbackDTO> feedbackDTOs = feedbacks.stream()
// .map(FeedbackMapper::toFeedbackDTO)
// .collect(Collectors.toList());
// return ResponseEntity.ok(feedbackDTOs);
// }

// // Get average rating for a restaurant
// @GetMapping("/restaurant/{restaurantId}/average-rating")
// public ResponseEntity<Double>
// calculateAverageRatingForRestaurant(@PathVariable String restaurantId) {
// if (restaurantId == null || restaurantId.isEmpty()) {
// return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
// } else if (!restaurantService.existsById(restaurantId)) {
// return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
// }
// double averageRating =
// feedbackService.calculateAverageRatingForRestaurant(restaurantId);
// return ResponseEntity.ok(averageRating);
// }

// // Get average rating for a menu item
// @GetMapping("/menu-item/{menuItemId}/average-rating")
// public ResponseEntity<Double> calculateAverageRatingForMenuItem(@PathVariable
// Long menuItemId) {
// if (!menuItemService.existsById(menuItemId)) {
// return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
// }
// double averageRating =
// feedbackService.calculateAverageRatingForMenuItem(menuItemId);
// return ResponseEntity.ok(averageRating);
// }

// // Get top feedbacks for a restaurant
// @GetMapping("/restaurant/{restaurantId}/top-feedbacks/{limit}")
// public ResponseEntity<List<FeedbackDTO>> getTopFeedbacksForRestaurant(
// @PathVariable String restaurantId,
// @PathVariable int limit) {

// if (limit <= 0) {
// throw new InvalidInputException("Limit must be greater than zero.");
// }
// if (restaurantId == null || restaurantId.isEmpty()) {
// return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
// } else if (!restaurantService.existsById(restaurantId)) {
// return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
// }
// List<Feedback> topFeedbacks =
// feedbackService.getTopFeedbacksForRestaurant(restaurantId, limit);

// if (topFeedbacks.isEmpty()) {
// return ResponseEntity.noContent().build();
// }

// List<FeedbackDTO> feedbackDTOs = topFeedbacks.stream()
// .map(FeedbackMapper::toFeedbackDTO)
// .collect(Collectors.toList());

// return ResponseEntity.ok(feedbackDTOs);
// }

// // Filter feedback by rating threshold
// @GetMapping("/filter-by-rating/{minRating}")
// public ResponseEntity<List<FeedbackDTO>>
// getFeedbackByRatingThreshold(@PathVariable int minRating) {
// if (minRating < 1 || minRating > 5) {
// throw new InvalidInputException("Rating threshold must be between 1 and 5.");
// }

// List<Feedback> feedbacks =
// feedbackService.getFeedbackByRatingThreshold(minRating);

// if (feedbacks.isEmpty()) {
// return ResponseEntity.noContent().build();
// }

// List<FeedbackDTO> feedbackDTOs = feedbacks.stream()
// .map(FeedbackMapper::toFeedbackDTO)
// .collect(Collectors.toList());

// return ResponseEntity.ok(feedbackDTOs);
// }

// // Filter feedback within date range
// @GetMapping("/filter-by-date/{startDate}/{endDate}")
// public ResponseEntity<List<FeedbackDTO>> getFeedbackWithinDateRange(
// @PathVariable String startDate,
// @PathVariable String endDate) {

// try {
// LocalDateTime start = LocalDateTime.parse(startDate,
// DateTimeFormatter.ISO_LOCAL_DATE_TIME);
// LocalDateTime end = LocalDateTime.parse(endDate,
// DateTimeFormatter.ISO_LOCAL_DATE_TIME);

// if (start.isAfter(end)) {
// throw new InvalidInputException("Start date cannot be after end date.");
// }

// List<Feedback> feedbacks = feedbackService.getFeedbackWithinDateRange(start,
// end);

// if (feedbacks.isEmpty()) {
// return ResponseEntity.noContent().build();
// }

// List<FeedbackDTO> feedbackDTOs = feedbacks.stream()
// .map(FeedbackMapper::toFeedbackDTO)
// .collect(Collectors.toList());

// return ResponseEntity.ok(feedbackDTOs);

// } catch (DateTimeParseException e) {
// throw new InvalidInputException("Invalid date format. Use ISO format:
// YYYY-MM-DDTHH:MM:SS.");
// }
// }

// // Search feedback by keyword
// @GetMapping("/search/{keyword}")
// public ResponseEntity<List<FeedbackDTO>>
// searchFeedbackByKeyword(@PathVariable String keyword) {
// if (keyword.trim().isEmpty()) {
// throw new InvalidInputException("Search keyword cannot be empty.");
// }
// List<Feedback> feedbacks = feedbackService.searchFeedbackByKeyword(keyword);

// if (feedbacks.isEmpty()) {
// return ResponseEntity.noContent().build();
// }

// List<FeedbackDTO> feedbackDTOs = feedbacks.stream()
// .map(FeedbackMapper::toFeedbackDTO)
// .collect(Collectors.toList());

// return ResponseEntity.ok(feedbackDTOs);
// }

// // Notify restaurant on low rating
// @PutMapping("/notify-restaurant")
// public ResponseEntity<String> notifyRestaurantOnLowRating(@RequestBody
// Feedback feedback) {
// feedbackService.notifyRestaurantOnLowRating(feedback);
// return ResponseEntity.ok("Notification sent to restaurant.");
// }

// // Thank user for positive feedback
// @PutMapping("/thank-user")
// public ResponseEntity<String> thankUserForPositiveFeedback(@RequestBody
// Feedback feedback) {
// feedbackService.thankUserForPositiveFeedback(feedback);
// return ResponseEntity.ok("Thank you message sent to user.");
// }
// }
