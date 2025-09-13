package com.legacykeep.chat.entity;

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
 * FamilyPassword Entity
 * 
 * Manages family-specific passwords for protected messages and secure communication.
 * Supports multiple password types and family-specific security features.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Entity
@Table(name = "family_passwords")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_id", nullable = false)
    private Long familyId;

    @Column(name = "password_name", nullable = false, length = 100)
    private String passwordName;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "password_salt", nullable = false)
    private String passwordSalt;

    @Enumerated(EnumType.STRING)
    @Column(name = "password_type", nullable = false, length = 20)
    private PasswordType passwordType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PasswordStatus status = PasswordStatus.ACTIVE;

    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Column(name = "usage_count", nullable = false)
    @Builder.Default
    private Long usageCount = 0L;

    @Column(name = "max_usage_count")
    private Long maxUsageCount;

    @Column(name = "is_shared", nullable = false)
    @Builder.Default
    private Boolean isShared = false;

    @Column(name = "is_emergency", nullable = false)
    @Builder.Default
    private Boolean isEmergency = false;

    @Column(name = "is_temporary", nullable = false)
    @Builder.Default
    private Boolean isTemporary = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "allowed_users", columnDefinition = "jsonb")
    private String allowedUsers; // JSON array of user IDs who can use this password

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "usage_history", columnDefinition = "jsonb")
    private String usageHistory; // JSON array of usage records

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "security_settings", columnDefinition = "jsonb")
    private String securitySettings; // JSON object with security settings

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // Additional metadata for the password

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Password Type Enumeration
     */
    public enum PasswordType {
        /**
         * General family password
         */
        FAMILY("FAMILY", "Family Password", "General family password for protected messages"),
        
        /**
         * Emergency password
         */
        EMERGENCY("EMERGENCY", "Emergency Password", "Emergency password for urgent family communication"),
        
        /**
         * Story-specific password
         */
        STORY("STORY", "Story Password", "Password for story-specific protected messages"),
        
        /**
         * Event-specific password
         */
        EVENT("EVENT", "Event Password", "Password for event-specific protected messages"),
        
        /**
         * Memory-specific password
         */
        MEMORY("MEMORY", "Memory Password", "Password for memory-specific protected messages"),
        
        /**
         * Temporary password
         */
        TEMPORARY("TEMPORARY", "Temporary Password", "Temporary password with limited validity"),
        
        /**
         * Personal password
         */
        PERSONAL("PERSONAL", "Personal Password", "Personal password for individual use");
        
        private final String typeName;
        private final String displayName;
        private final String description;
        
        PasswordType(String typeName, String displayName, String description) {
            this.typeName = typeName;
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getTypeName() {
            return typeName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getDescription() {
            return description;
        }
        
        /**
         * Check if this is a family-specific password type
         */
        public boolean isFamilySpecific() {
            return this == FAMILY || this == STORY || this == EVENT || this == MEMORY;
        }
        
        /**
         * Check if this is an emergency password type
         */
        public boolean isEmergency() {
            return this == EMERGENCY;
        }
        
        /**
         * Check if this is a temporary password type
         */
        public boolean isTemporary() {
            return this == TEMPORARY;
        }
    }

    /**
     * Password Status Enumeration
     */
    public enum PasswordStatus {
        /**
         * Active password
         */
        ACTIVE("ACTIVE", "Active", "Password is active and can be used"),
        
        /**
         * Inactive password
         */
        INACTIVE("INACTIVE", "Inactive", "Password is inactive and cannot be used"),
        
        /**
         * Expired password
         */
        EXPIRED("EXPIRED", "Expired", "Password has expired"),
        
        /**
         * Suspended password
         */
        SUSPENDED("SUSPENDED", "Suspended", "Password is temporarily suspended"),
        
        /**
         * Revoked password
         */
        REVOKED("REVOKED", "Revoked", "Password has been revoked");
        
        private final String statusName;
        private final String displayName;
        private final String description;
        
        PasswordStatus(String statusName, String displayName, String description) {
            this.statusName = statusName;
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getStatusName() {
            return statusName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getDescription() {
            return description;
        }
        
        /**
         * Check if the status allows usage
         */
        public boolean allowsUsage() {
            return this == ACTIVE;
        }
        
        /**
         * Check if the status is active
         */
        public boolean isActive() {
            return this == ACTIVE;
        }
        
        /**
         * Check if the status is expired
         */
        public boolean isExpired() {
            return this == EXPIRED;
        }
    }
    
    /**
     * Check if the password is active
     */
    public boolean isActive() {
        return status == PasswordStatus.ACTIVE;
    }
    
    /**
     * Check if the password is expired
     */
    public boolean isExpired() {
        return status == PasswordStatus.EXPIRED || 
               (expiresAt != null && expiresAt.isBefore(LocalDateTime.now()));
    }
    
    /**
     * Check if the password is temporary
     */
    public boolean isTemporary() {
        return isTemporary != null && isTemporary;
    }
    
    /**
     * Check if the password is emergency
     */
    public boolean isEmergency() {
        return isEmergency != null && isEmergency;
    }
    
    /**
     * Check if the password is shared
     */
    public boolean isShared() {
        return isShared != null && isShared;
    }
    
    /**
     * Check if the password has reached usage limit
     */
    public boolean hasReachedUsageLimit() {
        return maxUsageCount != null && usageCount >= maxUsageCount;
    }
    
    /**
     * Check if the password can be used
     */
    public boolean canBeUsed() {
        return isActive() && !isExpired() && !hasReachedUsageLimit();
    }
    
    /**
     * Check if the password is family-specific
     */
    public boolean isFamilySpecific() {
        return passwordType.isFamilySpecific();
    }
    
    /**
     * Check if the password is emergency type
     */
    public boolean isEmergencyType() {
        return passwordType.isEmergency();
    }
    
    /**
     * Check if the password is temporary type
     */
    public boolean isTemporaryType() {
        return passwordType.isTemporary();
    }
    
    /**
     * Get the password type display name
     */
    public String getPasswordTypeDisplayName() {
        return passwordType.getDisplayName();
    }
    
    /**
     * Get the password status display name
     */
    public String getPasswordStatusDisplayName() {
        return status.getDisplayName();
    }
    
    /**
     * Check if the password has been used recently
     */
    public boolean hasRecentUsage() {
        return lastUsedAt != null && 
               lastUsedAt.isAfter(LocalDateTime.now().minusDays(7));
    }
    
    /**
     * Check if the password has usage history
     */
    public boolean hasUsageHistory() {
        return usageCount > 0;
    }
}
