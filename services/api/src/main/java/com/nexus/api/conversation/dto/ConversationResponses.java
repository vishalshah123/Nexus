package com.nexus.api.conversation.dto;

import com.nexus.api.conversation.entity.ConversationEntity;
import com.nexus.api.conversation.entity.MessageEntity;

import java.time.Instant;
import java.util.UUID;

public final class ConversationResponses {
    private ConversationResponses() {}

    public record ConversationResponse(UUID id, String title, String provider,
                                        Instant createdAt, Instant updatedAt) {
        public static ConversationResponse from(ConversationEntity entity) {
            return new ConversationResponse(entity.getId(), entity.getTitle(), entity.getProvider(),
                    entity.getCreatedAt(), entity.getUpdatedAt());
        }
    }

    public record MessageResponse(UUID id, String role, String content,
                                  long sequenceNumber, Instant createdAt) {
        public static MessageResponse from(MessageEntity entity) {
            return new MessageResponse(entity.getId(), entity.getRole(), entity.getContent(),
                    entity.getSequenceNumber(), entity.getCreatedAt());
        }
    }
}
