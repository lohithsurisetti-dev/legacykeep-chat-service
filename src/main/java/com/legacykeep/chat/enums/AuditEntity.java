package com.legacykeep.chat.enums;

/**
 * Audit Entity Enumeration
 * 
 * Defines the different entities that can be audited in the chat system.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public enum AuditEntity {
    /**
     * Chat room entity
     */
    CHAT_ROOM("CHAT_ROOM", "Chat Room", "Chat room related activities"),
    
    /**
     * Message entity
     */
    MESSAGE("MESSAGE", "Message", "Message related activities"),
    
    /**
     * Participant entity
     */
    PARTICIPANT("PARTICIPANT", "Participant", "Participant related activities"),
    
    /**
     * Reaction entity
     */
    REACTION("REACTION", "Reaction", "Message reaction activities"),
    
    /**
     * Password entity
     */
    PASSWORD("PASSWORD", "Password", "Password related activities"),
    
    /**
     * User session entity
     */
    SESSION("SESSION", "Session", "User session activities"),
    
    /**
     * System entity
     */
    SYSTEM("SYSTEM", "System", "System related activities"),
    
    /**
     * Security entity
     */
    SECURITY("SECURITY", "Security", "Security related activities");
    
    private final String entityName;
    private final String displayName;
    private final String description;
    
    AuditEntity(String entityName, String displayName, String description) {
        this.entityName = entityName;
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getEntityName() {
        return entityName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
}
