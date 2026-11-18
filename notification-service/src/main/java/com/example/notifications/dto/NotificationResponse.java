package com.example.notifications.dto;

import com.example.notifications.entity.Notification;

import java.time.OffsetDateTime;

public record NotificationResponse(Long id, Long userId, Long orderId, String recipientEmail, String message,
                                   String status, OffsetDateTime createdAt) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(n.getId(), n.getUserId(), n.getOrderId(), n.getRecipientEmail(), n.getMessage(), n.getStatus(), n.getCreatedAt());
    }
}
