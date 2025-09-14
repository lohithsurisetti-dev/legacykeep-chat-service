package com.legacykeep.chat.service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing encryption keys for chat rooms and messages.
 * 
 * This service handles:
 * - Generation of encryption keys for chat rooms
 * - Key distribution to authorized participants
 * - Key rotation for enhanced security
 * - Key validation and access control
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public interface KeyManagementService {
    
    /**
     * Generate a new encryption key for a chat room
     * 
     * @param chatRoomId The ID of the chat room
     * @param createdByUserId The user ID who created the key
     * @return The generated encryption key (Base64 encoded)
     */
    String generateChatRoomKey(Long chatRoomId, Long createdByUserId);
    
    /**
     * Get the encryption key for a chat room
     * 
     * @param chatRoomId The ID of the chat room
     * @param userId The user ID requesting the key
     * @return Optional containing the encryption key if user has access
     */
    Optional<String> getChatRoomKey(Long chatRoomId, Long userId);
    
    /**
     * Rotate the encryption key for a chat room
     * 
     * @param chatRoomId The ID of the chat room
     * @param rotatedByUserId The user ID who initiated the rotation
     * @return The new encryption key (Base64 encoded)
     */
    String rotateChatRoomKey(Long chatRoomId, Long rotatedByUserId);
    
    /**
     * Add a user to the key access list for a chat room
     * 
     * @param chatRoomId The ID of the chat room
     * @param userId The user ID to add
     * @param addedByUserId The user ID who added the user
     * @return true if successful, false otherwise
     */
    boolean addUserToKeyAccess(Long chatRoomId, Long userId, Long addedByUserId);
    
    /**
     * Remove a user from the key access list for a chat room
     * 
     * @param chatRoomId The ID of the chat room
     * @param userId The user ID to remove
     * @param removedByUserId The user ID who removed the user
     * @return true if successful, false otherwise
     */
    boolean removeUserFromKeyAccess(Long chatRoomId, Long userId, Long removedByUserId);
    
    /**
     * Check if a user has access to the encryption key for a chat room
     * 
     * @param chatRoomId The ID of the chat room
     * @param userId The user ID to check
     * @return true if user has access, false otherwise
     */
    boolean hasKeyAccess(Long chatRoomId, Long userId);
    
    /**
     * Get all users who have access to the encryption key for a chat room
     * 
     * @param chatRoomId The ID of the chat room
     * @return List of user IDs with key access
     */
    List<Long> getUsersWithKeyAccess(Long chatRoomId);
    
    /**
     * Revoke all key access for a chat room (emergency security measure)
     * 
     * @param chatRoomId The ID of the chat room
     * @param revokedByUserId The user ID who initiated the revocation
     * @return true if successful, false otherwise
     */
    boolean revokeAllKeyAccess(Long chatRoomId, Long revokedByUserId);
}
