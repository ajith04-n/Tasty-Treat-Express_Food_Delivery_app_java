package com.tastytreat.backend.tasty_treat_express_backend.services;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.tastytreat.backend.tasty_treat_express_backend.exceptions.ReportNotFoundException;
import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;
import com.tastytreat.backend.tasty_treat_express_backend.models.Restaurant;
import com.tastytreat.backend.tasty_treat_express_backend.models.User;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.FeedbackRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.MenuItemRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.OrderRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.RestaurantRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.UserRepository;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public Feedback addFeedback(Long orderId, String restaurantId, Feedback feedback) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + restaurantId));

        User user = order.getUser();

        Feedback existingFeedback = feedbackRepository.findByUserAndOrdersAndRestaurant(user, order, restaurant);

        if (existingFeedback != null) {
            existingFeedback.setRating(feedback.getRating());
            existingFeedback.setComments(feedback.getComments());
            existingFeedback.setFeedbackDate(LocalDateTime.now());
            return feedbackRepository.save(existingFeedback);
        } else {
            feedback.setOrders(order);
            feedback.setRestaurant(restaurant);
            feedback.setUser(user);
            feedback.setFeedbackDate(LocalDateTime.now());
            return feedbackRepository.save(feedback);
        }
    }

    @Override
    public Feedback addMenuItemFeedback(Long orderId, Long menuItemId, Feedback feedback) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("MenuItem not found with ID: " + menuItemId));

        User user = order.getUser();

        // Check if feedback already exists for this user, order, and menu item
        Feedback existingFeedback = feedbackRepository.findByUserAndOrdersAndMenuItem(user, order, menuItem);

        if (existingFeedback != null) {
            existingFeedback.setRating(feedback.getRating());
            existingFeedback.setComments(feedback.getComments());
            existingFeedback.setFeedbackDate(LocalDateTime.now());
            return feedbackRepository.save(existingFeedback);
        } else {
            Optional<Restaurant> restaurant = restaurantRepository.findById(order.getRestaurant().getRestaurantId());
            feedback.setOrders(order);
            feedback.setMenuItems(menuItem);
            feedback.setUser(user);
            feedback.setFeedbackDate(LocalDateTime.now());
            feedback.setRestaurant(restaurant.get());
            return feedbackRepository.save(feedback);
        }
    }

    @Override
    public Feedback updateFeedback(Long feedbackId, Feedback feedback) {
        Feedback existingFeedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found with ID: " + feedbackId));

        existingFeedback.setRating(feedback.getRating());
        existingFeedback.setComments(feedback.getComments());
        existingFeedback.setUpdatedAt(LocalDateTime.now());
        return feedbackRepository.save(existingFeedback);
    }

    @Override
    public void deleteFeedback(Long feedbackId) {
        if (!feedbackRepository.existsById(feedbackId)) {
            throw new RuntimeException("Feedback not found with ID: " + feedbackId);
        }
        feedbackRepository.deleteById(feedbackId);
    }

    @Override
    public List<Feedback> getFeedbackForOrder(Long orderId) {
        return feedbackRepository.findByOrdersOrderId(orderId);
    }

    @Override
    public List<Feedback> getFeedbackForRestaurant(String restaurantId) {
        return feedbackRepository.findByRestaurantRestaurantId(restaurantId);
    }

    @Override
    public List<Feedback> getFeedbackForMenuItem(Long menuItemId) {
        return feedbackRepository.findByMenuItemId(menuItemId);
    }

    @Override
    public List<Feedback> getFeedbackForUser(Long userId) {
        return feedbackRepository.findByUser_Id(userId);
    }

    @Override
    public double calculateAverageRatingForRestaurant(String restaurantId) {
        List<Feedback> feedbacks = feedbackRepository.findByRestaurantRestaurantId(restaurantId);
        return feedbacks.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);
    }

    @Override
    public double calculateAverageRatingForMenuItem(Long menuItemId) {
        List<Feedback> feedbacks = feedbackRepository.findByMenuItemId(menuItemId);
        return feedbacks.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);
    }

    @Override
    public List<Feedback> getTopFeedbacksForRestaurant(String restaurantId, int limit) {
        return feedbackRepository.findTopFeedbacksForRestaurant(restaurantId, PageRequest.of(0, limit));
    }

    public List<Feedback> getFeedbackByRatingThreshold(int minRating) {
        return feedbackRepository.findAll().stream()
                .filter(feedback -> feedback.getRating() >= minRating)
                .collect(Collectors.toList());
    }

    public List<Feedback> getFeedbackWithinDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return feedbackRepository.findAll().stream()
                .filter(feedback -> feedback.getFeedbackDate().isAfter(startDate) &&
                        feedback.getFeedbackDate().isBefore(endDate))
                .collect(Collectors.toList());
    }

    public List<Feedback> searchFeedbackByKeyword(String keyword) {
        return feedbackRepository.findAll().stream()
                .filter(feedback -> feedback.getComments() != null && feedback.getComments().contains(keyword))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getFeedbackSummaryForRestaurant(String restaurantId) {
        List<Feedback> feedbacks = feedbackRepository.findByRestaurantRestaurantId(restaurantId);
        double averageRating = feedbacks.stream().mapToInt(Feedback::getRating).average().orElse(0.0);
        long totalFeedbacks = feedbacks.size();

        Map<String, Object> summary = new HashMap<>();
        summary.put("averageRating", averageRating);
        summary.put("totalFeedbacks", totalFeedbacks);
        summary.put("ratingDistribution", feedbacks.stream()
                .collect(Collectors.groupingBy(Feedback::getRating, Collectors.counting())));

        return summary;
    }

    public void notifyRestaurantOnLowRating(Feedback feedback) {
        Optional<Feedback> dFeedback = feedbackRepository.findById(feedback.getFeedbackId());
        if (dFeedback.isPresent()) {
            if (feedback.getRating() <= 2) {
                String message = "Your restaurant received a low rating of " + feedback.getRating() +
                        ". Please check the feedback and improve your services.";
                emailService.sendSimpleMessage(dFeedback.get().getRestaurant().getEmail(), "Low Rating Alert", message);
            }
        } else {
            throw new ReportNotFoundException("The restaurant does not exist on the server");
        }
    }

    public void thankUserForPositiveFeedback(Feedback feedback) {
        Optional<Feedback> dFeedback = feedbackRepository.findById(feedback.getFeedbackId());
        if (dFeedback.isPresent()) {
            Long uid = dFeedback.get().getUser().getId();
            Optional<User> user = userRepository.findById(uid);
            if (user.isPresent()) {
                String comments = feedback.getComments();
                String message = comments+ " Thank you for your feedback on our restaurant. We appreciate your support.Your satisfaction means a lot to us.";
                emailService.sendSimpleMessage(user.get().getEmail(), "Thank You", message);
                System.out.println(message + "****" + user.get().getEmail());
            }
        }
    }

    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll().stream()
                .collect(Collectors.toList());
    }

    public List<Feedback> getPersonalizedFeedbackDashboard(Long userId) {
        return feedbackRepository.findByUser_Id(userId).stream()
                .sorted(Comparator.comparing(Feedback::getFeedbackDate).reversed())
                .collect(Collectors.toList());
    }

}

