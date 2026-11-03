package com.nexus.api.memory.repository;

import com.nexus.api.memory.entity.Memory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MemoryRepository extends JpaRepository<Memory, UUID> {
    List<Memory> findAllByUserIdOrderByCreatedAtDesc(UUID userId);
    boolean existsByIdAndUserId(UUID id, UUID userId);
    void deleteByIdAndUserId(UUID id, UUID userId);
}
