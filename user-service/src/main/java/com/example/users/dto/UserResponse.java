package com.example.users.dto;

import com.example.users.entity.User;

import java.time.OffsetDateTime;

public record UserResponse(Long id, String name, String email, OffsetDateTime createdAt) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getCreatedAt());
    }
}
