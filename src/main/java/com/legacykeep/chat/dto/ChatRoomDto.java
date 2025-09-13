package com.legacykeep.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.legacykeep.chat.enums.ChatRoomStatus;
import com.legacykeep.chat.enums.ChatRoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Chat Room DTO for API responses.
 * 
 * Represents chat room information in API responses.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRoomDto {
    
    /**
     * Chat room ID
     */
    private Long id;
    
    /**
     * Chat room UUID
     */
    private String roomUuid;
    
    /**
     * Chat room name
     */
    private String roomName;
    
    /**
     * Chat room description
     */
    private String roomDescription;
    
    /**
     * Chat room type
     */
    private ChatRoomType roomType;
    
    /**
     * Chat room status
     */
    private ChatRoomStatus status;
    
    /**
     * User ID who created the room
     */
    private Long createdByUserId;
    
    /**
     * Family ID (if applicable)
     */
    private Long familyId;
    
    /**
     * Story ID (if applicable)
     */
    private Long storyId;
    
    /**
     * Event ID (if applicable)
     */
    private Long eventId;
    
    /**
     * Room photo URL
     */
    private String roomPhotoUrl;
    
    /**
     * Whether the room is encrypted
     */
    private Boolean isEncrypted;
    
    /**
     * Whether the room is archived
     */
    private Boolean isArchived;
    
    /**
     * Whether the room is muted
     */
    private Boolean isMuted;
    
    /**
     * Last message ID
     */
    private Long lastMessageId;
    
    /**
     * Last message timestamp
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime lastMessageAt;
    
    /**
     * User ID who sent the last message
     */
    private Long lastMessageByUserId;
    
    /**
     * Total message count
     */
    private Long messageCount;
    
    /**
     * Number of participants
     */
    private Integer participantCount;
    
    /**
     * Room settings (JSON)
     */
    private Map<String, Object> roomSettings;
    
    /**
     * Privacy settings (JSON)
     */
    private Map<String, Object> privacySettings;
    
    /**
     * Notification settings (JSON)
     */
    private Map<String, Object> notificationSettings;
    
    /**
     * Additional metadata (JSON)
     */
    private Map<String, Object> metadata;
    
    /**
     * Room creation timestamp
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;
    
    /**
     * Room last update timestamp
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updatedAt;
    
    /**
     * Display name for the room
     */
    private String displayName;
    
    /**
     * Whether the room has recent activity
     */
    private Boolean hasRecentActivity;
    
    /**
     * Whether the room is family-related
     */
    private Boolean isFamilyRelated;
}
