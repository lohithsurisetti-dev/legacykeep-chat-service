package com.legacykeep.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.legacykeep.chat.enums.MessageStatus;
import com.legacykeep.chat.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Message DTO for API responses.
 * 
 * Represents message information in API responses.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDto {
    
    /**
     * Message ID
     */
    private String id;
    
    /**
     * Message UUID
     */
    private String messageUuid;
    
    /**
     * Chat room ID
     */
    private Long chatRoomId;
    
    /**
     * Sender user ID
     */
    private Long senderUserId;
    
    /**
     * Message type
     */
    private MessageType messageType;
    
    /**
     * Message content
     */
    private String content;
    
    /**
     * Message status
     */
    private MessageStatus status;
    
    /**
     * Reply to message ID
     */
    private String replyToMessageId;
    
    /**
     * Forwarded from message ID
     */
    private String forwardedFromMessageId;
    
    /**
     * Edit timestamp
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime editedAt;
    
    /**
     * Delete timestamp
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime deletedAt;
    
    /**
     * User ID who deleted the message
     */
    private Long deletedByUserId;
    
    /**
     * Whether deleted for everyone
     */
    private Boolean isDeletedForEveryone;
    
    /**
     * Whether the message is starred
     */
    private Boolean isStarred;
    
    /**
     * Whether the message is encrypted
     */
    private Boolean isEncrypted;
    
    // Advanced Features - Tone & Context
    /**
     * Tone color for the message
     */
    private String toneColor;
    
    /**
     * AI confidence in tone detection
     */
    private Double toneConfidence;
    
    /**
     * Contextual wrapper for the message
     */
    private String contextWrapper;
    
    /**
     * Mood tag for the message
     */
    private String moodTag;
    
    // Advanced Features - Password Protection
    /**
     * Whether the message is protected
     */
    private Boolean isProtected;
    
    /**
     * Protection level
     */
    private String protectionLevel;
    
    /**
     * Self-destruct timestamp
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime selfDestructAt;
    
    /**
     * Whether screenshot protection is enabled
     */
    private Boolean screenshotProtection;
    
    /**
     * View count
     */
    private Integer viewCount;
    
    /**
     * Maximum views allowed
     */
    private Integer maxViews;
    
    // Advanced Features - AI Integration
    /**
     * Detected voice emotion
     */
    private String voiceEmotion;
    
    /**
     * AI-detected memory triggers
     */
    private List<String> memoryTriggers;
    
    /**
     * AI-suggested text
     */
    private String predictiveText;
    
    /**
     * AI-suggested tone
     */
    private String aiToneSuggestion;
    
    // Media and File Information
    /**
     * Media URL
     */
    private String mediaUrl;
    
    /**
     * Media thumbnail URL
     */
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
    private String locationAddress;
    
    /**
     * Location name
     */
    private String locationName;
    
    // Contact Information
    /**
     * Contact name
     */
    private String contactName;
    
    /**
     * Contact phone
     */
    private String contactPhone;
    
    /**
     * Contact email
     */
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
    
    // Message Reactions
    /**
     * Message reactions (emoji -> list of user IDs)
     */
    private Map<String, List<Long>> reactions;
    
    // Read Receipts
    /**
     * Read receipts (user ID -> read timestamp)
     */
    private Map<Long, LocalDateTime> readBy;
    
    /**
     * Message metadata
     */
    private Map<String, Object> metadata;
    
    /**
     * Message creation timestamp
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;
    
    /**
     * Message last update timestamp
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updatedAt;
    
    // Computed fields
    /**
     * Whether the message is a text message
     */
    private Boolean isTextMessage;
    
    /**
     * Whether the message is a media message
     */
    private Boolean isMediaMessage;
    
    /**
     * Whether the message is a family-specific message
     */
    private Boolean isFamilyMessage;
    
    /**
     * Whether the message has tone color
     */
    private Boolean hasToneColor;
    
    /**
     * Whether the message has media
     */
    private Boolean hasMedia;
    
    /**
     * Whether the message has location
     */
    private Boolean hasLocation;
    
    /**
     * Whether the message has contact information
     */
    private Boolean hasContact;
    
    /**
     * Whether the message has AI features
     */
    private Boolean hasAIFeatures;
}
