package com.nexus.api.conversation;

import com.nexus.api.conversation.dto.ConversationRequests.*;
import com.nexus.api.conversation.dto.ConversationResponses.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController("managedConversationController")
@RequestMapping("/api/v1/users/{userId}/conversations")
public class ConversationController {
    private final ConversationService service;

    public ConversationController(ConversationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationResponse create(@PathVariable UUID userId,
                                       @Valid @RequestBody CreateConversationRequest request) {
        return service.create(userId, request);
    }

    @GetMapping
    public Page<ConversationResponse> list(@PathVariable UUID userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return service.list(userId, page, size);
    }

    @GetMapping("/{conversationId}")
    public ConversationResponse get(@PathVariable UUID userId, @PathVariable UUID conversationId) {
        return service.get(userId, conversationId);
    }

    @PatchMapping("/{conversationId}")
    public ConversationResponse rename(@PathVariable UUID userId, @PathVariable UUID conversationId,
            @Valid @RequestBody RenameConversationRequest request) {
        return service.rename(userId, conversationId, request);
    }

    @DeleteMapping("/{conversationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID userId, @PathVariable UUID conversationId) {
        service.delete(userId, conversationId);
    }

    @PostMapping("/{conversationId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse appendMessage(@PathVariable UUID userId, @PathVariable UUID conversationId,
            @Valid @RequestBody AppendMessageRequest request) {
        return service.appendMessage(userId, conversationId, request);
    }

    @GetMapping("/{conversationId}/messages")
    public Page<MessageResponse> listMessages(@PathVariable UUID userId, @PathVariable UUID conversationId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "50") @Min(1) @Max(100) int size) {
        return service.listMessages(userId, conversationId, page, size);
    }
}
