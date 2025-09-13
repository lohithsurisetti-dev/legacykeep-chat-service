package com.legacykeep.chat.enums;

/**
 * Participant Status Enumeration
 * 
 * Defines the different status states of participants in chat rooms.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public enum ParticipantStatus {
    
    /**
     * Active participant - fully engaged in the chat
     */
    ACTIVE("ACTIVE", "Active", "Participant is active in the chat"),
    
    /**
     * Inactive participant - not actively participating
     */
    INACTIVE("INACTIVE", "Inactive", "Participant is not actively participating"),
    
    /**
     * Left participant - has left the chat
     */
    LEFT("LEFT", "Left", "Participant has left the chat"),
    
    /**
     * Blocked participant - blocked from the chat
     */
    BLOCKED("BLOCKED", "Blocked", "Participant is blocked from the chat"),
    
    /**
     * Pending participant - waiting for approval
     */
    PENDING("PENDING", "Pending", "Participant is waiting for approval"),
    
    /**
     * Suspended participant - temporarily suspended
     */
    SUSPENDED("SUSPENDED", "Suspended", "Participant is temporarily suspended");
    
    private final String statusName;
    private final String displayName;
    private final String description;
    
    ParticipantStatus(String statusName, String displayName, String description) {
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
     * Check if the status allows participation
     */
    public boolean allowsParticipation() {
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
     * Check if the status indicates the participant has left
     */
    public boolean hasLeft() {
        return this == LEFT;
    }
    
    /**
     * Check if the status indicates the participant is blocked
     */
    public boolean isBlocked() {
        return this == BLOCKED;
    }
    
    /**
     * Check if the status indicates the participant is pending
     */
    public boolean isPending() {
        return this == PENDING;
    }
    
    /**
     * Check if the status indicates the participant is suspended
     */
    public boolean isSuspended() {
        return this == SUSPENDED;
    }
}
