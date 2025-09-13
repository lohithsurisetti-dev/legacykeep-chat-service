package com.legacykeep.chat.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.lang.NonNull;

/**
 * MongoDB Configuration
 * 
 * Configures MongoDB connection, document scanning, and repository settings
 * for the Chat Service. Optimized for high-performance message operations
 * with proper indexing and connection pooling.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Configuration
@EnableMongoRepositories(
    basePackages = "com.legacykeep.chat.repository.mongo",
    mongoTemplateRef = "mongoTemplate"
)
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.auto-index-creation:true}")
    private boolean autoIndexCreation;

    /**
     * Configure MongoDB client with optimized settings
     */
    @Override
    @Bean
    @NonNull
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }

    /**
     * Get the database name from the MongoDB URI
     */
    @Override
    @NonNull
    protected String getDatabaseName() {
        // Extract database name from URI
        // Format: mongodb://localhost:27017/database_name
        String[] uriParts = mongoUri.split("/");
        if (uriParts.length > 3) {
            String dbPart = uriParts[uriParts.length - 1];
            // Remove any query parameters
            if (dbPart.contains("?")) {
                dbPart = dbPart.substring(0, dbPart.indexOf("?"));
            }
            return dbPart;
        }
        return "chat_messages"; // Default database name
    }

    /**
     * Configure MongoTemplate with custom settings
     */
    @Bean
    public MongoTemplate mongoTemplate() {
        MongoTemplate template = new MongoTemplate(mongoClient(), getDatabaseName());
        
        // Configure converter to remove _class field from documents
        MappingMongoConverter converter = (MappingMongoConverter) template.getConverter();
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        
        return template;
    }

    /**
     * Enable automatic index creation for better performance
     */
    @Override
    protected boolean autoIndexCreation() {
        return autoIndexCreation;
    }

    /**
     * Configure additional MongoDB settings
     */
    @Override
    protected void configureClientSettings(@NonNull com.mongodb.MongoClientSettings.Builder builder) {
        // Connection pool settings
        builder.applyToConnectionPoolSettings(pool -> {
            pool.maxSize(20)                    // Maximum connections
                .minSize(5)                     // Minimum connections
                .maxWaitTime(30000, java.util.concurrent.TimeUnit.MILLISECONDS)  // Max wait time
                .maxConnectionIdleTime(600000, java.util.concurrent.TimeUnit.MILLISECONDS)  // Max idle time
                .maxConnectionLifeTime(1800000, java.util.concurrent.TimeUnit.MILLISECONDS); // Max connection lifetime
        });

        // Socket settings
        builder.applyToSocketSettings(socket -> {
            socket.connectTimeout(10000, java.util.concurrent.TimeUnit.MILLISECONDS)  // Connection timeout
                  .readTimeout(30000, java.util.concurrent.TimeUnit.MILLISECONDS);    // Read timeout
        });

        // Server selection settings
        builder.applyToServerSettings(server -> {
            server.heartbeatFrequency(10000, java.util.concurrent.TimeUnit.MILLISECONDS)  // Heartbeat frequency
                  .minHeartbeatFrequency(500, java.util.concurrent.TimeUnit.MILLISECONDS); // Min heartbeat frequency
        });

        // Application name for monitoring
        builder.applicationName("LegacyKeep-ChatService");
    }
}
