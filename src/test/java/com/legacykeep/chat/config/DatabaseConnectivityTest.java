package com.legacykeep.chat.config;

import com.legacykeep.chat.entity.ChatRoom;
import com.legacykeep.chat.entity.Message;
import com.legacykeep.chat.enums.ChatRoomType;
import com.legacykeep.chat.enums.ChatRoomStatus;
import com.legacykeep.chat.enums.MessageType;
import com.legacykeep.chat.enums.MessageStatus;
import com.legacykeep.chat.repository.postgres.ChatRoomRepository;
import com.legacykeep.chat.repository.mongo.MessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Database Connectivity Test
 * 
 * Tests the database configuration and basic entity persistence
 * for both PostgreSQL and MongoDB connections.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
class DatabaseConnectivityTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Test
    @Transactional
    void testPostgreSQLConnection() {
        // Test PostgreSQL connection by creating and saving a ChatRoom
        ChatRoom chatRoom = ChatRoom.builder()
                .roomUuid(UUID.randomUUID())
                .roomName("Test Chat Room")
                .roomDescription("Test room for connectivity")
                .roomType(ChatRoomType.GROUP)
                .status(ChatRoomStatus.ACTIVE)
                .createdByUserId(1L)
                .familyId(1L)
                .isEncrypted(false)
                .isArchived(false)
                .isMuted(false)
                .messageCount(0L)
                .participantCount(0)
                .build();

        // Save to PostgreSQL
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        
        // Verify the save was successful
        assertNotNull(savedChatRoom);
        assertNotNull(savedChatRoom.getId());
        assertEquals("Test Chat Room", savedChatRoom.getRoomName());
        assertEquals(ChatRoomType.GROUP, savedChatRoom.getRoomType());
        assertEquals(ChatRoomStatus.ACTIVE, savedChatRoom.getStatus());
        
        // Test retrieval
        ChatRoom retrievedChatRoom = chatRoomRepository.findById(savedChatRoom.getId()).orElse(null);
        assertNotNull(retrievedChatRoom);
        assertEquals(savedChatRoom.getRoomName(), retrievedChatRoom.getRoomName());
        assertEquals(savedChatRoom.getRoomType(), retrievedChatRoom.getRoomType());
        
        // Clean up
        chatRoomRepository.delete(savedChatRoom);
    }

    @Test
    void testMongoDBConnection() {
        // Test MongoDB connection by creating and saving a Message
        Message message = Message.builder()
                .messageUuid(UUID.randomUUID().toString())
                .chatRoomId(1L)
                .senderUserId(1L)
                .messageType(MessageType.TEXT)
                .content("Test message for connectivity")
                .status(MessageStatus.SENT)
                .isStarred(false)
                .isEncrypted(false)
                .isProtected(false)
                .viewCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Save to MongoDB
        Message savedMessage = messageRepository.save(message);
        
        // Verify the save was successful
        assertNotNull(savedMessage);
        assertNotNull(savedMessage.getId());
        assertEquals("Test message for connectivity", savedMessage.getContent());
        assertEquals(MessageType.TEXT, savedMessage.getMessageType());
        assertEquals(MessageStatus.SENT, savedMessage.getStatus());
        
        // Test retrieval
        Message retrievedMessage = messageRepository.findById(savedMessage.getId()).orElse(null);
        assertNotNull(retrievedMessage);
        assertEquals(savedMessage.getContent(), retrievedMessage.getContent());
        assertEquals(savedMessage.getMessageType(), retrievedMessage.getMessageType());
        
        // Clean up
        messageRepository.delete(savedMessage);
    }

    @Test
    void testRepositoryInjection() {
        // Test that repositories are properly injected
        assertNotNull(chatRoomRepository, "ChatRoomRepository should be injected");
        assertNotNull(messageRepository, "MessageRepository should be injected");
    }
}
