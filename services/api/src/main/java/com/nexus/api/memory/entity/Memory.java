package com.nexus.api.memory.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "memories")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Memory {
    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 100)
    private String category;

    @Column(length = 100)
    private String source;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Memory(UUID id, UUID userId, String content, String category, String source) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.category = category;
        this.source = source;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }
}
