package com.nexus.api.ai.service;

import org.springframework.core.ParameterizedTypeReference;
import com.nexus.api.ai.OpenAiProperties;
import com.nexus.api.ai.dto.ChatRequest;
import com.nexus.api.ai.dto.ChatResponse;
import com.nexus.api.common.exception.NotFoundException;
import com.nexus.api.conversation.entity.Conversation;
import com.nexus.api.conversation.repository.LegacyConversationRepository;
import com.nexus.api.message.dto.MessageResponse;
import com.nexus.api.message.entity.Message;
import com.nexus.api.message.repository.LegacyMessageRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OpenAiChatService {
    private final RestClient client;
    private final OpenAiProperties properties;
    private final LegacyConversationRepository conversations;
    private final LegacyMessageRepository messages;

    public OpenAiChatService(RestClient openAiRestClient, OpenAiProperties properties,
                             LegacyConversationRepository conversations, LegacyMessageRepository messages) {
        this.client = openAiRestClient;
        this.properties = properties;
        this.conversations = conversations;
        this.messages = messages;
    }

    @Transactional
    public ChatResponse chat(UUID conversationId, ChatRequest request) {
        Conversation conversation = conversations.findById(conversationId)
                .orElseThrow(() -> new NotFoundException("Conversation not found: " + conversationId));

        if (properties.apiKey() == null || properties.apiKey().isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY is not configured for the API process");
        }

        long nextSequence = messages.findMaxSequenceNumber(conversationId) + 1;
        Message userMessage = messages.save(new Message(UUID.randomUUID(), conversationId,
                "user", request.message().trim(), nextSequence));

        List<Map<String, String>> chatMessages = new ArrayList<>();
        if (request.systemPrompt() != null && !request.systemPrompt().isBlank()) {
            chatMessages.add(Map.of("role", "system", "content", request.systemPrompt().trim()));
        }
        List<Message> history = messages.findAllByConversationIdOrderBySequenceNumberAsc(conversationId);
        for (Message m : history) {
            if (m.getRole().equals("user") || m.getRole().equals("assistant") || m.getRole().equals("system")) {
                chatMessages.add(Map.of("role", m.getRole(), "content", m.getContent()));
            }
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", properties.model());
        payload.put("messages", chatMessages);

        String assistantText;
        try {
            Map<String, Object> response = client.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.apiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            assistantText = extractAssistantContent(response);
            if (assistantText == null || assistantText.isBlank()) {
                throw new IllegalStateException("OpenAI returned an empty response");
            }
        } catch (RestClientException ex) {
            throw new IllegalStateException("OpenAI request failed. Check API key, model, network and provider response.", ex);
        }

        long assistantSequence = messages.findMaxSequenceNumber(conversationId) + 1;
        Message assistantMessage = messages.save(new Message(UUID.randomUUID(), conversationId,
                "assistant", assistantText, assistantSequence));
        conversation.setUpdatedAt(assistantMessage.getCreatedAt());

        return new ChatResponse(conversationId, MessageResponse.from(userMessage),
                MessageResponse.from(assistantMessage), properties.model());
    }
    @SuppressWarnings("unchecked")
    private String extractAssistantContent(Map<String, Object> response) {
        if (response == null) return null;
        Object choicesObject = response.get("choices");
        if (!(choicesObject instanceof List<?> choices) || choices.isEmpty()) return null;
        Object first = choices.get(0);
        if (!(first instanceof Map<?, ?> firstChoice)) return null;
        Object messageObject = firstChoice.get("message");
        if (!(messageObject instanceof Map<?, ?> message)) return null;
        Object content = message.get("content");
        return content instanceof String value ? value : null;
    }

}
