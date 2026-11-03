package com.nexus.api.memory.service;

import com.nexus.api.common.exception.NotFoundException;
import com.nexus.api.memory.dto.CreateMemoryRequest;
import com.nexus.api.memory.dto.MemoryResponse;
import com.nexus.api.memory.entity.Memory;
import com.nexus.api.memory.repository.MemoryRepository;
import com.nexus.api.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MemoryService {
    private final MemoryRepository memories;
    private final UserRepository users;

    public MemoryService(MemoryRepository memories, UserRepository users) {
        this.memories = memories;
        this.users = users;
    }

    @Transactional
    public MemoryResponse create(UUID userId, CreateMemoryRequest request) {
        if (!users.existsById(userId)) throw new NotFoundException("User not found: " + userId);
        Memory m = new Memory(UUID.randomUUID(), userId, request.content().trim(),
                clean(request.category()), clean(request.source()));
        return MemoryResponse.from(memories.save(m));
    }

    @Transactional(readOnly = true)
    public List<MemoryResponse> list(UUID userId) {
        if (!users.existsById(userId)) throw new NotFoundException("User not found: " + userId);
        return memories.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(MemoryResponse::from).toList();
    }

    @Transactional
    public void delete(UUID userId, UUID memoryId) {
        if (!users.existsById(userId)) throw new NotFoundException("User not found: " + userId);
        if (!memories.existsByIdAndUserId(memoryId, userId)) {
            throw new NotFoundException("Memory not found for this user: " + memoryId);
        }
        memories.deleteByIdAndUserId(memoryId, userId);
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
