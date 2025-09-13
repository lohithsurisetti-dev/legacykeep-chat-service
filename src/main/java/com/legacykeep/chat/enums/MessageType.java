package com.legacykeep.chat.enums;

/**
 * Message Type Enumeration
 * 
 * Defines the different types of messages supported in the family communication system.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public enum MessageType {
    
    /**
     * Text message
     */
    TEXT("TEXT", "Text", "Plain text message"),
    
    /**
     * Image message
     */
    IMAGE("IMAGE", "Image", "Image file message"),
    
    /**
     * Video message
     */
    VIDEO("VIDEO", "Video", "Video file message"),
    
    /**
     * Audio/Voice message
     */
    AUDIO("AUDIO", "Audio", "Audio/voice message"),
    
    /**
     * Document message
     */
    DOCUMENT("DOCUMENT", "Document", "Document file message"),
    
    /**
     * Location message
     */
    LOCATION("LOCATION", "Location", "Location sharing message"),
    
    /**
     * Contact message
     */
    CONTACT("CONTACT", "Contact", "Contact sharing message"),
    
    /**
     * Story message
     */
    STORY("STORY", "Story", "Family story message"),
    
    /**
     * Memory message
     */
    MEMORY("MEMORY", "Memory", "Family memory message"),
    
    /**
     * Event message
     */
    EVENT("EVENT", "Event", "Family event message"),
    
    /**
     * System message
     */
    SYSTEM("SYSTEM", "System", "System-generated message"),
    
    /**
     * Notification message
     */
    NOTIFICATION("NOTIFICATION", "Notification", "Notification message"),
    
    /**
     * Poll message
     */
    POLL("POLL", "Poll", "Poll/voting message"),
    
    /**
     * Sticker message
     */
    STICKER("STICKER", "Sticker", "Sticker message"),
    
    /**
     * GIF message
     */
    GIF("GIF", "GIF", "Animated GIF message");
    
    private final String typeName;
    private final String displayName;
    private final String description;
    
    MessageType(String typeName, String displayName, String description) {
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
     * Check if this is a media message type
     */
    public boolean isMediaType() {
        return this == IMAGE || this == VIDEO || this == AUDIO || this == DOCUMENT || this == GIF || this == STICKER;
    }
    
    /**
     * Check if this is a family-specific message type
     */
    public boolean isFamilySpecific() {
        return this == STORY || this == MEMORY || this == EVENT;
    }
    
    /**
     * Check if this is a system message type
     */
    public boolean isSystemType() {
        return this == SYSTEM || this == NOTIFICATION;
    }
    
    /**
     * Check if this is a text-based message type
     */
    public boolean isTextBased() {
        return this == TEXT || this == STORY || this == MEMORY || this == EVENT;
    }
}
