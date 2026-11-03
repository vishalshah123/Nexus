package com.nexus.api.message.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages",
       uniqueConstraints = @UniqueConstraint(name = "uq_messages_conversation_sequence",
               columnNames = {"conversation_id", "sequence_number"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message {
    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "conversation_id", nullable = false)
    private UUID conversationId;

    @Column(nullable = false, length = 30)
    private String role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "sequence_number", nullable = false)
    private long sequenceNumber;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public Message(UUID id, UUID conversationId, String role, String content, long sequenceNumber) {
        this.id = id;
        this.conversationId = conversationId;
        this.role = role;
        this.content = content;
        this.sequenceNumber = sequenceNumber;
        this.createdAt = OffsetDateTime.now();
    }
}
