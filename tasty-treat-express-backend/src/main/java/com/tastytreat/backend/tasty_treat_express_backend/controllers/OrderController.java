package com.tastytreat.backend.tasty_treat_express_backend.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tastyTreatExpress.DTO.OrderDTO;
import com.tastyTreatExpress.DTO.OrderMapper;
import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;
import com.tastytreat.backend.tasty_treat_express_backend.services.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Place an order
    @PostMapping("/placeOrder/{userId}/{restaurantId}")
    public ResponseEntity<OrderDTO> placeOrder(
            @PathVariable Long userId,
            @PathVariable String restaurantId,
            @RequestBody Order orderobj) {
        try {
            Order placedOrder = orderService.placeOrder(userId, restaurantId, orderobj);
            OrderDTO placedOrderDTO = OrderMapper.toOrderDTO(placedOrder);
            return ResponseEntity.status(HttpStatus.CREATED).body(placedOrderDTO);
        } catch (RuntimeException e) {
        	 System.out.println(e.getMessage().toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Retrieve order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            OrderDTO orderDTO = OrderMapper.toOrderDTO(order);
            return ResponseEntity.ok(orderDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Get orders by restaurant ID
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByRestaurant(@PathVariable String restaurantId) {
        try {
            List<Order> orders = orderService.getOrdersByRestaurant(restaurantId);
            List<OrderDTO> orderDTOs = orders.stream()
                    .map(OrderMapper::toOrderDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(orderDTOs);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Get orders by customer ID
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomer(@PathVariable Long customerId) {
        try {
            List<Order> orders = orderService.getOrdersByCustomer(customerId);
            List<OrderDTO> orderDTOs = orders.stream()
                    .map(OrderMapper::toOrderDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(orderDTOs);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Update order status
    @PutMapping("/updateStatus/{orderId}")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(orderId, status);
            OrderDTO updatedOrderDTO = OrderMapper.toOrderDTO(updatedOrder);
            return ResponseEntity.ok(updatedOrderDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Update delivery time for an order
    @PutMapping("/updateDeliveryTime/{orderId}")
    public ResponseEntity<OrderDTO> updateOrderDeliveryTime(@PathVariable Long orderId,
            @RequestParam String deliveryTime) {
        try {
            LocalDateTime updatedDeliveryTime = LocalDateTime.parse(deliveryTime);
            Order updatedOrder = orderService.updateOrderDeliveryTime(orderId, updatedDeliveryTime);
            OrderDTO updatedOrderDTO = OrderMapper.toOrderDTO(updatedOrder);
            return ResponseEntity.ok(updatedOrderDTO);
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
    public ResponseEntity<OrderDTO> applyDiscount(@PathVariable Long orderId, @RequestParam String couponCode) {
        try {
            Order discountedOrder = orderService.applyDiscount(orderId, couponCode);
            OrderDTO discountedOrderDTO = OrderMapper.toOrderDTO(discountedOrder);
            return ResponseEntity.ok(discountedOrderDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Reorder an existing order
    @PostMapping("/reorder/{orderId}")
    public ResponseEntity<OrderDTO> reorder(@PathVariable Long orderId) {
        try {
            Order reorderedOrder = orderService.reorder(orderId);
            OrderDTO reorderedOrderDTO = OrderMapper.toOrderDTO(reorderedOrder);
            return ResponseEntity.status(HttpStatus.CREATED).body(reorderedOrderDTO);
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

    // Notify customer if order is late
    @PostMapping("/notify-late-customer/{orderId}")
    public ResponseEntity<String> notifyCustomerIfLate(@PathVariable Long orderId) {
        try {
            orderService.notifyCustomerIfOrderIsLate(orderId);
            return ResponseEntity.ok("Customer notified successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error notifying customer.");
        }
    }

    // Notify restaurant if order is late
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
