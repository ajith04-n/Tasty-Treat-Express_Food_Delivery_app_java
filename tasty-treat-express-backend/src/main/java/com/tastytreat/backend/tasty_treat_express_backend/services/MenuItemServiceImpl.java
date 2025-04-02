package com.tastytreat.backend.tasty_treat_express_backend.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.ReportNotFoundException;
import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.Restaurant;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.MenuItemRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.FeedbackRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.RestaurantRepository;

@Service
public class MenuItemServiceImpl implements MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public MenuItem addMenuItem(String restaurantId, MenuItem menuItem) {

        if (restaurantRepository.existsById(restaurantId)) {
            menuItem.setRestaurant(restaurantRepository.findById(restaurantId).orElse(null));
            return menuItemRepository.save(menuItem);
        } else {
            throw new RuntimeException("Restaurant with id " + restaurantId + " does not exist");
        }
    }

    @Override
    public MenuItem getMenuItemById(long id) {
        Optional<MenuItem> menuItem = menuItemRepository.findById(id);
        if (menuItem.isPresent()) {
            return menuItem.get();
        } else {
            throw new RuntimeException("MenuItem not found with id " + id);
        }
    }

    @Override
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    @Override
    public List<MenuItem> getAllMenuItemsByRestaurant(String restaurantId) {
        if (restaurantRepository.existsById(restaurantId)) {
            return menuItemRepository.findByRestaurantRestaurantId(restaurantId);
        } else {
            throw new RuntimeException("Restaurant with id " + restaurantId + " does not exist");
        }
    }

    @Override
    public List<MenuItem> getMenuItemsByCategory(String category) {
        return menuItemRepository.findByCategory(category);
    }

    @Override
    public MenuItem updateMenuItem(MenuItem menuItem) {
        if (menuItemRepository.existsById(menuItem.getId())) {
            MenuItem existingMenuItem = menuItemRepository.findById(menuItem.getId())
                    .orElseThrow(() -> new ReportNotFoundException("MenuItem not found with id " + menuItem.getId()));

            if (menuItem.getName() != null && !menuItem.getName().isEmpty()) {
                existingMenuItem.setName(menuItem.getName());
            }
            if (menuItem.getPrice() >= 0) {
                existingMenuItem.setPrice(menuItem.getPrice());
            }
            if (menuItem.getQuantity() >= 0) {
                existingMenuItem.setQuantity(menuItem.getQuantity());
            }
            if (menuItem.getDescription() != null && !menuItem.getDescription().isEmpty()) {
                existingMenuItem.setDescription(menuItem.getDescription());
            }
            return menuItemRepository.save(existingMenuItem);
        } else {
            throw new RuntimeException("MenuItem not found with id " + menuItem.getId());
        }
    }
    
  
    public MenuItem updateMenuItemQnty(MenuItem menuItem,int qnty) {
        if (menuItemRepository.existsById(menuItem.getId())) {
        	Optional<MenuItem> x=menuItemRepository.findById(menuItem.getId()); 
        	MenuItem y=x.get();
        	y.setQuantity(qnty); 
            return menuItemRepository.save(y);
        } else {
            throw new RuntimeException("MenuItem not found with id " + menuItem.getId());
        }
    }

    @Override
    public void deleteMenuItem(long id) {
        if (menuItemRepository.existsById(id)) {
            menuItemRepository.deleteById(id);
        } else {
            throw new RuntimeException("MenuItem not found with id " + id);
        }
    }

    @Override
    public List<MenuItem> addMenuItems(String restaurantId, List<MenuItem> menuItems) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);

        if (restaurant == null) {
            throw new RuntimeException("Restaurant with id " + restaurantId + " does not exist");
        }

        for (MenuItem menuItem : menuItems) {
            menuItem.setRestaurant(restaurant);
            menuItemRepository.save(menuItem);
        }

        return menuItems;
    }

    @Override
    public List<MenuItem> getPopularMenuItems() {
        return menuItemRepository.findPopularMenuItems();
    }

    @Override
    public List<MenuItem> getPopularMenuItemsByRestaurant(String restaurantId) {
        if (restaurantRepository.existsById(restaurantId)) {
            return menuItemRepository.findPopularMenuItemsByRestaurant(restaurantId);
        } else {
            throw new RuntimeException("Restaurant with id " + restaurantId + " does not exist");
        }
    }

    @Override
    public List<MenuItem> getTopNPopularMenuItems(int limit) {
        return menuItemRepository.findTopPopularMenuItems(PageRequest.of(0, limit));
    }

    @Override
    public void adjustMenuBasedOnFeedback() {
        
        List<MenuItem> allMenuItems = menuItemRepository.findAll();
        for (MenuItem menuItem : allMenuItems) {
            Float avgRating = feedbackRepository.getAverageRatingForMenuItem(menuItem);

            if (avgRating == null) {
                continue;
            }

            if (avgRating >= 4.5) {
                menuItem.setStatus("Popular");
            } else if (avgRating >= 3.5) {
                menuItem.setStatus("Regular");
            } else {
                menuItem.setStatus("LowDemand");

                menuItem.setStatus("LowDemand");
            }

            menuItemRepository.save(menuItem);
        }
    }

    @Override
    public List<Feedback> getFeedbackForMenuItem(Long menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("MenuItem not found with id " + menuItemId));
        return menuItem.getFeedbacks();
    }

    @Override
    public MenuItem toggleMenuItemAvailability(Long menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("MenuItem not found with id " + menuItemId));
        menuItem.setIsAvailable(!menuItem.getIsAvailable());
        return menuItemRepository.save(menuItem);
    }

    @Override
    public List<MenuItem> updatePricesByCategory(String category, double percentageIncrease) {
        List<MenuItem> menuItems = menuItemRepository.findByCategory(category);
        for (MenuItem menuItem : menuItems) {
            double newPrice = menuItem.getPrice() + (menuItem.getPrice() * (percentageIncrease / 100));
            menuItem.setPrice(newPrice);
            menuItemRepository.save(menuItem);
        }
        return menuItems;
    }

    @Override
    public List<MenuItem> getAvailableMenuItemsByRestaurant(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant with id " + restaurantId + " does not exist"));
        return restaurant.getMenu().stream()
                .filter(MenuItem::getIsAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public void adjustMenuStatusBasedOnOrderCount() {
        List<MenuItem> allMenuItems = menuItemRepository.findAll();

        for (MenuItem menuItem : allMenuItems) {
            long orderCount = menuItem.getOrders().size();

            if (orderCount > 50) {
                menuItem.setStatus("Popular");
            } else if (orderCount > 20) {
                menuItem.setStatus("Regular");
            } else {
                menuItem.setStatus("LowDemand");
            }

            menuItemRepository.save(menuItem);
        }
    }

    @Override
    public boolean existsByNameAndRestaurantId(String name, String restaurantId) {
        return menuItemRepository.existsByNameAndRestaurant_RestaurantId(name, restaurantId);
    }

    @Override
    public boolean existsById(long id) {
        return menuItemRepository.existsById(id);
    }

    @Override
    public boolean existsByRestaurantId(String restaurantId) {
        return restaurantRepository.existsById(restaurantId);
    }

}