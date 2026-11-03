package com.nexus.api.conversation.controller;

import com.nexus.api.conversation.dto.ConversationResponse;
import com.nexus.api.conversation.dto.CreateConversationRequest;
import com.nexus.api.conversation.service.ConversationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    private final ConversationService service;

    public ConversationController(ConversationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationResponse create(@Valid @RequestBody CreateConversationRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<ConversationResponse> listByUser(@RequestParam UUID userId) {
        return service.listByUser(userId);
    }
}
