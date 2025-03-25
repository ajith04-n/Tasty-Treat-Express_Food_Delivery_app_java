package com.tastytreatexpress.tastytreatexpress.restaurant;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.models.Restaurant;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.MenuItemRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.RestaurantRepository;
import com.tastytreat.backend.tasty_treat_express_backend.services.RestaurantServiceImpl;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    private Restaurant restaurant;

    @BeforeEach
    public void setUp() {
        restaurant = new Restaurant(
                "Test Restaurant",
                "Test Address",
                "Description",
                "test@example.com",
                "password123",
                "1234567890");
    }

    @Test
    public void testSaveRestaurant() {
        Mockito.when(restaurantRepository.save(Mockito.any(Restaurant.class))).thenReturn(restaurant);

        Restaurant savedRestaurant = restaurantService.saveRestaurant(restaurant);

        Assertions.assertNotNull(savedRestaurant);
        Assertions.assertEquals("Test Restaurant", savedRestaurant.getName());
        Mockito.verify(restaurantRepository, Mockito.times(1)).save(restaurant);
    }

    @Test
    public void testAuthenticateRestaurant_Success() {
        restaurant.setPassword(new BCryptPasswordEncoder().encode("password123"));
        Mockito.when(restaurantRepository.findByEmail("test@example.com")).thenReturn(restaurant);

        boolean isAuthenticated = restaurantService.authenticateRestaurant("test@example.com", "password123");

        Assertions.assertTrue(isAuthenticated);
        Mockito.verify(restaurantRepository, Mockito.times(1)).findByEmail("test@example.com");
    }

    @Test
    public void testAuthenticateRestaurant_Failure() {
        Mockito.when(restaurantRepository.findByEmail("test@example.com")).thenReturn(null);

        boolean isAuthenticated = restaurantService.authenticateRestaurant("test@example.com", "wrongpassword");

        Assertions.assertFalse(isAuthenticated);
        Mockito.verify(restaurantRepository, Mockito.times(1)).findByEmail("test@example.com");
    }

    @Test
    public void testExistsByEmail() {
        Mockito.when(restaurantRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean exists = restaurantService.existsByEmail("test@example.com");

        Assertions.assertTrue(exists);
        Mockito.verify(restaurantRepository, Mockito.times(1)).existsByEmail("test@example.com");
    }

    @Test
    public void testFindByEmail() {
        Mockito.when(restaurantRepository.findByEmail("test@example.com")).thenReturn(restaurant);

        Restaurant foundRestaurant = restaurantService.findByEmail("test@example.com");

        Assertions.assertNotNull(foundRestaurant);
        Assertions.assertEquals("Test Restaurant", foundRestaurant.getName());
        Mockito.verify(restaurantRepository, Mockito.times(1)).findByEmail("test@example.com");
    }

    @Test
    public void testUpdateRestaurant() {
        Mockito.when(restaurantRepository.save(Mockito.any(Restaurant.class))).thenReturn(restaurant);

        Restaurant updatedRestaurant = restaurantService.updateRestaurant(restaurant);

        Assertions.assertNotNull(updatedRestaurant);
        Assertions.assertEquals("Test Restaurant", updatedRestaurant.getName());
        Mockito.verify(restaurantRepository, Mockito.times(1)).save(restaurant);
    }

    @Test
    public void testFindAll() {
        List<Restaurant> restaurants = List.of(restaurant);
        Mockito.when(restaurantRepository.findAll()).thenReturn(restaurants);

        List<Restaurant> foundRestaurants = restaurantService.findAll();

        Assertions.assertEquals(1, foundRestaurants.size());
        Mockito.verify(restaurantRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void testGetRestaurantById() {
        Mockito.when(restaurantRepository.findById("1")).thenReturn(java.util.Optional.ofNullable(restaurant));

        Restaurant foundRestaurant = restaurantService.getRestaurantById("1");

        Assertions.assertNotNull(foundRestaurant);
        Assertions.assertEquals("Test Restaurant", foundRestaurant.getName());
        Mockito.verify(restaurantRepository, Mockito.times(1)).findById("1");
    }

    @Test
    public void testDeleteRestaurant() {
        Mockito.doNothing().when(restaurantRepository).deleteById("1");

        restaurantService.deleteRestaurant("1");

        Mockito.verify(restaurantRepository, Mockito.times(1)).deleteById("1");
    }

    @Test
    public void testGetRestaurantMenu() {
        List<MenuItem> menu = List.of(new MenuItem("Pizza", "Delicious pizza", 200.0, "Italian", null, 10));
        restaurant.setMenu(menu);
        Mockito.when(restaurantRepository.findById("1")).thenReturn(java.util.Optional.ofNullable(restaurant));

        List<MenuItem> foundMenu = restaurantService.getRestaurantMenu("1");

        Assertions.assertEquals(1, foundMenu.size());
        Assertions.assertEquals("Pizza", foundMenu.get(0).getName());
        Mockito.verify(restaurantRepository, Mockito.times(1)).findById("1");
    }

    @Test
    public void testAddMenuItem() {
        MenuItem menuItem = new MenuItem("Burger", "Tasty burger", 150.0, "Fast Food", null, 20);
        List<MenuItem> menu = new ArrayList<>();
        restaurant.setMenu(menu);

        Mockito.when(restaurantRepository.findById("1")).thenReturn(java.util.Optional.ofNullable(restaurant));
        Mockito.when(restaurantRepository.save(Mockito.any(Restaurant.class))).thenReturn(restaurant);

        List<MenuItem> updatedMenu = restaurantService.addMenuItem("1", menuItem);

        Assertions.assertEquals(1, updatedMenu.size());
        Assertions.assertEquals("Burger", updatedMenu.get(0).getName());
        Mockito.verify(restaurantRepository, Mockito.times(1)).findById("1");
        Mockito.verify(restaurantRepository, Mockito.times(1)).save(restaurant);
    }

    // @Test
    // public void testCalculateAverageRating() {
    //     List<Feedback> feedbacks = List.of(
    //             new Feedback(4, "Great food!"),
    //             new Feedback(5, "Excellent service!"));
    //     restaurant.setFeedbacks(feedbacks);
    //     Mockito.when(restaurantRepository.findById("1")).thenReturn(java.util.Optional.ofNullable(restaurant));

    //     double averageRating = restaurantService.calculateAverageRating("1");

    //     Assertions.assertEquals(4.5, averageRating);
    //     Mockito.verify(restaurantRepository, Mockito.times(1)).findById("1");
    // }

    @Test
    public void testFindRestaurantsNearby() {
        Restaurant restaurantNearby = new Restaurant("Nearby Restaurant", "Nearby Address", null, null, null, null);
        restaurantNearby.setLatitude(10.0);
        restaurantNearby.setLongitude(10.0);

        List<Restaurant> restaurants = List.of(restaurantNearby);
        Mockito.when(restaurantRepository.findAll()).thenReturn(restaurants);

        List<Restaurant> foundRestaurants = restaurantService.findRestaurantsNearby(10.0, 10.0, 5.0);

        Assertions.assertEquals(1, foundRestaurants.size());
        Assertions.assertEquals("Nearby Restaurant", foundRestaurants.get(0).getName());
        Mockito.verify(restaurantRepository, Mockito.times(1)).findAll();
    }
}
