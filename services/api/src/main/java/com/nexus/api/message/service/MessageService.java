package com.nexus.api.message.service;

import com.nexus.api.common.exception.NotFoundException;
import com.nexus.api.conversation.repository.LegacyConversationRepository;
import com.nexus.api.message.dto.CreateMessageRequest;
import com.nexus.api.message.dto.MessageResponse;
import com.nexus.api.message.entity.Message;
import com.nexus.api.message.repository.LegacyMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class MessageService {
    private final LegacyMessageRepository messages;
    private final LegacyConversationRepository conversations;

    public MessageService(LegacyMessageRepository messages, LegacyConversationRepository conversations) {
        this.messages = messages;
        this.conversations = conversations;
    }

    @Transactional
    public MessageResponse create(UUID conversationId, CreateMessageRequest request) {
        if (!conversations.existsById(conversationId)) {
            throw new NotFoundException("Conversation not found: " + conversationId);
        }
        long sequence = messages.findMaxSequenceNumber(conversationId) + 1;
        Message message = new Message(UUID.randomUUID(), conversationId,
                request.role().trim().toLowerCase(Locale.ROOT),
                request.content().trim(), sequence);
        return MessageResponse.from(messages.save(message));
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> list(UUID conversationId) {
        if (!conversations.existsById(conversationId)) {
            throw new NotFoundException("Conversation not found: " + conversationId);
        }
        return messages.findAllByConversationIdOrderBySequenceNumberAsc(conversationId)
                .stream().map(MessageResponse::from).toList();
    }
}
