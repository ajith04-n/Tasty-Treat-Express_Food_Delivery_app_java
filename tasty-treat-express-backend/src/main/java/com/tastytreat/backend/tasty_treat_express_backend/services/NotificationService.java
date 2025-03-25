package com.tastytreat.backend.tasty_treat_express_backend.services;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendNotification(Long userId, String message) throws Exception {
        try {
            // Simulating push notification logic
            System.out.println("Sending push notification to User ID: " + userId + " - Message: " + message);

            // Simulated failure for testing
            if (message.contains("fail")) {
                throw new RuntimeException("Push notification service failed.");
            }

        } catch (RuntimeException e) {
            throw new Exception("Notification delivery failed: " + e.getMessage());
        }
    }
}
