package com.yourpackage.config;

import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;

public class CustomPostgresChatMemoryRepositoryDialect implements JdbcChatMemoryRepositoryDialect {
    
    private static final String SCHEMA_NAME = "ragapp";
    private static final String TABLE_NAME = SCHEMA_NAME + ".ai_chat_memory";
    
    @Override
    public String getSelectMessagesQuery() {
        return """
            SELECT content, message_type, "timestamp"
            FROM %s 
            WHERE conversation_id = ? 
            ORDER BY "timestamp" ASC 
            LIMIT ?
            """.formatted(TABLE_NAME);
    }
    
    @Override
    public String getInsertMessageQuery() {
        return """
            INSERT INTO %s (conversation_id, content, message_type, "timestamp") 
            VALUES (?, ?, ?, ?)
            """.formatted(TABLE_NAME);
    }
    
    @Override
    public String getDeleteMessagesQuery() {
        return """
            DELETE FROM %s 
            WHERE conversation_id = ?
            """.formatted(TABLE_NAME);
    }
    
    @Override
    public String getSelectConversationIdsQuery() {
        return """
            SELECT DISTINCT conversation_id 
            FROM %s
            """.formatted(TABLE_NAME);
    }
    
    @Override
    public String getCreateTableQuery() {
        return """
            CREATE TABLE IF NOT EXISTS %s (
                conversation_id VARCHAR(36) NOT NULL,
                content TEXT NOT NULL,
                message_type VARCHAR(10) NOT NULL CHECK (message_type IN ('USER', 'ASSISTANT', 'SYSTEM', 'TOOL')),
                "timestamp" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """.formatted(TABLE_NAME);
    }
    
    @Override
    public String getCreateIndexQuery() {
        return """
            CREATE INDEX IF NOT EXISTS ai_chat_memory_conversation_id_timestamp_idx 
            ON %s(conversation_id, "timestamp" DESC)
            """.formatted(TABLE_NAME);
    }
}