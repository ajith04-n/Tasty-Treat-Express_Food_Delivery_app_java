package com.tastytreat.backend.tasty_treat_express_backend.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.management.relation.RelationNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tastyTreatExpress.DTO.OrderDTO;
import com.tastyTreatExpress.DTO.OrderMapper;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.InvalidEnumValueException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.InvalidCouponException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.InvalidInputException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.InvalidOrderStatusException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.InvalidPaymentMethodException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.RestaurantNotFoundException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.OrderNotFoundException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.OrderValidationException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.ReportNotFoundException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.SuccessResponse;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.UserNotFoundException;
import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;
import com.tastytreat.backend.tasty_treat_express_backend.services.OrderService;
import com.tastytreat.backend.tasty_treat_express_backend.services.RestaurantService;
import com.tastytreat.backend.tasty_treat_express_backend.services.UserService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private RestaurantService restaurantService;

    // // Place an order
    @PostMapping("/placeOrder/{userId}/{restaurantId}")
    public ResponseEntity<OrderDTO> placeOrder(
            @PathVariable Long userId,
            @PathVariable String restaurantId,
            @RequestBody Order orderobj) {

        System.out.println("order req came..."+ orderobj.toString());

        if (userId == null || !userService.existsById(userId)) {
            throw new UserNotFoundException("User not found with id " + userId);
        }

        if (restaurantId == null || !restaurantService.existsById(restaurantId)) {
            throw new RestaurantNotFoundException("Restaurant not found with id " + restaurantId);
        }

        if (orderobj == null || orderobj.getMenuItems().isEmpty()) {
            throw new OrderValidationException("Order must contain at least one item.");
        }

        if (orderobj.getPaymentMethod() == null) {
            throw new OrderValidationException("Payment method is required.");
        }

        for (MenuItem item : orderobj.getMenuItems()) {
            if (item.getId() == null || item.getQuantity() <= 0) {
                throw new OrderValidationException("Invalid menu item: " + item);
            }
        }
        if (orderobj.getDeliveryAddress() == null || orderobj.getDeliveryAddress().isEmpty()) {
            throw new OrderValidationException("Delivery address is required.");
        }

        Order placedOrder = orderService.placeOrder(userId, restaurantId, orderobj);
        OrderDTO placedOrderDTO = OrderMapper.toOrderDTO(placedOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(placedOrderDTO);
    }

    // Retrieve order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                throw new OrderNotFoundException("Order not found with id " + orderId);
            }
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

    // Get orders by User ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUser(@PathVariable Long userId) {
        try {
            List<Order> orders = orderService.getOrdersByCustomer(userId);
            List<OrderDTO> orderDTOs = orders.stream()
                    .map(OrderMapper::toOrderDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(orderDTOs);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/updateStatus/{orderId}/{status}")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @PathVariable String status) {

        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            throw new OrderNotFoundException("Order not found with id " + orderId);
        }

        List<String> validStatuses = Arrays.asList("PREPARING", "PENDING", "SHIPPED", "DELIVERED", "CANCELLED");

        if (!validStatuses.contains(status.toUpperCase())) {
            throw new InvalidOrderStatusException("Invalid order status: " + status);
        }

        Order updatedOrder = orderService.updateOrderStatus(orderId, status.toUpperCase());
        OrderDTO orderDTO = OrderMapper.toOrderDTO(updatedOrder);

        return ResponseEntity.ok(orderDTO);
    }

    @PutMapping("/updateDeliveryTime/{orderId}/{deliveryTime}")
    public ResponseEntity<OrderDTO> updateOrderDeliveryTime(
            @PathVariable Long orderId,
            @PathVariable String deliveryTime) {

        try {
            LocalDateTime updatedDeliveryTime = LocalDateTime.parse(deliveryTime,
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Order updatedOrder = orderService.updateOrderDeliveryTime(orderId, updatedDeliveryTime);
            OrderDTO updatedOrderDTO = OrderMapper.toOrderDTO(updatedOrder);

            return ResponseEntity.ok(updatedOrderDTO);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Invalid date format. Please use ISO format (YYYY-MM-DDTHH:MM:SS).");
        }
    }

    // Delete an order
    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        if (orderId == null) {
            throw new InvalidInputException("Order ID is required.");
        }
        if (orderService.getOrderById(orderId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }
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
    @PutMapping("/payment/{orderId}/{paymentMethod}")
    public ResponseEntity<SuccessResponse> processPayment(
            @PathVariable Long orderId,
            @PathVariable String paymentMethod) {

        if (paymentMethod.trim().isEmpty()) {
            throw new InvalidPaymentMethodException("Payment method cannot be empty.");
        }
        String paymentResult = orderService.processPayment(orderId, paymentMethod);
        return ResponseEntity.ok(new SuccessResponse("Payment processed successfully", paymentResult));
    }

    // Cancel an order
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
        if (orderId == null) {
            throw new InvalidInputException("Order ID is required.");
        }
        if (orderService.getOrderById(orderId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }
        try {
            String cancellationResult = orderService.cancelOrder(orderId);
            return ResponseEntity.ok(cancellationResult);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Apply a discount to an order
    @PutMapping("/discount/{orderId}/{couponCode}")
    public ResponseEntity<OrderDTO> applyDiscount(
            @PathVariable Long orderId,
            @PathVariable String couponCode) {

        if (couponCode.trim().isEmpty()) {
            throw new InvalidCouponException("Coupon code cannot be empty.");
        }
        Order discountedOrder = orderService.applyDiscount(orderId, couponCode);
        OrderDTO discountedOrderDTO = OrderMapper.toOrderDTO(discountedOrder);
        return ResponseEntity.ok(discountedOrderDTO);
    }

    // Reorder an existing order
    @PostMapping("/reorder/{orderId}")
    public ResponseEntity<OrderDTO> reorder(@PathVariable Long orderId) {
        if (orderId == null) {
            throw new InvalidInputException("Order ID is required.");
        }
        if (orderService.getOrderById(orderId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
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
        if (orderId == null) {
            throw new InvalidInputException("Order ID is required.");
        }
        if (orderService.getOrderById(orderId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        try {
            LocalDateTime estimatedTime = orderService.estimateDeliveryTime(orderId);
            return ResponseEntity.ok(estimatedTime);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Notify User if delivery is near
    @PostMapping("/notify-near/{orderId}")
    public ResponseEntity<String> notifyUserIfNear(@PathVariable Long orderId) {
        if (orderId == null) {
            throw new InvalidInputException("Order ID is required.");
        }
        if (orderService.getOrderById(orderId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }
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
    @PostMapping("/notify-late-user/{orderId}")
    public ResponseEntity<String> notifyUserIfLate(@PathVariable Long orderId) {
        try {
            orderService.notifyCustomerIfOrderIsLate(orderId);
            return ResponseEntity.ok("User notified successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error notifying User.");
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
