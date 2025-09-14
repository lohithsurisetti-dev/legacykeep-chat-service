package com.legacykeep.chat.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.legacykeep.chat.dto.request.CreateChatRoomRequest;
import com.legacykeep.chat.dto.request.SendMessageRequest;
import com.legacykeep.chat.dto.request.ReactionRequest;
import com.legacykeep.chat.enums.ChatRoomType;
import com.legacykeep.chat.enums.MessageType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DTO Serialization Test
 * 
 * Tests the serialization and deserialization of DTOs
 * to ensure they work correctly with JSON.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
class DtoSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void testApiResponseSerialization() throws Exception {
        // Test successful response
        ApiResponse<String> successResponse = ApiResponse.success("Test data", "Operation successful");
        String json = objectMapper.writeValueAsString(successResponse);
        
        assertNotNull(json);
        assertTrue(json.contains("success"));
        assertTrue(json.contains("Test data"));
        assertTrue(json.contains("Operation successful"));
        
        // Test error response
        ApiResponse<String> errorResponse = ApiResponse.error("Something went wrong", "Internal error", 500);
        String errorJson = objectMapper.writeValueAsString(errorResponse);
        
        assertNotNull(errorJson);
        assertTrue(errorJson.contains("error"));
        assertTrue(errorJson.contains("Something went wrong"));
    }

    @Test
    void testChatRoomDtoSerialization() throws Exception {
        ChatRoomDto chatRoom = ChatRoomDto.builder()
                .id(1L)
                .roomUuid("test-uuid")
                .roomName("Test Room")
                .roomDescription("Test Description")
                .roomType(ChatRoomType.GROUP)
                .createdByUserId(1L)
                .familyId(1L)
                .isEncrypted(false)
                .isArchived(false)
                .isMuted(false)
                .messageCount(0L)
                .participantCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        String json = objectMapper.writeValueAsString(chatRoom);
        
        assertNotNull(json);
        assertTrue(json.contains("Test Room"));
        assertTrue(json.contains("GROUP"));
        assertTrue(json.contains("test-uuid"));
    }

    @Test
    void testCreateChatRoomRequestSerialization() throws Exception {
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
                .roomName("New Room")
                .roomDescription("New Description")
                .roomType(ChatRoomType.FAMILY_GROUP)
                .familyId(1L)
                .isEncrypted(false)
                .participantUserIds(List.of(1L, 2L, 3L))
                .build();

        String json = objectMapper.writeValueAsString(request);
        
        assertNotNull(json);
        assertTrue(json.contains("New Room"));
        assertTrue(json.contains("FAMILY_GROUP"));
        assertTrue(json.contains("participantUserIds"));
    }

    @Test
    void testMessageDtoSerialization() throws Exception {
        MessageDto message = MessageDto.builder()
                .id("msg-123")
                .messageUuid("msg-uuid-123")
                .chatRoomId(1L)
                .senderUserId(1L)
                .messageType(MessageType.TEXT)
                .content("Hello, world!")
                .isStarred(false)
                .isEncrypted(false)
                .isProtected(false)
                .toneColor("#FF5733")
                .moodTag("happy")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        String json = objectMapper.writeValueAsString(message);
        
        assertNotNull(json);
        assertTrue(json.contains("Hello, world!"));
        assertTrue(json.contains("TEXT"));
        assertTrue(json.contains("#FF5733"));
        assertTrue(json.contains("happy"));
    }

    @Test
    void testSendMessageRequestSerialization() throws Exception {
        SendMessageRequest request = SendMessageRequest.builder()
                .messageType(MessageType.TEXT)
                .content("Test message")
                .toneColor("#00FF00")
                .moodTag("excited")
                .isProtected(false)
                .screenshotProtection(false)
                .build();

        String json = objectMapper.writeValueAsString(request);
        
        assertNotNull(json);
        assertTrue(json.contains("Test message"));
        assertTrue(json.contains("TEXT"));
        assertTrue(json.contains("#00FF00"));
        assertTrue(json.contains("excited"));
    }


    @Test
    void testReactionRequestSerialization() throws Exception {
        ReactionRequest request = ReactionRequest.builder()
                .emoji("👍")
                .reactionType("EMOJI")
                .build();

        String json = objectMapper.writeValueAsString(request);
        
        assertNotNull(json);
        assertTrue(json.contains("👍"));
        assertTrue(json.contains("EMOJI"));
    }

    @Test
    void testForwardMessageRequestSerialization() throws Exception {
        ForwardMessageRequest request = ForwardMessageRequest.builder()
                .targetChatRoomIds(List.of(1L, 2L, 3L))
                .additionalContent("Forwarded message")
                .includeMetadata(true)
                .build();

        String json = objectMapper.writeValueAsString(request);
        
        assertNotNull(json);
        assertTrue(json.contains("Forwarded message"));
        assertTrue(json.contains("targetChatRoomIds"));
        assertTrue(json.contains("\"includeMetadata\":true"));
    }
}
