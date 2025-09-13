package com.legacykeep.chat.enums;

/**
 * Audit Action Enumeration
 * 
 * Defines the different actions that can be audited in the chat system.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public enum AuditAction {
    // CRUD Operations
    CREATE("CREATE", "Create", "Entity was created"),
    READ("READ", "Read", "Entity was read/accessed"),
    UPDATE("UPDATE", "Update", "Entity was updated"),
    DELETE("DELETE", "Delete", "Entity was deleted"),
    
    // Chat Specific Actions
    SEND_MESSAGE("SEND_MESSAGE", "Send Message", "Message was sent"),
    RECEIVE_MESSAGE("RECEIVE_MESSAGE", "Receive Message", "Message was received"),
    READ_MESSAGE("READ_MESSAGE", "Read Message", "Message was read"),
    EDIT_MESSAGE("EDIT_MESSAGE", "Edit Message", "Message was edited"),
    DELETE_MESSAGE("DELETE_MESSAGE", "Delete Message", "Message was deleted"),
    FORWARD_MESSAGE("FORWARD_MESSAGE", "Forward Message", "Message was forwarded"),
    STAR_MESSAGE("STAR_MESSAGE", "Star Message", "Message was starred"),
    UNSTAR_MESSAGE("UNSTAR_MESSAGE", "Unstar Message", "Message was unstarred"),
    
    // Reaction Actions
    ADD_REACTION("ADD_REACTION", "Add Reaction", "Reaction was added"),
    REMOVE_REACTION("REMOVE_REACTION", "Remove Reaction", "Reaction was removed"),
    
    // Room Actions
    JOIN_ROOM("JOIN_ROOM", "Join Room", "User joined chat room"),
    LEAVE_ROOM("LEAVE_ROOM", "Leave Room", "User left chat room"),
    CREATE_ROOM("CREATE_ROOM", "Create Room", "Chat room was created"),
    ARCHIVE_ROOM("ARCHIVE_ROOM", "Archive Room", "Chat room was archived"),
    UNARCHIVE_ROOM("UNARCHIVE_ROOM", "Unarchive Room", "Chat room was unarchived"),
    MUTE_ROOM("MUTE_ROOM", "Mute Room", "Chat room was muted"),
    UNMUTE_ROOM("UNMUTE_ROOM", "Unmute Room", "Chat room was unmuted"),
    
    // Participant Actions
    ADD_PARTICIPANT("ADD_PARTICIPANT", "Add Participant", "Participant was added"),
    REMOVE_PARTICIPANT("REMOVE_PARTICIPANT", "Remove Participant", "Participant was removed"),
    PROMOTE_PARTICIPANT("PROMOTE_PARTICIPANT", "Promote Participant", "Participant was promoted"),
    DEMOTE_PARTICIPANT("DEMOTE_PARTICIPANT", "Demote Participant", "Participant was demoted"),
    BLOCK_PARTICIPANT("BLOCK_PARTICIPANT", "Block Participant", "Participant was blocked"),
    UNBLOCK_PARTICIPANT("UNBLOCK_PARTICIPANT", "Unblock Participant", "Participant was unblocked"),
    
    // Password Actions
    CREATE_PASSWORD("CREATE_PASSWORD", "Create Password", "Password was created"),
    USE_PASSWORD("USE_PASSWORD", "Use Password", "Password was used"),
    REVOKE_PASSWORD("REVOKE_PASSWORD", "Revoke Password", "Password was revoked"),
    RESET_PASSWORD("RESET_PASSWORD", "Reset Password", "Password was reset"),
    
    // Security Actions
    LOGIN("LOGIN", "Login", "User logged in"),
    LOGOUT("LOGOUT", "Logout", "User logged out"),
    AUTHENTICATE("AUTHENTICATE", "Authenticate", "User was authenticated"),
    AUTHORIZE("AUTHORIZE", "Authorize", "User was authorized"),
    ACCESS_DENIED("ACCESS_DENIED", "Access Denied", "Access was denied"),
    SUSPICIOUS_ACTIVITY("SUSPICIOUS_ACTIVITY", "Suspicious Activity", "Suspicious activity detected"),
    
    // System Actions
    SYSTEM_START("SYSTEM_START", "System Start", "System started"),
    SYSTEM_STOP("SYSTEM_STOP", "System Stop", "System stopped"),
    SYSTEM_ERROR("SYSTEM_ERROR", "System Error", "System error occurred"),
    SYSTEM_WARNING("SYSTEM_WARNING", "System Warning", "System warning occurred");
    
    private final String actionName;
    private final String displayName;
    private final String description;
    
    AuditAction(String actionName, String displayName, String description) {
        this.actionName = actionName;
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getActionName() {
        return actionName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if this is a CRUD action
     */
    public boolean isCrudAction() {
        return this == CREATE || this == READ || this == UPDATE || this == DELETE;
    }
    
    /**
     * Check if this is a message action
     */
    public boolean isMessageAction() {
        return this == SEND_MESSAGE || this == RECEIVE_MESSAGE || this == READ_MESSAGE ||
               this == EDIT_MESSAGE || this == DELETE_MESSAGE || this == FORWARD_MESSAGE ||
               this == STAR_MESSAGE || this == UNSTAR_MESSAGE;
    }
    
    /**
     * Check if this is a security action
     */
    public boolean isSecurityAction() {
        return this == LOGIN || this == LOGOUT || this == AUTHENTICATE || this == AUTHORIZE ||
               this == ACCESS_DENIED || this == SUSPICIOUS_ACTIVITY;
    }
    
    /**
     * Check if this is a system action
     */
    public boolean isSystemAction() {
        return this == SYSTEM_START || this == SYSTEM_STOP || this == SYSTEM_ERROR || this == SYSTEM_WARNING;
    }
}
