-- Safe additive migration for the existing V1 schema.
CREATE INDEX IF NOT EXISTS idx_messages_conversation_sequence
    ON messages (conversation_id, sequence_number);

CREATE INDEX IF NOT EXISTS idx_conversations_user_id
    ON conversations (user_id);
