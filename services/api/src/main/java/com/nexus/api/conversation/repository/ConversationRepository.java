package com.nexus.api.conversation.repository;

import com.nexus.api.conversation.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    List<Conversation> findAllByUserIdOrderByUpdatedAtDesc(UUID userId);
}
