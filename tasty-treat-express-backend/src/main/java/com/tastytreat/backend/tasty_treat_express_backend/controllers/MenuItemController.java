package com.tastytreat.backend.tasty_treat_express_backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.services.MenuItemService;

@RestController
@RequestMapping("/api/menuItems")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    
    @PostMapping("/add/{restaurantId}")
    public ResponseEntity<MenuItem> addMenuItem(@PathVariable String restaurantId, @RequestBody MenuItem menuItem) {
        MenuItem createdMenuItem = menuItemService.addMenuItem(restaurantId, menuItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMenuItem);
    }

    @PostMapping("/addMultiMenu/{restaurantId}")
    public ResponseEntity<List<MenuItem>> addMenuItems(@PathVariable String restaurantId,
            @RequestBody List<MenuItem> menuItems) {
        List<MenuItem> addedItems = menuItemService.addMenuItems(restaurantId, menuItems);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedItems);
    }

 
    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable long id) {
        MenuItem menuItem = menuItemService.getMenuItemById(id);
        return ResponseEntity.ok(menuItem);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        List<MenuItem> menuItems = menuItemService.getAllMenuItems();
        return ResponseEntity.ok(menuItems);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItem>> getMenuItemsByRestaurant(@PathVariable String restaurantId) {
        List<MenuItem> menuItems = menuItemService.getAllMenuItemsByRestaurant(restaurantId);
        return ResponseEntity.ok(menuItems);
    }

    @PutMapping("/update")
    public ResponseEntity<MenuItem> updateMenuItem(@RequestBody MenuItem menuItem) {
        MenuItem updatedMenuItem = menuItemService.updateMenuItem(menuItem);
        return ResponseEntity.ok(updatedMenuItem);
    }
    
    @PutMapping("/update-qnty")
    public ResponseEntity<MenuItem> updateMenuItemQnty(@RequestBody MenuItem menuItem,@RequestParam int quantity) {
        MenuItem updatedMenuItem = menuItemService.updateMenuItemQnty(menuItem,quantity);
        return ResponseEntity.ok(updatedMenuItem);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable long id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/popular")
    public ResponseEntity<List<MenuItem>> getPopularMenuItems() {
        List<MenuItem> popularItems = menuItemService.getPopularMenuItems();
        return ResponseEntity.ok(popularItems);
    }

    @GetMapping("/popular/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItem>> getPopularMenuItemsByRestaurant(@PathVariable String restaurantId) {
        List<MenuItem> popularItems = menuItemService.getPopularMenuItemsByRestaurant(restaurantId);
        return ResponseEntity.ok(popularItems);
    }

    @GetMapping("/popular/top/{limit}")
    public ResponseEntity<List<MenuItem>> getTopPopularMenuItems(@PathVariable int limit) {
        List<MenuItem> topItems = menuItemService.getTopNPopularMenuItems(limit);
        return ResponseEntity.ok(topItems);
    }

   
    @PostMapping("/adjust-based-on-feedback")
    public ResponseEntity<String> adjustMenuBasedOnFeedback() {
        try {
            menuItemService.adjustMenuBasedOnFeedback();
            return ResponseEntity.ok("Menu items adjusted based on feedback successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adjusting menu items: " + e.getMessage());
        }
    }

    @GetMapping("/feedback/{menuItemId}")
    public ResponseEntity<List<Feedback>> getFeedbackForMenuItem(@PathVariable Long menuItemId) {
        List<Feedback> feedbacks = menuItemService.getFeedbackForMenuItem(menuItemId);
        return ResponseEntity.ok(feedbacks);
    }
    @PutMapping("/toggle-availability/{menuItemId}")
    public ResponseEntity<MenuItem> toggleMenuItemAvailability(@PathVariable Long menuItemId) {
        MenuItem updatedMenuItem = menuItemService.toggleMenuItemAvailability(menuItemId);
        return ResponseEntity.ok(updatedMenuItem);
    }

    @PutMapping("/update-price/{category}/{percentageIncrease}")
    public ResponseEntity<List<MenuItem>> updatePricesByCategory(@PathVariable String category,
            @PathVariable double percentageIncrease) {
        List<MenuItem> updatedMenuItems = menuItemService.updatePricesByCategory(category, percentageIncrease);
        return ResponseEntity.ok(updatedMenuItems);
    }

    @GetMapping("/available/{restaurantId}")
    public ResponseEntity<List<MenuItem>> getAvailableMenuItemsByRestaurant(@PathVariable String restaurantId) {
        List<MenuItem> availableItems = menuItemService.getAvailableMenuItemsByRestaurant(restaurantId);
        return ResponseEntity.ok(availableItems);
    }

    @PostMapping("/adjust-status-by-order")
    public ResponseEntity<String> adjustMenuStatusBasedOnOrderCount() {
        try {
            menuItemService.adjustMenuStatusBasedOnOrderCount();
            return ResponseEntity.ok("Menu item statuses adjusted based on order count successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adjusting menu item statuses: " + e.getMessage());
        }
    }
}
