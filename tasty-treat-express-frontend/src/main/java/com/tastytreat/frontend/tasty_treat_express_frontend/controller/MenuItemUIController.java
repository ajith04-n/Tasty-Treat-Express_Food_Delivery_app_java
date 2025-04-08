package com.tastytreat.frontend.tasty_treat_express_frontend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.tastytreat.frontend.tasty_treat_express_frontend.models.MenuItemDTO;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/restaurant/menu-items")
public class MenuItemUIController {

    @Autowired
    private RestTemplate restTemplate;

    // Display all menu items for a restaurant
    @GetMapping
    public String getAllMenuItemsForRestaurant(HttpSession session, Model model) {
        String restaurantId = (String) session.getAttribute("restaurantId");
        if (restaurantId == null) {
            return "redirect:/?showRestaurantLogin=true";
        }

        try {
            List<MenuItemDTO> menuItems = restTemplate.getForObject(
                    "http://localhost:8080/api/menuItems/restaurant/" + restaurantId,
                    List.class);
            model.addAttribute("menuItems", menuItems);
            return "resdashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load menu items: " + e.getMessage());
            return "error";
        }
    }

    // Show form to add new menu item
    @GetMapping("/add")
    public String showAddMenuItemForm(Model model) {
        model.addAttribute("menuItem", new MenuItemDTO());
        return "resdashboard"; // The form is part of the dashboard
    }

    // Process adding new menu item
    @PostMapping("/add")
    public String addMenuItem(@ModelAttribute MenuItemDTO menuItem,
            HttpSession session,
            Model model) {
        String restaurantId = (String) session.getAttribute("restaurantId");
        if (restaurantId == null) {
            return "redirect:/?showRestaurantLogin=true";
        }
        try {
            restTemplate.postForObject(
                    "http://localhost:8080/api/menuItems/add/" + restaurantId,
                    menuItem,
                    MenuItemDTO.class);
            return "redirect:/restaurant/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add menu item: " + e.getMessage());
            return "error";
        }
    }
    // Show edit form for menu item
    @GetMapping("/edit/{id}")
    public String showEditMenuItemForm(@PathVariable Long id, Model model) {
        try {
            MenuItemDTO menuItem = restTemplate.getForObject(
                    "http://localhost:8080/api/menuItems/" + id,
                    MenuItemDTO.class);
            model.addAttribute("menuItem", menuItem);
            return "resdashboard"; 
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load menu item: " + e.getMessage());
            return "error";
        }
    }

    // Process updating menu item
    @PostMapping("/update")
    public String updateMenuItem(@ModelAttribute MenuItemDTO menuItem, Model model) {
        try {
            restTemplate.put(
                    "http://localhost:8080/api/menuItems/update",
                    menuItem);
            return "redirect:/restaurant/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update menu item: " + e.getMessage());
            return "error";
        }
    }

    // Delete menu item
    @GetMapping("/delete/{id}")
    public String deleteMenuItem(@PathVariable Long id, Model model) {
        try {
            restTemplate.delete("http://localhost:8080/api/menuItems/delete/" + id);
            return "redirect:/restaurant/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to delete menu item: " + e.getMessage());
            return "error";
        }
    }
}