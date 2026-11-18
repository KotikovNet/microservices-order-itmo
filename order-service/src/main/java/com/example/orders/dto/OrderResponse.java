package com.example.orders.dto;

import com.example.orders.entity.Order;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record OrderResponse(Long id, Long userId, String description, BigDecimal amount, String status,
                            OffsetDateTime createdAt) {
    public static OrderResponse from(Order o) {
        return new OrderResponse(o.getId(), o.getUserId(), o.getDescription(), o.getAmount(), o.getStatus(), o.getCreatedAt());
    }
}
