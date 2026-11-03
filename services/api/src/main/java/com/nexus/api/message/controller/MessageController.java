package com.nexus.api.message.controller;

import com.nexus.api.message.dto.CreateMessageRequest;
import com.nexus.api.message.dto.MessageResponse;
import com.nexus.api.message.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations/{conversationId}/messages")
public class MessageController {
    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse create(@PathVariable UUID conversationId,
                                  @Valid @RequestBody CreateMessageRequest request) {
        return service.create(conversationId, request);
    }

    @GetMapping
    public List<MessageResponse> list(@PathVariable UUID conversationId) {
        return service.list(conversationId);
    }
}
