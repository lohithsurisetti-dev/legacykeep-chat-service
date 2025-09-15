package com.legacykeep.chat.config;

import com.legacykeep.chat.entity.Message;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.PostConstruct;

/**
 * MongoDB Text Index Configuration
 * 
 * Ensures text indexes are created for full-text search functionality
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class MongoTextIndexConfig {

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void createTextIndexes() {
        try {
            log.info("Creating MongoDB text indexes for message search...");
            
            IndexOperations indexOps = mongoTemplate.indexOps(Message.class);
            
            // Create text index on content field with high weight
            TextIndexDefinition textIndex = new TextIndexDefinition.TextIndexDefinitionBuilder()
                    .onField("content", 10.0f)  // High weight for content
                    .build();
            
            // Check if index already exists
            if (!indexOps.getIndexInfo().stream()
                    .anyMatch(index -> index.getName().equals("content_text"))) {
                indexOps.ensureIndex(textIndex);
                log.info("✅ Text index created successfully for message content");
            } else {
                log.info("✅ Text index already exists for message content");
            }
            
        } catch (Exception e) {
            log.error("❌ Failed to create text indexes: {}", e.getMessage(), e);
        }
    }
}
