package com.legacykeep.chat.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.legacykeep.chat.enums.ChatRoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Request DTO for creating a new chat room.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateChatRoomRequest {
    
    /**
     * Creator user ID
     */
    @NotNull(message = "Creator user ID is required")
    private Long createdByUserId;
    
    /**
     * Chat room name
     */
    @NotBlank(message = "Room name is required")
    @Size(min = 1, max = 255, message = "Room name must be between 1 and 255 characters")
    private String roomName;
    
    /**
     * Chat room description
     */
    @Size(max = 1000, message = "Room description must not exceed 1000 characters")
    private String roomDescription;
    
    /**
     * Chat room type
     */
    @NotNull(message = "Room type is required")
    private ChatRoomType roomType;
    
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
    @Size(max = 500, message = "Room photo URL must not exceed 500 characters")
    private String roomPhotoUrl;
    
    /**
     * Whether the room should be encrypted
     */
    @Builder.Default
    private Boolean isEncrypted = false;
    
    /**
     * Initial participants (user IDs)
     */
    private List<Long> participantUserIds;
    
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
    
    /**
     * Getter for room name (alias for roomName)
     */
    public String getName() {
        return roomName;
    }
    
    /**
     * Getter for room description (alias for roomDescription)
     */
    public String getDescription() {
        return roomDescription;
    }
    
    /**
     * Getter for room type (alias for roomType)
     */
    public ChatRoomType getType() {
        return roomType;
    }
}
