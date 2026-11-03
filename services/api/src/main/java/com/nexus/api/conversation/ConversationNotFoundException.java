package com.nexus.api.conversation;

public class ConversationNotFoundException extends RuntimeException {
    public ConversationNotFoundException() {
        super("Conversation or user was not found");
    }
}
