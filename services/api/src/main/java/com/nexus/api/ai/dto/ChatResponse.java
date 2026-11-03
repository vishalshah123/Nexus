package com.nexus.api.ai.dto;

import com.nexus.api.message.dto.MessageResponse;

import java.util.UUID;

public record ChatResponse(UUID conversationId, MessageResponse userMessage,
                           MessageResponse assistantMessage, String model) {}
