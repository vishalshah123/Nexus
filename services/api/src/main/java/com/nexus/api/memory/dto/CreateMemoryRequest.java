package com.nexus.api.memory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMemoryRequest(
        @NotBlank @Size(max = 10000) String content,
        @Size(max = 100) String category,
        @Size(max = 100) String source
) {}
