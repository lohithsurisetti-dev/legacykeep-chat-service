package com.legacykeep.chat.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for updating a chat room.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateChatRoomRequest {
    
    /**
     * User ID who is updating the chat room
     */
    private Long updatedByUserId;
    
    /**
     * Chat room name
     */
    @Size(min = 1, max = 255, message = "Room name must be between 1 and 255 characters")
    private String roomName;
    
    /**
     * Chat room description
     */
    @Size(max = 1000, message = "Room description must not exceed 1000 characters")
    private String roomDescription;
    
    /**
     * Room photo URL
     */
    @Size(max = 500, message = "Room photo URL must not exceed 500 characters")
    private String roomPhotoUrl;
    
    /**
     * Whether the room should be encrypted
     */
    private Boolean isEncrypted;
    
    /**
     * Whether the room should be archived
     */
    private Boolean isArchived;
    
    /**
     * Whether the room should be muted
     */
    private Boolean isMuted;
    
    /**
     * Room settings
     */
    private Map<String, Object> roomSettings;
    
    /**
     * Privacy settings
     */
    private Map<String, Object> privacySettings;
    
    /**
     * Notification settings
     */
    private Map<String, Object> notificationSettings;
    
    /**
     * Additional metadata
     */
    private Map<String, Object> metadata;
}
