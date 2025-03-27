package com.tastytreat.backend.tasty_treat_express_backend.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;
import com.tastytreat.backend.tasty_treat_express_backend.services.OrderService;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Place an order
    @PostMapping("/placeOrder/{userId}/{restaurantId}")  
    public ResponseEntity<Order> placeOrder(
    		@PathVariable Long userId,
    		@PathVariable String restaurantId, 
            @RequestBody Order orderobj) {
        try {
            Order order = orderService.placeOrder(userId, restaurantId,orderobj);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Retrieve order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Get orders by restaurant ID
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Order>> getOrdersByRestaurant(@PathVariable String restaurantId) {
        try {
            List<Order> orders = orderService.getOrdersByRestaurant(restaurantId);
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Get orders by customer ID
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable Long customerId) {
        try {
            List<Order> orders = orderService.getOrdersByCustomer(customerId);
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Update order status
    @PutMapping("/updateStatus/{orderId}")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Update delivery time for an order
    @PutMapping("/updateDeliveryTime/{orderId}")
    public ResponseEntity<Order> updateOrderDeliveryTime(@PathVariable Long orderId,
            @RequestParam String deliveryTime) {
        try {
            LocalDateTime updatedDeliveryTime = LocalDateTime.parse(deliveryTime);
            Order updatedOrder = orderService.updateOrderDeliveryTime(orderId, updatedDeliveryTime);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Delete an order
    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok("Order deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }
    }

    // Get popular ordered items globally
    @GetMapping("/popular-items")
    public ResponseEntity<List<MenuItem>> getPopularOrderedItems() {
        try {
            List<MenuItem> popularItems = orderService.getPopularOrderedItems();
            return ResponseEntity.ok(popularItems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Process payment for an order
    @PutMapping("/payment/{orderId}")
    public ResponseEntity<String> processPayment(@PathVariable Long orderId, @RequestParam String paymentMethod) {
        try {
            String paymentResult = orderService.processPayment(orderId, paymentMethod);
            return ResponseEntity.ok(paymentResult);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Cancel an order
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
        try {
            String cancellationResult = orderService.cancelOrder(orderId);
            return ResponseEntity.ok(cancellationResult);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Apply a discount to an order
    @PutMapping("/discount/{orderId}")
    public ResponseEntity<Order> applyDiscount(@PathVariable Long orderId, @RequestParam String couponCode) {
        try {
            Order discountedOrder = orderService.applyDiscount(orderId, couponCode);
            return ResponseEntity.ok(discountedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Reorder an existing order
    @PostMapping("/reorder/{orderId}")
    public ResponseEntity<Order> reorder(@PathVariable Long orderId) {
        try {
            Order reorderedOrder = orderService.reorder(orderId);
            return ResponseEntity.status(HttpStatus.CREATED).body(reorderedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Estimate delivery time for an order
    @GetMapping("/estimate-delivery/{orderId}")
    public ResponseEntity<LocalDateTime> estimateDeliveryTime(@PathVariable Long orderId) {
        try {
            LocalDateTime estimatedTime = orderService.estimateDeliveryTime(orderId);
            return ResponseEntity.ok(estimatedTime);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Notify customer if delivery is near
    @PostMapping("/notify-near/{orderId}")
    public ResponseEntity<String> notifyCustomerIfNear(@PathVariable Long orderId) {
        try {
            orderService.notifyCustomerIfNear(orderId);
            return ResponseEntity.ok("Customer notified successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error notifying customer.");
        }
    }

    // Calculate delivery distance for an order
    @GetMapping("/calculate-distance/{orderId}")
    public ResponseEntity<Double> calculateDistance(@PathVariable Long orderId) {
        try {
            double distance = orderService.calculateDistance(orderId);
            return ResponseEntity.ok(distance);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    // notify customer if order is late
    @PostMapping("/notify-late-customer/{orderId}")
    public ResponseEntity<String> notifyCustomerIfLate(@PathVariable Long orderId) {
        try {
            orderService.notifyCustomerIfOrderIsLate(orderId);
            return ResponseEntity.ok("Customer notified successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error notifying customer.");
        }
    }
    // notify restaurant if order is late
    @PostMapping("/notify-late-restaurant/{orderId}")
    public ResponseEntity<String> notifyRestaurantIfLate(@PathVariable Long orderId) {
        try {
            orderService.notifyRestaurantIfOrderIsLate(orderId);
            return ResponseEntity.ok("Restaurant notified successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error notifying restaurant.");
        }
    }
}
