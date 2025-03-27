package com.tastytreat.backend.tasty_treat_express_backend.services;

import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.models.User;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;
import com.tastytreat.backend.tasty_treat_express_backend.models.Restaurant;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.FeedbackRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.MenuItemRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.UserRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.OrderRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface FeedbackService {
    Feedback addFeedback(Long orderId, String restaurantId, Feedback feedback);

    Feedback addMenuItemFeedback(Long orderId, Long menuItemId, Feedback feedback);

    Feedback updateFeedback(Long feedbackId, Feedback feedback);

    void deleteFeedback(Long feedbackId);

    List<Feedback> getFeedbackForOrder(Long orderId);

    List<Feedback> getFeedbackForRestaurant(String restaurantId);

    List<Feedback> getFeedbackForMenuItem(Long menuItemId);

    List<Feedback> getFeedbackForUser(Long userId);

    double calculateAverageRatingForRestaurant(String restaurantId);

    double calculateAverageRatingForMenuItem(Long menuItemId);

    List<Feedback> getTopFeedbacksForRestaurant(String restaurantId, int limit);

    //
    List<Feedback> getFeedbackByRatingThreshold(int minRating);

    List<Feedback> getFeedbackWithinDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<Feedback> searchFeedbackByKeyword(String keyword);

    Map<String, Object> getFeedbackSummaryForRestaurant(String restaurantId);

    void notifyRestaurantOnLowRating(Feedback feedback);

    void thankUserForPositiveFeedback(Feedback feedback);

    List<Feedback> getPersonalizedFeedbackDashboard(Long userId);

}


