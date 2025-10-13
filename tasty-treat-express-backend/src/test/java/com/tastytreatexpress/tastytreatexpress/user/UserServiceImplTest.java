package com.tastytreatexpress.tastytreatexpress.user;

import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.Restaurant;
import com.tastytreat.backend.tasty_treat_express_backend.models.User;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.UserRepository;
import com.tastytreat.backend.tasty_treat_express_backend.services.UserService;
import com.tastytreat.backend.tasty_treat_express_backend.services.UserServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    public void testSaveUser() {
        User user = new User("test@example.com", "Test User", "password", "123 Test St", "1234567890");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        User savedUser = userService.saveUser(user);

        Assertions.assertEquals("test@example.com", savedUser.getEmail());
        Assertions.assertEquals("Test User", savedUser.getName());
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    public void testFindUserByEmail() {
        User user = new User("test@example.com", "Test User", "password", "123 Test St", "1234567890");
        Mockito.when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User foundUser = userService.findUserByEmail("test@example.com");

        Assertions.assertEquals("Test User", foundUser.getName());
        Mockito.verify(userRepository, Mockito.times(1)).findByEmail("test@example.com");
    }

    @Test
    public void testAuthenticateUser() {
        User user = new User("test@example.com", "Test User", passwordEncoder.encode("password"), "123 Test St",
                "1234567890");
        Mockito.when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        boolean isAuthenticated = userService.authenticateUser("test@example.com", "password");

        Assertions.assertTrue(isAuthenticated);
        Mockito.verify(userRepository, Mockito.times(1)).findByEmail("test@example.com");
    }

    @Test
    public void testExistsByEmail() {
        Mockito.when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean exists = userService.existsByEmail("test@example.com");

        Assertions.assertTrue(exists);
        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail("test@example.com");
    }

    @Test
    public void testFindAllUsers() {
        List<User> users = List.of(new User("test1@example.com", "Test User 1", "password", "Address 1", "1234567890"),
                new User("test2@example.com", "Test User 2", "password", "Address 2", "0987654321"));
        Mockito.when(userRepository.findAll()).thenReturn(users);

        List<User> foundUsers = userService.findAll();

        Assertions.assertEquals(2, foundUsers.size());
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void testGetUserById() {
        User user = new User("test@example.com", "Test User", "password", "123 Test St", "1234567890");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(1L);

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals("test@example.com", foundUser.getEmail());
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    public void testUpdateUser() {
        User user = new User("test@example.com", "Test User", "password", "123 Test St", "1234567890");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        long userId = user.getId();
        User updatedUser = userService.updateUser(userId, user);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals("Test User", updatedUser.getName());
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    public void testDeleteUser() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        Mockito.verify(userRepository, Mockito.times(1)).existsById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
    }

    // @Test
    // public void testGenerateUserOrderSummaryReport() {
    // User user = new User("test@example.com", "Test User", "password", "123 Test
    // St", "1234567890");
    // List<Order> orders = List.of(
    // new Order(1L, "PLACED", 100.0, LocalDateTime.now(), user),
    // new Order(2L, "DELIVERED", 200.0, LocalDateTime.now().minusDays(1), user)
    // );
    // Mockito.when(orderRepository.findByCustomer_Id(1L)).thenReturn(orders);

    // Map<String, Object> report = userService.generateUserOrderSummaryReport(1L);

    // Assertions.assertEquals(2, report.get("totalOrders"));
    // Assertions.assertEquals(300.0, report.get("totalRevenue"));
    // Mockito.verify(orderRepository, Mockito.times(1)).findByCustomer_Id(1L);
    // }

    // @Test
    // public void testAddFeedback() {
    // User user = new User("test@example.com", "Test User", "password", "123 Test
    // St", "1234567890");
    // Restaurant restaurant = new Restaurant(1L, "Test Restaurant", "Cuisine",
    // "Location");
    // Feedback feedback = new Feedback(5, "Great food!", user, restaurant);

    // Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    // Mockito.when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
    // Mockito.when(feedbackRepo.save(Mockito.any(Feedback.class))).thenReturn(feedback);

    // Feedback savedFeedback = userService.addFeedback(1L, 1L, feedback);

    // Assertions.assertNotNull(savedFeedback);
    // Assertions.assertEquals(5, savedFeedback.getRating());
    // Mockito.verify(feedbackRepo, Mockito.times(1)).save(feedback);
    // }

}

// class UserServiceImplTest {
//
// @Mock
// private UserRepository userRepository;
//
// @InjectMocks
// private UserService userService;
//
// private Optional<User> user;
//
// @BeforeEach
// void setUp() {
// MockitoAnnotations.openMocks(this);
// user = Optional.ofNullable(new User());
// user.get().setEmail("test@example.com");
// user.get().setPassword("password123");
// }
//
// @Test
// void testSaveUser() {
// BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
// String hashedPassword = encoder.encode(user.getPassword());
// user.setPassword(hashedPassword);
//
//
// when(userRepository.save(any(User.class))).thenReturn(user);
//
//
// User savedUser = userService.saveUser(user);
//
// // Assertions
// assertNotNull(savedUser);
// assertEquals(user.getEmail(), savedUser.getEmail());
// assertTrue(encoder.matches("password123", savedUser.getPassword()));
// }
//
// @Test
// void testAuthenticateUser_Success() {
// // Set up mock user with hashed password
// BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
// String hashedPassword = encoder.encode("password123");
// user.setPassword(hashedPassword);
//
// // Mock repository find behavior
// when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
//
// // Call the authenticateUser method
// boolean isAuthenticated = userService.authenticateUser(user.getEmail(),
// "password123");
//
// // Assertions
// assertTrue(isAuthenticated); // Authentication should succeed
// }
//
// @Test
// void testAuthenticateUser_Failure() {
// // Mock repository to return null for non-existing user
// when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
//
// // Call the authenticateUser method with incorrect credentials
// boolean isAuthenticated = userService.authenticateUser(user.getEmail(),
// "wrongpassword");
//
// // Assertions
// assertFalse(isAuthenticated); // Authentication should fail because user
// doesn't exist
// }
//
// @Test
// void testAuthenticateUser_IncorrectPassword() {
// // Set up mock user with hashed password
// BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
// String hashedPassword = encoder.encode("password123");
// user.setPassword(hashedPassword);
//
// // Mock repository find behavior
// when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
//
// // Call the authenticateUser method with incorrect password
// boolean isAuthenticated = userService.authenticateUser(user.getEmail(),
// "incorrectPassword");
//
// // Assertions
// assertFalse(isAuthenticated); // Authentication should fail because password
// doesn't match
// }
// }
