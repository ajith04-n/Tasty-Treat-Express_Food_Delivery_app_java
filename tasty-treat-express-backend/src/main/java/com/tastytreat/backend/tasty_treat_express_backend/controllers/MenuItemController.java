package com.tastytreat.backend.tasty_treat_express_backend.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tastyTreatExpress.DTO.MenuItemDTO;
import com.tastyTreatExpress.DTO.MenuItemMapper;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.DuplicateMenuItemException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.InvalidInputException;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.ReportNotFoundException;
import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.services.MenuItemService;

@RestController
@RequestMapping("/api/menuItems")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    // Add a menu item
    @PostMapping("/add/{restaurantId}")
    public ResponseEntity<MenuItemDTO> addMenuItem(@PathVariable String restaurantId, @RequestBody MenuItem menuItem) {
        if (restaurantId == null || restaurantId.isEmpty()) {
            throw new ReportNotFoundException("Restaurant does not exist with the given Id.");
        }
        if (menuItem.getPrice() < 0) {
            throw new IllegalArgumentException("Price of menu item cannot be negative.");
        }
        if (menuItem.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity of menu item cannot be negative.");
        }
        if (menuItem.getName() == null || menuItem.getName().isEmpty()) {
            throw new IllegalArgumentException("Menu item name cannot be empty.");
        }
        if (menuItemService.existsByNameAndRestaurantId(menuItem.getName(), restaurantId)) {
            throw new DuplicateMenuItemException("Menu item with this name already exists in this restaurant.");
        }
        MenuItem createdMenuItem = menuItemService.addMenuItem(restaurantId, menuItem);
        MenuItemDTO createdMenuItemDTO = MenuItemMapper.toMenuItemDTO(createdMenuItem);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdMenuItemDTO);
    }

    // Add multiple menu items
    @PostMapping("/addMultiMenu/{restaurantId}")
    public ResponseEntity<List<MenuItemDTO>> addMenuItems(@PathVariable String restaurantId,
            @RequestBody List<MenuItem> menuItems) {
        List<MenuItem> addedItems = menuItemService.addMenuItems(restaurantId, menuItems);
        List<MenuItemDTO> addedItemDTOs = addedItems.stream()
                .map(MenuItemMapper::toMenuItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(addedItemDTOs);
    }

    // Get a menu item by ID
    @GetMapping("/{id}")
    public ResponseEntity<MenuItemDTO> getMenuItemById(@PathVariable long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Menu item ID must be positive.");
        }
        if (!menuItemService.existsById(id)) {
            throw new ReportNotFoundException("Menu item not found with the given ID: " + id);
        }
        MenuItem menuItem = menuItemService.getMenuItemById(id);
        if (menuItem != null) {
            MenuItemDTO menuItemDTO = MenuItemMapper.toMenuItemDTO(menuItem);
            return ResponseEntity.ok(menuItemDTO);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get all menu items
    @GetMapping("/all")
    public ResponseEntity<List<MenuItemDTO>> getAllMenuItems() {
        List<MenuItem> menuItems = menuItemService.getAllMenuItems();
        List<MenuItemDTO> menuItemDTOs = menuItems.stream()
                .map(MenuItemMapper::toMenuItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(menuItemDTOs);
    }

    // Get menu items by restaurant
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItemDTO>> getMenuItemsByRestaurant(@PathVariable String restaurantId) {
        if (restaurantId == null || restaurantId.isEmpty()) {
            throw new ReportNotFoundException("Restaurant does not exist with the given Id.");
        }
        if (!menuItemService.existsByRestaurantId(restaurantId)) {
            throw new ReportNotFoundException("No menu items found for this restaurant.");
        }
        List<MenuItem> menuItems = menuItemService.getAllMenuItemsByRestaurant(restaurantId);
        List<MenuItemDTO> menuItemDTOs = menuItems.stream()
                .map(MenuItemMapper::toMenuItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(menuItemDTOs);
    }

    // Update a menu item
    @PutMapping("/update")
    public ResponseEntity<MenuItemDTO> updateMenuItem(@RequestBody MenuItem menuItem) {
        if (menuItem == null) {
            throw new IllegalArgumentException("Menu item cannot be null.");
        }
        if (menuItem.getId() <= 0) {
            throw new IllegalArgumentException("Menu item ID must be positive.");
        }
        if (menuItem.getPrice() < 0) {
            throw new IllegalArgumentException("Price of menu item cannot be negative.");
        }
        if (menuItem.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity of menu item cannot be negative.");
        }
        MenuItem updatedMenuItem = menuItemService.updateMenuItem(menuItem);
        MenuItemDTO updatedMenuItemDTO = MenuItemMapper.toMenuItemDTO(updatedMenuItem);
        return ResponseEntity.ok(updatedMenuItemDTO);
    }

    @PutMapping("/update-qnty/{menuItemId}/{quantity}")
    public ResponseEntity<MenuItemDTO> updateMenuItemQnty(
            @PathVariable Long menuItemId,
            @PathVariable int quantity) {

        if (quantity < 0) {
            throw new InvalidInputException("Quantity cannot be negative.");
        }

        if (!menuItemService.existsById(menuItemId)) {
            throw new ReportNotFoundException("Menu item not found with the given ID: " + menuItemId);
        }
        MenuItem menuItem = menuItemService.getMenuItemById(menuItemId);
        MenuItem updatedMenuItem = menuItemService.updateMenuItemQnty(menuItem, quantity);
        MenuItemDTO updatedMenuItemDTO = MenuItemMapper.toMenuItemDTO(updatedMenuItem);

        return ResponseEntity.ok(updatedMenuItemDTO);
    }

    // Update menu item quantity
    @PutMapping("/update-qnty")
    public ResponseEntity<MenuItemDTO> updateMenuItemQnty(@RequestBody MenuItem menuItem,
            @RequestParam int quantity) {
        if (menuItem == null) {
            throw new IllegalArgumentException("Menu item cannot be null.");
        }
        if (menuItem.getId() <= 0) {
            throw new IllegalArgumentException("Menu item ID must be positive.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity of menu item cannot be negative.");
        }
        if (!menuItemService.existsById(menuItem.getId())) {
            throw new ReportNotFoundException("Menu item not found with the given ID: " + menuItem.getId());
        }
        if (!menuItemService.existsByNameAndRestaurantId(menuItem.getName(),
                menuItem.getRestaurant().getRestaurantId())) {
            throw new ReportNotFoundException("Menu item not found with the given name and restaurant ID.");
        }
        MenuItem updatedMenuItem = menuItemService.updateMenuItemQnty(menuItem, quantity);
        MenuItemDTO updatedMenuItemDTO = MenuItemMapper.toMenuItemDTO(updatedMenuItem);
        return ResponseEntity.ok(updatedMenuItemDTO);
    }

    // Delete a menu item
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Menu item ID must be positive.");
        }
        if (!menuItemService.existsById(id)) {
            throw new ReportNotFoundException("Menu item not found with the given ID: " + id);
        }
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }

    // Get popular menu items
    @GetMapping("/popular")
    public ResponseEntity<List<MenuItemDTO>> getPopularMenuItems() {
        List<MenuItem> popularItems = menuItemService.getPopularMenuItems();
        List<MenuItemDTO> popularItemDTOs = popularItems.stream()
                .map(MenuItemMapper::toMenuItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(popularItemDTOs);
    }

    // Get popular menu items by restaurant
    @GetMapping("/popular/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItemDTO>> getPopularMenuItemsByRestaurant(@PathVariable String restaurantId) {
        List<MenuItem> popularItems = menuItemService.getPopularMenuItemsByRestaurant(restaurantId);
        List<MenuItemDTO> popularItemDTOs = popularItems.stream()
                .map(MenuItemMapper::toMenuItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(popularItemDTOs);
    }

    // Get top N popular menu items
    @GetMapping("/popular/top/{limit}")
    public ResponseEntity<List<MenuItemDTO>> getTopPopularMenuItems(@PathVariable int limit) {
        List<MenuItem> topItems = menuItemService.getTopNPopularMenuItems(limit);
        List<MenuItemDTO> topItemDTOs = topItems.stream()
                .map(MenuItemMapper::toMenuItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(topItemDTOs);
    }

    // Adjust menu based on feedback
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

    // Get feedback for a menu item
    @GetMapping("/feedback/{menuItemId}")
    public ResponseEntity<List<Feedback>> getFeedbackForMenuItem(@PathVariable Long menuItemId) {
        List<Feedback> feedbacks = menuItemService.getFeedbackForMenuItem(menuItemId);
        return ResponseEntity.ok(feedbacks);
    }

    // Toggle menu item availability
    @PutMapping("/toggle-availability/{menuItemId}")
    public ResponseEntity<MenuItemDTO> toggleMenuItemAvailability(@PathVariable Long menuItemId) {
        MenuItem updatedMenuItem = menuItemService.toggleMenuItemAvailability(menuItemId);
        MenuItemDTO updatedMenuItemDTO = MenuItemMapper.toMenuItemDTO(updatedMenuItem);
        return ResponseEntity.ok(updatedMenuItemDTO);
    }

    // Update prices by category
    @PutMapping("/update-price/{category}/{percentageIncrease}")
    public ResponseEntity<List<MenuItemDTO>> updatePricesByCategory(@PathVariable String category,
            @PathVariable double percentageIncrease) {
        List<MenuItem> updatedMenuItems = menuItemService.updatePricesByCategory(category, percentageIncrease);
        List<MenuItemDTO> updatedMenuItemDTOs = updatedMenuItems.stream()
                .map(MenuItemMapper::toMenuItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(updatedMenuItemDTOs);
    }

    // Get available menu items by restaurant
    @GetMapping("/available/{restaurantId}")
    public ResponseEntity<List<MenuItemDTO>> getAvailableMenuItemsByRestaurant(@PathVariable String restaurantId) {
        List<MenuItem> availableItems = menuItemService.getAvailableMenuItemsByRestaurant(restaurantId);
        List<MenuItemDTO> availableItemDTOs = availableItems.stream()
                .map(MenuItemMapper::toMenuItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(availableItemDTOs);
    }

    // Adjust menu statuses based on order count
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
