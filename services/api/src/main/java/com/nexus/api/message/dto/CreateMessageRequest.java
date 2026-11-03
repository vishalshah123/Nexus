package com.nexus.api.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateMessageRequest(
        @NotBlank @Pattern(regexp = "(?i)user|assistant|tool") String role,
        @NotBlank @Size(max = 100000) String content
) {}
