package com.tastyTreatExpress.DTO;

import java.util.List;
import java.util.stream.Collectors;

import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;
import com.tastytreat.backend.tasty_treat_express_backend.models.Report;
import com.tastytreat.backend.tasty_treat_express_backend.models.Restaurant;

public class RestaurantMapper {

    public static RestaurantDTO toRestaurantDTO(Restaurant restaurant) {
        if (restaurant == null)
            return null;

        List<Long> menuItemIds = restaurant.getMenu().stream()
                .map(MenuItem::getMenuId)
                .collect(Collectors.toList());

        List<Long> orderIds = restaurant.getOrders().stream()
                .map(Order::getOrderId)
                .collect(Collectors.toList());

        List<Long> feedbackIds = restaurant.getFeedbacks().stream()
                .map(Feedback::getFeedbackId)
                .collect(Collectors.toList());

        List<Long> reportIds = restaurant.getReports().stream()
                .map(Report::getId)
                .collect(Collectors.toList());

        return new RestaurantDTO(
                restaurant.getRestaurantId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getDescription(),
                restaurant.getEmail(),
                restaurant.getPhoneNumber(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                restaurant.getRating(),
                menuItemIds,
                orderIds,
                feedbackIds,
                reportIds);
    }
}
