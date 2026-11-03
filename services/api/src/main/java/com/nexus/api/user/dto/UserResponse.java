package com.nexus.api.user.dto;

import com.nexus.api.user.entity.User;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponse(UUID id, String email, String displayName, OffsetDateTime createdAt) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getDisplayName(), user.getCreatedAt());
    }
}
