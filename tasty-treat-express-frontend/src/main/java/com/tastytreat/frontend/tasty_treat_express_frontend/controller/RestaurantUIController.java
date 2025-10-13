package com.tastytreat.frontend.tasty_treat_express_frontend.controller;

import java.io.UnsupportedEncodingException;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tastytreat.frontend.tasty_treat_express_frontend.models.MenuItemDTO;
import com.tastytreat.frontend.tasty_treat_express_frontend.models.RestaurantDTO;
import com.tastytreat.frontend.tasty_treat_express_frontend.models.SuccessResponse;
import com.tastytreat.frontend.tasty_treat_express_frontend.models.UserDTO;

import jakarta.servlet.http.HttpSession;

@Controller
public class RestaurantUIController {
        @Autowired
        private RestTemplate restTemplate;

        @GetMapping("/user/placeOrder")
        public String placeOrder(@RequestParam("restaurantId") String restaurantId, Model model, HttpSession session) {
                RestaurantDTO restaurantDetails = restTemplate
                                .exchange("http://localhost:8080/api/restaurants/" + restaurantId, HttpMethod.GET, null,
                                                new ParameterizedTypeReference<RestaurantDTO>() {
                                                })
                                .getBody();
                model.addAttribute("restaurantDetails", restaurantDetails);
                model.addAttribute("resId", restaurantId);
                Long userId = (Long) session.getAttribute("userId");
                model.addAttribute("userId", userId);
                UserDTO delAdd = (UserDTO) session.getAttribute("user");
                String address = (String) session.getAttribute("address");
                if (delAdd != null) {
                        model.addAttribute("address", delAdd.getAddress());
                } else {
                        model.addAttribute("address", "No address found");
                }
                // model.addAttribute("address", delAdd.getAddress());
                model.addAttribute("address", address);

                List<MenuItemDTO> menuItems = restTemplate.exchange(
                                "http://localhost:8080/api/menuItems/restaurant/" + restaurantId,
                                HttpMethod.GET,
                                null,
                                new ParameterizedTypeReference<List<MenuItemDTO>>() {
                                }).getBody();

                model.addAttribute("menuItems", menuItems);
                return "placeOrder";
        }

        @GetMapping("/placeOrder/{rid}")
        public String placeOrder2(@PathVariable String rid, Model model) {
                model.addAttribute("rid", rid);
                return "placeOrder3";
        }

        @GetMapping("/restaurants")
        public String getRestaurants(Model model) {
                String url = "http://localhost:8080/api/restaurants/all";
                List<RestaurantDTO> restaurants = restTemplate.exchange(url, HttpMethod.GET, null,
                                new ParameterizedTypeReference<List<RestaurantDTO>>() {
                                }).getBody();

                model.addAttribute("restaurants", restaurants);
                return "restaurantPage";
        }

