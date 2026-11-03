package com.nexus.api.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank @Size(max = 100000) String message,
        @Size(max = 1000) String systemPrompt
) {}
