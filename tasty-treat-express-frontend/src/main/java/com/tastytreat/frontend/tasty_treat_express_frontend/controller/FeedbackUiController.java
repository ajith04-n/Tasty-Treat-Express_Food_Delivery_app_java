package com.tastytreat.frontend.tasty_treat_express_frontend.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.tastytreat.frontend.tasty_treat_express_frontend.models.Feedback;



@Controller
@RequestMapping("/ui/feedback")
public class FeedbackUiController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_BASE_URL = "http://localhost:7878/feedback";

    // Show the main feedback page (feedbackForm.html)
    @GetMapping("/{userId}/{orderId}/{restaurantId}/{menuItemId}")
    public String showFeedbackPage(
            @PathVariable Long userId,
            @PathVariable Long orderId,
            @PathVariable Long restaurantId,
            @PathVariable Long menuItemId,
            Model model) {
        model.addAttribute("userId", userId);
        model.addAttribute("orderId", orderId);
        model.addAttribute("restaurantId", restaurantId);
        model.addAttribute("menuItemId", menuItemId);

        // Fetch existing feedbacks for the menu item
        ResponseEntity<Feedback[]> menuFeedbackResponse = restTemplate.getForEntity(
                API_BASE_URL + "/menu-item/" + menuItemId,
                Feedback[].class
        );
        List<Feedback> menuFeedbacks = Arrays.asList(menuFeedbackResponse.getBody());
        model.addAttribute("menuFeedbacks", menuFeedbacks);

        // Fetch existing feedbacks for the restaurant
        ResponseEntity<Feedback[]> restaurantFeedbackResponse = restTemplate.getForEntity(
                API_BASE_URL + "/restaurant/" + restaurantId,
                Feedback[].class
        );
        List<Feedback> restaurantFeedbacks = Arrays.asList(restaurantFeedbackResponse.getBody());
        model.addAttribute("restaurantFeedbacks", restaurantFeedbacks);

        // Check if user has already submitted feedback
        @SuppressWarnings("null")
        boolean hasMenuFeedback = restTemplate.getForObject(
                API_BASE_URL + "/exists/" + userId + "/" + orderId,
                Boolean.class
        );
        model.addAttribute("hasMenuFeedback", hasMenuFeedback);

        // Placeholder for delivery status
        model.addAttribute("deliveryCompleted", true);

        return "feedbackForm";
    }

    // Submit menu item feedback
    @PostMapping("/menu/submit")
    public String submitMenuFeedback(
            @RequestParam Long userId,
            @RequestParam Long orderId,
            @RequestParam Long restaurantId,
            @RequestParam Long menuItemId,
            @RequestParam Float rating,
            @RequestParam String comments,
            Model model) {
        Feedback feedback = new Feedback();
        feedback.setRating(rating);
        feedback.setComments(comments);

        ResponseEntity<Feedback> response = restTemplate.postForEntity(
                API_BASE_URL + "/add-for-menu-item/" + userId + "/" + orderId + "/" + restaurantId + "/" + menuItemId,
                feedback,
                Feedback.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return "redirect:/ui/feedback/" + userId + "/" + orderId + "/" + restaurantId + "/" + menuItemId;
        } else {
            model.addAttribute("error", "Failed to submit menu feedback");
            return "feedbackForm";
        }
    }

    // Submit restaurant feedback
    @PostMapping("/restaurant/submit")
    public String submitRestaurantFeedback(
            @RequestParam Long userId,
            @RequestParam Long orderId,
            @RequestParam Long restaurantId,
            @RequestParam Float rating,
            @RequestParam String comments,
            Model model) {
        Feedback feedback = new Feedback();
        feedback.setRating(rating);
        feedback.setComments(comments);

        ResponseEntity<Feedback> response = restTemplate.postForEntity(
                API_BASE_URL + "/add/" + userId + "/" + orderId + "/" + restaurantId,
                feedback,
                Feedback.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return "redirect:/ui/feedback/" + userId + "/" + orderId + "/" + restaurantId + "/0";
        } else {
            model.addAttribute("error", "Failed to submit restaurant feedback");
            return "feedbackForm";
        }
    }

    // Show user reviews (myReviews.html)
    @GetMapping("/reviews/{userId}")
    public String showUserReviews(@PathVariable Long userId, Model model) {
        ResponseEntity<Feedback[]> response = restTemplate.getForEntity(
                API_BASE_URL + "/all",
                Feedback[].class
        );
        // Fix: Compare Long objects using equals() or unbox to long
        // List<Feedback> feedbacks = Arrays.asList(response.getBody())
        //         .stream()
        //         .filter(f -> f.getUserID() != null && f.getUser().getUserID() == userId) // Use equals() for Long comparison
        //         .toList();
        // model.addAttribute("feedbacks", feedbacks);
        // model.addAttribute("userId", userId);

        return "myReviews";
    }

    // Update feedback
    @PostMapping("/update/{feedbackId}")
    public String updateFeedback(
            @PathVariable Long feedbackId,
            @RequestParam Float rating,
            @RequestParam String comments,
            @RequestParam Long userId,
            Model model) {
        Feedback feedback = new Feedback();
        feedback.setFeedbackID(feedbackId);
        feedback.setRating(rating);
        feedback.setComments(comments);

        HttpEntity<Feedback> request = new HttpEntity<>(feedback);
        ResponseEntity<Feedback> response = restTemplate.exchange(
                API_BASE_URL + "/update/" + feedbackId,
                HttpMethod.PUT,
                request,
                Feedback.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return "redirect:/ui/feedback/reviews/" + userId;
        } else {
            model.addAttribute("error", "Failed to update feedback");
            return "myReviews";
        }
    }

    // Delete feedback
    @GetMapping("/delete/{feedbackId}/{userId}")
    public String deleteFeedback(@PathVariable Long feedbackId, @PathVariable Long userId) {
        restTemplate.delete(API_BASE_URL + "/delete/" + feedbackId);
        return "redirect:/ui/feedback/reviews/" + userId;
    }
}