package com.tastytreat.frontend.tasty_treat_express_frontend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import com.tastytreat.frontend.tasty_treat_express_frontend.models.OrderDTO;
import com.tastytreat.frontend.tasty_treat_express_frontend.models.RestaurantDTO;
import com.tastytreat.frontend.tasty_treat_express_frontend.models.UserDTO;

import jakarta.servlet.http.HttpSession;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;

@Controller
public class UserDashboardController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        UserDTO userDTO = (UserDTO) session.getAttribute("userDTO");
        if (userDTO != null) {

            model.addAttribute("userName", userDTO.getName());
            Long userId = userDTO.getId();

            // Fetch orders from API
            String ordersUrl = "http://localhost:8080/api/orders/user/" + userId;
            OrderDTO[] orders = restTemplate.getForObject(ordersUrl, OrderDTO[].class);

            // Calculate stats
            int ordersThisMonth = getOrdersThisMonth(orders);
            double totalSpent = getTotalSpent(orders);
            double avgDeliveryTime = getAvgDeliveryTime(orders);
            String favoriteRestaurant = getFavoriteRestaurant(orders);

            // Set stats in model
            model.addAttribute("ordersThisMonth", ordersThisMonth);
            model.addAttribute("totalSpent", totalSpent);
            model.addAttribute("avgDeliveryTime", avgDeliveryTime);
            model.addAttribute("favoriteRestaurant", favoriteRestaurant);

            // Fetch recent orders
            model.addAttribute("recentOrders", getRecentOrders(orders));

            // Return the dashboard page
            return "userdashboard";
        }
        return "error"; // Handle case when user is not found in session
    }

    // Calculate the number of orders this month
    private int getOrdersThisMonth(OrderDTO[] orders) {
        int count = 0;
        LocalDateTime currentDate = LocalDateTime.now();
        for (OrderDTO order : orders) {
            LocalDateTime orderDate = order.getOrderDate();
            if (orderDate.getMonth() == currentDate.getMonth()) {
                count++;
            }
        }
        return count;
    }

    // Calculate total amount spent
    private double getTotalSpent(OrderDTO[] orders) {
        double total = 0;
        for (OrderDTO order : orders) {
            total += order.getTotalAmount();
        }
        return total;
    }

    // Calculate average delivery time
    private double getAvgDeliveryTime(OrderDTO[] orders) {
        double totalDeliveryTime = 0;
        int orderCount = orders.length;

        for (OrderDTO order : orders) {
            LocalDateTime orderTime = order.getOrderDate();
            LocalDateTime deliveryTime = order.getDeliveryTime();
            Duration duration = Duration.between(orderTime, deliveryTime);
            totalDeliveryTime += duration.toMinutes();
        }

        return orderCount > 0 ? totalDeliveryTime / orderCount : 0;
    }

    private String getFavoriteRestaurant(OrderDTO[] orders) {
        Map<String, Integer> restaurantOrderCount = new HashMap<>();

        for (OrderDTO order : orders) {
            String restaurantId = order.getRestaurantId();
            String restaurantName = getRestaurantName(restaurantId);
            restaurantOrderCount.put(restaurantName, restaurantOrderCount.getOrDefault(restaurantName, 0) + 1);
        }

        return restaurantOrderCount.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No favorite restaurant");
    }

    private String getRestaurantName(String restaurantId) {
        String url = "http://localhost:8080/api/restaurants/" + restaurantId;
        RestaurantDTO restaurant = restTemplate.getForObject(url, RestaurantDTO.class);
        return restaurant != null ? restaurant.getName() : "Unknown Restaurant";
    }

    private List<OrderDTO> getRecentOrders(OrderDTO[] orders) {
        List<OrderDTO> recentOrders = new ArrayList<>();

        for (OrderDTO order : orders) {
            // Assuming OrderDTO has properties like orderId, restaurant, status, etc.
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setOrderId(order.getOrderId());
            orderDTO.setStatus(order.getStatus());
            orderDTO.setRestaurantId(getRestaurantName(order.getRestaurantId()));
            // orderDTO.setDeliveryTime(LocalDateTime.); // Placeholder
            recentOrders.add(orderDTO);
        }

        return recentOrders;
    }
}
