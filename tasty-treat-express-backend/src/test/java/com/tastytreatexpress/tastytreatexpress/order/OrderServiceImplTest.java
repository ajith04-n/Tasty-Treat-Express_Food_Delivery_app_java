package com.tastytreatexpress.tastytreatexpress.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;
import com.tastytreat.backend.tasty_treat_express_backend.models.Restaurant;
import com.tastytreat.backend.tasty_treat_express_backend.models.User;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.MenuItemRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.OrderRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.RestaurantRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.UserRepository;
import com.tastytreat.backend.tasty_treat_express_backend.services.NotificationService;
import com.tastytreat.backend.tasty_treat_express_backend.services.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private NotificationService notificationService;

    private Order order;
    private User user;
    private Restaurant restaurant;
    private MenuItem menuItem;

    @BeforeEach
    public void setUp() {
        user = new User("diwakar.allu.3435@gmail.com", "Diwakar Allu", "password", "123 Street", "1234567890");

        restaurant = new Restaurant("Test Restaurant", "123 Restaurant Street", "Great food!", "test@example.com",
                "password123", "1234567890");

        menuItem = new MenuItem("Pizza", "Delicious pizza", 200.0, "Italian", null, 10);
        menuItem.setId(1L);

        order = new Order();
        order.setOrderId(1L);
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setMenuItems(List.of(menuItem));
        order.setTotalAmount(400.0);
        order.setStatus("Pending");
        order.setDeliveryAddress("123 Delivery Street");
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveryTime(LocalDateTime.now().plusHours(1));
        order.setCurrentLatitude(12.3456);
        order.setCurrentLongitude(78.9012);
    }

    // @Test
    // public void testPlaceOrder_Success() {
    // Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    // Mockito.when(restaurantRepository.findById("R1")).thenReturn(Optional.of(restaurant));
    // Mockito.when(menuItemRepository.findById(menuItem.getId())).thenReturn(Optional.of(menuItem));
    // Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenReturn(order);

    // Order placedOrder = orderService.placeOrder(1L, "R1", List.of(menuItem), "123
    // Delivery Street", "CreditCard");

    // Assertions.assertNotNull(placedOrder);
    // Assertions.assertEquals("Pending", placedOrder.getStatus());
    // Assertions.assertEquals(400.0, placedOrder.getTotalAmount());
    // Mockito.verify(orderRepository,
    // Mockito.times(1)).save(Mockito.any(Order.class));
    // }

    // @Test
    // public void testPlaceOrder_MenuItemOutOfStock() {
    // menuItem.setQuantity(0); // No stock
    // Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    // Mockito.when(restaurantRepository.findById("R1")).thenReturn(Optional.of(restaurant));
    // Mockito.when(menuItemRepository.findById(menuItem.getId())).thenReturn(Optional.of(menuItem));

    // RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
    // () -> {
    // orderService.placeOrder(1L, "R1", List.of(menuItem), "123 Delivery Street",
    // "CreditCard");
    // });

    // Assertions.assertEquals("Item Pizza is out of stock. Available quantity: 0",
    // exception.getMessage());
    // }

    @Test
    public void testGetOrderById_Success() {
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order foundOrder = orderService.getOrderById(1L);

        Assertions.assertNotNull(foundOrder);
        Assertions.assertEquals(1L, foundOrder.getOrderId());
        Mockito.verify(orderRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    public void testGetOrderById_NotFound() {
        Mockito.when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            orderService.getOrderById(999L);
        });

        Assertions.assertEquals("Order not found with id 999", exception.getMessage());
    }

    @Test
    public void testGetOrdersByRestaurant() {
        List<Order> orders = List.of(order);
        Mockito.when(orderRepository.findByRestaurantRestaurantId("R1")).thenReturn(orders);

        List<Order> foundOrders = orderService.getOrdersByRestaurant("R1");

        Assertions.assertEquals(1, foundOrders.size());
        Assertions.assertEquals(order.getOrderId(), foundOrders.get(0).getOrderId());
    }

    @Test
    public void testUpdateOrderStatus_Success() {
        order.setStatus("Pending");
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenReturn(order);

        Order updatedOrder = orderService.updateOrderStatus(1L, "Confirmed");

        Assertions.assertEquals("Confirmed", updatedOrder.getStatus());
        Mockito.verify(orderRepository, Mockito.times(1)).save(order);
    }

    @Test
    public void testDeleteOrder_Success() {
        Mockito.when(orderRepository.existsById(1L)).thenReturn(true);
        Mockito.doNothing().when(orderRepository).deleteById(1L);

        Assertions.assertDoesNotThrow(() -> orderService.deleteOrder(1L));
        Mockito.verify(orderRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteOrder_NotFound() {
        Mockito.when(orderRepository.existsById(999L)).thenReturn(false);

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            orderService.deleteOrder(999L);
        });

        Assertions.assertEquals("Order not found with id 999", exception.getMessage());
    }

    @Test
    public void testApplyDiscount_ValidCoupon() {
        order.setTotalAmount(500.0);
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenReturn(order);

        Order discountedOrder = orderService.applyDiscount(1L, "DISCOUNT10");

        Assertions.assertEquals(450.0, discountedOrder.getTotalAmount()); // 10% discount
    }

    @Test
    public void testApplyDiscount_InvalidCoupon() {
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            orderService.applyDiscount(1L, "INVALID");
        });

        Assertions.assertEquals("Invalid coupon code", exception.getMessage());
    }

    @Test
    public void testNotifyCustomerIfNear_Success() throws Exception {
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        Mockito.doNothing().when(notificationService).sendNotification(Mockito.anyLong(), Mockito.anyString());

        orderService.notifyCustomerIfNear(1L);

        Mockito.verify(notificationService, Mockito.times(1)).sendNotification(Mockito.eq(order.getUser().getId()),
                Mockito.contains("Your order is arriving soon!"));
    }

}
