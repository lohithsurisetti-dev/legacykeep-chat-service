package com.legacykeep.chat.service.impl;

import com.legacykeep.chat.entity.ChatRoom;
import com.legacykeep.chat.repository.postgres.ChatRoomRepository;
import com.legacykeep.chat.service.EncryptionService;
import com.legacykeep.chat.service.KeyManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of KeyManagementService for managing encryption keys.
 * 
 * Features:
 * - In-memory key storage for performance (in production, use Redis or secure key store)
 * - Key access control based on chat room participants
 * - Key rotation for enhanced security
 * - User access management for encryption keys
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KeyManagementServiceImpl implements KeyManagementService {
    
    private final EncryptionService encryptionService;
    private final ChatRoomRepository chatRoomRepository;
    
    // In-memory storage for encryption keys (in production, use Redis or secure key store)
    private final Map<Long, String> chatRoomKeys = new ConcurrentHashMap<>();
    
    // In-memory storage for key access control (in production, use database)
    private final Map<Long, Set<Long>> keyAccessControl = new ConcurrentHashMap<>();
    
    @Override
    @Transactional
    public String generateChatRoomKey(Long chatRoomId, Long createdByUserId) {
        log.info("Generating encryption key for chat room: {} by user: {}", chatRoomId, createdByUserId);
        
        try {
            // Check if chat room exists
            Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(chatRoomId);
            if (chatRoomOpt.isEmpty()) {
                throw new IllegalArgumentException("Chat room not found: " + chatRoomId);
            }
            
            ChatRoom chatRoom = chatRoomOpt.get();
            
            // Generate new encryption key
            String encryptionKey = encryptionService.generateEncryptionKey();
            
            // Store the key
            chatRoomKeys.put(chatRoomId, encryptionKey);
            
            // Initialize key access control
            Set<Long> authorizedUsers = new HashSet<>();
            authorizedUsers.add(createdByUserId);
            authorizedUsers.add(chatRoom.getCreatedByUserId()); // Room creator always has access
            keyAccessControl.put(chatRoomId, authorizedUsers);
            
            log.info("Successfully generated encryption key for chat room: {}", chatRoomId);
            return encryptionKey;
            
        } catch (Exception e) {
            log.error("Failed to generate encryption key for chat room {}: {}", chatRoomId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate encryption key", e);
        }
    }
    
    @Override
    public Optional<String> getChatRoomKey(Long chatRoomId, Long userId) {
        log.debug("Getting encryption key for chat room: {} by user: {}", chatRoomId, userId);
        
        try {
            // Check if user has access
            if (!hasKeyAccess(chatRoomId, userId)) {
                log.warn("User {} does not have access to encryption key for chat room {}", userId, chatRoomId);
                return Optional.empty();
            }
            
            // Get the key
            String key = chatRoomKeys.get(chatRoomId);
            if (key == null) {
                log.warn("No encryption key found for chat room: {}", chatRoomId);
                return Optional.empty();
            }
            
            return Optional.of(key);
            
        } catch (Exception e) {
            log.error("Failed to get encryption key for chat room {}: {}", chatRoomId, e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    @Override
    @Transactional
    public String rotateChatRoomKey(Long chatRoomId, Long rotatedByUserId) {
        log.info("Rotating encryption key for chat room: {} by user: {}", chatRoomId, rotatedByUserId);
        
        try {
            // Check if user has permission to rotate key (must have current key access)
            if (!hasKeyAccess(chatRoomId, rotatedByUserId)) {
                throw new IllegalArgumentException("User does not have permission to rotate key");
            }
            
            // Generate new key
            String newKey = encryptionService.generateEncryptionKey();
            
            // Update the key
            chatRoomKeys.put(chatRoomId, newKey);
            
            log.info("Successfully rotated encryption key for chat room: {}", chatRoomId);
            return newKey;
            
        } catch (Exception e) {
            log.error("Failed to rotate encryption key for chat room {}: {}", chatRoomId, e.getMessage(), e);
            throw new RuntimeException("Failed to rotate encryption key", e);
        }
    }
    
    @Override
    @Transactional
    public boolean addUserToKeyAccess(Long chatRoomId, Long userId, Long addedByUserId) {
        log.info("Adding user {} to key access for chat room: {} by user: {}", userId, chatRoomId, addedByUserId);
        
        try {
            // Check if the user adding has permission (must have current key access)
            if (!hasKeyAccess(chatRoomId, addedByUserId)) {
                log.warn("User {} does not have permission to add users to key access", addedByUserId);
                return false;
            }
            
            // Add user to key access
            keyAccessControl.computeIfAbsent(chatRoomId, k -> new HashSet<>()).add(userId);
            
            log.info("Successfully added user {} to key access for chat room: {}", userId, chatRoomId);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to add user {} to key access for chat room {}: {}", userId, chatRoomId, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean removeUserFromKeyAccess(Long chatRoomId, Long userId, Long removedByUserId) {
        log.info("Removing user {} from key access for chat room: {} by user: {}", userId, chatRoomId, removedByUserId);
        
        try {
            // Check if the user removing has permission (must have current key access)
            if (!hasKeyAccess(chatRoomId, removedByUserId)) {
                log.warn("User {} does not have permission to remove users from key access", removedByUserId);
                return false;
            }
            
            // Remove user from key access
            Set<Long> authorizedUsers = keyAccessControl.get(chatRoomId);
            if (authorizedUsers != null) {
                authorizedUsers.remove(userId);
            }
            
            log.info("Successfully removed user {} from key access for chat room: {}", userId, chatRoomId);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to remove user {} from key access for chat room {}: {}", userId, chatRoomId, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean hasKeyAccess(Long chatRoomId, Long userId) {
        Set<Long> authorizedUsers = keyAccessControl.get(chatRoomId);
        if (authorizedUsers == null) {
            return false;
        }
        
        // Check if user is in the authorized list
        boolean hasAccess = authorizedUsers.contains(userId);
        log.debug("User {} {} access to encryption key for chat room {}", userId, hasAccess ? "has" : "does not have", chatRoomId);
        return hasAccess;
    }
    
    @Override
    public List<Long> getUsersWithKeyAccess(Long chatRoomId) {
        Set<Long> authorizedUsers = keyAccessControl.get(chatRoomId);
        if (authorizedUsers == null) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(authorizedUsers);
    }
    
    @Override
    @Transactional
    public boolean revokeAllKeyAccess(Long chatRoomId, Long revokedByUserId) {
        log.warn("Revoking all key access for chat room: {} by user: {}", chatRoomId, revokedByUserId);
        
        try {
            // Check if the user has permission (must have current key access)
            if (!hasKeyAccess(chatRoomId, revokedByUserId)) {
                log.warn("User {} does not have permission to revoke all key access", revokedByUserId);
                return false;
            }
            
            // Clear all key access
            keyAccessControl.remove(chatRoomId);
            
            // Optionally, remove the key as well
            chatRoomKeys.remove(chatRoomId);
            
            log.warn("Successfully revoked all key access for chat room: {}", chatRoomId);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to revoke all key access for chat room {}: {}", chatRoomId, e.getMessage(), e);
            return false;
        }
    }
}
