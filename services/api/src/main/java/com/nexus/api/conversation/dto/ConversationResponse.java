package com.nexus.api.conversation.dto;

import com.nexus.api.conversation.entity.Conversation;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ConversationResponse(UUID id, UUID userId, String title, String provider,
                                   OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    public static ConversationResponse from(Conversation c) {
        return new ConversationResponse(c.getId(), c.getUserId(), c.getTitle(), c.getProvider(),
                c.getCreatedAt(), c.getUpdatedAt());
    }
}
