// MongoDB Indexes for Chat Service
// Indexes for optimal performance in message operations
// Version: 1.0.0

// Switch to the chat_messages database
use chat_messages;

// Create indexes for messages collection
db.messages.createIndex({ "messageUuid": 1 }, { unique: true, name: "idx_message_uuid" });
db.messages.createIndex({ "chatRoomId": 1, "createdAt": -1 }, { name: "idx_chat_room_created_at" });
db.messages.createIndex({ "senderUserId": 1, "createdAt": -1 }, { name: "idx_sender_created_at" });
db.messages.createIndex({ "messageType": 1, "createdAt": -1 }, { name: "idx_message_type_created_at" });
db.messages.createIndex({ "status": 1, "createdAt": -1 }, { name: "idx_status_created_at" });
db.messages.createIndex({ "isStarred": 1, "createdAt": -1 }, { name: "idx_starred_created_at" });
db.messages.createIndex({ "isProtected": 1, "createdAt": -1 }, { name: "idx_protected_created_at" });
db.messages.createIndex({ "isEncrypted": 1, "createdAt": -1 }, { name: "idx_encrypted_created_at" });
db.messages.createIndex({ "toneColor": 1, "createdAt": -1 }, { name: "idx_tone_color_created_at" });
db.messages.createIndex({ "mediaUrl": 1, "createdAt": -1 }, { name: "idx_media_created_at" });
db.messages.createIndex({ "storyId": 1, "createdAt": -1 }, { name: "idx_story_created_at" });
db.messages.createIndex({ "memoryId": 1, "createdAt": -1 }, { name: "idx_memory_created_at" });
db.messages.createIndex({ "eventId": 1, "createdAt": -1 }, { name: "idx_event_created_at" });
db.messages.createIndex({ "createdAt": -1 }, { name: "idx_created_at_desc" });
db.messages.createIndex({ "updatedAt": -1 }, { name: "idx_updated_at_desc" });

// Composite indexes for common queries
db.messages.createIndex({ "chatRoomId": 1, "messageType": 1, "createdAt": -1 }, { name: "idx_room_type_created_at" });
db.messages.createIndex({ "chatRoomId": 1, "status": 1, "createdAt": -1 }, { name: "idx_room_status_created_at" });
db.messages.createIndex({ "chatRoomId": 1, "senderUserId": 1, "createdAt": -1 }, { name: "idx_room_sender_created_at" });
db.messages.createIndex({ "chatRoomId": 1, "isStarred": 1, "createdAt": -1 }, { name: "idx_room_starred_created_at" });
db.messages.createIndex({ "chatRoomId": 1, "isProtected": 1, "createdAt": -1 }, { name: "idx_room_protected_created_at" });
db.messages.createIndex({ "chatRoomId": 1, "toneColor": 1, "createdAt": -1 }, { name: "idx_room_tone_created_at" });
db.messages.createIndex({ "chatRoomId": 1, "mediaUrl": 1, "createdAt": -1 }, { name: "idx_room_media_created_at" });

// Indexes for AI features
db.messages.createIndex({ "voiceEmotion": 1, "createdAt": -1 }, { name: "idx_voice_emotion_created_at" });
db.messages.createIndex({ "memoryTriggers": 1, "createdAt": -1 }, { name: "idx_memory_triggers_created_at" });
db.messages.createIndex({ "predictiveText": 1, "createdAt": -1 }, { name: "idx_predictive_text_created_at" });
db.messages.createIndex({ "aiToneSuggestion": 1, "createdAt": -1 }, { name: "idx_ai_tone_suggestion_created_at" });

// Indexes for location and contact features
db.messages.createIndex({ "locationLatitude": 1, "locationLongitude": 1, "createdAt": -1 }, { name: "idx_location_created_at" });
db.messages.createIndex({ "contactName": 1, "createdAt": -1 }, { name: "idx_contact_name_created_at" });
db.messages.createIndex({ "contactPhone": 1, "createdAt": -1 }, { name: "idx_contact_phone_created_at" });

