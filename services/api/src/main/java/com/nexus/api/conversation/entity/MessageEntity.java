package com.nexus.api.conversation.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "messages",
       uniqueConstraints = @UniqueConstraint(name = "uq_messages_conversation_sequence",
                                             columnNames = {"conversation_id", "sequence_number"}))
public class MessageEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ConversationEntity conversation;

    @Column(nullable = false, length = 30)
    private String role;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "sequence_number", nullable = false)
    private long sequenceNumber;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected MessageEntity() {
    }

    public MessageEntity(ConversationEntity conversation, String role, String content, long sequenceNumber) {
        this.id = UUID.randomUUID();
        this.conversation = conversation;
        this.role = role;
        this.content = content;
        this.sequenceNumber = sequenceNumber;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getRole() { return role; }
    public String getContent() { return content; }
    public long getSequenceNumber() { return sequenceNumber; }
    public Instant getCreatedAt() { return createdAt; }
}
