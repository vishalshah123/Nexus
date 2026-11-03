package com.nexus.api.conversation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class ConversationRequests {
    private ConversationRequests() {}

    public record CreateConversationRequest(
            @NotBlank @Size(max = 255) String title,
            @Size(max = 50) String provider) {}

    public record RenameConversationRequest(
            @NotBlank @Size(max = 255) String title) {}

    public record AppendMessageRequest(
            @NotBlank @Size(max = 30) String role,
            @NotBlank String content) {}
}
