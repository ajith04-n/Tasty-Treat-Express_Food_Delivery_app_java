package com.tastytreat.frontend.tasty_treat_express_frontend.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.tastytreat.frontend.tasty_treat_express_frontend.models.UserDTO;

import java.security.Principal;

@Controller
public class HomeController {
	/*

    @GetMapping("/login")
    public String Login() {
        return "login";
    }

    @GetMapping("/register")
    public String Register() {
        return "register";
    }

    // Display the update profile form
    @GetMapping("/updateProfile")
    public String UpdateProfile(Model model, Principal principal) {
        // Check if the user is authenticated
        if (principal == null) {
            return "redirect:/login"; // Redirect to the login page if the user is not authenticated
        }

        // For now, create a dummy user object (replace this with actual user fetching logic)
        User user = new User();
        user.setName("John Doe");
        user.setEmail(principal.getName()); // Use the logged-in user's email
        user.setPassword(""); // Leave password empty for security
        user.setPhoneNumber("123-456-7890");
        user.setAddress("123 Main St, City, Country");

        model.addAttribute("user", user); // Pass the user object to the view
        return "updateprofile"; // Return the updateprofile.html page
    }

    // Handle the form submission for updating the profile
    @PostMapping("/updateProfile")
    public String updateProfile(@ModelAttribute("user") User updatedUser, Principal principal) {
        // Check if the user is authenticated
        if (principal == null) {
            return "redirect:/login"; // Redirect to the login page if the user is not authenticated
        }

        // For now, just print the updated user details (replace this with actual update logic)
        System.out.println("Updated User Details:");
        System.out.println("Name: " + updatedUser.getName());
        System.out.println("Email: " + updatedUser.getEmail());
        System.out.println("Password: " + updatedUser.getPassword());
        System.out.println("Phone Number: " + updatedUser.getPhoneNumber());
        System.out.println("Address: " + updatedUser.getAddress());

        // Redirect to the profile page with a success message
        return "redirect:/updateProfile?success";
    }
    */
}