package com.tastytreatexpress.tastytreatexpress.menu;

import java.util.List;

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
import com.tastytreat.backend.tasty_treat_express_backend.repositories.MenuItemRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.RestaurantRepository;
import com.tastytreat.backend.tasty_treat_express_backend.services.MenuItemServiceImpl;

@ExtendWith(MockitoExtension.class)
public class MenuItemServiceImplTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private MenuItemServiceImpl menuItemService;

    private Restaurant restaurant;
    private MenuItem menuItem;

    @BeforeEach
    public void setUp() {
        restaurant = new Restaurant(
                "Test Restaurant",
                "Test Address",
                "Description",
                "test@example.com",
                "password123",
                "1234567890");

        restaurant.setRestaurantId("1");

        menuItem = new MenuItem(
                "Pizza",
                "Delicious pizza",
                200.0,
                "Italian",
                "http://example.com/pizza.jpg",
                10);

        menuItem.setId(1L);
    }

    // Test addMenuItem
    @Test
    public void testAddMenuItem_Success() {
        Mockito.when(restaurantRepository.existsById("1")).thenReturn(true);
        Mockito.when(restaurantRepository.findById("1")).thenReturn(java.util.Optional.ofNullable(restaurant));
        Mockito.when(menuItemRepository.save(Mockito.any(MenuItem.class))).thenReturn(menuItem);

        MenuItem createdMenuItem = menuItemService.addMenuItem("1", menuItem);

        Assertions.assertNotNull(createdMenuItem);
        Assertions.assertEquals("Pizza", createdMenuItem.getName());
        Mockito.verify(menuItemRepository, Mockito.times(1)).save(menuItem);
    }

    @Test
    public void testAddMenuItem_Failure_RestaurantNotFound() {
        Mockito.when(restaurantRepository.existsById("2")).thenReturn(false);

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            menuItemService.addMenuItem("2", menuItem);
        });

        Assertions.assertEquals("Restaurant with id 2 does not exist", exception.getMessage());
        Mockito.verify(menuItemRepository, Mockito.never()).save(menuItem);
    }

    // Test getMenuItemById
    @Test
    public void testGetMenuItemById_Success() {
        Mockito.when(menuItemRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(menuItem));

        MenuItem foundMenuItem = menuItemService.getMenuItemById(1L);

        Assertions.assertNotNull(foundMenuItem);
        Assertions.assertEquals("Pizza", foundMenuItem.getName());
        Mockito.verify(menuItemRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    public void testGetMenuItemById_Failure_NotFound() {
        Mockito.when(menuItemRepository.findById(2L)).thenReturn(java.util.Optional.empty());

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            menuItemService.getMenuItemById(2L);
        });

        Assertions.assertEquals("MenuItem not found with id 2", exception.getMessage());
        Mockito.verify(menuItemRepository, Mockito.times(1)).findById(2L);
    }

    // Test getAllMenuItems
    @Test
    public void testGetAllMenuItems() {
        List<MenuItem> menuItems = List.of(menuItem);
        Mockito.when(menuItemRepository.findAll()).thenReturn(menuItems);

        List<MenuItem> foundMenuItems = menuItemService.getAllMenuItems();

        Assertions.assertEquals(1, foundMenuItems.size());
        Assertions.assertEquals("Pizza", foundMenuItems.get(0).getName());
        Mockito.verify(menuItemRepository, Mockito.times(1)).findAll();
    }

    // Test getAllMenuItemsByRestaurant
    @Test
    public void testGetAllMenuItemsByRestaurant_Success() {
        List<MenuItem> menuItems = List.of(menuItem);
        Mockito.when(restaurantRepository.existsById("1")).thenReturn(true);
        Mockito.when(menuItemRepository.findByRestaurantRestaurantId("1")).thenReturn(menuItems);

        List<MenuItem> foundMenuItems = menuItemService.getAllMenuItemsByRestaurant("1");

        Assertions.assertEquals(1, foundMenuItems.size());
        Assertions.assertEquals("Pizza", foundMenuItems.get(0).getName());
        Mockito.verify(menuItemRepository, Mockito.times(1)).findByRestaurantRestaurantId("1");
    }

    @Test
    public void testGetAllMenuItemsByRestaurant_Failure_RestaurantNotFound() {
        Mockito.when(restaurantRepository.existsById("2")).thenReturn(false);

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            menuItemService.getAllMenuItemsByRestaurant("2");
        });

        Assertions.assertEquals("Restaurant with id 2 does not exist", exception.getMessage());
        Mockito.verify(menuItemRepository, Mockito.never()).findByRestaurantRestaurantId("2");
    }

    // Test updateMenuItem
    @Test
    public void testUpdateMenuItem_Success() {
        Mockito.when(menuItemRepository.existsById(1L)).thenReturn(true);
        Mockito.when(menuItemRepository.save(Mockito.any(MenuItem.class))).thenReturn(menuItem);

        MenuItem updatedMenuItem = menuItemService.updateMenuItem(menuItem);

        Assertions.assertNotNull(updatedMenuItem);
        Assertions.assertEquals("Pizza", updatedMenuItem.getName());
        Mockito.verify(menuItemRepository, Mockito.times(1)).save(menuItem);
    }


    @Test
    public void testUpdateMenuItem_Failure_NotFound() {
        // Ensure the menuItem ID matches the stubbed value
        menuItem.setId(2L);

        Mockito.when(menuItemRepository.existsById(menuItem.getId())).thenReturn(false);

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            menuItemService.updateMenuItem(menuItem);
        });

        Assertions.assertEquals("MenuItem not found with id 2", exception.getMessage());
        Mockito.verify(menuItemRepository, Mockito.times(1)).existsById(menuItem.getId());
        Mockito.verify(menuItemRepository, Mockito.never()).save(menuItem);
    }

    // Test deleteMenuItem
    @Test
    public void testDeleteMenuItem_Success() {
        Mockito.when(menuItemRepository.existsById(1L)).thenReturn(true);

        menuItemService.deleteMenuItem(1L);

        Mockito.verify(menuItemRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteMenuItem_Failure_NotFound() {
        Mockito.when(menuItemRepository.existsById(2L)).thenReturn(false);

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            menuItemService.deleteMenuItem(2L);
        });

        Assertions.assertEquals("MenuItem not found with id 2", exception.getMessage());
        Mockito.verify(menuItemRepository, Mockito.never()).deleteById(2L);
    }

    // Test getPopularMenuItems
    @Test
    public void testGetPopularMenuItems() {
        List<MenuItem> menuItems = List.of(menuItem);
        Mockito.when(menuItemRepository.findPopularMenuItems()).thenReturn(menuItems);

        List<MenuItem> popularMenuItems = menuItemService.getPopularMenuItems();

        Assertions.assertEquals(1, popularMenuItems.size());
        Assertions.assertEquals("Pizza", popularMenuItems.get(0).getName());
        Mockito.verify(menuItemRepository, Mockito.times(1)).findPopularMenuItems();
    }

    @Test
    public void testAddMenuItems_Success() {
        List<MenuItem> menuItems = List.of(
                new MenuItem("Pizza", "Delicious pizza", 200.0, "Italian", "http://example.com/pizza.jpg", 10),
                new MenuItem("Burger", "Tasty burger", 150.0, "Fast Food", "http://example.com/burger.jpg", 20));

        Mockito.when(restaurantRepository.findById("1")).thenReturn(java.util.Optional.ofNullable(restaurant));

        List<MenuItem> addedMenuItems = menuItemService.addMenuItems("1", menuItems);

        Assertions.assertEquals(2, addedMenuItems.size());
        Assertions.assertEquals("Pizza", addedMenuItems.get(0).getName());
        Mockito.verify(menuItemRepository, Mockito.times(2)).save(Mockito.any(MenuItem.class));
    }

    @Test
    public void testGetPopularMenuItemsByRestaurant_Success() {
        List<MenuItem> menuItems = List.of(menuItem);
        Mockito.when(restaurantRepository.existsById("1")).thenReturn(true);
        Mockito.when(menuItemRepository.findPopularMenuItemsByRestaurant("1")).thenReturn(menuItems);

        List<MenuItem> popularMenuItems = menuItemService.getPopularMenuItemsByRestaurant("1");

        Assertions.assertEquals(1, popularMenuItems.size());
        Assertions.assertEquals("Pizza", popularMenuItems.get(0).getName());
        Mockito.verify(menuItemRepository, Mockito.times(1)).findPopularMenuItemsByRestaurant("1");
    }

    @Test
