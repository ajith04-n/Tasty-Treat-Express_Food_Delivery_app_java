package com.tastytreat.frontend.tasty_treat_express_frontend.controller;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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
        String url = "http://localhost:8080/api/restaurants/all";
        List<RestaurantDTO> restaurants = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<RestaurantDTO>>() {
                }).getBody();

        model.addAttribute("restaurants", restaurants);
        model.addAttribute("name", "Diwakar");
        return "Landing_pageBFR";
    }

    @GetMapping("/faq")
    public String showFaqPage() {
        return "faq";
    }

    @GetMapping("/forgotpassword")
    public String showContactPage() {
        return "forgotpassword";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "loginUser";
    }

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

    @PostMapping("/user/login")
    public String loginUser(@RequestParam String email, @RequestParam String password, Model model,
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
                return "redirect:/home";
            } else if (response.getStatusCode().is4xxClientError()) {
                model.addAttribute("error", response.getBody());
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred: " + e.getMessage());
            System.out.print(e.getMessage());
            return "error";
        }

        return "loginUser";
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
    public String dashboard() {
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
    public String userSignUp(@RequestParam String email, @RequestParam String password,
            @RequestParam String name, @RequestParam String phoneNumber, Model model) {

        System.out.println("Signup request to backend...");

        UserDTO user = new UserDTO();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setPhoneNumber(phoneNumber);

        String url = "http://localhost:8080/api/users/register";
        try {
            ResponseEntity<UserDTO> response = restTemplate.postForEntity(url, user, UserDTO.class);
            if (response.getStatusCode().equals(HttpStatus.CREATED)) {
                model.addAttribute("message", "User registered successfully!");
                return "redirect:/Landing_pageAFT";
            } else {
                model.addAttribute("error", "Error: " + response.getBody());
                return "Landing_pageBFR";
            }
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            model.addAttribute("error", "Registration failed: " + ex.getResponseBodyAsString());
            return "Landing_pageBFR";
        } catch (Exception ex) {
            model.addAttribute("error", "An unexpected error occurred: " + ex.getMessage());
            return "Landing_pageBFR";
        }
    }

    @PostMapping("/user/forgetpassword")
    public String forgetPassword(@RequestParam String email, Model model) {
        System.out.println("Forget password request to backend...");
        String url = "http://localhost:8080/api/users/forgotPassword/" + email;
        try {
            restTemplate.postForObject(url, email, String.class);
            model.addAttribute("message", "Password reset link sent to your email.");
            return "redirect:/";
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            model.addAttribute("error", "Error: " + ex.getResponseBodyAsString());
            return "Landing_pageBFR";
        } catch (Exception ex) {
            model.addAttribute("error", "An unexpected error occurred: " + ex.getMessage());
            return "Landing_pageBFR";
        }
    }

}
