package com.nexus.api.conversation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "conversations")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Conversation {
    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 255)
    private String title = "New conversation";

    @Column(length = 50)
    private String provider;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Conversation(UUID id, UUID userId, String title, String provider) {
        this.id = id;
        this.userId = userId;
        this.title = title == null || title.isBlank() ? "New conversation" : title.trim();
        this.provider = provider;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }
}
