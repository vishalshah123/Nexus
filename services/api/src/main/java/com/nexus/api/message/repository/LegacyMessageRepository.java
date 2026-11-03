package com.nexus.api.message.repository;

import com.nexus.api.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LegacyMessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findAllByConversationIdOrderBySequenceNumberAsc(UUID conversationId);

    @Query("select coalesce(max(m.sequenceNumber), 0) from Message m where m.conversationId = :id")
    long findMaxSequenceNumber(@Param("id") UUID conversationId);
}
