package com.example.frontend.Controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.frontend.model.User;

@Controller
public class PageController {

  

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

    @GetMapping("/addMenu")
    public String addMenu() {
        return "addMenu";
    }

    @GetMapping("/address-form")
    public String addressForm() {
        return "address-form";
    }

    @GetMapping("/addRestaurant")
    public String addRestaurant() {
        return "addRestaurant";
    }

    @GetMapping("/addtocart")
    public String addToCart() {
        return "addtocart";
    }

    @GetMapping("/confirmation")
    public String confirmation() {
        return "confirmation";
    }

    @GetMapping("/confirmorder")
    public String confirmOrder() {
        return "confirmorder";
    }

    @GetMapping("/edit-profile")
    public String editProfile() {
        return "edit-profile";
    }

    @GetMapping("/editMenu")
    public String editMenu() {
        return "editMenu";
    }

    @GetMapping("/existing_reviews_landing")
    public String existingReviewsLanding() {
        return "existing_reviews_landing";
    }

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }

    @GetMapping("/Fetchordersbyid")
    public String fetchOrdersById() {
        return "Fetchordersbyid";
    }

    @GetMapping("/generate")
    public String generate() {
        return "generate";
    }

    @GetMapping("/Landing_page")
    public String landingPage() {
        return "Landing_page";
    }

    @GetMapping("/location")
    public String location() {
        return "location";
    }

    @GetMapping("/login-user")
    public String loginUser() {
        return "login-user";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/manual-entry")
    public String manualEntry() {
        return "manual-entry";
    }

    @GetMapping("/menuDetails")
    public String menuDetails() {
        return "menuDetails";
    }

    @GetMapping("/orderDetails")
    public String orderDetails() {
        return "orderDetails";
    }

    @GetMapping("/Order_By_Filter")
    public String orderByFilter() {
        return "Order_By_Filter";
    }

    @GetMapping("/payment_page")
    public String paymentPage() {
        return "payment_page";
    }

    @GetMapping("/register-user")
    public String registerUser() {
        return "register-user";
    }

    @GetMapping("/reports")
    public String reports() {
        return "reports";
    }

    @GetMapping("/responsefeedback")
    public String responseFeedback() {
        return "responsefeedback";
    }

    @GetMapping("/restaurant-login")
    public String restaurantLogin() {
        return "restaurant-login";
    }

    @GetMapping("/restaurant-menu")
    public String restaurantMenu() {
        return "restaurant-menu";
    }

    @GetMapping("/restaurant-register")
    public String restaurantRegister() {
        return "restaurant-register";
    }

    @GetMapping("/restaurant")
    public String restaurant() {
        return "restaurant";
    }

    @GetMapping("/restaurant_orders")
    public String restaurantOrders() {
        return "restaurant_orders";
    }

    @GetMapping("/reviews")
    public String reviews() {
        return "reviews";
    }

    @GetMapping("/review_landing")
    public String reviewLanding() {
        return "review_landing";
    }

    @GetMapping("/save")
    public String save() {
        return "save";
    }

    @GetMapping("/select-restaurant")
    public String selectRestaurant() {
        return "select-restaurant";
    }

    @GetMapping("/updatereport")
    public String updateReport() {
        return "updatereport";
    }

    @GetMapping("/Update_Order")
    public String updateOrder() {
        return "Update_Order";
    }

    @GetMapping("/viewUser")
    public String viewUser() {
        return "viewUser";
    }
}
