package com.tastyTreatExpress.DTO;

import java.util.stream.Collectors;

import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;

public class FeedbackMapper {

    public static FeedbackDTO toFeedbackDTO(Feedback feedback) {
        if (feedback == null)
            return null;

        return new FeedbackDTO(
                feedback.getFeedbackId(),
                feedback.getUser().getId(),
                feedback.getUser().getName(),
                feedback.getOrders().getOrderId(),
                feedback.getRestaurant().getRestaurantId(),
                feedback.getMenuItems() != null ? feedback.getMenuItems().getMenuId() : null,
                feedback.getRating(),
                feedback.getComments(),
                feedback.getFeedbackDate());
    }

    public static Feedback toFeedbackEntity(FeedbackDTO feedbackDTO) {
        if (feedbackDTO == null)
            return null;

        Feedback feedback = new Feedback();
        feedback.setFeedbackId(feedbackDTO.getFeedbackId());
        feedback.setRating(feedbackDTO.getRating());
        feedback.setComments(feedbackDTO.getComments());
        feedback.setFeedbackDate(feedbackDTO.getFeedbackDate());

        return feedback;
    }
}

// package com.tastyTreatExpress.DTO;

// import java.util.stream.Collectors;

// import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;

// public class FeedbackMapper {

// public static FeedbackDTO toFeedbackDTO(Feedback feedback) {
// if (feedback == null)
// return null;

// return new FeedbackDTO(
// feedback.getFeedbackId(),
// feedback.getUser().getId(),
// feedback.getOrders().getOrderId(),
// feedback.getRestaurant().getRestaurantId(),
// feedback.getMenuItems() != null ? feedback.getMenuItems().getMenuId() : null,
// feedback.getRating(),
// feedback.getComments(),
// feedback.getFeedbackDate());
// }

// public static Feedback toFeedbackEntity(FeedbackDTO feedbackDTO) {
// if (feedbackDTO == null)
// return null;

// Feedback feedback = new Feedback();
// feedback.setFeedbackId(feedbackDTO.getFeedbackId());
// feedback.setRating(feedbackDTO.getRating());
// feedback.setComments(feedbackDTO.getComments());
// feedback.setFeedbackDate(feedbackDTO.getFeedbackDate());

// return feedback;
// }
// }
