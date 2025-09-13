package com.legacykeep.chat.enums;

/**
 * Chat Room Status Enumeration
 * 
 * Defines the different status states of chat rooms in the family communication system.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public enum ChatRoomStatus {
    
    /**
     * Active chat room - fully functional
     */
    ACTIVE("ACTIVE", "Active", "Chat room is active and functional"),
    
    /**
     * Inactive chat room - temporarily disabled
     */
    INACTIVE("INACTIVE", "Inactive", "Chat room is temporarily inactive"),
    
    /**
     * Suspended chat room - temporarily suspended
     */
    SUSPENDED("SUSPENDED", "Suspended", "Chat room is temporarily suspended"),
    
    /**
     * Archived chat room - archived but accessible
     */
    ARCHIVED("ARCHIVED", "Archived", "Chat room is archived but still accessible"),
    
    /**
     * Deleted chat room - soft deleted
     */
    DELETED("DELETED", "Deleted", "Chat room is deleted");
    
    private final String statusName;
    private final String displayName;
    private final String description;
    
    ChatRoomStatus(String statusName, String displayName, String description) {
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
     * Check if the status allows messaging
     */
    public boolean allowsMessaging() {
        return this == ACTIVE;
    }
    
    /**
     * Check if the status is active
     */
    public boolean isActive() {
        return this == ACTIVE;
    }
    
    /**
     * Check if the status is inactive
     */
    public boolean isInactive() {
        return this == INACTIVE || this == SUSPENDED;
    }
    
    /**
     * Check if the status is archived
     */
    public boolean isArchived() {
        return this == ARCHIVED;
    }
    
    /**
     * Check if the status is deleted
     */
    public boolean isDeleted() {
        return this == DELETED;
    }
}
