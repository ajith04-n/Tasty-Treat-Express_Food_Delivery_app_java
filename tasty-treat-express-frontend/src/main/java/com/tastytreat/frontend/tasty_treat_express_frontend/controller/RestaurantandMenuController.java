// package com.tastytreat.frontend.tasty_treat_express_frontend.controller;

// import java.util.Arrays;
// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.client.RestTemplate;

// import com.tastytreat.frontend.tasty_treat_express_frontend.models.MenuItem;



// @Controller
// public class RestaurantandMenuController {

//     @Autowired
//     private RestTemplate restTemplate;

//     // Base URL for your backend API (adjusted to match previous backend)
//     private static final String API_BASE_URL = "http://localhost:7878";

//     // Hardcoded restaurantId for simplicity (in a real app, fetch from session or auth)
//     private static final String RESTAURANT_ID = "b73f80bc-8083-4023-845f-f0e92db06d11"; // Replace with dynamic retrieval if needed

//     // Dashboard page
//     @GetMapping("/dashboard-")
//     public String showDashboard(Model model) {
//         // Fetch restaurant details (optional, for display purposes)
//         model.addAttribute("restaurantId", RESTAURANT_ID);
//         return "dashboard"; // Returns dashboard.html
//     }

//     // Show add menu item form
//     @GetMapping("/add-menu")
//     public String showAddMenuForm(Model model) {
//         model.addAttribute("menu", new MenuItem());
//         model.addAttribute("restaurantId", RESTAURANT_ID);
//         return "addMenu"; // Returns addMenu.html
//     }

//     // Handle add menu item submission
//     @PostMapping("/add-menu")
//     public String addMenu(@ModelAttribute("menu") MenuItem menu) {
//         // Add menu item for the specific restaurant
//         ResponseEntity<MenuItem> response = restTemplate.postForEntity(
//             API_BASE_URL + "/api/menuItems/add/" + RESTAURANT_ID,
//             menu,
//             MenuItem.class
//         );
//         return "redirect:/dashboard";
//     }

//     // Show edit menu item form
//     @GetMapping("/edit-menu")
//     public String showEditMenuForm(Model model, @RequestParam(required = false) Long menuId) {
//         if (menuId != null) {
//             // Fetch the specific menu item by ID
//             MenuItem menu = restTemplate.getForObject(
//                 API_BASE_URL + "/api/menuItems/" + menuId,
//                 MenuItem.class
//             );
//             model.addAttribute("menu", menu);
//         } else {
//             model.addAttribute("menu", new MenuItem());
//         }
//         model.addAttribute("restaurantId", RESTAURANT_ID);
//         return "editMenu"; // Returns editMenu.html
//     }

//     // Handle edit menu item submission
//     @PostMapping("/edit-menu")
//     public String updateMenu(@ModelAttribute MenuItem menu) {
//         // Update the menu item
//         HttpEntity<MenuItem> request = new HttpEntity<>(menu);
//         restTemplate.exchange(
//             API_BASE_URL + "/api/menuItems/update",
//             HttpMethod.PUT,
//             request,
//             MenuItem.class
//         );
//         return "redirect:/menu-details";
//     }

//     // Show menu details
//     @GetMapping("/menu-details")
//     public String showMenuDetails(Model model) {
//         // Fetch all menu items for the restaurant
//         ResponseEntity<MenuItem[]> response = restTemplate.getForEntity(
//             API_BASE_URL + "/api/menuItems/restaurant/" + RESTAURANT_ID,
//             MenuItem[].class
//         );
//         List<MenuItem> menuItems = Arrays.asList(response.getBody());
//         model.addAttribute("menuItems", menuItems);
//         model.addAttribute("restaurantId", RESTAURANT_ID);
//         return "menuDetails"; // Returns menuDetails.html
//     }

//     // Delete menu item
//     @GetMapping("/deleteMenu")
//     public String deleteMenu(@RequestParam Long id) {
//         restTemplate.delete(API_BASE_URL + "/api/menuItems/delete/" + id);
//         return "redirect:/menu-details"; // Redirect to menu-details
//     }

//     // Show order details
//     @GetMapping("/order-details")
//     public String showOrderDetails(Model model) {
//         // Assuming there's an endpoint for orders (not provided, so placeholder)
//         ResponseEntity<Order[]> response = restTemplate.getForEntity(
//             API_BASE_URL + "/orders", // Adjust this if you have a specific endpoint
//             Order[].class
//         );
//         List<Order> orders = Arrays.asList(response.getBody());
//         model.addAttribute("orders", orders);
//         model.addAttribute("restaurantId", RESTAURANT_ID);
//         return "orderDetails"; // Returns orderDetails.html
//     }

//     // Show popular menu items
//     @GetMapping("/popular-menu")
//     public String showPopularMenuItems(Model model) {
//         // Fetch popular menu items for the restaurant
//         ResponseEntity<MenuItem[]> response = restTemplate.getForEntity(
//             API_BASE_URL + "/api/menuItems/popular/restaurant/" + RESTAURANT_ID,
//             MenuItem[].class
//         );
//         List<MenuItem> popularItems = Arrays.asList(response.getBody());
//         model.addAttribute("popularItems", popularItems);
//         model.addAttribute("restaurantId", RESTAURANT_ID);
//         return "popularMenu"; // Returns popularMenu.html
//     }
// }