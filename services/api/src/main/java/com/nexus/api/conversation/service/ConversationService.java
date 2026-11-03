package com.nexus.api.conversation.service;

import com.nexus.api.common.exception.NotFoundException;
import com.nexus.api.conversation.dto.ConversationResponse;
import com.nexus.api.conversation.dto.CreateConversationRequest;
import com.nexus.api.conversation.entity.Conversation;
import com.nexus.api.conversation.repository.ConversationRepository;
import com.nexus.api.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ConversationService {
    private final ConversationRepository conversations;
    private final UserRepository users;

    public ConversationService(ConversationRepository conversations, UserRepository users) {
        this.conversations = conversations;
        this.users = users;
    }

    @Transactional
    public ConversationResponse create(CreateConversationRequest request) {
        if (!users.existsById(request.userId())) {
            throw new NotFoundException("User not found: " + request.userId());
        }
        String title = request.title() == null || request.title().isBlank()
                ? "New conversation" : request.title().trim();
        String provider = request.provider() == null || request.provider().isBlank()
                ? null : request.provider().trim();
        Conversation c = new Conversation(UUID.randomUUID(), request.userId(), title, provider);
        return ConversationResponse.from(conversations.save(c));
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> listByUser(UUID userId) {
        if (!users.existsById(userId)) {
            throw new NotFoundException("User not found: " + userId);
        }
        return conversations.findAllByUserIdOrderByUpdatedAtDesc(userId)
                .stream().map(ConversationResponse::from).toList();
    }
}
