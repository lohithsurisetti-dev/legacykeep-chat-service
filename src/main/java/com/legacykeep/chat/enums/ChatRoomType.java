package com.legacykeep.chat.enums;

/**
 * Chat Room Type Enumeration
 * 
 * Defines the different types of chat rooms supported in the family communication system.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public enum ChatRoomType {
    
    /**
     * Individual chat between two users
     */
    INDIVIDUAL("INDIVIDUAL", "Individual Chat", "Direct message between two family members"),
    
    /**
     * General group chat
     */
    GROUP("GROUP", "Group Chat", "Multi-participant group conversation"),
    
    /**
     * Family-specific group chat
     */
    FAMILY_GROUP("FAMILY_GROUP", "Family Group", "Dedicated family group chat"),
    
    /**
     * Story-specific chat room
     */
    STORY_CHAT("STORY_CHAT", "Story Chat", "Chat room dedicated to a specific family story"),
    
    /**
     * Event-specific chat room
     */
    EVENT_CHAT("EVENT_CHAT", "Event Chat", "Chat room for family events and gatherings");
    
    private final String typeName;
    private final String displayName;
    private final String description;
    
    ChatRoomType(String typeName, String displayName, String description) {
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
     * Check if this is a group chat type
     */
    public boolean isGroupType() {
        return this != INDIVIDUAL;
    }
    
    /**
     * Check if this is a family-related chat type
     */
    public boolean isFamilyRelated() {
        return this == FAMILY_GROUP || this == STORY_CHAT || this == EVENT_CHAT;
    }
}
