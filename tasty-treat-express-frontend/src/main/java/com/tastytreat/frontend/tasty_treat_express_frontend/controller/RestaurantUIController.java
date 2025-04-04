package com.tastytreat.frontend.tasty_treat_express_frontend.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
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
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tastytreat.frontend.tasty_treat_express_frontend.models.MenuItemDTO;
import com.tastytreat.frontend.tasty_treat_express_frontend.models.RestaurantDTO;
import com.tastytreat.frontend.tasty_treat_express_frontend.models.UserDTO;

import jakarta.servlet.http.HttpSession;

@Controller
public class RestaurantUIController {
        @Autowired
        private RestTemplate restTemplate;

        @GetMapping("/user/placeOrder")
        public String placeOrder(@RequestParam("restaurantId") String restaurantId, Model model,HttpSession session) {
                RestaurantDTO restaurantDetails = restTemplate
                                .exchange("http://localhost:8080/api/restaurants/" + restaurantId, HttpMethod.GET, null,
                                                new ParameterizedTypeReference<RestaurantDTO>() {
                                                })
                                .getBody();
                model.addAttribute("restaurantDetails", restaurantDetails);
                model.addAttribute("resId", restaurantId);
                Long userId = (Long) session.getAttribute("userId");
                model.addAttribute("userId",userId);
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

        @PostMapping("/restaurant/signup")
        public String restaurantSignin(@RequestParam String email, @RequestParam String password,
                        @RequestParam String name, @RequestParam String phoneNumber, @RequestParam String address,
                        Model model) {

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
                                model.addAttribute("message", "Restaurant registered successfully!");
                                System.out.println("Registered Successfully!");
                                return "redirect:/home";
                        } else {
                                model.addAttribute("error", "Error: " + response.getBody());
                                System.out.println("Error occur: " + response.getBody());
                                return "Landing_pageBFR";
                        }
                } catch (HttpClientErrorException | HttpServerErrorException ex) {
                        model.addAttribute("error", "Registration failed: " + ex.getResponseBodyAsString());
                        System.out.println(ex.getResponseBodyAsString());
                        return "Landing_pageBFR";
                } catch (Exception ex) {
                        model.addAttribute("error", "An unexpected error occurred: " + ex.getMessage());
                        System.out.println(ex.getMessage());
                        return "Landing_pageBFR";
                }
        }

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

        @PostMapping("/restaurant/login")
        public String loginUser(@RequestParam String email, @RequestParam String password, Model model,
                        HttpSession session) {
                String url = "http://localhost:8080/api/restaurants/login";

                Map<String, String> loginRequest = new HashMap<>();
                loginRequest.put("email", email);
                loginRequest.put("password", password);
                System.out.println("Login request to backend...");

                try {
                        ResponseEntity<String> response = restTemplate.postForEntity(url, loginRequest, String.class);

                        if (response.getStatusCode().is2xxSuccessful()) {
                                model.addAttribute("message", response.getBody());
                                System.out.println("Login Successful!");
                                session.setAttribute("restaurant", "login");
                                return "redirect:/home";
                        } else if (response.getStatusCode().is4xxClientError()) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                                String message = jsonResponse.get("message").asText();
                                String statusCode = jsonResponse.get("statusCode").asText();
                                String timestamp = jsonResponse.get("timestamp").asText();

                                model.addAttribute("errorMessage", message);
                                model.addAttribute("errorDetails",
                                                "Status: " + statusCode + ", Timestamp: " + timestamp);

                                System.out.println("Error occurred: " + message);
                                return "redirect:/error?message=" + encodeURIComponent(message) +
                                                "&details=" + encodeURIComponent(
                                                                "Status: " + statusCode + ", Timestamp: " + timestamp);
                        }
                } catch (Exception e) {
                        model.addAttribute("errorMessage", "An error occurred during login");
                        model.addAttribute("errorDetails", e.getMessage());
                        System.out.print(e.getMessage());
                        return "redirect:/error?message=" + encodeURIComponent("An error occurred during login") +
                                        "&details=" + encodeURIComponent(e.getMessage());
                }

                // Fallback for unknown cases
                return "redirect:/error?message=" + encodeURIComponent("Unknown error occurred") +
                                "&details=" + encodeURIComponent("Please try again later.");
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
                return "checkout";
        }

        @GetMapping("/error")
        public String showErrorPage(@RequestParam String message, @RequestParam String details, Model model) {
                model.addAttribute("errorMessage", message);
                model.addAttribute("errorDetails", details);
                return "error";
        }

}
