package com.legacykeep.chat.entity;

import com.legacykeep.chat.enums.MessageType;
import com.legacykeep.chat.enums.MessageStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Message Entity (MongoDB Document)
 * 
 * Represents individual messages in the family communication system.
 * Stored in MongoDB for high-performance message operations.
 * Supports all advanced features including tone colors, password protection, and AI features.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Document(collection = "messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    private String id;

    @Field("message_uuid")
    @Indexed(unique = true)
    private String messageUuid;

    @Field("chat_room_id")
    @Indexed
    private Long chatRoomId;

    @Field("sender_user_id")
    @Indexed
    private Long senderUserId;

    @Field("message_type")
    private MessageType messageType;

    @Field("content")
    private String content;

    @Field("status")
    private MessageStatus status;

    @Field("reply_to_message_id")
    private String replyToMessageId;

    @Field("forwarded_from_message_id")
    private String forwardedFromMessageId;

    @Field("edited_at")
    private LocalDateTime editedAt;

    @Field("deleted_at")
    private LocalDateTime deletedAt;

    @Field("deleted_by_user_id")
    private Long deletedByUserId;

    @Field("is_deleted_for_everyone")
    private Boolean isDeletedForEveryone;

    @Field("is_starred")
    private Boolean isStarred;

    @Field("is_encrypted")
    private Boolean isEncrypted;

    // Advanced Features - Tone & Context
    @Field("tone_color")
    private String toneColor; // Hex color for message tone

    @Field("tone_confidence")
    private Double toneConfidence; // AI confidence in tone detection

    @Field("context_wrapper")
    private String contextWrapper; // Contextual wrapper for message

    @Field("mood_tag")
    private String moodTag; // Mood tag for the message

    // Advanced Features - Password Protection
    @Field("is_protected")
    private Boolean isProtected;

    @Field("protection_level")
    private String protectionLevel; // PASSWORD, SCREENSHOT, SELF_DESTRUCT, MULTI_LAYER

    @Field("password_hash")
    private String passwordHash;

    @Field("self_destruct_at")
    private LocalDateTime selfDestructAt;

    @Field("screenshot_protection")
    private Boolean screenshotProtection;

    @Field("view_count")
    private Integer viewCount;

    @Field("max_views")
    private Integer maxViews;

    // Advanced Features - AI Integration
    @Field("voice_emotion")
    private String voiceEmotion; // Detected emotion from voice

    @Field("memory_triggers")
    private List<String> memoryTriggers; // AI-detected memory triggers

    @Field("predictive_text")
    private String predictiveText; // AI-suggested text

    @Field("ai_tone_suggestion")
    private String aiToneSuggestion; // AI-suggested tone

    // Media and File Information
    @Field("media_url")
    private String mediaUrl;

    @Field("media_thumbnail_url")
    private String mediaThumbnailUrl;

    @Field("media_size")
    private Long mediaSize;

    @Field("media_duration")
    private Integer mediaDuration; // For audio/video in seconds

    @Field("media_format")
    private String mediaFormat;

    @Field("media_metadata")
    private Map<String, Object> mediaMetadata;

    // Location Information
    @Field("location_latitude")
    private Double locationLatitude;

    @Field("location_longitude")
    private Double locationLongitude;

    @Field("location_address")
    private String locationAddress;

    @Field("location_name")
    private String locationName;

    // Contact Information
    @Field("contact_name")
    private String contactName;

    @Field("contact_phone")
    private String contactPhone;

    @Field("contact_email")
    private String contactEmail;

    // Story and Memory Integration
    @Field("story_id")
    private Long storyId;

    @Field("memory_id")
    private Long memoryId;

    @Field("event_id")
    private Long eventId;

    // Message Reactions
    @Field("reactions")
    private Map<String, List<Long>> reactions; // emoji -> list of user IDs

    // Read Receipts
    @Field("read_by")
    private Map<Long, LocalDateTime> readBy; // user ID -> read timestamp

    // Message Metadata
    @Field("metadata")
    private Map<String, Object> metadata;

    @Field("created_at")
    @Indexed
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    /**
     * Check if the message is a text message
     */
    public boolean isTextMessage() {
        return messageType == MessageType.TEXT;
    }

    /**
     * Check if the message is a media message
     */
    public boolean isMediaMessage() {
        return messageType.isMediaType();
    }

    /**
     * Check if the message is a family-specific message
     */
    public boolean isFamilyMessage() {
        return messageType.isFamilySpecific();
    }

    /**
     * Check if the message is protected
     */
    public boolean isProtected() {
        return isProtected != null && isProtected;
    }

    /**
     * Check if the message has tone color
     */
    public boolean hasToneColor() {
        return toneColor != null && !toneColor.trim().isEmpty();
    }

    /**
     * Check if the message is encrypted
     */
    public boolean isEncrypted() {
        return isEncrypted != null && isEncrypted;
    }

    /**
     * Check if the message is starred
     */
    public boolean isStarred() {
        return isStarred != null && isStarred;
    }

    /**
     * Check if the message is deleted
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * Check if the message is edited
     */
    public boolean isEdited() {
        return editedAt != null;
    }

    /**
     * Check if the message has been read by a specific user
     */
    public boolean isReadBy(Long userId) {
        return readBy != null && readBy.containsKey(userId);
    }

    /**
     * Check if the message has reactions
     */
    public boolean hasReactions() {
        return reactions != null && !reactions.isEmpty();
    }

    /**
     * Check if the message has media
     */
    public boolean hasMedia() {
        return mediaUrl != null && !mediaUrl.trim().isEmpty();
    }

    /**
     * Check if the message has location
     */
    public boolean hasLocation() {
        return locationLatitude != null && locationLongitude != null;
    }

    /**
     * Check if the message has contact information
     */
    public boolean hasContact() {
        return contactName != null && !contactName.trim().isEmpty();
    }

    /**
     * Check if the message is associated with a story
     */
    public boolean hasStory() {
        return storyId != null;
    }

    /**
     * Check if the message is associated with a memory
     */
    public boolean hasMemory() {
        return memoryId != null;
    }

    /**
     * Check if the message is associated with an event
     */
    public boolean hasEvent() {
        return eventId != null;
    }

    /**
     * Check if the message has AI features
     */
    public boolean hasAIFeatures() {
        return voiceEmotion != null || 
               (memoryTriggers != null && !memoryTriggers.isEmpty()) ||
               predictiveText != null ||
               aiToneSuggestion != null;
    }

    /**
     * Check if the message has screenshot protection
     */
    public boolean hasScreenshotProtection() {
        return screenshotProtection != null && screenshotProtection;
    }

    /**
     * Check if the message has view limits
     */
    public boolean hasViewLimits() {
        return maxViews != null && maxViews > 0;
    }

    /**
     * Check if the message has reached view limit
     */
    public boolean hasReachedViewLimit() {
        return hasViewLimits() && viewCount != null && viewCount >= maxViews;
    }

    /**
     * Check if the message has self-destruct
     */
    public boolean hasSelfDestruct() {
        return selfDestructAt != null;
    }

    /**
     * Check if the message has expired (self-destructed)
     */
    public boolean hasExpired() {
        return hasSelfDestruct() && selfDestructAt.isBefore(LocalDateTime.now());
    }
}