        // http://localhost:9213/restaurant/signup
        @PostMapping("/restaurant/signup")
        public ResponseEntity<Map<String, String>> restaurantSignup(@RequestParam String email,
                        @RequestParam String password,
                        @RequestParam String name,
                        @RequestParam String phoneNumber,
                        @RequestParam String address) {
                RestaurantDTO restaurant = new RestaurantDTO();
                restaurant.setEmail(email);
                restaurant.setPassword(password);
                restaurant.setName(name);
                restaurant.setPhoneNumber(phoneNumber);
                restaurant.setAddress(address);

                String url = "http://localhost:8080/api/restaurants/register";
                try {
                        ResponseEntity<RestaurantDTO> response = restTemplate.postForEntity(url, restaurant,
                                        RestaurantDTO.class);

                        if (response.getStatusCode().equals(HttpStatus.CREATED)) {
                                Map<String, String> successResponse = new HashMap<>();
                                successResponse.put("status", "success");
                                successResponse.put("message", "Restaurant registered successfully!");
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
                        errorResponse.put("message", "Registration failed: " + ex.getResponseBodyAsString());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                } catch (Exception ex) {
                        Map<String, String> errorResponse = new HashMap<>();
                        errorResponse.put("status", "error");
                        errorResponse.put("message", "An unexpected error occurred: " + ex.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                }
        }

        // http://localhost:9213/restaurant/login
        @PostMapping("/restaurant/login")
        public ResponseEntity<Map<String, String>> loginRestaurant(@RequestParam String email,
                        @RequestParam String password, HttpSession session) {
                String url = "http://localhost:8080/api/restaurants/login";
                System.out.println("Login request to backend...");
                System.out.println("Email: " + email);

                Map<String, String> loginRequest = new HashMap<>();
                loginRequest.put("email", email);
                loginRequest.put("password", password);

                try {
                        ResponseEntity<SuccessResponse> response = restTemplate.postForEntity(url, loginRequest,
                                        SuccessResponse.class);

                        if (response.getStatusCode().is2xxSuccessful()) {
                                SuccessResponse successResponse = response.getBody();

                                RestaurantDTO resDTO = successResponse.getResData();
                                System.out.println("*****ResDTO from backend:***** " + resDTO);

                                session.setAttribute("restaurantDTO", resDTO);
                                session.setAttribute("restaurantId", resDTO.getRestaurantId());
                                session.setAttribute("authenticatedRes", true);

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

        private String encodeURIComponent(String value) {
                try {
                        return java.net.URLEncoder.encode(value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                        return value;
                }
        }

        @GetMapping("/restaurant/checkout")
        public String checkout(Model model, HttpSession session) {
                return "payment_updated";
        }

        @GetMapping("/error")
        public String showErrorPage(@RequestParam String message, @RequestParam String details, Model model) {
                model.addAttribute("errorMessage", message);
                model.addAttribute("errorDetails", details);
                return "error";
        }

        @GetMapping("/res/forgotpassword")
        public String showContactPage() {
                return "forgotpassword_res";
        }

        @PostMapping("/restaurant/forgetpassword")
        public ResponseEntity<Map<String, String>> forgetPassword(@RequestParam String email) {
                System.out.println("Forget password request to backend...");
                String url = "http://localhost:8080/api/restaurants/forgotPassword/" + email;
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

        @GetMapping("/res/profile")
        public String restaurantProfile(Model model, HttpSession session) {
                RestaurantDTO restaurantDTO = (RestaurantDTO) session.getAttribute("restaurantDTO");
                if (restaurantDTO != null) {
                        model.addAttribute("restaurant", restaurantDTO);
                        return "res-profile";
                } else {
                        model.addAttribute("error", "Restaurant not found in session.");
                        return "error";
                }
        }

        @GetMapping("/res/logoutReq")
        public String logout(HttpSession session, Model model) {
                String url = "http://localhost:8080/api/restaurants/logout";
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

        @GetMapping("/restaurant/dashboard")
        public String restaurantDashboard(Model model, HttpSession session) {
                RestaurantDTO restaurantDTO = (RestaurantDTO) session.getAttribute("restaurantDTO");
                if (restaurantDTO != null) {
                        model.addAttribute("restaurant", restaurantDTO);
                }
                // return "restdashboard";
                return "resdashboard4";
        }

        // http://localhost:9213/user/updatePassword
        @PostMapping("/restaurant/updatePassword")
        public ResponseEntity<Map<String, String>> updatePassword(@RequestParam String currentPassword,
                        @RequestParam String newPassword,
                        @RequestParam String confirmPassword,
                        HttpSession session) {

                String restaurantId = (String) session.getAttribute("restaurantId");
                String url = "http:/localhost:8080/api/restaurants/updatePassword/" + restaurantId;

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
                                errorResponse.put("message",
                                                "The Old password is incorrect. Please check again and try.");
                        } else if (errorMessage.contains("Weak new password")) {
                                errorResponse.put("message",
                                                "The new password is too weak. Please choose a stronger password.");
                        } else {
                                errorResponse.put("message",
                                                "Password update failed. Please re-check your inputs: " + errorMessage);
                        }
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

                } catch (Exception ex) {

                        Map<String, String> errorResponse = new HashMap<>();
                        errorResponse.put("status", "error");
                        errorResponse.put("message", "An unexpected error occurred: " + ex.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                }
        }

        @PostMapping("/res/updateProfile")
        public String profileUpdate(@RequestParam String name, @RequestParam String email, @RequestParam String address,
                        @RequestParam String phoneNumber, @RequestParam String description,  Model model, HttpSession session) {

                RestaurantDTO restaurantDTO = new RestaurantDTO();
                restaurantDTO.setName(name);
                restaurantDTO.setEmail(email);
                restaurantDTO.setAddress(address);
                restaurantDTO.setPhoneNumber(phoneNumber);
                restaurantDTO.setDescription(description);

                String restaurantId = (String) session.getAttribute("restaurantId");

                if (restaurantId == null) {
                        model.addAttribute("error", "User not logged in. Please log in again.");
                        return "redirect:/";
                }

                String url = "http://localhost:8080/api/restaurants/update/" + restaurantId;

                try {

                        ResponseEntity<RestaurantDTO> response = restTemplate.exchange(url, HttpMethod.PUT,
                                        new HttpEntity<>(restaurantDTO),
                                        RestaurantDTO.class);

                        if (response.getStatusCode().is2xxSuccessful()) {
                                RestaurantDTO updatedRestaurantDTO = response.getBody();

                                session.setAttribute("restaurantDTO", updatedRestaurantDTO);
                                session.setAttribute("restaurantId", updatedRestaurantDTO.getRestaurantId());
                                session.setAttribute("address", updatedRestaurantDTO.getAddress());

                                return "redirect:/res/profile";
                        } else {
                                model.addAttribute("error", "Error updating profile: " + response.getStatusCode());
                                return "error";
                        }
                } catch (RestClientException e) {
                        model.addAttribute("error", "Error updating profile: " + e.getMessage());
                        return "error";
                }
        }

}

/*
 * These are the safe-side methods which are tested and works fine and are not
 * used in the project due to some best alternatives available.
 */

// @PostMapping("/--restaurant/signup")
// public String restaurantSignin2(@RequestParam String email, @RequestParam
// String password,
// @RequestParam String name, @RequestParam String phoneNumber, @RequestParam
// String address,
// Model model) {

// RestaurantDTO restaurant = new RestaurantDTO();
// restaurant.setEmail(email);
// restaurant.setPassword(password);
// restaurant.setName(name);
// restaurant.setPhoneNumber(phoneNumber);
// restaurant.setAddress(address);

// String url = "http://localhost:8080/api/restaurants/register";
// try {
// ResponseEntity<RestaurantDTO> response = restTemplate.postForEntity(url,
// restaurant,
// RestaurantDTO.class);
// if (response.getStatusCode().equals(HttpStatus.CREATED)) {
// model.addAttribute("message", "Restaurant registered successfully!");
// System.out.println("Registered Successfully!");
// return "redirect:/home";
// } else {
// model.addAttribute("error", "Error: " + response.getBody());
// System.out.println("Error occur: " + response.getBody());
// return "Landing_pageBFR";
// }
// } catch (HttpClientErrorException | HttpServerErrorException ex) {
// model.addAttribute("error", "Registration failed: " +
// ex.getResponseBodyAsString());
// System.out.println(ex.getResponseBodyAsString());
// return "Landing_pageBFR";
// } catch (Exception ex) {
// model.addAttribute("error", "An unexpected error occurred: " +
// ex.getMessage());
// System.out.println(ex.getMessage());
// return "Landing_pageBFR";
// }
// }
// ----------------

// @PostMapping("/restaurant/login")
// public String loginUser(@RequestParam String email, @RequestParam String
// password, Model model,
// HttpSession session) {
// String url = "http://localhost:8080/api/restaurants/login";
// Map<String, String> loginRequest = new HashMap<>();
// loginRequest.put("email", email);
// loginRequest.put("password", password);
// System.out.println("Login request to backend...");
// try {
// ResponseEntity<String> response = restTemplate.postForEntity(url,
// loginRequest, String.class);

// if (response.getStatusCode().is2xxSuccessful()) {
// model.addAttribute("message", response.getBody());
// System.out.println("Login Successful!");
// session.setAttribute("restaurant", "login");
// return "redirect:/home";
// } else if (response.getStatusCode().is4xxClientError()) {
// model.addAttribute("error", response.getBody());
// System.out.println("Error occur: " + response.getBody());
// return "loginUser";
// }
// } catch (Exception e) {
// model.addAttribute("error", "An error occurred: " + e.getMessage());
// System.out.print(e.getMessage());
// return "loginUser";
// }
// return "loginUser";
// }
// ------------------
// @PostMapping("/restaurant/login")
// public String loginUser2(@RequestParam String email, @RequestParam String
// password, Model model,
// HttpSession session) {
// String url = "http://localhost:8080/api/restaurants/login";

// Map<String, String> loginRequest = new HashMap<>();
// loginRequest.put("email", email);
// loginRequest.put("password", password);
// System.out.println("Login request to backend...");

// try {
// ResponseEntity<String> response = restTemplate.postForEntity(url,
// loginRequest, String.class);

// if (response.getStatusCode().is2xxSuccessful()) {
// model.addAttribute("message", response.getBody());
// System.out.println("Login Successful!");
// session.setAttribute("restaurant", "login");
// return "redirect:/home";
// } else if (response.getStatusCode().is4xxClientError()) {
// ObjectMapper objectMapper = new ObjectMapper();
// JsonNode jsonResponse = objectMapper.readTree(response.getBody());
// String message = jsonResponse.get("message").asText();
// String statusCode = jsonResponse.get("statusCode").asText();
// String timestamp = jsonResponse.get("timestamp").asText();

// model.addAttribute("errorMessage", message);
// model.addAttribute("errorDetails",
// "Status: " + statusCode + ", Timestamp: " + timestamp);

// System.out.println("Error occurred: " + message);
// return "redirect:/error?message=" + encodeURIComponent(message) +
// "&details=" + encodeURIComponent(
// "Status: " + statusCode + ", Timestamp: " + timestamp);
// }
// } catch (Exception e) {
// model.addAttribute("errorMessage", "An error occurred during login");
// model.addAttribute("errorDetails", e.getMessage());
// System.out.print(e.getMessage());
// return "redirect:/error?message=" + encodeURIComponent("An error occurred
// during login") +
// "&details=" + encodeURIComponent(e.getMessage());
// }

// // Fallback for unknown cases
// return "redirect:/error?message=" + encodeURIComponent("Unknown error
// occurred") +
// "&details=" + encodeURIComponent("Please try again later.");
// }

// ------------------
/*
 * // http://localhost:9213/restaurant/login
 * 
 * @PostMapping("/restaurant/login")
 * public ResponseEntity<Map<String, String>> loginRestaurant(@RequestParam
 * String email,
 * 
 * @RequestParam String password,
 * HttpSession session) {
 * String url = "http://localhost:8080/api/restaurants/login";
 * System.out.println("Login request to backend...");
 * System.out.println("Email: " + email);
 * 
 * Map<String, String> loginRequest = new HashMap<>();
 * loginRequest.put("email", email);
 * loginRequest.put("password", password);
 * System.out.println("Login request to backend...");
 * 
 * try {
 * ResponseEntity<SuccessResponse> response = restTemplate.postForEntity(url,
 * loginRequest,
 * SuccessResponse.class);
 * 
 * if (response.getStatusCode().is2xxSuccessful()) {
 * SuccessResponse successResponse = response.getBody();
 * 
 * RestaurantDTO resDTO = successResponse.getResData();
 * System.out.println("*****ResDTO from backend:***** " + resDTO);
 * 
 * session.setAttribute("restaurantDTO", resDTO);
 * session.setAttribute("restaurantId", resDTO.getRestaurantId());
 * session.setAttribute("authenticatedUser", true);
 * 
 * Map<String, String> successResponseMap = new HashMap<>();
 * successResponseMap.put("status", "success");
 * successResponseMap.put("message", "Login Successful");
 * return ResponseEntity.ok(successResponseMap);
 * } else {
 * Map<String, String> errorResponse = new HashMap<>();
 * errorResponse.put("status", "error");
 * errorResponse.put("message", "Invalid credentials. Please try again.");
 * return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
 * }
 * } catch (HttpClientErrorException | HttpServerErrorException ex) {
 * 
 * Map<String, String> errorResponse = new HashMap<>();
 * errorResponse.put("status", "error");
 * errorResponse.put("message",
 * "Login failed Please re-check your inputs: " + ex.getResponseBodyAsString());
 * return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
 * } catch (Exception ex) {
 * 
 * Map<String, String> errorResponse = new HashMap<>();
 * errorResponse.put("status", "error");
 * errorResponse.put("message", "An unexpected error occurred: " +
 * ex.getMessage());
 * return
 * ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
 * }
 * }
 * 
 */
// ------------------
// @PostMapping("/restaurant/login")
// public String loginUser2(@RequestParam String email, @RequestParam String
// password, Model model,
// HttpSession session) {
// String url = "http://localhost:8080/api/restaurants/login";

// Map<String, String> loginRequest = new HashMap<>();
// loginRequest.put("email", email);
// loginRequest.put("password", password);
// System.out.println("Login request to backend...");

// try {
// ResponseEntity<SuccessResponse> response = restTemplate.postForEntity(url,
// loginRequest,
// SuccessResponse.class);
// if (response.getStatusCode().is2xxSuccessful()) {
// SuccessResponse successResponse = response.getBody();

// RestaurantDTO resDTO = successResponse.getResData();
// System.out.println("*****ResDTO from backend:***** " + resDTO);
// session.setAttribute("restaurantDTO", resDTO);
// session.setAttribute("restaurantId", resDTO.getRestaurantId());
// session.setAttribute("authenticatedUser", true);
// return "redirect:/home";
// } else if (response.getStatusCode().is4xxClientError()) {
// model.addAttribute("error", response.getBody());
// return "error";
// }
// } catch (Exception e) {
// model.addAttribute("error", "An error occurred: " + e.getMessage());
// System.out.print(e.getMessage());
// return "error";
// }

// return "loginUser";
// }
// -----------------
// @PostMapping("/restaurant/forgetpassword")
// public String forgetPassword(@RequestParam String email, Model model) {
// System.out.println("Forget password request to backend...");
// String url = "http://localhost:8080/api/restaurants/forgotPassword/" + email;
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