// Indexes for password protection features
db.messages.createIndex({ "protectionLevel": 1, "createdAt": -1 }, { name: "idx_protection_level_created_at" });
db.messages.createIndex({ "selfDestructAt": 1 }, { name: "idx_self_destruct_at" });
db.messages.createIndex({ "maxViews": 1, "viewCount": 1 }, { name: "idx_view_limits" });
db.messages.createIndex({ "screenshotProtection": 1, "createdAt": -1 }, { name: "idx_screenshot_protection_created_at" });

// Indexes for message relationships
db.messages.createIndex({ "replyToMessageId": 1, "createdAt": -1 }, { name: "idx_reply_to_created_at" });
db.messages.createIndex({ "forwardedFromMessageId": 1, "createdAt": -1 }, { name: "idx_forwarded_from_created_at" });

// Indexes for read receipts
db.messages.createIndex({ "readBy": 1, "createdAt": -1 }, { name: "idx_read_by_created_at" });

// Indexes for reactions
db.messages.createIndex({ "reactions": 1, "createdAt": -1 }, { name: "idx_reactions_created_at" });

// Text index for message content search
db.messages.createIndex({ "content": "text", "contextWrapper": "text" }, { name: "idx_text_search" });

// TTL index for self-destructing messages (if selfDestructAt is set)
db.messages.createIndex({ "selfDestructAt": 1 }, { expireAfterSeconds: 0, name: "idx_ttl_self_destruct" });

// Partial indexes for better performance
db.messages.createIndex(
    { "chatRoomId": 1, "createdAt": -1 },
    { 
        partialFilterExpression: { "isDeleted": { $ne: true } },
        name: "idx_room_active_messages"
    }
);

db.messages.createIndex(
    { "chatRoomId": 1, "isStarred": 1, "createdAt": -1 },
    { 
        partialFilterExpression: { "isStarred": true },
        name: "idx_room_starred_messages"
    }
);

db.messages.createIndex(
    { "chatRoomId": 1, "isProtected": 1, "createdAt": -1 },
    { 
        partialFilterExpression: { "isProtected": true },
        name: "idx_room_protected_messages"
    }
);

db.messages.createIndex(
    { "chatRoomId": 1, "mediaUrl": 1, "createdAt": -1 },
    { 
        partialFilterExpression: { "mediaUrl": { $exists: true, $ne: null } },
        name: "idx_room_media_messages"
    }
);

// Create indexes for media files collection (if it exists)
db.media_files.createIndex({ "messageId": 1, "createdAt": -1 }, { name: "idx_media_message_created_at" });
db.media_files.createIndex({ "userId": 1, "createdAt": -1 }, { name: "idx_media_user_created_at" });
db.media_files.createIndex({ "fileType": 1, "createdAt": -1 }, { name: "idx_media_type_created_at" });
db.media_files.createIndex({ "isProcessed": 1, "createdAt": -1 }, { name: "idx_media_processed_created_at" });

// Create indexes for chat sessions collection (if it exists)
db.chat_sessions.createIndex({ "userId": 1, "lastActivity": -1 }, { name: "idx_session_user_activity" });
db.chat_sessions.createIndex({ "sessionId": 1 }, { unique: true, name: "idx_session_id" });
db.chat_sessions.createIndex({ "connectionStatus": 1, "lastActivity": -1 }, { name: "idx_session_status_activity" });
db.chat_sessions.createIndex({ "expiresAt": 1 }, { expireAfterSeconds: 0, name: "idx_ttl_sessions" });

// Print index creation summary
print("MongoDB indexes created successfully for Chat Service");
print("Total indexes created for messages collection: " + db.messages.getIndexes().length);
print("Total indexes created for media_files collection: " + (db.media_files.getIndexes().length || 0));
print("Total indexes created for chat_sessions collection: " + (db.chat_sessions.getIndexes().length || 0));