// ** This is before restaurant Integration code **

// package com.tastytreat.backend.tasty_treat_express_backend.services;
// import java.time.LocalDateTime;
// import java.util.Comparator;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
// import java.util.stream.Collectors;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.stereotype.Service;

// import
// com.tastytreat.backend.tasty_treat_express_backend.exceptions.ReportNotFoundException;
// import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
// import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
// import com.tastytreat.backend.tasty_treat_express_backend.models.Order;
// import com.tastytreat.backend.tasty_treat_express_backend.models.Restaurant;
// import com.tastytreat.backend.tasty_treat_express_backend.models.User;
// import
// com.tastytreat.backend.tasty_treat_express_backend.repositories.FeedbackRepository;
// import
// com.tastytreat.backend.tasty_treat_express_backend.repositories.MenuItemRepository;
// import
// com.tastytreat.backend.tasty_treat_express_backend.repositories.OrderRepository;
// import
// com.tastytreat.backend.tasty_treat_express_backend.repositories.RestaurantRepository;
// import
// com.tastytreat.backend.tasty_treat_express_backend.repositories.UserRepository;

// @Service
// public class FeedbackServiceImpl implements FeedbackService {

// @Autowired
// private FeedbackRepository feedbackRepository;

// @Autowired
// private OrderRepository orderRepository;

