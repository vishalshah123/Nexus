package com.nexus.api.ai.controller;

import com.nexus.api.ai.dto.ChatRequest;
import com.nexus.api.ai.dto.ChatResponse;
import com.nexus.api.ai.service.OpenAiChatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/conversations/{conversationId}/chat")
public class ChatController {
    private final OpenAiChatService service;

    public ChatController(OpenAiChatService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ChatResponse chat(@PathVariable UUID conversationId,
                             @Valid @RequestBody ChatRequest request) {
        return service.chat(conversationId, request);
    }
}
