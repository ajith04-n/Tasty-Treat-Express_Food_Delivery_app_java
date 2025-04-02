package com.tastytreatexpress.tastytreatexpress.feedback;

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
import org.springframework.data.domain.PageRequest;

import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;
import com.tastytreat.backend.tasty_treat_express_backend.models.Restaurant;
import com.tastytreat.backend.tasty_treat_express_backend.models.User;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.FeedbackRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.MenuItemRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.OrderRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.RestaurantRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.UserRepository;

import com.tastytreat.backend.tasty_treat_express_backend.services.FeedbackServiceImpl;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceImplTest {

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private UserRepository userRepository;

    private Feedback feedback;
    private Order order;
    private Restaurant restaurant;
    private MenuItem menuItem;
    private User user;

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

        feedback = new Feedback();
        feedback.setFeedbackId(1L);
        feedback.setOrders(order);
        feedback.setRestaurant(restaurant);
        feedback.setUser(user);
        feedback.setMenuItems(menuItem);
        feedback.setRating(5);
        feedback.setComments("Great food!");
        feedback.setFeedbackDate(LocalDateTime.now());
    }

    @Test
    public void testAddFeedback_Success() {
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        Mockito.when(restaurantRepository.findById("R1")).thenReturn(Optional.of(restaurant));
        Mockito.when(feedbackRepository.save(Mockito.any(Feedback.class))).thenReturn(feedback);

        Feedback createdFeedback = feedbackService.addFeedback(1L, "R1", feedback);

        Assertions.assertNotNull(createdFeedback);
        Assertions.assertEquals("Great food!", createdFeedback.getComments());
        Assertions.assertEquals(5, createdFeedback.getRating());
        Mockito.verify(feedbackRepository, Mockito.times(1)).save(feedback);
    }

    @Test
    public void testAddMenuItemFeedback_Success() {
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        Mockito.when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));
        Mockito.when(feedbackRepository.save(Mockito.any(Feedback.class))).thenReturn(feedback);

        Feedback createdFeedback = feedbackService.addMenuItemFeedback(1L, 1L, feedback);

        Assertions.assertNotNull(createdFeedback);
        Assertions.assertEquals("Pizza", createdFeedback.getMenuItems().getName());
        Assertions.assertEquals(5, createdFeedback.getRating());
        Mockito.verify(feedbackRepository, Mockito.times(1)).save(feedback);
    }

    @Test
    public void testUpdateFeedback_Success() {
        Feedback updatedFeedback = new Feedback();
        updatedFeedback.setRating(4);
        updatedFeedback.setComments("Good food!");

        Mockito.when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));
        Mockito.when(feedbackRepository.save(Mockito.any(Feedback.class))).thenReturn(updatedFeedback);

        Feedback result = feedbackService.updateFeedback(1L, updatedFeedback);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(4, result.getRating());
        Assertions.assertEquals("Good food!", result.getComments());
        Mockito.verify(feedbackRepository, Mockito.times(1)).save(feedback);
    }

    @Test
    public void testDeleteFeedback_Success() {
        Mockito.when(feedbackRepository.existsById(1L)).thenReturn(true);
        Mockito.doNothing().when(feedbackRepository).deleteById(1L);

        Assertions.assertDoesNotThrow(() -> feedbackService.deleteFeedback(1L));
        Mockito.verify(feedbackRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteFeedback_NotFound() {
        Mockito.when(feedbackRepository.existsById(999L)).thenReturn(false);

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            feedbackService.deleteFeedback(999L);
        });

        Assertions.assertEquals("Feedback not found with ID: 999", exception.getMessage());
    }

    @Test
    public void testCalculateAverageRatingForRestaurant() {
        Feedback feedback1 = new Feedback();
        feedback1.setFeedbackId(1L);
        feedback1.setRating(5);
        feedback1.setComments("Excellent!");
        feedback1.setRestaurant(restaurant);

        Feedback feedback2 = new Feedback();
        feedback2.setFeedbackId(2L);
        feedback2.setRating(4);
        feedback2.setComments("Good!");
        feedback2.setRestaurant(restaurant);

        // List<Feedback> feedbacks = List.of(feedback1, feedback2);

        List<Feedback> feedbacks = List.of(
                new Feedback(1L, 5, "Excellent!", null, null, null, null),
                new Feedback(2L, 4, "Good!", null, null, null, null));
        Mockito.when(feedbackRepository.findByRestaurantRestaurantId("R1")).thenReturn(feedbacks);

        double avgRating = feedbackService.calculateAverageRatingForRestaurant("R1");

        Assertions.assertEquals(4.5, avgRating);
    }

    @Test
    public void testGetFeedbackForMenuItem() {
        List<Feedback> feedbacks = List.of(feedback);
        Mockito.when(feedbackRepository.findByMenuItemId(1L)).thenReturn(feedbacks);

        List<Feedback> result = feedbackService.getFeedbackForMenuItem(1L);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Great food!", result.get(0).getComments());
        Mockito.verify(feedbackRepository, Mockito.times(1)).findByMenuItemId(1L);
    }

    @Test
    public void testGetTopFeedbacksForRestaurant() {
        List<Feedback> feedbacks = List.of(feedback);
        Mockito.when(feedbackRepository.findTopFeedbacksForRestaurant("R1", PageRequest.of(0, 1)))
                .thenReturn(feedbacks);

        List<Feedback> topFeedbacks = feedbackService.getTopFeedbacksForRestaurant("R1", 1);

        Assertions.assertEquals(1, topFeedbacks.size());
        Assertions.assertEquals("Great food!", topFeedbacks.get(0).getComments());
    }

}
