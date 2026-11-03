package com.nexus.api.conversation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateConversationRequest(
        @NotNull UUID userId,
        @Size(max = 255) String title,
        @Size(max = 50) String provider
) {}