public void testGetTopNPopularMenuItems_Success() {
    List<MenuItem> menuItems = List.of(menuItem);
    Mockito.when(menuItemRepository.findTopPopularMenuItems(Mockito.any(PageRequest.class))).thenReturn(menuItems);

    List<MenuItem> topMenuItems = menuItemService.getTopNPopularMenuItems(5);

    Assertions.assertEquals(1, topMenuItems.size());
    Assertions.assertEquals("Pizza", topMenuItems.get(0).getName());
    Mockito.verify(menuItemRepository, Mockito.times(1)).findTopPopularMenuItems(Mockito.any(PageRequest.class));
}

// @Test
// public void testAdjustMenuBasedOnFeedback_Success() {
//     List<MenuItem> menuItems = List.of(menuItem);
//     Float avgRating = 4.8f;

//     Mockito.when(menuItemRepository.findAll()).thenReturn(menuItems);
//     Mockito.when(feedbackRepository.getAverageRatingForMenuItem(menuItem)).thenReturn(avgRating);

//     menuItemService.adjustMenuBasedOnFeedback();

//     Assertions.assertEquals("Popular", menuItem.getStatus());
//     Mockito.verify(menuItemRepository, Mockito.times(1)).save(menuItem);
// }

// @Test
// public void testGetFeedbackForMenuItem_Success() {
//     List<Feedback> feedbacks = List.of(new Feedback(5, "Excellent!"), new Feedback(4, "Very good!"));
//     menuItem.setFeedbacks(feedbacks);
//     Mockito.when(menuItemRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(menuItem));

