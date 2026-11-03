package com.nexus.api.memory.dto;

import com.nexus.api.memory.entity.Memory;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MemoryResponse(UUID id, UUID userId, String content, String category,
                             String source, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    public static MemoryResponse from(Memory m) {
        return new MemoryResponse(m.getId(), m.getUserId(), m.getContent(), m.getCategory(),
                m.getSource(), m.getCreatedAt(), m.getUpdatedAt());
    }
}
