package com.nexus.api.conversation.repository;

import com.nexus.api.conversation.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {
    Page<MessageEntity> findAllByConversation_IdOrderBySequenceNumberAsc(UUID conversationId, Pageable pageable);

    @Query("select coalesce(max(m.sequenceNumber), 0) from MessageEntity m where m.conversation.id = :conversationId")
    long findMaxSequenceNumber(@Param("conversationId") UUID conversationId);
}
