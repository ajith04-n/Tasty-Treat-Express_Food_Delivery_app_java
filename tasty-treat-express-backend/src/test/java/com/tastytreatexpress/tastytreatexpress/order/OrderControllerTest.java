package com.tastytreatexpress.tastytreatexpress.order;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.tastytreat.backend.tasty_treat_express_backend.TastytreatexpressApplication;
import com.tastytreat.backend.tasty_treat_express_backend.controllers.OrderController;
import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;
import com.tastytreat.backend.tasty_treat_express_backend.models.Restaurant;
import com.tastytreat.backend.tasty_treat_express_backend.models.User;
import com.tastytreat.backend.tasty_treat_express_backend.services.OrderService;
import com.tastytreat.backend.tasty_treat_express_backend.services.RestaurantService;
import com.tastytreat.backend.tasty_treat_express_backend.services.UserService;

@WebMvcTest(OrderController.class)
@SpringBootTest(classes = TastytreatexpressApplication.class)
public class OrderControllerTest {
        /*
         * @Autowired
         * private MockMvc mockMvc;
         * 
         * @MockBean
         * private OrderService orderService;
         * 
         * @MockBean
         * private RestaurantService restaurantService;
         * 
         * @MockBean
         * private UserService userService;
         * 
         * private Order order;
         * private MenuItem menuItem;
         * 
         * @BeforeEach
         * public void setUp() {
         * menuItem = new MenuItem("Pizza", "Delicious pizza", 200.0, "Italian",
         * "http://example.com/pizza.jpg",
         * 2);
         * menuItem.setId(1L);
         * 
         * order = new Order();
         * order.setOrderId(1L);
         * order.setUser(
         * new User("diwakar.allu.3435@gmail.com", "DiwakarAllu", "password",
         * "123 Street",
         * "1234567890"));
         * order.setRestaurant(
         * new Restaurant("Test Restaurant", "123 Restaurant Street", "Great food!",
         * "test@example.com",
         * "password123", "1234567890"));
         * order.setMenuItems(List.of(menuItem));
         * order.setTotalAmount(400.0);
         * order.setStatus("Pending");
         * order.setDeliveryAddress("123 Delivery Street");
         * order.setPaymentStatus("Pending");
         * order.setOrderDate(LocalDateTime.now());
         * }
         * 
         * @Test
         * public void testPlaceOrder_Success() throws Exception {
         * Mockito.when(orderService.placeOrder(Mockito.anyLong(), Mockito.anyString(),
         * Mockito.anyList(),
         * Mockito.anyString(), Mockito.anyString())).thenReturn(order);
         * 
         * mockMvc.perform(MockMvcRequestBuilders.post("/api/orders/placeOrder")
         * .param("customerId", "1")
         * .param("restaurantId", "R1")
         * .contentType(MediaType.APPLICATION_JSON)
         * .content("[{\"id\":1,\"quantity\":2}]")
         * .param("deliveryAddress", "123 Delivery Street")
         * .param("paymentMethod", "CreditCard"))
         * .andExpect(MockMvcResultMatchers.status().isCreated())
         * .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").value(order.getOrderId
         * ()))
         * .andExpect(MockMvcResultMatchers.jsonPath("$.totalAmount")
         * .value(order.getTotalAmount()));
         * }
         * 
         */
}