// @Autowired
// private RestaurantRepository restaurantRepository;

// @Autowired
// private MenuItemRepository menuItemRepository;

// @Autowired
// private UserRepository userRepository;

// @Autowired
// private EmailService emailService;

// @Override
// public Feedback addFeedback(Long orderId, String restaurantId, Feedback
// feedback) {
// Order order = orderRepository.findById(orderId)
// .orElseThrow(() -> new RuntimeException("Order not found with ID: " +
// orderId));

// Restaurant restaurant = restaurantRepository.findById(restaurantId)
// .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " +
// restaurantId));

// User user = order.getUser();

// //Feedback existingFeedback =
// feedbackRepository.findByUserAndOrdersAndRestaurant(user, order, restaurant);
// Feedback existingFeedback =
// feedbackRepository.findByUserAndRestaurant(user,restaurant);

// if (existingFeedback != null) {
// existingFeedback.setRating(feedback.getRating());
// existingFeedback.setComments(feedback.getComments());
// existingFeedback.setFeedbackDate(LocalDateTime.now());
// return feedbackRepository.save(existingFeedback);
// } else {
// feedback.setOrders(order);
// feedback.setRestaurant(restaurant);
// feedback.setUser(user);
// feedback.setFeedbackDate(LocalDateTime.now());
// return feedbackRepository.save(feedback);
// }
// }

// @Override
// public Feedback addMenuItemFeedback(Long orderId, Long menuItemId, Feedback
// feedback) {
// Order order = orderRepository.findById(orderId)
// .orElseThrow(() -> new RuntimeException("Order not found with ID: " +
// orderId));

// MenuItem menuItem = menuItemRepository.findById(menuItemId)
// .orElseThrow(() -> new RuntimeException("MenuItem not found with ID: " +
// menuItemId));

// User user = order.getUser();

// // Check if feedback already exists for this user, order, and menu item
// Feedback existingFeedback =
// feedbackRepository.findByUserAndOrdersAndMenuItem(user, order, menuItem);

// if (existingFeedback != null) {
// existingFeedback.setRating(feedback.getRating());
// existingFeedback.setComments(feedback.getComments());
// existingFeedback.setFeedbackDate(LocalDateTime.now());
// return feedbackRepository.save(existingFeedback);
// } else {
// Optional<Restaurant> restaurant =
// restaurantRepository.findById(order.getRestaurant().getRestaurantId());
// feedback.setOrders(order);
// feedback.setMenuItems(menuItem);
// feedback.setUser(user);
// feedback.setFeedbackDate(LocalDateTime.now());
// feedback.setRestaurant(restaurant.get());
// return feedbackRepository.save(feedback);
// }
// }

// @Override
// public Feedback updateFeedback(Long feedbackId, Feedback feedback) {
// Feedback existingFeedback = feedbackRepository.findById(feedbackId)
// .orElseThrow(() -> new RuntimeException("Feedback not found with ID: " +
// feedbackId));

// existingFeedback.setRating(feedback.getRating());
// existingFeedback.setComments(feedback.getComments());
// existingFeedback.setUpdatedAt(LocalDateTime.now());
// return feedbackRepository.save(existingFeedback);
// }

// @Override
// public void deleteFeedback(Long feedbackId) {
// if (!feedbackRepository.existsById(feedbackId)) {
// throw new RuntimeException("Feedback not found with ID: " + feedbackId);
// }
// feedbackRepository.deleteById(feedbackId);
// }

// @Override
// public List<Feedback> getFeedbackForOrder(Long orderId) {
// return feedbackRepository.findByOrdersOrderId(orderId);
// }

