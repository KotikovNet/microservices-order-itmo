package com.example.notifications.dto;

import jakarta.validation.constraints.*;

public record NotificationRequest(@NotNull @Positive Long userId, @NotNull @Positive Long orderId,
                                  @NotBlank @Email @Size(max = 254) String recipientEmail,
                                  @NotBlank @Size(max = 1000) String message) {
}
