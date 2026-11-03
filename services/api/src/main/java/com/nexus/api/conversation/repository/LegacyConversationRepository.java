package com.nexus.api.conversation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nexus.api.conversation.entity.Conversation;
import java.util.List;
import java.util.UUID;

public interface LegacyConversationRepository extends JpaRepository<Conversation, UUID> {
    List<Conversation> findAllByUserIdOrderByUpdatedAtDesc(UUID userId);
}
