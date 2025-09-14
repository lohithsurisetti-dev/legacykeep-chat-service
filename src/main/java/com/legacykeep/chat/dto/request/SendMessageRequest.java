package com.legacykeep.chat.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.legacykeep.chat.enums.MessageType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for sending a new message.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendMessageRequest {
    
    /**
     * Chat room ID
     */
    @NotNull(message = "Chat room ID is required")
    private Long chatRoomId;
    
    /**
     * Sender user ID
     */
    @NotNull(message = "Sender user ID is required")
    private Long senderUserId;
    
    /**
     * Message type
     */
    @NotNull(message = "Message type is required")
    private MessageType messageType;
    
    /**
     * Message content
     */
    @Size(max = 10000, message = "Message content must not exceed 10000 characters")
    private String content;
    
    /**
     * Reply to message ID
     */
    @Size(max = 255, message = "Reply to message ID must not exceed 255 characters")
    private String replyToMessageId;
    
    /**
     * Forwarded from message ID
     */
    @Size(max = 255, message = "Forwarded from message ID must not exceed 255 characters")
    private String forwardedFromMessageId;
    
    // Advanced Features - Tone & Context
    /**
     * Tone color for the message
     */
    @Size(max = 7, message = "Tone color must be a valid hex color (7 characters)")
    private String toneColor;
    
    /**
     * Contextual wrapper for the message
     */
    @Size(max = 500, message = "Context wrapper must not exceed 500 characters")
    private String contextWrapper;
    
    /**
     * Mood tag for the message
     */
    @Size(max = 50, message = "Mood tag must not exceed 50 characters")
    private String moodTag;
    
    /**
     * Tone confidence level
     */
    private Double toneConfidence;
    
    /**
     * Voice emotion detected
     */
    @Size(max = 50, message = "Voice emotion must not exceed 50 characters")
    private String voiceEmotion;
    
    /**
     * Memory triggers
     */
    private java.util.List<String> memoryTriggers;
    
    /**
     * Predictive text suggestions
     */
    private java.util.List<String> predictiveText;
    
    /**
     * AI tone suggestion
     */
    @Size(max = 100, message = "AI tone suggestion must not exceed 100 characters")
    private String aiToneSuggestion;
    
    // Advanced Features - Password Protection
    /**
     * Whether the message should be protected
     */
    @Builder.Default
    private Boolean isProtected = false;
    
    /**
     * Whether the message is encrypted
     */
    @Builder.Default
    private Boolean isEncrypted = false;
    
    /**
     * Protection level
     */
    @Size(max = 50, message = "Protection level must not exceed 50 characters")
    private String protectionLevel;
    
    /**
     * Password for protected message
     */
    @Size(max = 255, message = "Password must not exceed 255 characters")
    private String password;
    
    /**
     * Password hash for protected message
     */
    @Size(max = 255, message = "Password hash must not exceed 255 characters")
    private String passwordHash;
    
    /**
     * Self-destruct duration in minutes
     */
    private Integer selfDestructMinutes;
    
    /**
     * Self-destruct timestamp
     */
    private java.time.LocalDateTime selfDestructAt;
    
    /**
     * Whether screenshot protection should be enabled
     */
    @Builder.Default
    private Boolean screenshotProtection = false;
    
    /**
     * Maximum views allowed
     */
    private Integer maxViews;
    
    // Media and File Information
    /**
     * Media URL
     */
    @Size(max = 1000, message = "Media URL must not exceed 1000 characters")
    private String mediaUrl;
    
    /**
     * Media thumbnail URL
     */
    @Size(max = 1000, message = "Media thumbnail URL must not exceed 1000 characters")
    private String mediaThumbnailUrl;
    
    /**
     * Media file size
     */
    private Long mediaSize;
    
    /**
     * Media duration (for audio/video)
     */
    private Integer mediaDuration;
    
    /**
     * Media format
     */
    @Size(max = 50, message = "Media format must not exceed 50 characters")
    private String mediaFormat;
    
    /**
     * Media metadata
     */
    private Map<String, Object> mediaMetadata;
    
    // Location Information
    /**
     * Location latitude
     */
    private Double locationLatitude;
    
    /**
     * Location longitude
     */
    private Double locationLongitude;
    
    /**
     * Location address
     */
    @Size(max = 500, message = "Location address must not exceed 500 characters")
    private String locationAddress;
    
    /**
     * Location name
     */
    @Size(max = 255, message = "Location name must not exceed 255 characters")
    private String locationName;
    
    // Contact Information
    /**
     * Contact name
     */
    @Size(max = 255, message = "Contact name must not exceed 255 characters")
    private String contactName;
    
    /**
     * Contact phone
     */
    @Size(max = 50, message = "Contact phone must not exceed 50 characters")
    private String contactPhone;
    
    /**
     * Contact email
     */
    @Size(max = 255, message = "Contact email must not exceed 255 characters")
    private String contactEmail;
    
    // Story and Memory Integration
    /**
     * Associated story ID
     */
    private Long storyId;
    
    /**
     * Associated memory ID
     */
    private Long memoryId;
    
    /**
     * Associated event ID
     */
    private Long eventId;
    
    /**
     * Message metadata
     */
    private Map<String, Object> metadata;
}
