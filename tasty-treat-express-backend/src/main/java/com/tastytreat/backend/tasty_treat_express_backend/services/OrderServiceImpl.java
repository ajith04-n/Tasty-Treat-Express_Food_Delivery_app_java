package com.tastytreat.backend.tasty_treat_express_backend.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;
import com.tastytreat.backend.tasty_treat_express_backend.models.Restaurant;
import com.tastytreat.backend.tasty_treat_express_backend.models.User;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.MenuItemRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.OrderRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.RestaurantRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderServiceImpl implements OrderService{
    
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RestaurantRepository restaurantRepository;
    @Autowired
    MenuItemRepository menuItemRepository;

    @Autowired
    private EmailService emailService;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OrderServiceImpl.class);
    

    
    
    /* 
    @Override
    @Transactional
    public Order placeOrder(Long customerId, String restaurantId, List<MenuItem> menuItems, String deliveryAddress, String paymentMethod) {
            User customer = userRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            Restaurant restaurant = restaurantRepository.findById(restaurantId)
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));

            
            for (MenuItem menuItem : menuItems) {
                MenuItem dbMenuItem = menuItemRepository.findById(menuItem.getId())
                        .orElseThrow(() -> new RuntimeException("MenuItem not found"));

                if (dbMenuItem.getQuantity() < menuItem.getQuantity()) {
                    throw new RuntimeException("Item " + dbMenuItem.getName() + " is out of stock. Available quantity: " + dbMenuItem.getQuantity());
                }
                
            }

        
            
            double totalAmount = 0.0;
            for (MenuItem menuItem : menuItems) {
                MenuItem dbMenuItem = menuItemRepository.findById(menuItem.getId())
                        .orElseThrow(() -> new RuntimeException("MenuItem not found"));
                totalAmount += dbMenuItem.getPrice() * menuItem.getQuantity();
            }

            
            Order order = new Order();
            order.setCustomer(customer);
            order.setRestaurant(restaurant);
        // order.setMenuItems(menuItems);
            order.setTotalAmount(totalAmount);  
            order.setStatus("Pending");
            order.setDeliveryAddress(deliveryAddress);
            order.setPaymentMethod(paymentMethod);
            order.setPaymentStatus("Pending"); 
            order.setOrderDate(LocalDateTime.now());
            order.setDeliveryTime(LocalDateTime.now().plusHours(2));

            //--
            List<MenuItem> managedMenuItems = menuItems.stream()
                .map(menuItem -> menuItemRepository.findById(menuItem.getId())
                .orElseThrow(() -> new RuntimeException("MenuItem not found")))
                .collect(Collectors.toList());
            
            order.setMenuItems(managedMenuItems);
            
            Order savedOrder = orderRepository.save(order);

        
            for (MenuItem menuItem : menuItems) {
                MenuItem dbMenuItem = menuItemRepository.findById(menuItem.getId())
                        .orElseThrow(() -> new RuntimeException("MenuItem not found"));
                
                int remainingQuantity = dbMenuItem.getQuantity() - menuItem.getQuantity();

                
                dbMenuItem.setQuantity(remainingQuantity);

                if (remainingQuantity == 0) {
                    dbMenuItem.setIsAvailable(false);  
                }

                menuItemRepository.save(dbMenuItem);
            }

            return savedOrder;
    }
    */


    @Override
    @Transactional
    public Order placeOrder(Long customerId, String restaurantId, List<MenuItem> menuItems, String deliveryAddress,
            String paymentMethod) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        for (MenuItem menuItem : menuItems) {
            MenuItem dbMenuItem = menuItemRepository.findById(menuItem.getId())
                    .orElseThrow(() -> new RuntimeException("MenuItem not found"));

            // Log the check
            logger.info("Checking stock for {}: Requested: {}, Available: {}", dbMenuItem.getName(),
                    menuItem.getQuantity(), dbMenuItem.getQuantity());

            if (dbMenuItem.getQuantity() < menuItem.getQuantity()) {
                logger.error("Out of stock for {}: Requested: {}, Available: {}", dbMenuItem.getName(),
                        menuItem.getQuantity(), dbMenuItem.getQuantity());
                throw new RuntimeException("Item " + dbMenuItem.getName() + " is out of stock. Available quantity: "
                        + dbMenuItem.getQuantity());
            }
        }

        double totalAmount = 0.0;
        for (MenuItem menuItem : menuItems) {
            MenuItem dbMenuItem = menuItemRepository.findById(menuItem.getId())
                    .orElseThrow(() -> new RuntimeException("MenuItem not found"));
            totalAmount += dbMenuItem.getPrice() * menuItem.getQuantity();
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setTotalAmount(totalAmount);
        order.setStatus("Pending");
        order.setDeliveryAddress(deliveryAddress);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus("Pending");
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveryTime(LocalDateTime.now().plusHours(2));

        List<MenuItem> managedMenuItems = menuItems.stream()
                .map(menuItem -> menuItemRepository.findById(menuItem.getId())
                        .orElseThrow(() -> new RuntimeException("MenuItem not found")))
                .collect(Collectors.toList());

        order.setMenuItems(managedMenuItems);

        Order savedOrder = orderRepository.save(order);

        for (MenuItem menuItem : menuItems) {
            MenuItem dbMenuItem = menuItemRepository.findById(menuItem.getId())
                    .orElseThrow(() -> new RuntimeException("MenuItem not found"));

            int remainingQuantity = dbMenuItem.getQuantity() - menuItem.getQuantity();
            dbMenuItem.setQuantity(remainingQuantity);

            if (remainingQuantity == 0) {
                dbMenuItem.setIsAvailable(false);
            }

            menuItemRepository.save(dbMenuItem);
        }

        return savedOrder;
    }

    @Override
    public Order getOrderById(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            return order.get();
        } else {
            throw new RuntimeException("Order not found with id " + orderId);
        }
    }
    
    @Override
    public List<Order> getOrdersByRestaurant(String restaurantId) {
        return orderRepository.findByRestaurantRestaurantId(restaurantId);
    }

    @Override
    public List<Order> getOrdersByCustomer(Long customerId) {
        
        return orderRepository.findByCustomer_Id(customerId);
    }

    @Override
    public List<Order> getOrdersByStatus(String status) {
        
        return orderRepository.findByStatus(status);
    }


    
    public Order updateOrderDeliveryTime(Long orderId, LocalDateTime deliveryTime) {
         Order order = getOrderById(orderId);
         order.setDeliveryTime(deliveryTime);
         return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long orderId) {
         if (orderRepository.existsById(orderId)) {
                orderRepository.deleteById(orderId);
         } else {
                throw new RuntimeException("Order not found with id " + orderId);
         }
    }


    @Override
    public List<MenuItem> getPopularOrderedItems() {
        List<Order> allOrders = orderRepository.findAll();
    
        Map<Long, Integer> itemFrequency = new HashMap<>();
        
        for (Order order : allOrders) {
            for (MenuItem item : order.getMenuItems()) {
                Long itemId = item.getId();
                itemFrequency.put(itemId, itemFrequency.getOrDefault(itemId, 0) + 1);
            }
        }
        List<Map.Entry<Long, Integer>> sortedItems = new ArrayList<>(itemFrequency.entrySet());
        sortedItems.sort(Map.Entry.<Long, Integer>comparingByValue().reversed());
        
        List<MenuItem> popularItems = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : sortedItems) {
            menuItemRepository.findById(entry.getKey()).ifPresent(popularItems::add);
        }
        
        return popularItems;
    }

    // New methods
    @Autowired
	private NotificationService notificationService;

	@Override
	public void sendPushNotification(Long customerId, String message) throws Exception {
	    try {
	        User customer = userRepository.findById(customerId)
	                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

	        String notificationMessage = "Hello " + customer.getName() + "! " + message;
	        notificationService.sendNotification(customer.getId(), notificationMessage);
            emailService.sendSimpleMessage(customer.getEmail(), "Order Notification", notificationMessage);
	    } catch (RuntimeException e) {
	        throw new Exception("Error sending notification: " + e.getMessage());
	    } catch (Exception e) {
	        throw new Exception("Unexpected error occurred while sending notification.");
	    }
	}
	
    @Override
	public LocalDateTime estimateDeliveryTime(Long orderId) {
	    Order order = getOrderById(orderId);
	    int additionalMinutes = order.getMenuItems().size() * 10;
	    return order.getOrderDate().plusMinutes(30 + additionalMinutes);
	}
	@Override
	public Order applyDiscount(Long orderId, String couponCode) {
	    Order order = getOrderById(orderId);
	    
	    double discount = 0.0;
	    if (couponCode.equalsIgnoreCase("DISCOUNT10")) {
	        discount = 0.10;
	    } else if (couponCode.equalsIgnoreCase("DISCOUNT20")) {
	        discount = 0.20;
	    } else {
	        throw new RuntimeException("Invalid coupon code");
	    }
	    
	    double newTotal = order.getTotalAmount() * (1 - discount);
	    order.setTotalAmount(newTotal);
	    return orderRepository.save(order);
	}

	@Override
	@Transactional
	public Order reorder(Long orderId) {
	    Order oldOrder = getOrderById(orderId);

	    Order newOrder = new Order();
	    newOrder.setCustomer(oldOrder.getCustomer());
	    newOrder.setRestaurant(oldOrder.getRestaurant());
	    newOrder.setMenuItems(oldOrder.getMenuItems());
	    newOrder.setTotalAmount(oldOrder.getTotalAmount());
	    newOrder.setStatus("Pending");
	    newOrder.setDeliveryAddress(oldOrder.getDeliveryAddress());
	    newOrder.setPaymentMethod(oldOrder.getPaymentMethod());
	    newOrder.setPaymentStatus("Pending");
	    newOrder.setOrderDate(LocalDateTime.now());
	    newOrder.setDeliveryTime(LocalDateTime.now().plusHours(2));

	    return orderRepository.save(newOrder);
	}
	
	@Override
	public void updateDeliveryLocation(Long orderId, Double latitude, Double longitude) {
	    Order order = getOrderById(orderId);
	    order.setCurrentLatitude(latitude);
	    order.setCurrentLongitude(longitude);
	    orderRepository.save(order);
	}

	@Override
	public double[] getDeliveryLocation(Long orderId) {
	    Order order = getOrderById(orderId);
	    return new double[]{order.getCurrentLatitude(), order.getCurrentLongitude()};
	}
	
    public void notifyCustomerIfNear(Long orderId) {
	    Order order = getOrderById(orderId);

        if (order.getCurrentLatitude() == null || order.getCurrentLongitude() == null) {
            throw new RuntimeException("Delivery location is not set for this order.");
        }


	    double customerLat = order.getCustomer().getLatitude();  
	    double customerLng = order.getCustomer().getLongitude();  
	    double deliveryLat = order.getCurrentLatitude();
	    double deliveryLng = order.getCurrentLongitude();

	    double distance = calculateDistance(customerLat, customerLng, deliveryLat, deliveryLng);

	    if (distance < 1.0) {
	        try {
				notificationService.sendNotification(order.getCustomer().getId(), "Your order is arriving soon! 🚀");
                emailService.sendSimpleMessage(order.getCustomer().getEmail(), "Order Update", "Your order is arriving soon! ");
            } catch (Exception e) {
				e.printStackTrace();
			}
	    }
	}

	private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
	    double R = 6371; 
	    double dLat = Math.toRadians(lat2 - lat1);
	    double dLon = Math.toRadians(lon2 - lon1);
	    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLon / 2) * Math.sin(dLon / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    return R * c;
	}

    
    @Scheduled(fixedRate = 60000)
    public void autoUpdateDeliveryLocation() {
        List<Order> activeOrders = orderRepository.findByStatus("Out for Delivery");
        for (Order order : activeOrders) {
            double newLat = order.getCurrentLatitude() + Math.random() * 0.002 - 0.001;
            double newLng = order.getCurrentLongitude() + Math.random() * 0.002 - 0.001;
            updateDeliveryLocation(order.getOrderId(), newLat, newLng);
            notifyCustomerIfNear(order.getOrderId());
        }
    }
	
    @Override
	public double calculateDistance(Long orderId) {
	    Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

	    double restaurantLat = order.getRestaurant().getLatitude();
	    double restaurantLng = order.getRestaurant().getLongitude();
	    double deliveryLat = order.getCurrentLatitude();
	    double deliveryLng = order.getCurrentLongitude();

	    return haversineDistance(restaurantLat, restaurantLng, deliveryLat, deliveryLng);
	}
	// Calculate the distance using the Haversine formula
	private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
	    double R = 6371; 
	    double dLat = Math.toRadians(lat2 - lat1);
	    double dLon = Math.toRadians(lon2 - lon1);
	    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLon / 2) * Math.sin(dLon / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    return R * c;
	}

    public String processPayment(Long orderId, String paymentMethod) {
        Order order = getOrderById(orderId);
        if (!order.getPaymentStatus().equalsIgnoreCase("Pending")) {
            throw new RuntimeException("Payment already processed for this order.");
        }
        String transactionId = UUID.randomUUID().toString();

        order.setTransactionId(transactionId);
        order.setPaymentStatus("Paid");
        orderRepository.save(order);
        return "Payment processed successfully using " + paymentMethod;
    }

    @Override
    public String cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order.getPaymentStatus().equalsIgnoreCase("Paid")) {
            order.setPaymentStatus("Refunded");
        }
        order.setStatus("Cancelled");
        orderRepository.save(order);
        return "Order cancelled successfully. Refund status: " + order.getPaymentStatus();
    }


    @Override
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        try {
            notificationService.sendNotification(order.getCustomer().getId(),
                    "Your order status has been updated to: " + status);
            emailService.sendSimpleMessage(order.getCustomer().getEmail(), "Order Update",
                    "Your order status has been updated to: " + status);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return updatedOrder;
    }
 

    public Map<String, Object> getOrderAnalytics(String restaurantId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findByRestaurantRestaurantIdAndOrderDateBetween(restaurantId, startDate,
                endDate);
        double totalRevenue = orders.stream().mapToDouble(Order::getTotalAmount).sum();
        int totalOrders = orders.size();
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalOrders", totalOrders);
        analytics.put("totalRevenue", totalRevenue);
        return analytics;
    }

    public void notifyCustomerIfOrderIsLate(Long orderId) {
        Order order = getOrderById(orderId);
        LocalDateTime estimatedDeliveryTime = estimateDeliveryTime(orderId);
        if (LocalDateTime.now().isAfter(estimatedDeliveryTime)) {
            try {
                notificationService.sendNotification(order.getCustomer().getId(), "Your order is delayed. Please check the status.");
                emailService.sendSimpleMessage(order.getCustomer().getEmail(), "Order Update", "Your order is delayed. Please check the status.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyRestaurantIfOrderIsLate(Long orderId) {
        Order order = getOrderById(orderId);
        LocalDateTime estimatedDeliveryTime = estimateDeliveryTime(orderId);
        if (LocalDateTime.now().isAfter(estimatedDeliveryTime)) {
            try {
                emailService.sendSimpleMessage(order.getRestaurant().getEmail(), "Order Update", "Your order #" + order.getOrderId() + " is delayed. Please check the status.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    
        
}