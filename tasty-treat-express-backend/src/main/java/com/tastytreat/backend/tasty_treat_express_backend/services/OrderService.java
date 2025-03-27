package com.tastytreat.backend.tasty_treat_express_backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;

public interface OrderService {
    //public Order placeOrder(Long customerId, String restaurantId, List<MenuItem> menuItems, String deliveryAddress, String paymentMethod);
    public Order placeOrder(Long customerId, String restaurantId, Order orderObj);
    
    
    public Order getOrderById(Long orderId);
    public List<Order> getOrdersByRestaurant(String restaurantId);
    public List<Order> getOrdersByCustomer(Long customerId);
    public List<Order> getOrdersByStatus(String status);
    public Order updateOrderStatus(Long orderId, String status);
    public Order updateOrderDeliveryTime(Long orderId, LocalDateTime deliveryTime);
    public void deleteOrder(Long orderId);
    
    // New method
    public void sendPushNotification(Long customerId, String message) throws Exception;
    
    public LocalDateTime estimateDeliveryTime(Long orderId);
    public Order applyDiscount(Long orderId, String couponCode);
    public Order reorder(Long orderId);
    public void updateDeliveryLocation(Long orderId, Double latitude, Double longitude);
    public double[] getDeliveryLocation(Long orderId);
    public double calculateDistance(Long orderId);
    public List<MenuItem> getPopularOrderedItems();

    public String cancelOrder(Long orderId);
    public String processPayment(Long orderId, String paymentMethod);
    public Map<String, Object> getOrderAnalytics(String restaurantId, LocalDateTime startDate, LocalDateTime endDate);
    public void notifyCustomerIfNear(Long orderId);
    
    public void notifyCustomerIfOrderIsLate(Long orderId);
    public void notifyRestaurantIfOrderIsLate(Long orderId);


}
