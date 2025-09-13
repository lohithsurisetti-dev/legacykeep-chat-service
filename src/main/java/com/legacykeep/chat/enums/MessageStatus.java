package com.legacykeep.chat.enums;

/**
 * Message Status Enumeration
 * 
 * Defines the different status states of messages in the family communication system.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public enum MessageStatus {
    
    /**
     * Message is being sent
     */
    SENDING("SENDING", "Sending", "Message is being sent"),
    
    /**
     * Message has been sent successfully
     */
    SENT("SENT", "Sent", "Message has been sent successfully"),
    
    /**
     * Message has been delivered to recipient
     */
    DELIVERED("DELIVERED", "Delivered", "Message has been delivered to recipient"),
    
    /**
     * Message has been read by recipient
     */
    READ("READ", "Read", "Message has been read by recipient"),
    
    /**
     * Message failed to send
     */
    FAILED("FAILED", "Failed", "Message failed to send"),
    
    /**
     * Message has been edited
     */
    EDITED("EDITED", "Edited", "Message has been edited"),
    
    /**
     * Message has been deleted
     */
    DELETED("DELETED", "Deleted", "Message has been deleted"),
    
    /**
     * Message has been recalled
     */
    RECALLED("RECALLED", "Recalled", "Message has been recalled");
    
    private final String statusName;
    private final String displayName;
    private final String description;
    
    MessageStatus(String statusName, String displayName, String description) {
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
     * Check if the status indicates successful delivery
     */
    public boolean isDelivered() {
        return this == DELIVERED || this == READ;
    }
    
    /**
     * Check if the status indicates the message was read
     */
    public boolean isRead() {
        return this == READ;
    }
    
    /**
     * Check if the status indicates the message was sent
     */
    public boolean isSent() {
        return this == SENT || this == DELIVERED || this == READ;
    }
    
    /**
     * Check if the status indicates the message failed
     */
    public boolean isFailed() {
        return this == FAILED;
    }
    
    /**
     * Check if the status indicates the message was modified
     */
    public boolean isModified() {
        return this == EDITED || this == DELETED || this == RECALLED;
    }
    
    /**
     * Check if the status indicates the message is in transit
     */
    public boolean isInTransit() {
        return this == SENDING;
    }
}
