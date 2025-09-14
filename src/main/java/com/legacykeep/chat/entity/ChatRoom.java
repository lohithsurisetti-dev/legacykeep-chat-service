package com.legacykeep.chat.entity;

import com.legacykeep.chat.enums.ChatRoomType;
import com.legacykeep.chat.enums.ChatRoomStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ChatRoom Entity
 * 
 * Represents both individual and group chat rooms in the family communication system.
 * Supports individual chats, family groups, story-specific chats, and event-based chats.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Entity
@Table(name = "chat_rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_uuid", nullable = false, unique = true)
    private UUID roomUuid;

    @Column(name = "room_name", length = 255)
    private String roomName;

    @Column(name = "room_description", columnDefinition = "TEXT")
    private String roomDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false, length = 20)
    private ChatRoomType roomType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ChatRoomStatus status = ChatRoomStatus.ACTIVE;

    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    @Column(name = "family_id")
    private Long familyId;

    @Column(name = "story_id")
    private Long storyId;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "room_photo_url")
    private String roomPhotoUrl;

    @Column(name = "is_encrypted", nullable = false)
    @Builder.Default
    private Boolean isEncrypted = false;

    @Column(name = "is_archived", nullable = false)
    @Builder.Default
    private Boolean isArchived = false;

    @Column(name = "is_muted", nullable = false)
    @Builder.Default
    private Boolean isMuted = false;

    @Column(name = "last_message_id")
    private String lastMessageId;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @Column(name = "last_message_by_user_id")
    private Long lastMessageByUserId;

    @Column(name = "message_count", nullable = false)
    @Builder.Default
    private Long messageCount = 0L;

    @Column(name = "participant_count", nullable = false)
    @Builder.Default
    private Integer participantCount = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "room_settings", columnDefinition = "jsonb")
    private String roomSettings; // JSON object with room-specific settings

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "privacy_settings", columnDefinition = "jsonb")
    private String privacySettings; // JSON object with privacy settings

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "notification_settings", columnDefinition = "jsonb")
    private String notificationSettings; // JSON object with notification preferences

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // Additional metadata for the chat room

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Check if this is an individual chat room
     */
    public boolean isIndividualChat() {
        return roomType == ChatRoomType.INDIVIDUAL;
    }

    /**
     * Check if this is a group chat room
     */
    public boolean isGroupChat() {
        return roomType == ChatRoomType.GROUP;
    }

    /**
     * Check if this is a family group chat
     */
    public boolean isFamilyGroup() {
        return roomType == ChatRoomType.FAMILY_GROUP;
    }

    /**
     * Check if this is a story-specific chat
     */
    public boolean isStoryChat() {
        return roomType == ChatRoomType.STORY_CHAT;
    }

    /**
     * Check if this is an event-specific chat
     */
    public boolean isEventChat() {
        return roomType == ChatRoomType.EVENT_CHAT;
    }

    /**
     * Check if the room is active
     */
    public boolean isActive() {
        return status == ChatRoomStatus.ACTIVE;
    }

    /**
     * Check if the room is archived
     */
    public boolean isArchived() {
        return isArchived;
    }

    /**
     * Check if the room is encrypted
     */
    public boolean isEncrypted() {
        return isEncrypted;
    }

    /**
     * Check if the room is muted
     */
    public boolean isMuted() {
        return isMuted;
    }

    /**
     * Check if the room has recent activity
     */
    public boolean hasRecentActivity() {
        return lastMessageAt != null && lastMessageAt.isAfter(LocalDateTime.now().minusDays(7));
    }

    /**
     * Check if the room is a family-related chat
     */
    public boolean isFamilyRelated() {
        return familyId != null || isFamilyGroup() || isStoryChat() || isEventChat();
    }

    /**
     * Get the display name for the room
     */
    public String getDisplayName() {
        if (roomName != null && !roomName.trim().isEmpty()) {
            return roomName;
        }
        
        switch (roomType) {
            case INDIVIDUAL:
                return "Individual Chat";
            case GROUP:
                return "Group Chat";
            case FAMILY_GROUP:
                return "Family Group";
            case STORY_CHAT:
                return "Story Chat";
            case EVENT_CHAT:
                return "Event Chat";
            default:
                return "Chat Room";
        }
    }

    /**
     * Check if the room has a story associated
     */
    public boolean hasStory() {
        return storyId != null;
    }

    /**
     * Check if the room has an event associated
     */
    public boolean hasEvent() {
        return eventId != null;
    }

    /**
     * Check if the room has a family associated
     */
    public boolean hasFamily() {
        return familyId != null;
    }
}
