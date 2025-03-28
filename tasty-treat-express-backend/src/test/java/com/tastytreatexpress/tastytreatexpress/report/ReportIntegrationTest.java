package com.tastytreatexpress.tastytreatexpress.report;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;
import com.tastytreat.backend.tasty_treat_express_backend.models.Report;
import com.tastytreat.backend.tasty_treat_express_backend.models.Restaurant;
import com.tastytreat.backend.tasty_treat_express_backend.models.User;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.FeedbackRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.OrderRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.ReportRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.UserRepository;
import com.tastytreat.backend.tasty_treat_express_backend.services.ReportService;

@ExtendWith(MockitoExtension.class)
public class ReportIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportService reportService;

    private User mockUser;
    private Restaurant mockRestaurant;
    private MenuItem mockMenuItem;
    private Order mockOrder;

    @BeforeEach
    public void setup() {
        // Create Mock User

        mockUser = new User("test@example.com", "Test User", "password", "123 Test St", "1234567890");
        mockUser.setId(1L);
        userRepository.save(mockUser);

        // Create Mock Restaurant
        mockRestaurant = new Restaurant("Test Restaurant", "123 Food Lane", "Great Place", "test@restaurant.com",
                "password123", "9876543210");
        mockRestaurant.setRestaurantId("R1");

        // Create Mock MenuItem
        mockMenuItem = new MenuItem("Pizza", "Cheesy and delicious", 12.99, "Italian", "pizza.jpg", 10);
        mockMenuItem.setMenuId(1L);
        mockMenuItem.setRestaurant(mockRestaurant);

        // Create Mock Order
        mockOrder = new Order(mockUser, mockRestaurant, List.of(mockMenuItem), 12.99, "DELIVERED",
                "123 Delivery St", "Paid", "Credit Card", LocalDateTime.now().minusDays(1), LocalDateTime.now());
        mockOrder.setOrderId(1L);
        orderRepository.save(mockOrder);

        // Add Feedback
        Feedback feedback = new Feedback();
        feedback.setOrders(mockOrder);
        feedback.setMenuItems(mockMenuItem);
        feedback.setRating(5);
        feedback.setComments("Amazing pizza!");
        feedback.setUser(mockUser);
        feedback.setFeedbackDate(LocalDateTime.now().minusDays(1));
        feedbackRepository.save(feedback);
    }

    @Test
    public void testGenerateDailyReport() {
        LocalDate today = LocalDate.now();

        // Call the generateDailyReports method
        reportService.generateDailyReports();

        // Fetch generated report
        List<Report> reports = reportRepository.findByReportType("DAILY");

        // Validate report
        Assertions.assertFalse(reports.isEmpty());
        Report dailyReport = reports.get(0);

        Assertions.assertEquals("DAILY", dailyReport.getReportType());
        Assertions.assertEquals(1, dailyReport.getTotalOrders());
        Assertions.assertEquals(12.99, dailyReport.getTotalOrderValue());
        Assertions.assertEquals("Pizza", dailyReport.getBestSellingItem());
    }

}
