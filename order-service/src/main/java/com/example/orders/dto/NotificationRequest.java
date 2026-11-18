package com.example.orders.dto;

public record NotificationRequest(Long userId, Long orderId, String recipientEmail, String message) {
}
