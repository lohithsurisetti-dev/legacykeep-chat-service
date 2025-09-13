package com.legacykeep.chat.entity;

import com.legacykeep.chat.enums.AuditAction;
import com.legacykeep.chat.enums.AuditEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * ChatAudit Entity
 * 
 * Comprehensive audit logging for all chat-related activities.
 * Tracks user actions, system events, and security-related activities.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Entity
@Table(name = "chat_audit_logs", 
       indexes = {
           @Index(name = "idx_audit_user_id", columnList = "user_id"),
           @Index(name = "idx_audit_entity_type", columnList = "entity_type"),
           @Index(name = "idx_audit_action", columnList = "action"),
           @Index(name = "idx_audit_created_at", columnList = "created_at"),
           @Index(name = "idx_audit_chat_room_id", columnList = "chat_room_id")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "chat_room_id")
    private Long chatRoomId;

    @Column(name = "message_id")
    private String messageId; // MongoDB message ID

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 20)
    private AuditEntity entityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 30)
    private AuditAction action;

    @Column(name = "entity_id")
    private String entityId; // ID of the affected entity

    @Column(name = "old_values", columnDefinition = "TEXT")
    private String oldValues; // JSON string of old values

    @Column(name = "new_values", columnDefinition = "TEXT")
    private String newValues; // JSON string of new values

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "request_id", length = 100)
    private String requestId;

    @Column(name = "is_successful", nullable = false)
    @Builder.Default
    private Boolean isSuccessful = true;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "error_code", length = 50)
    private String errorCode;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    @Column(name = "family_id")
    private Long familyId;

    @Column(name = "story_id")
    private Long storyId;

    @Column(name = "event_id")
    private Long eventId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "context_data", columnDefinition = "jsonb")
    private String contextData; // JSON object with additional context

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "security_context", columnDefinition = "jsonb")
    private String securityContext; // JSON object with security-related context

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // Additional metadata for the audit log

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    
    /**
     * Check if the audit log is successful
     */
    public boolean isSuccessful() {
        return isSuccessful != null && isSuccessful;
    }
    
    /**
     * Check if the audit log has an error
     */
    public boolean hasError() {
        return !isSuccessful() || (errorMessage != null && !errorMessage.trim().isEmpty());
    }
    
    /**
     * Check if the audit log is related to a specific user
     */
    public boolean isUserRelated() {
        return userId != null;
    }
    
    /**
     * Check if the audit log is related to a specific chat room
     */
    public boolean isChatRoomRelated() {
        return chatRoomId != null;
    }
    
    /**
     * Check if the audit log is related to a specific message
     */
    public boolean isMessageRelated() {
        return messageId != null && !messageId.trim().isEmpty();
    }
    
    /**
     * Check if the audit log is family-related
     */
    public boolean isFamilyRelated() {
        return familyId != null;
    }
    
    /**
     * Check if the audit log is story-related
     */
    public boolean isStoryRelated() {
        return storyId != null;
    }
    
    /**
     * Check if the audit log is event-related
     */
    public boolean isEventRelated() {
        return eventId != null;
    }
    
    /**
     * Check if the audit log is a CRUD operation
     */
    public boolean isCrudOperation() {
        return action.isCrudAction();
    }
    
    /**
     * Check if the audit log is a message operation
     */
    public boolean isMessageOperation() {
        return action.isMessageAction();
    }
    
    /**
     * Check if the audit log is a security operation
     */
    public boolean isSecurityOperation() {
        return action.isSecurityAction();
    }
    
    /**
     * Check if the audit log is a system operation
     */
    public boolean isSystemOperation() {
        return action.isSystemAction();
    }
    
    /**
     * Get the entity type display name
     */
    public String getEntityTypeDisplayName() {
        return entityType.getDisplayName();
    }
    
    /**
     * Get the action display name
     */
    public String getActionDisplayName() {
        return action.getDisplayName();
    }
    
    /**
     * Check if the audit log has context data
     */
    public boolean hasContextData() {
        return contextData != null && !contextData.trim().isEmpty();
    }
    
    /**
     * Check if the audit log has security context
     */
    public boolean hasSecurityContext() {
        return securityContext != null && !securityContext.trim().isEmpty();
    }
    
    /**
     * Check if the audit log has metadata
     */
    public boolean hasMetadata() {
        return metadata != null && !metadata.trim().isEmpty();
    }
}
