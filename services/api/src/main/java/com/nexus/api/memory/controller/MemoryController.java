package com.nexus.api.memory.controller;

import com.nexus.api.memory.dto.CreateMemoryRequest;
import com.nexus.api.memory.dto.MemoryResponse;
import com.nexus.api.memory.service.MemoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController("legacyMemoryController")
@RequestMapping("/api/users/{userId}/memories")
public class MemoryController {
    private final MemoryService service;

    public MemoryController(MemoryService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemoryResponse create(@PathVariable UUID userId,
                                 @Valid @RequestBody CreateMemoryRequest request) {
        return service.create(userId, request);
    }

    @GetMapping
    public List<MemoryResponse> list(@PathVariable UUID userId) {
        return service.list(userId);
    }

    @DeleteMapping("/{memoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID userId, @PathVariable UUID memoryId) {
        service.delete(userId, memoryId);
    }
}
