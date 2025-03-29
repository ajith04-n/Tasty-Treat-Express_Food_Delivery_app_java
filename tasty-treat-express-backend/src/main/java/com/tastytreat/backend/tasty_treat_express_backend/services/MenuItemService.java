package com.tastytreat.backend.tasty_treat_express_backend.services;

import java.util.List;

import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;

public interface MenuItemService {
    MenuItem addMenuItem(String restaurantId, MenuItem menuItem);

    MenuItem getMenuItemById(long id);

    List<MenuItem> getAllMenuItems();

    List<MenuItem> getAllMenuItemsByRestaurant(String restaurantId);

    List<MenuItem> getMenuItemsByCategory(String category);

    MenuItem updateMenuItem(MenuItem menuItem);

    void deleteMenuItem(long id);

    List<MenuItem> addMenuItems(String restaurantId, List<MenuItem> menuItems);

    List<MenuItem> getPopularMenuItems();

    List<MenuItem> getPopularMenuItemsByRestaurant(String restaurantId);

    List<MenuItem> getTopNPopularMenuItems(int limit);

    void adjustMenuBasedOnFeedback();

    // Newly Added Methods
    List<Feedback> getFeedbackForMenuItem(Long menuItemId);

    MenuItem toggleMenuItemAvailability(Long menuItemId);

    List<MenuItem> updatePricesByCategory(String category, double percentageIncrease);

    List<MenuItem> getAvailableMenuItemsByRestaurant(String restaurantId);

    void adjustMenuStatusBasedOnOrderCount();

	MenuItem updateMenuItemQnty(MenuItem menuItem, int quantity);

    boolean existsByNameAndRestaurantId(String name, String restaurantId);

    boolean existsById(long id);

    boolean existsByRestaurantId(String restaurantId); 
}
