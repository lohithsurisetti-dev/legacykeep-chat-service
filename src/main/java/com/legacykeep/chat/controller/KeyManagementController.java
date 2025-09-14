package com.legacykeep.chat.controller;

import com.legacykeep.chat.dto.ApiResponse;
import com.legacykeep.chat.service.KeyManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for encryption key management operations.
 * 
 * Provides endpoints for:
 * - Generating encryption keys for chat rooms
 * - Managing key access for users
 * - Rotating encryption keys
 * - Key access control
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/keys")
@RequiredArgsConstructor
public class KeyManagementController {
    
    private final KeyManagementService keyManagementService;
    
    /**
     * Generate encryption key for a chat room
     */
    @PostMapping("/chat-room/{chatRoomId}/generate")
    public ResponseEntity<ApiResponse<String>> generateChatRoomKey(
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam("userId") Long userId) {
        log.info("Generating encryption key for chat room: {} by user: {}", chatRoomId, userId);
        
        try {
            String encryptionKey = keyManagementService.generateChatRoomKey(chatRoomId, userId);
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Encryption key generated successfully")
                    .data(encryptionKey)
                    .build());
        } catch (Exception e) {
            log.error("Failed to generate encryption key for chat room {}: {}", chatRoomId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<String>builder()
                            .success(false)
                            .message("Failed to generate encryption key: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Get encryption key for a chat room
     */
    @GetMapping("/chat-room/{chatRoomId}")
    public ResponseEntity<ApiResponse<String>> getChatRoomKey(
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam("userId") Long userId) {
        log.debug("Getting encryption key for chat room: {} by user: {}", chatRoomId, userId);
        
        try {
            Optional<String> encryptionKeyOpt = keyManagementService.getChatRoomKey(chatRoomId, userId);
            if (encryptionKeyOpt.isPresent()) {
                return ResponseEntity.ok(ApiResponse.<String>builder()
                        .success(true)
                        .message("Encryption key retrieved successfully")
                        .data(encryptionKeyOpt.get())
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.<String>builder()
                                .success(false)
                                .message("User does not have access to encryption key")
                                .build());
            }
        } catch (Exception e) {
            log.error("Failed to get encryption key for chat room {}: {}", chatRoomId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<String>builder()
                            .success(false)
                            .message("Failed to get encryption key: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Rotate encryption key for a chat room
     */
    @PostMapping("/chat-room/{chatRoomId}/rotate")
    public ResponseEntity<ApiResponse<String>> rotateChatRoomKey(
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam("userId") Long userId) {
        log.info("Rotating encryption key for chat room: {} by user: {}", chatRoomId, userId);
        
        try {
            String newKey = keyManagementService.rotateChatRoomKey(chatRoomId, userId);
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Encryption key rotated successfully")
                    .data(newKey)
                    .build());
        } catch (Exception e) {
            log.error("Failed to rotate encryption key for chat room {}: {}", chatRoomId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<String>builder()
                            .success(false)
                            .message("Failed to rotate encryption key: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Add user to key access for a chat room
     */
    @PostMapping("/chat-room/{chatRoomId}/access/add")
    public ResponseEntity<ApiResponse<Boolean>> addUserToKeyAccess(
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam("userId") Long userId,
            @RequestParam("addedByUserId") Long addedByUserId) {
        log.info("Adding user {} to key access for chat room: {} by user: {}", userId, chatRoomId, addedByUserId);
        
        try {
            boolean success = keyManagementService.addUserToKeyAccess(chatRoomId, userId, addedByUserId);
            if (success) {
                return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                        .success(true)
                        .message("User added to key access successfully")
                        .data(true)
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.<Boolean>builder()
                                .success(false)
                                .message("Failed to add user to key access")
                                .data(false)
                                .build());
            }
        } catch (Exception e) {
            log.error("Failed to add user to key access for chat room {}: {}", chatRoomId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Boolean>builder()
                            .success(false)
                            .message("Failed to add user to key access: " + e.getMessage())
                            .data(false)
                            .build());
        }
    }
    
    /**
     * Remove user from key access for a chat room
     */
    @DeleteMapping("/chat-room/{chatRoomId}/access/remove")
    public ResponseEntity<ApiResponse<Boolean>> removeUserFromKeyAccess(
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam("userId") Long userId,
            @RequestParam("removedByUserId") Long removedByUserId) {
        log.info("Removing user {} from key access for chat room: {} by user: {}", userId, chatRoomId, removedByUserId);
        
        try {
            boolean success = keyManagementService.removeUserFromKeyAccess(chatRoomId, userId, removedByUserId);
            if (success) {
                return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                        .success(true)
                        .message("User removed from key access successfully")
                        .data(true)
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.<Boolean>builder()
                                .success(false)
                                .message("Failed to remove user from key access")
                                .data(false)
                                .build());
            }
        } catch (Exception e) {
            log.error("Failed to remove user from key access for chat room {}: {}", chatRoomId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Boolean>builder()
                            .success(false)
                            .message("Failed to remove user from key access: " + e.getMessage())
                            .data(false)
                            .build());
        }
    }
    
    /**
     * Check if user has key access for a chat room
     */
    @GetMapping("/chat-room/{chatRoomId}/access/check")
    public ResponseEntity<ApiResponse<Boolean>> hasKeyAccess(
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam("userId") Long userId) {
        log.debug("Checking key access for user {} in chat room: {}", userId, chatRoomId);
        
        try {
            boolean hasAccess = keyManagementService.hasKeyAccess(chatRoomId, userId);
            return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                    .success(true)
                    .message("Key access check completed")
                    .data(hasAccess)
                    .build());
        } catch (Exception e) {
            log.error("Failed to check key access for chat room {}: {}", chatRoomId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Boolean>builder()
                            .success(false)
                            .message("Failed to check key access: " + e.getMessage())
                            .data(false)
                            .build());
        }
    }
    
    /**
     * Get all users with key access for a chat room
     */
    @GetMapping("/chat-room/{chatRoomId}/access/users")
    public ResponseEntity<ApiResponse<List<Long>>> getUsersWithKeyAccess(
            @PathVariable("chatRoomId") Long chatRoomId) {
        log.debug("Getting users with key access for chat room: {}", chatRoomId);
        
        try {
            List<Long> users = keyManagementService.getUsersWithKeyAccess(chatRoomId);
            return ResponseEntity.ok(ApiResponse.<List<Long>>builder()
                    .success(true)
                    .message("Users with key access retrieved successfully")
                    .data(users)
                    .build());
        } catch (Exception e) {
            log.error("Failed to get users with key access for chat room {}: {}", chatRoomId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<Long>>builder()
                            .success(false)
                            .message("Failed to get users with key access: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Revoke all key access for a chat room (emergency security measure)
     */
    @PostMapping("/chat-room/{chatRoomId}/access/revoke-all")
    public ResponseEntity<ApiResponse<Boolean>> revokeAllKeyAccess(
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam("userId") Long userId) {
        log.warn("Revoking all key access for chat room: {} by user: {}", chatRoomId, userId);
        
        try {
            boolean success = keyManagementService.revokeAllKeyAccess(chatRoomId, userId);
            if (success) {
                return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                        .success(true)
                        .message("All key access revoked successfully")
                        .data(true)
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.<Boolean>builder()
                                .success(false)
                                .message("Failed to revoke all key access")
                                .data(false)
                                .build());
            }
        } catch (Exception e) {
            log.error("Failed to revoke all key access for chat room {}: {}", chatRoomId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Boolean>builder()
                            .success(false)
                            .message("Failed to revoke all key access: " + e.getMessage())
                            .data(false)
                            .build());
        }
    }
}
f