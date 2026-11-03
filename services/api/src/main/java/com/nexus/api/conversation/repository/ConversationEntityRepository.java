package com.nexus.api.conversation.repository;

import com.nexus.api.conversation.entity.ConversationEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ConversationEntityRepository extends JpaRepository<ConversationEntity, UUID> {
    Page<ConversationEntity> findAllByUser_IdOrderByUpdatedAtDesc(UUID userId, Pageable pageable);

    Optional<ConversationEntity> findByIdAndUser_Id(UUID id, UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ConversationEntity c where c.id = :id and c.user.id = :userId")
    Optional<ConversationEntity> findForUpdate(@Param("id") UUID id, @Param("userId") UUID userId);
}
