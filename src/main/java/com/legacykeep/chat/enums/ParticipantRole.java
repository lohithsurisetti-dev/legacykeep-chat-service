package com.legacykeep.chat.enums;

/**
 * Participant Role Enumeration
 * 
 * Defines the different roles that participants can have in chat rooms.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public enum ParticipantRole {
    
    /**
     * Super admin - highest level of control
     */
    SUPER_ADMIN("SUPER_ADMIN", "Super Admin", "Highest level of control over the chat"),
    
    /**
     * Admin - can manage chat settings and members
     */
    ADMIN("ADMIN", "Admin", "Can manage chat settings and members"),
    
    /**
     * Moderator - can moderate messages and members
     */
    MODERATOR("MODERATOR", "Moderator", "Can moderate messages and members"),
    
    /**
     * Regular member - standard participant
     */
    MEMBER("MEMBER", "Member", "Standard chat participant");
    
    private final String roleName;
    private final String displayName;
    private final String description;
    
    ParticipantRole(String roleName, String displayName, String description) {
        this.roleName = roleName;
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getRoleName() {
        return roleName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if this role has admin privileges
     */
    public boolean hasAdminPrivileges() {
        return this == SUPER_ADMIN || this == ADMIN;
    }
    
    /**
     * Check if this role has moderator privileges
     */
    public boolean hasModeratorPrivileges() {
        return this == SUPER_ADMIN || this == ADMIN || this == MODERATOR;
    }
    
    /**
     * Check if this role can manage members
     */
    public boolean canManageMembers() {
        return this == SUPER_ADMIN || this == ADMIN;
    }
    
    /**
     * Check if this role can moderate messages
     */
    public boolean canModerateMessages() {
        return this == SUPER_ADMIN || this == ADMIN || this == MODERATOR;
    }
}
