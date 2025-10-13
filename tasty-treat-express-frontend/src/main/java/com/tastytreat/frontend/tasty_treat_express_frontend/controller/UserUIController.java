package com.tastytreat.frontend.tasty_treat_express_frontend.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.tastytreat.frontend.tasty_treat_express_frontend.models.OrderDTO;
import com.tastytreat.frontend.tasty_treat_express_frontend.models.RestaurantDTO;
import com.tastytreat.frontend.tasty_treat_express_frontend.models.SuccessResponse;
import com.tastytreat.frontend.tasty_treat_express_frontend.models.UserDTO;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class UserUIController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/")
    public String home(Model model) {
        // handle the case when the backend connection is not available
        try {
            String url = "http://localhost:8080/api/restaurants/all";
            List<RestaurantDTO> restaurants = restTemplate.exchange(url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<RestaurantDTO>>() {
                    }).getBody();
            model.addAttribute("restaurants", restaurants);
        } catch (Exception e) {
            System.out.println("Error fetching restaurant data: " + e.getMessage());
            model.addAttribute("error", "Unable to fetch restaurant data. Please try again later.");
        }

        // String url = "http://localhost:8080/api/restaurants/all";
        // List<RestaurantDTO> restaurants = restTemplate.exchange(url, HttpMethod.GET,
        // null,
        // new ParameterizedTypeReference<List<RestaurantDTO>>() {
        // }).getBody();
        // model.addAttribute("restaurants", restaurants);
        // model.addAttribute("name", "Diwakar");
        return "Landing_pageBFR";
    }

    @GetMapping("/faq")
    public String showFaqPage() {
        return "faq";
    }

    @GetMapping("/forgotpassword")
    public String showContactPage() {
        return "forgotpassword_user";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "loginUser";
    }

    // http://localhost:9213/user/login
    @PostMapping("/user/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestParam String email, @RequestParam String password,
            Model model,
            HttpSession session) {
        String url = "http://localhost:8080/api/users/login";

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", email);
        loginRequest.put("password", password);

        System.out.println("Login request to backend...");

        try {
            ResponseEntity<SuccessResponse> response = restTemplate.postForEntity(url, loginRequest,
                    SuccessResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                SuccessResponse successResponse = response.getBody();

                UserDTO userDTO = successResponse.getData();
                System.out.println("*****UserDTO from backend:***** " + userDTO);

                session.setAttribute("userDTO", userDTO);
                session.setAttribute("userId", userDTO.getId());
                session.setAttribute("authenticatedUser", true);

                Map<String, String> successResponseMap = new HashMap<>();
                successResponseMap.put("status", "success");
                successResponseMap.put("message", "Login Successful");
                return ResponseEntity.ok(successResponseMap);

            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Invalid credentials. Please try again.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String errorMessage = ex.getResponseBodyAsString();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");

            if (errorMessage.contains("Invalid email or password")) {
                errorResponse.put("message", "Invalid password.");
            } else if (errorMessage.contains("Email not found")) {
                errorResponse.put("message", "Email not found.");
            } else if (errorMessage.contains("Invalid password")) {
                errorResponse.put("message", "Invalid password.");
            } else {
                errorResponse.put("message",
                        "Login failed. Please re-check your inputs: " + errorMessage);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An unexpected error occurred: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/home")
    public String showHomePage(Model model) {
        String url = "http://localhost:8080/api/restaurants/all";
        List<RestaurantDTO> restaurants = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<RestaurantDTO>>() {
                }).getBody();

        model.addAttribute("restaurants", restaurants);
        return "Landing_pageAFT";
    }
    //
    // @GetMapping("/user/placeOrder-without-res")
    // public String placeOrder() {
    // return "placeOrder";
    // }

    @GetMapping("/user/userdashboard")
    public String dashboard(Model model, HttpSession session) {
        UserDTO userDTO = (UserDTO) session.getAttribute("userDTO");
        model.addAttribute("userDTO", userDTO);
        System.out.println("UserDTO from session: " + userDTO);
        if (userDTO != null) {

            model.addAttribute("userName", userDTO.getName());
            Long userId = userDTO.getId();

            // Fetch orders from API
            String ordersUrl = "http://localhost:8080/api/orders/user/" + userId;
            OrderDTO[] orders = restTemplate.getForObject(ordersUrl, OrderDTO[].class);

            // Calculate stats
            int ordersThisMonth = getOrdersThisMonth(orders);
            double totalSpent = Math.round(getTotalSpent(orders) * 100.0) / 100.0;
            // double totalSpent = getTotalSpent(orders);
            double avgDeliveryTime = getAvgDeliveryTime(orders);
            String favoriteRestaurant = getFavoriteRestaurant(orders);

            // Set stats in model
            model.addAttribute("ordersThisMonth", ordersThisMonth);
            model.addAttribute("totalSpent", totalSpent);
            model.addAttribute("avgDeliveryTime", avgDeliveryTime);
            model.addAttribute("favoriteRestaurant", favoriteRestaurant);

            // Fetch recent orders
            // model.addAttribute("recentOrders", getRecentOrders(orders));

            OrderDTO mostRecentOrder = getRecentOrders(orders);
            if (mostRecentOrder != null) {
                String restaurantName = getRestaurantNameById(mostRecentOrder.getRestaurantId());
                mostRecentOrder.setRestaurantId(restaurantName);
            }
            model.addAttribute("mostRecentOrder", mostRecentOrder);

        }

        return "userdashboard";
    }

    @GetMapping("/user/profile")
    public String profile_(Model model, HttpSession session) {
        Object user = session.getAttribute("user");
        model.addAttribute("userProfile", user);
        return "profile";
    }

    @PostMapping("/user/updateProfile")
    public String profileUpdate(@RequestParam String name, @RequestParam String email, @RequestParam String address,
            @RequestParam String phoneNumber, Model model, HttpSession session) {

        UserDTO userDTO = new UserDTO();
        userDTO.setName(name);
        userDTO.setEmail(email);
        userDTO.setAddress(address);
        userDTO.setPhoneNumber(phoneNumber);

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            model.addAttribute("error", "User not logged in. Please log in again.");
            return "redirect:/";
        }

        String url = "http://localhost:8080/api/users/update/" + userId;

        try {

            ResponseEntity<UserDTO> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(userDTO),
                    UserDTO.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                UserDTO updatedUserDTO = response.getBody();

                session.setAttribute("userDTO", updatedUserDTO);
                session.setAttribute("userId", updatedUserDTO.getId());
                session.setAttribute("address", updatedUserDTO.getAddress());
                return "redirect:/user/profile";
            } else {
                model.addAttribute("error", "Error updating profile: " + response.getStatusCode());
                return "error";
            }
        } catch (RestClientException e) {
            model.addAttribute("error", "Error updating profile: " + e.getMessage());
            return "error";
        }
    }

    // http://localhost:9213/user/updatePassword
    @PostMapping("/user/updatePassword")
    public ResponseEntity<Map<String, String>> updatePassword(@RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        String url = "http://localhost:8080/api/users/updatePassword/" + userId;
        // String url = "http://localhost:8080/api/users/user/updatePassword";

        // Create the request map to send to the backend service
        Map<String, String> passwordUpdateRequest = new HashMap<>();
        passwordUpdateRequest.put("oldPassword", currentPassword);
        passwordUpdateRequest.put("newPassword", newPassword);
        passwordUpdateRequest.put("confirmPassword", confirmPassword);

        if (!newPassword.equals(confirmPassword)) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "New password and confirm password do not match.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        System.out.println("Password update request to backend...");

        try {

            // ResponseEntity<SuccessResponse> response = restTemplate.postForEntity(url,
            // passwordUpdateRequest,
            // SuccessResponse.class);
            ResponseEntity<SuccessResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    new HttpEntity<>(passwordUpdateRequest, null),
                    SuccessResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {

                SuccessResponse successResponse = response.getBody();
                System.out.println("Password updated successfully for user: " + successResponse);

                Map<String, String> successResponseMap = new HashMap<>();
                successResponseMap.put("status", "success");
                successResponseMap.put("message", "Password updated successfully.");
                return ResponseEntity.ok(successResponseMap);

            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Failed to update password. Please try again.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String errorMessage = ex.getResponseBodyAsString();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");

            if (errorMessage.contains("Invalid current password")) {
                errorResponse.put("message", "The current password you entered is incorrect.");
            } else if (errorMessage.contains("Old password is incorrect")) {
                errorResponse.put("message", "The Old password is incorrect. Please check again and try.");
            } else if (errorMessage.contains("Weak new password")) {
                errorResponse.put("message", "The new password is too weak. Please choose a stronger password.");
            } else {
                errorResponse.put("message", "Password update failed. Please re-check your inputs: " + errorMessage);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception ex) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An unexpected error occurred: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/cart")
    public String cart() {
        return "Cart";
    }

    @GetMapping("/logoutReq")
    public String logout(HttpSession session, Model model) {
        String url = "http://localhost:8080/api/users/logout";
        System.out.println("logout req....");
        session.invalidate();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                model.addAttribute("message", "Successfully logged out.");
                session.invalidate();
            } else {
                model.addAttribute("error", "Logout failed.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred: " + e.getMessage());
            return "redirect:/";
        }
        return "redirect:/";
    }

    @PostMapping("/user/signup")
    public ResponseEntity<Map<String, String>> userSignUp(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String name,
            @RequestParam String phoneNumber, Model model) {

        System.out.println("User Signup request to backend...");

        UserDTO user = new UserDTO();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setPhoneNumber(phoneNumber);


        String url = "http://localhost:8080/api/users/register";
        try {
            ResponseEntity<UserDTO> response = restTemplate.postForEntity(url, user, UserDTO.class);
            if (response.getStatusCode().equals(HttpStatus.CREATED)) {
                Map<String, String> successResponse = new HashMap<>();
                successResponse.put("status", "success");
                successResponse.put("message", "User registered successfully!");
                return ResponseEntity.ok(successResponse);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Error: " + response.getBody());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", ex.getResponseBodyAsString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An unexpected error occurred: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/user/forgetpassword")
    public ResponseEntity<Map<String, String>> forgetPassword(@RequestParam String email) {
        System.out.println("Forget password request to backend...");
        String url = "http://localhost:8080/api/users/forgotPassword/" + email;

        System.out.println("Email: " + email);
        Map<String, String> response = new HashMap<>();

        try {
            restTemplate.postForObject(url, email, String.class);
            response.put("message", "Password reset link sent to your email.");
            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            response.put("error", "Error: " + ex.getResponseBodyAsString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception ex) {
            response.put("error", "An unexpected error occurred: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @DeleteMapping("/user/delete-account")
    public ResponseEntity<Map<String, String>> deleteAccount(
            @RequestParam String userId,
            @RequestParam String password,
            HttpSession session) {

        UserDTO authenticatedUser = (UserDTO) session.getAttribute("userDTO");
        if (authenticatedUser == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "User not authenticated.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        if (authenticatedUser.getId() != Long.parseLong(userId)) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "User ID mismatch.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        System.out.println("Delete account request to backend...");

        String url = "http://localhost:8080/api/users/delete-account/" + userId + "/" + password;

        Map<String, String> response = new HashMap<>();
        try {
            ResponseEntity<SuccessResponse> externalResponse = restTemplate.exchange(
                    url, HttpMethod.DELETE, null, SuccessResponse.class);

            String result = externalResponse.toString();

            if (externalResponse.getStatusCode().is2xxSuccessful()) {
                response.put("status", "success");
                response.put("message", "Account deleted successfully.");
                session.invalidate();
                return ResponseEntity.ok(response);

            } else if (externalResponse.getStatusCode().is4xxClientError()) {
                response.put("status", "error");
                response.put("message", "Incorrect password.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

            } else {
                if (result.contains("Incorrect password")) {
                    response.put("status", "error");
                    response.put("message", "Incorrect password.");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
                response.put("status", "error");
                response.put("message", "Failed to delete account in the backend.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error occurred while communicating with the backend.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/my-payment")
    public String payment() {
        return "payment_updated";
    }

    @GetMapping("/me")
    public String place() {
        return "placeOrder5";
    }

    private String getRestaurantNameById(String restaurantId) {
        String restaurantUrl = "http://localhost:8080/api/restaurants/" + restaurantId;
        RestaurantDTO restaurant = restTemplate.getForObject(restaurantUrl, RestaurantDTO.class);
        return restaurant != null ? restaurant.getName() : "Unknown Restaurant";
    }

    private OrderDTO getRecentOrders(OrderDTO[] orders) {
        if (orders == null || orders.length == 0) {
            return null; // If no orders, return null
        }
        // Sort orders by orderDate in descending order to get the most recent one
        return Arrays.stream(orders)
                .max(Comparator.comparing(OrderDTO::getOrderDate))
                .orElse(null);
    }

    // Calculate the number of orders this month
    private int getOrdersThisMonth(OrderDTO[] orders) {
        int count = 0;
        LocalDateTime currentDate = LocalDateTime.now();
        for (OrderDTO order : orders) {
            LocalDateTime orderDate = order.getOrderDate();
            if (orderDate.getMonth() == currentDate.getMonth()) {
                count++;
            }
        }
        return count;
    }

    // Calculate total amount spent
    private double getTotalSpent(OrderDTO[] orders) {
        double total = 0;
        for (OrderDTO order : orders) {
            total += order.getTotalAmount();
        }
        return total;
    }

    // Calculate average delivery time
    private double getAvgDeliveryTime(OrderDTO[] orders) {
        double totalDeliveryTime = 0;
        int orderCount = orders.length;

        for (OrderDTO order : orders) {
            LocalDateTime orderTime = order.getOrderDate();
            LocalDateTime deliveryTime = order.getDeliveryTime();
            Duration duration = Duration.between(orderTime, deliveryTime);
            totalDeliveryTime += duration.toMinutes();
        }

        return orderCount > 0 ? totalDeliveryTime / orderCount : 0;
    }

    private String getFavoriteRestaurant(OrderDTO[] orders) {
        Map<String, Integer> restaurantOrderCount = new HashMap<>();

        for (OrderDTO order : orders) {
            String restaurantId = order.getRestaurantId();
            String restaurantName = getRestaurantName(restaurantId);
            restaurantOrderCount.put(restaurantName, restaurantOrderCount.getOrDefault(restaurantName, 0) + 1);
        }

        return restaurantOrderCount.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No favorite restaurant");
    }

    private String getRestaurantName(String restaurantId) {
        String url = "http://localhost:8080/api/restaurants/" + restaurantId;
        RestaurantDTO restaurant = restTemplate.getForObject(url, RestaurantDTO.class);
        return restaurant != null ? restaurant.getName() : "Unknown Restaurant";
    }

    // @PostMapping("/user/forgetpassword")
    // public String forgetPassword(@RequestParam String email, Model model) {
    // System.out.println("Forget password request to backend...");
    // String url = "http://localhost:8080/api/users/forgotPassword/" + email;
    // try {
    // restTemplate.postForObject(url, email, String.class);
    // model.addAttribute("message", "Password reset link sent to your email.");
    // return "redirect:/";
    // } catch (HttpClientErrorException | HttpServerErrorException ex) {
    // model.addAttribute("error", "Error: " + ex.getResponseBodyAsString());
    // return "Landing_pageBFR";
    // } catch (Exception ex) {
    // model.addAttribute("error", "An unexpected error occurred: " +
    // ex.getMessage());
    // return "Landing_pageBFR";
    // }
    // }
    // ------------------
    // @PostMapping("/user/login")
    // public String loginUser(@RequestParam String email, @RequestParam String
    // password, Model model,
    // HttpSession session) {
    // String url = "http://localhost:8080/api/users/login";

    // Map<String, String> loginRequest = new HashMap<>();
    // loginRequest.put("email", email);
    // loginRequest.put("password", password);
    // System.out.println("Login request to backend...");

    // try {
    // ResponseEntity<String> response = restTemplate.postForEntity(url,
    // loginRequest, String.class);

    // if (response.getStatusCode().is2xxSuccessful()) {
    // model.addAttribute("message", response.getBody());

    // session.setAttribute("user", "login");
    // return "redirect:/home";
    // } else if (response.getStatusCode().is4xxClientError()) {
    // model.addAttribute("error", response.getBody());
    // return "loginUser";
    // }
    // } catch (Exception e) {
    // model.addAttribute("error", "An error occurred: " + e.getMessage());
    // System.out.print(e.getMessage());
    // return "loginUser";
    // }
    // return "loginUser";
    // }

}
