package com.legacykeep.chat.controller;

import com.legacykeep.chat.dto.ApiResponse;
import com.legacykeep.chat.dto.request.TypingIndicatorRequest;
import com.legacykeep.chat.dto.request.ConnectionStatusRequest;
import com.legacykeep.chat.dto.request.SubscriptionRequest;
import com.legacykeep.chat.dto.request.SendMessageRequest;
import com.legacykeep.chat.dto.response.WebSocketStats;
import com.legacykeep.chat.service.WebSocketService;
import com.legacykeep.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * WebSocket Controller for real-time messaging.
 * Provides WebSocket endpoints for family-centric real-time communication.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Slf4j
@Controller
@RequestMapping("/api/v1/websocket")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WebSocketController {

    private final WebSocketService webSocketService;
    private final MessageService messageService;

    /**
     * Handle incoming chat messages via WebSocket
     */
    @MessageMapping("/messages")
    public void handleChatMessage(@Payload SendMessageRequest request, SimpMessageHeaderAccessor headerAccessor) {
        log.debug("Received chat message from user: {} in chat room: {} - content: {}", 
                request.getSenderUserId(), request.getChatRoomId(), request.getContent());
        
        try {
            // Send the message through the message service (which will handle WebSocket broadcasting)
            messageService.sendMessage(request);
            log.info("Message processed and broadcasted via WebSocket from user: {} to room: {}", 
                    request.getSenderUserId(), request.getChatRoomId());
        } catch (Exception e) {
            log.error("Error handling chat message: {}", e.getMessage(), e);
        }
    }

    /**
     * Handle typing indicator via WebSocket
     */
    @MessageMapping("/typing")
    public void handleTypingIndicator(@Payload TypingIndicatorRequest request, SimpMessageHeaderAccessor headerAccessor) {
        log.debug("Received typing indicator from user: {} in chat room: {} - typing: {}", 
                request.getUserId(), request.getChatRoomId(), request.getIsTyping());
        
        try {
            webSocketService.sendTypingIndicator(request.getChatRoomId(), request.getUserId(), request.getIsTyping());
        } catch (Exception e) {
            log.error("Error handling typing indicator: {}", e.getMessage(), e);
        }
    }

    /**
     * Handle connection status via WebSocket
     */
    @MessageMapping("/connection")
    public void handleConnectionStatus(@Payload ConnectionStatusRequest request, SimpMessageHeaderAccessor headerAccessor) {
        log.debug("Received connection status from user: {} - status: {}", request.getUserId(), request.getStatus());
        
        try {
            webSocketService.sendConnectionStatus(request.getUserId(), request.getStatus(), request.getMessage());
        } catch (Exception e) {
            log.error("Error handling connection status: {}", e.getMessage(), e);
        }
    }

    /**
     * Handle subscription request via WebSocket
     */
    @MessageMapping("/subscribe")
    public void handleSubscription(@Payload SubscriptionRequest request, SimpMessageHeaderAccessor headerAccessor) {
        log.debug("Received subscription request from user: {} - type: {} - id: {}", 
                request.getUserId(), request.getSubscriptionType(), request.getSubscriptionId());
        
        try {
            switch (request.getSubscriptionType().toLowerCase()) {
                case "chatroom":
                    webSocketService.subscribeUserToRoom(request.getUserId(), request.getSubscriptionId());
                    break;
                case "family":
                    webSocketService.subscribeUserToFamily(request.getUserId(), request.getSubscriptionId());
                    break;
                case "story":
                    webSocketService.subscribeUserToStory(request.getUserId(), request.getSubscriptionId());
                    break;
                case "event":
                    webSocketService.subscribeUserToEvent(request.getUserId(), request.getSubscriptionId());
                    break;
                default:
                    log.warn("Unknown subscription type: {}", request.getSubscriptionType());
            }
        } catch (Exception e) {
            log.error("Error handling subscription: {}", e.getMessage(), e);
        }
    }

    /**
     * Handle unsubscription request via WebSocket
     */
    @MessageMapping("/unsubscribe")
    public void handleUnsubscription(@Payload SubscriptionRequest request, SimpMessageHeaderAccessor headerAccessor) {
        log.debug("Received unsubscription request from user: {} - type: {} - id: {}", 
                request.getUserId(), request.getSubscriptionType(), request.getSubscriptionId());
        
        try {
            switch (request.getSubscriptionType().toLowerCase()) {
                case "chatroom":
                    webSocketService.unsubscribeUserFromRoom(request.getUserId(), request.getSubscriptionId());
                    break;
                case "family":
                    webSocketService.unsubscribeUserFromFamily(request.getUserId(), request.getSubscriptionId());
                    break;
                case "story":
                    webSocketService.unsubscribeUserFromStory(request.getUserId(), request.getSubscriptionId());
                    break;
                case "event":
                    webSocketService.unsubscribeUserFromEvent(request.getUserId(), request.getSubscriptionId());
                    break;
                default:
                    log.warn("Unknown subscription type: {}", request.getSubscriptionType());
            }
        } catch (Exception e) {
            log.error("Error handling unsubscription: {}", e.getMessage(), e);
        }
    }

    /**
     * Get WebSocket statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<WebSocketStats>> getWebSocketStats() {
        log.debug("Getting WebSocket statistics");
        
        try {
            com.legacykeep.chat.service.WebSocketService.WebSocketStats serviceStats = webSocketService.getWebSocketStats();
            WebSocketStats stats = WebSocketStats.fromServiceStats(serviceStats);
            return ResponseEntity.ok(ApiResponse.<WebSocketStats>builder()
                    .success(true)
                    .message("WebSocket statistics retrieved successfully")
                    .data(stats)
                    .build());
        } catch (Exception e) {
            log.error("Error getting WebSocket statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<WebSocketStats>builder()
                            .success(false)
                            .message("Failed to retrieve WebSocket statistics: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get connected users count
     */
    @GetMapping("/connected-users/count")
    public ResponseEntity<ApiResponse<Integer>> getConnectedUsersCount() {
        log.debug("Getting connected users count");
        
        try {
            int count = webSocketService.getConnectedUsersCount();
            return ResponseEntity.ok(ApiResponse.<Integer>builder()
                    .success(true)
                    .message("Connected users count retrieved successfully")
                    .data(count)
                    .build());
        } catch (Exception e) {
            log.error("Error getting connected users count: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Integer>builder()
                            .success(false)
                            .message("Failed to retrieve connected users count: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get connected users in a chat room
     */
    @GetMapping("/connected-users/room/{chatRoomId}")
    public ResponseEntity<ApiResponse<List<Long>>> getConnectedUsersInRoom(@PathVariable("chatRoomId") Long chatRoomId) {
        log.debug("Getting connected users in chat room: {}", chatRoomId);
        
        try {
            List<Long> users = webSocketService.getConnectedUsersInRoom(chatRoomId);
            return ResponseEntity.ok(ApiResponse.<List<Long>>builder()
                    .success(true)
                    .message("Connected users in room retrieved successfully")
                    .data(users)
                    .build());
        } catch (Exception e) {
            log.error("Error getting connected users in room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<Long>>builder()
                            .success(false)
                            .message("Failed to retrieve connected users in room: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get connected users in a family
     */
    @GetMapping("/connected-users/family/{familyId}")
    public ResponseEntity<ApiResponse<List<Long>>> getConnectedUsersInFamily(@PathVariable("familyId") Long familyId) {
        log.debug("Getting connected users in family: {}", familyId);
        
        try {
            List<Long> users = webSocketService.getConnectedUsersInFamily(familyId);
            return ResponseEntity.ok(ApiResponse.<List<Long>>builder()
                    .success(true)
                    .message("Connected users in family retrieved successfully")
                    .data(users)
                    .build());
        } catch (Exception e) {
            log.error("Error getting connected users in family: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<Long>>builder()
                            .success(false)
                            .message("Failed to retrieve connected users in family: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Check if user is connected
     */
    @GetMapping("/connected-users/{userId}/status")
    public ResponseEntity<ApiResponse<Boolean>> isUserConnected(@PathVariable("userId") Long userId) {
        log.debug("Checking if user is connected: {}", userId);
        
        try {
            boolean isConnected = webSocketService.isUserConnected(userId);
            return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                    .success(true)
                    .message("User connection status retrieved successfully")
                    .data(isConnected)
                    .build());
        } catch (Exception e) {
            log.error("Error checking user connection status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Boolean>builder()
                            .success(false)
                            .message("Failed to check user connection status: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Check if user is connected to a specific room
     */
    @GetMapping("/connected-users/{userId}/room/{chatRoomId}/status")
    public ResponseEntity<ApiResponse<Boolean>> isUserConnectedToRoom(
            @PathVariable("userId") Long userId,
            @PathVariable("chatRoomId") Long chatRoomId) {
        log.debug("Checking if user: {} is connected to room: {}", userId, chatRoomId);
        
        try {
            boolean isConnected = webSocketService.isUserConnectedToRoom(userId, chatRoomId);
            return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                    .success(true)
                    .message("User room connection status retrieved successfully")
                    .data(isConnected)
                    .build());
        } catch (Exception e) {
            log.error("Error checking user room connection status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Boolean>builder()
                            .success(false)
                            .message("Failed to check user room connection status: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get user connection information
     */
    @GetMapping("/connected-users/{userId}/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserConnectionInfo(@PathVariable("userId") Long userId) {
        log.debug("Getting user connection info: {}", userId);
        
        try {
            Map<String, Object> info = webSocketService.getUserConnectionInfo(userId);
            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("User connection info retrieved successfully")
                    .data(info)
                    .build());
        } catch (Exception e) {
            log.error("Error getting user connection info: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Map<String, Object>>builder()
                            .success(false)
                            .message("Failed to retrieve user connection info: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get user subscription information
     */
    @GetMapping("/subscriptions/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserSubscriptionInfo(@PathVariable("userId") Long userId) {
        log.debug("Getting user subscription info: {}", userId);
        
        try {
            Map<String, Object> info = webSocketService.getUserSubscriptionInfo(userId);
            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("User subscription info retrieved successfully")
                    .data(info)
                    .build());
        } catch (Exception e) {
            log.error("Error getting user subscription info: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Map<String, Object>>builder()
                            .success(false)
                            .message("Failed to retrieve user subscription info: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Disconnect a user
     */
    @PostMapping("/disconnect/{userId}")
    public ResponseEntity<ApiResponse<Void>> disconnectUser(@PathVariable("userId") Long userId) {
        log.info("Disconnecting user: {}", userId);
        
        try {
            webSocketService.disconnectUser(userId);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("User disconnected successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error disconnecting user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to disconnect user: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Disconnect user from a specific room
     */
    @PostMapping("/disconnect/{userId}/room/{chatRoomId}")
    public ResponseEntity<ApiResponse<Void>> disconnectUserFromRoom(
            @PathVariable("userId") Long userId,
            @PathVariable("chatRoomId") Long chatRoomId) {
        log.info("Disconnecting user: {} from room: {}", userId, chatRoomId);
        
        try {
            webSocketService.disconnectUserFromRoom(userId, chatRoomId);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("User disconnected from room successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error disconnecting user from room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to disconnect user from room: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Subscribe user to a chat room
     */
    @PostMapping("/subscribe/room")
    public ResponseEntity<ApiResponse<Void>> subscribeUserToRoom(@Valid @RequestBody SubscriptionRequest request) {
        log.info("Subscribing user: {} to chat room: {}", request.getUserId(), request.getSubscriptionId());
        
        try {
            webSocketService.subscribeUserToRoom(request.getUserId(), request.getSubscriptionId());
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("User subscribed to room successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error subscribing user to room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to subscribe user to room: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Unsubscribe user from a chat room
     */
    @PostMapping("/unsubscribe/room")
    public ResponseEntity<ApiResponse<Void>> unsubscribeUserFromRoom(@Valid @RequestBody SubscriptionRequest request) {
        log.info("Unsubscribing user: {} from chat room: {}", request.getUserId(), request.getSubscriptionId());
        
        try {
            webSocketService.unsubscribeUserFromRoom(request.getUserId(), request.getSubscriptionId());
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("User unsubscribed from room successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error unsubscribing user from room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to unsubscribe user from room: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Subscribe user to a family
     */
    @PostMapping("/subscribe/family")
    public ResponseEntity<ApiResponse<Void>> subscribeUserToFamily(@Valid @RequestBody SubscriptionRequest request) {
        log.info("Subscribing user: {} to family: {}", request.getUserId(), request.getSubscriptionId());
        
        try {
            webSocketService.subscribeUserToFamily(request.getUserId(), request.getSubscriptionId());
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("User subscribed to family successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error subscribing user to family: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to subscribe user to family: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Unsubscribe user from a family
     */
    @PostMapping("/unsubscribe/family")
    public ResponseEntity<ApiResponse<Void>> unsubscribeUserFromFamily(@Valid @RequestBody SubscriptionRequest request) {
        log.info("Unsubscribing user: {} from family: {}", request.getUserId(), request.getSubscriptionId());
        
        try {
            webSocketService.unsubscribeUserFromFamily(request.getUserId(), request.getSubscriptionId());
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("User unsubscribed from family successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error unsubscribing user from family: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to unsubscribe user from family: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Subscribe user to a story
     */
    @PostMapping("/subscribe/story")
    public ResponseEntity<ApiResponse<Void>> subscribeUserToStory(@Valid @RequestBody SubscriptionRequest request) {
        log.info("Subscribing user: {} to story: {}", request.getUserId(), request.getSubscriptionId());
        
        try {
            webSocketService.subscribeUserToStory(request.getUserId(), request.getSubscriptionId());
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("User subscribed to story successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error subscribing user to story: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to subscribe user to story: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Unsubscribe user from a story
     */
    @PostMapping("/unsubscribe/story")
    public ResponseEntity<ApiResponse<Void>> unsubscribeUserFromStory(@Valid @RequestBody SubscriptionRequest request) {
        log.info("Unsubscribing user: {} from story: {}", request.getUserId(), request.getSubscriptionId());
        
        try {
            webSocketService.unsubscribeUserFromStory(request.getUserId(), request.getSubscriptionId());
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("User unsubscribed from story successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error unsubscribing user from story: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to unsubscribe user from story: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Subscribe user to an event
     */
    @PostMapping("/subscribe/event")
    public ResponseEntity<ApiResponse<Void>> subscribeUserToEvent(@Valid @RequestBody SubscriptionRequest request) {
        log.info("Subscribing user: {} to event: {}", request.getUserId(), request.getSubscriptionId());
        
        try {
            webSocketService.subscribeUserToEvent(request.getUserId(), request.getSubscriptionId());
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("User subscribed to event successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error subscribing user to event: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to subscribe user to event: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Unsubscribe user from an event
     */
    @PostMapping("/unsubscribe/event")
    public ResponseEntity<ApiResponse<Void>> unsubscribeUserFromEvent(@Valid @RequestBody SubscriptionRequest request) {
        log.info("Unsubscribing user: {} from event: {}", request.getUserId(), request.getSubscriptionId());
        
        try {
            webSocketService.unsubscribeUserFromEvent(request.getUserId(), request.getSubscriptionId());
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("User unsubscribed from event successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error unsubscribing user from event: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to unsubscribe user from event: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Cleanup inactive connections
     */
    @PostMapping("/cleanup/inactive")
    public ResponseEntity<ApiResponse<Void>> cleanupInactiveConnections() {
        log.info("Cleaning up inactive connections");
        
        try {
            webSocketService.cleanupInactiveConnections();
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Inactive connections cleaned up successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error cleaning up inactive connections: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to cleanup inactive connections: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Send system notification to a user
     */
    @PostMapping("/notify/user/{userId}")
    public ResponseEntity<ApiResponse<Void>> sendSystemNotification(
            @PathVariable("userId") Long userId,
            @RequestParam("notificationType") String notificationType,
            @RequestBody(required = false) Map<String, Object> notificationData) {
        log.info("Sending system notification to user: {} - type: {}", userId, notificationType);
        
        try {
            webSocketService.sendSystemNotification(userId, notificationType, notificationData);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("System notification sent successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error sending system notification: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to send system notification: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Send error notification to a user
     */
    @PostMapping("/notify/user/{userId}/error")
    public ResponseEntity<ApiResponse<Void>> sendErrorNotification(
            @PathVariable("userId") Long userId,
            @RequestParam("errorType") String errorType,
            @RequestParam("errorMessage") String errorMessage) {
        log.info("Sending error notification to user: {} - type: {}", userId, errorType);
        
        try {
            webSocketService.sendErrorNotification(userId, errorType, errorMessage);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Error notification sent successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error sending error notification: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to send error notification: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Broadcast to all users
     */
    @PostMapping("/broadcast/all")
    public ResponseEntity<ApiResponse<Void>> broadcastToAll(
            @RequestParam String messageType,
            @RequestBody(required = false) Map<String, Object> messageData) {
        log.info("Broadcasting to all users - type: {}", messageType);
        
        try {
            webSocketService.broadcastToAll(messageType, messageData);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Broadcast sent to all users successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error broadcasting to all users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to broadcast to all users: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Broadcast to a family
     */
    @PostMapping("/broadcast/family/{familyId}")
    public ResponseEntity<ApiResponse<Void>> broadcastToFamily(
            @PathVariable("familyId") Long familyId,
            @RequestParam("messageType") String messageType,
            @RequestBody(required = false) Map<String, Object> messageData) {
        log.info("Broadcasting to family: {} - type: {}", familyId, messageType);
        
        try {
            webSocketService.broadcastToFamily(familyId, messageType, messageData);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Broadcast sent to family successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error broadcasting to family: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to broadcast to family: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Broadcast to a chat room
     */
    @PostMapping("/broadcast/room/{chatRoomId}")
    public ResponseEntity<ApiResponse<Void>> broadcastToChatRoom(
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam("messageType") String messageType,
            @RequestBody(required = false) Map<String, Object> messageData) {
        log.info("Broadcasting to chat room: {} - type: {}", chatRoomId, messageType);
        
        try {
            webSocketService.broadcastToChatRoom(chatRoomId, messageType, messageData);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Broadcast sent to chat room successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error broadcasting to chat room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to broadcast to chat room: " + e.getMessage())
                            .build());
        }
    }
}