// @Override
// public List<Feedback> getFeedbackForRestaurant(String restaurantId) {
// return feedbackRepository.findByRestaurantRestaurantId(restaurantId);
// }

// @Override
// public List<Feedback> getFeedbackForMenuItem(Long menuItemId) {
// return feedbackRepository.findByMenuItemId(menuItemId);
// }

// @Override
// public List<Feedback> getFeedbackForUser(Long userId) {
// return feedbackRepository.findByUser_Id(userId);
// }

// @Override
// public double calculateAverageRatingForRestaurant(String restaurantId) {
// List<Feedback> feedbacks =
// feedbackRepository.findByRestaurantRestaurantId(restaurantId);
// return feedbacks.stream()
// .mapToInt(Feedback::getRating)
// .average()
// .orElse(0.0);
// }

// @Override
// public double calculateAverageRatingForMenuItem(Long menuItemId) {
// List<Feedback> feedbacks = feedbackRepository.findByMenuItemId(menuItemId);
// return feedbacks.stream()
// .mapToInt(Feedback::getRating)
// .average()
// .orElse(0.0);
// }

// @Override
// public List<Feedback> getTopFeedbacksForRestaurant(String restaurantId, int
// limit) {
// return feedbackRepository.findTopFeedbacksForRestaurant(restaurantId,
// PageRequest.of(0, limit));
// }

// public List<Feedback> getFeedbackByRatingThreshold(int minRating) {
// return feedbackRepository.findAll().stream()
// .filter(feedback -> feedback.getRating() >= minRating)
// .collect(Collectors.toList());
// }

// public List<Feedback> getFeedbackWithinDateRange(LocalDateTime startDate,
// LocalDateTime endDate) {
// return feedbackRepository.findAll().stream()
// .filter(feedback -> feedback.getFeedbackDate().isAfter(startDate) &&
// feedback.getFeedbackDate().isBefore(endDate))
// .collect(Collectors.toList());
// }

// public List<Feedback> searchFeedbackByKeyword(String keyword) {
// return feedbackRepository.findAll().stream()
// .filter(feedback -> feedback.getComments() != null &&
// feedback.getComments().contains(keyword))
// .collect(Collectors.toList());
// }

// public Map<String, Object> getFeedbackSummaryForRestaurant(String
// restaurantId) {
// List<Feedback> feedbacks =
// feedbackRepository.findByRestaurantRestaurantId(restaurantId);
// double averageRating =
// feedbacks.stream().mapToInt(Feedback::getRating).average().orElse(0.0);
// long totalFeedbacks = feedbacks.size();

// Map<String, Object> summary = new HashMap<>();
// summary.put("averageRating", averageRating);
// summary.put("totalFeedbacks", totalFeedbacks);
// summary.put("ratingDistribution", feedbacks.stream()
// .collect(Collectors.groupingBy(Feedback::getRating, Collectors.counting())));

// return summary;
// }

// public void notifyRestaurantOnLowRating(Feedback feedback) {
// Optional<Feedback> dFeedback =
// feedbackRepository.findById(feedback.getFeedbackId());
// if (dFeedback.isPresent()) {
// if (feedback.getRating() <= 2) {
// String message = "Your restaurant received a low rating of " +
// feedback.getRating() +
// ". Please check the feedback and improve your services.";
// emailService.sendSimpleMessage(dFeedback.get().getRestaurant().getEmail(),
// "Low Rating Alert", message);
// }
// } else {
// throw new ReportNotFoundException("The restaurant does not exist on the
// server");
// }
// }

// public void thankUserForPositiveFeedback(Feedback feedback) {
// if (feedback.getRating() >= 4) {
// String message = "Thank you for your positive feedback! Your satisfaction
// means a lot to us.";
// emailService.sendSimpleMessage(feedback.getUser().getEmail(), "Thank You for
// Your Feedback", message);
// }
// }

// public List<Feedback> getPersonalizedFeedbackDashboard(Long userId) {
// return feedbackRepository.findByUser_Id(userId).stream()
// .sorted(Comparator.comparing(Feedback::getFeedbackDate).reversed())
// .collect(Collectors.toList());
// }

// }
