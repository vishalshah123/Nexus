package com.nexus.api.message.dto;

import com.nexus.api.message.entity.Message;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MessageResponse(UUID id, UUID conversationId, String role, String content,
                              long sequenceNumber, OffsetDateTime createdAt) {
    public static MessageResponse from(Message m) {
        return new MessageResponse(m.getId(), m.getConversationId(), m.getRole(), m.getContent(),
                m.getSequenceNumber(), m.getCreatedAt());
    }
}
