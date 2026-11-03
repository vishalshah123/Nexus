package com.nexus.api.conversation;

import com.nexus.api.conversation.dto.ConversationRequests.*;
import com.nexus.api.conversation.dto.ConversationResponses.*;
import com.nexus.api.conversation.entity.ConversationEntity;
import com.nexus.api.conversation.entity.MessageEntity;
import com.nexus.api.conversation.entity.UserEntity;
import com.nexus.api.conversation.repository.ConversationRepository;
import com.nexus.api.conversation.repository.MessageRepository;
import com.nexus.api.conversation.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class ConversationService {
    private static final Set<String> ALLOWED_ROLES = Set.of("system", "user", "assistant", "tool");

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ConversationService(UserRepository userRepository,
                               ConversationRepository conversationRepository,
                               MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    public ConversationResponse create(UUID userId, CreateConversationRequest request) {
        UserEntity user = requireUser(userId);
        ConversationEntity entity = new ConversationEntity(user, request.title().trim(), request.provider());
        return ConversationResponse.from(conversationRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<ConversationResponse> list(UUID userId, int page, int size) {
        requireUser(userId);
        validatePagination(page, size);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        return conversationRepository.findAllByUser_IdOrderByUpdatedAtDesc(userId, pageable)
                .map(ConversationResponse::from);
    }

    @Transactional(readOnly = true)
    public ConversationResponse get(UUID userId, UUID conversationId) {
        return ConversationResponse.from(requireConversation(userId, conversationId));
    }

    public ConversationResponse rename(UUID userId, UUID conversationId, RenameConversationRequest request) {
        ConversationEntity entity = requireConversation(userId, conversationId);
        entity.rename(request.title().trim());
        return ConversationResponse.from(entity);
    }

    public void delete(UUID userId, UUID conversationId) {
        conversationRepository.delete(requireConversation(userId, conversationId));
    }

    public MessageResponse appendMessage(UUID userId, UUID conversationId, AppendMessageRequest request) {
        String role = request.role().trim().toLowerCase(Locale.ROOT);
        if (!ALLOWED_ROLES.contains(role)) {
            throw new IllegalArgumentException("role must be one of: system, user, assistant, tool");
        }

        // Serialize message appends per conversation so sequence numbers remain unique under concurrency.
        ConversationEntity conversation = conversationRepository.findForUpdate(conversationId, userId)
                .orElseThrow(ConversationNotFoundException::new);
        long nextSequence = messageRepository.findMaxSequenceNumber(conversationId) + 1;
        MessageEntity saved = messageRepository.save(
                new MessageEntity(conversation, role, request.content(), nextSequence));
        conversation.touch();
        return MessageResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<MessageResponse> listMessages(UUID userId, UUID conversationId, int page, int size) {
        requireConversation(userId, conversationId);
        validatePagination(page, size);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sequenceNumber"));
        return messageRepository.findAllByConversation_IdOrderBySequenceNumberAsc(conversationId, pageable)
                .map(MessageResponse::from);
    }

    private UserEntity requireUser(UUID userId) {
        return userRepository.findById(userId).orElseThrow(ConversationNotFoundException::new);
    }

    private ConversationEntity requireConversation(UUID userId, UUID conversationId) {
        return conversationRepository.findByIdAndUser_Id(conversationId, userId)
                .orElseThrow(ConversationNotFoundException::new);
    }

    private void validatePagination(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("page must be >= 0 and size must be between 1 and 100");
        }
    }
}