//     List<Feedback> foundFeedbacks = menuItemService.getFeedbackForMenuItem(1L);

//     Assertions.assertEquals(2, foundFeedbacks.size());
//     Assertions.assertEquals(5, foundFeedbacks.get(0).getRating());
//     Mockito.verify(menuItemRepository, Mockito.times(1)).findById(1L);
// }

@Test
public void testToggleMenuItemAvailability_Success() {
    menuItem.setIsAvailable(true);
    Mockito.when(menuItemRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(menuItem));
    Mockito.when(menuItemRepository.save(Mockito.any(MenuItem.class))).thenReturn(menuItem);

    MenuItem updatedMenuItem = menuItemService.toggleMenuItemAvailability(1L);

    Assertions.assertFalse(updatedMenuItem.getIsAvailable());
    Mockito.verify(menuItemRepository, Mockito.times(1)).findById(1L);
    Mockito.verify(menuItemRepository, Mockito.times(1)).save(menuItem);
}

@Test
public void testUpdatePricesByCategory_Success() {
    List<MenuItem> menuItems = List.of(menuItem);
    Mockito.when(menuItemRepository.findByCategory("Italian")).thenReturn(menuItems);

    List<MenuItem> updatedMenuItems = menuItemService.updatePricesByCategory("Italian", 10);

    Assertions.assertEquals(220.0, updatedMenuItems.get(0).getPrice()); // 200 + 10% increase
    Mockito.verify(menuItemRepository, Mockito.times(1)).save(Mockito.any(MenuItem.class));
}

@Test
public void testGetAvailableMenuItemsByRestaurant_Success() {
    menuItem.setIsAvailable(true);
    List<MenuItem> menuItems = List.of(menuItem);
    restaurant.setMenu(menuItems);

    Mockito.when(restaurantRepository.findById("1")).thenReturn(java.util.Optional.ofNullable(restaurant));

    List<MenuItem> availableMenuItems = menuItemService.getAvailableMenuItemsByRestaurant("1");

    Assertions.assertEquals(1, availableMenuItems.size());
    Assertions.assertTrue(availableMenuItems.get(0).getIsAvailable());
    Mockito.verify(restaurantRepository, Mockito.times(1)).findById("1");
}

@Test
public void testAdjustMenuStatusBasedOnOrderCount_Success() {
    List<MenuItem> menuItems = List.of(menuItem);
    List<Order> orders = List.of(new Order(), new Order(), new Order());
    menuItem.setOrders(orders); 

    Mockito.when(menuItemRepository.findAll()).thenReturn(menuItems);

    menuItemService.adjustMenuStatusBasedOnOrderCount();

    Assertions.assertEquals("LowDemand", menuItem.getStatus()); 
    Mockito.verify(menuItemRepository, Mockito.times(1)).save(menuItem);
}

}
