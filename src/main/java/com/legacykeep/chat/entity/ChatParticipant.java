package com.legacykeep.chat.entity;

import com.legacykeep.chat.enums.ParticipantRole;
import com.legacykeep.chat.enums.ParticipantStatus;
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

/**
 * ChatParticipant Entity
 * 
 * Represents the relationship between users and chat rooms.
 * Manages participant roles, permissions, and status in both individual and group chats.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Entity
@Table(name = "chat_participants", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"chat_room_id", "user_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_room_id", nullable = false)
    private Long chatRoomId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private ParticipantRole role = ParticipantRole.MEMBER;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ParticipantStatus status = ParticipantStatus.ACTIVE;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "last_read_message_id")
    private Long lastReadMessageId;

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @Column(name = "is_muted", nullable = false)
    @Builder.Default
    private Boolean isMuted = false;

    @Column(name = "is_archived", nullable = false)
    @Builder.Default
    private Boolean isArchived = false;

    @Column(name = "is_pinned", nullable = false)
    @Builder.Default
    private Boolean isPinned = false;

    @Column(name = "is_notifications_enabled", nullable = false)
    @Builder.Default
    private Boolean isNotificationsEnabled = true;

    @Column(name = "unread_count", nullable = false)
    @Builder.Default
    private Integer unreadCount = 0;

    @Column(name = "message_count", nullable = false)
    @Builder.Default
    private Long messageCount = 0L;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "permissions", columnDefinition = "jsonb")
    private String permissions; // JSON object with specific permissions

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "notification_settings", columnDefinition = "jsonb")
    private String notificationSettings; // JSON object with notification preferences

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // Additional metadata for the participant

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Check if the participant is an admin
     */
    public boolean isAdmin() {
        return role == ParticipantRole.ADMIN || role == ParticipantRole.SUPER_ADMIN;
    }

    /**
     * Check if the participant is a super admin
     */
    public boolean isSuperAdmin() {
        return role == ParticipantRole.SUPER_ADMIN;
    }

    /**
     * Check if the participant is a moderator
     */
    public boolean isModerator() {
        return role == ParticipantRole.MODERATOR;
    }

    /**
     * Check if the participant is a regular member
     */
    public boolean isMember() {
        return role == ParticipantRole.MEMBER;
    }

    /**
     * Check if the participant is active
     */
    public boolean isActive() {
        return status == ParticipantStatus.ACTIVE;
    }

    /**
     * Check if the participant has left
     */
    public boolean hasLeft() {
        return status == ParticipantStatus.LEFT;
    }

    /**
     * Check if the participant is blocked
     */
    public boolean isBlocked() {
        return status == ParticipantStatus.BLOCKED;
    }

    /**
     * Check if the participant is muted
     */
    public boolean isMuted() {
        return isMuted != null && isMuted;
    }

    /**
     * Check if the participant has archived the chat
     */
    public boolean isArchived() {
        return isArchived != null && isArchived;
    }

    /**
     * Check if the participant has pinned the chat
     */
    public boolean isPinned() {
        return isPinned != null && isPinned;
    }

    /**
     * Check if notifications are enabled
     */
    public boolean hasNotificationsEnabled() {
        return isNotificationsEnabled != null && isNotificationsEnabled;
    }

    /**
     * Check if the participant has unread messages
     */
    public boolean hasUnreadMessages() {
        return unreadCount != null && unreadCount > 0;
    }

    /**
     * Check if the participant can send messages
     */
    public boolean canSendMessages() {
        return isActive() && !isBlocked();
    }

    /**
     * Check if the participant can manage the chat
     */
    public boolean canManageChat() {
        return isAdmin() || isModerator();
    }

    /**
     * Check if the participant can add members
     */
    public boolean canAddMembers() {
        return isAdmin() || isModerator();
    }

    /**
     * Check if the participant can remove members
     */
    public boolean canRemoveMembers() {
        return isAdmin();
    }

    /**
     * Check if the participant can change chat settings
     */
    public boolean canChangeSettings() {
        return isAdmin();
    }

    /**
     * Check if the participant has been active recently
     */
    public boolean hasRecentActivity() {
        return lastActivityAt != null && 
               lastActivityAt.isAfter(LocalDateTime.now().minusDays(7));
    }

    /**
     * Check if the participant has read recent messages
     */
    public boolean hasReadRecentMessages() {
        return lastReadAt != null && 
               lastReadAt.isAfter(LocalDateTime.now().minusDays(1));
    }

    /**
     * Get the participant's display role
     */
    public String getDisplayRole() {
        return role != null ? role.getDisplayName() : "Member";
    }

    /**
     * Get the participant's status description
     */
    public String getStatusDescription() {
        return status != null ? status.getDescription() : "Unknown";
    }
}
