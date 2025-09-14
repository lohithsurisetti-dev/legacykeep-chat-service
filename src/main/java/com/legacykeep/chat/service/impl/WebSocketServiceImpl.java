package com.legacykeep.chat.service.impl;

import com.legacykeep.chat.entity.Message;
import com.legacykeep.chat.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of WebSocketService.
 * Provides real-time messaging capabilities with family-centric features.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketServiceImpl implements WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    
    // Connection tracking
    private final Map<Long, String> userConnections = new ConcurrentHashMap<>();
    private final Map<Long, List<Long>> userSubscriptions = new ConcurrentHashMap<>();
    private final Map<Long, List<Long>> familySubscriptions = new ConcurrentHashMap<>();
    private final Map<Long, List<Long>> storySubscriptions = new ConcurrentHashMap<>();
    private final Map<Long, List<Long>> eventSubscriptions = new ConcurrentHashMap<>();
    private final Map<Long, List<Long>> chatRoomSubscriptions = new ConcurrentHashMap<>();
    
    // Statistics
    private final AtomicInteger totalConnections = new AtomicInteger(0);
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private final AtomicLong messagesSent = new AtomicLong(0);
    private final AtomicLong notificationsSent = new AtomicLong(0);
    private final AtomicLong errorsOccurred = new AtomicLong(0);

    @Override
    public void sendMessageToRoom(Long chatRoomId, Message message) {
        log.debug("Sending message to chat room: {}", chatRoomId);
        
        try {
            String destination = "/topic/chat/room/" + chatRoomId;
            messagingTemplate.convertAndSend(destination, message);
            messagesSent.incrementAndGet();
            log.debug("Sent message to chat room: {} via destination: {}", chatRoomId, destination);
        } catch (Exception e) {
            log.error("Error sending message to chat room: {}", chatRoomId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendMessageToUser(Long userId, Message message) {
        log.debug("Sending message to user: {}", userId);
        
        try {
            String destination = "/queue/user/" + userId;
            messagingTemplate.convertAndSend(destination, message);
            messagesSent.incrementAndGet();
            log.debug("Sent message to user: {} via destination: {}", userId, destination);
        } catch (Exception e) {
            log.error("Error sending message to user: {}", userId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendTypingIndicator(Long chatRoomId, Long userId, boolean isTyping) {
        log.debug("Sending typing indicator for chat room: {} from user: {} - typing: {}", chatRoomId, userId, isTyping);
        
        try {
            String destination = "/topic/chat/room/" + chatRoomId + "/typing";
            Map<String, Object> typingData = Map.of(
                "userId", userId,
                "isTyping", isTyping,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, typingData);
            notificationsSent.incrementAndGet();
            log.debug("Sent typing indicator to chat room: {}", chatRoomId);
        } catch (Exception e) {
            log.error("Error sending typing indicator to chat room: {}", chatRoomId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendMessageStatusUpdate(String messageId, String status, Long userId) {
        log.debug("Sending message status update for message: {} - status: {} - user: {}", messageId, status, userId);
        
        try {
            String destination = "/topic/message/" + messageId + "/status";
            Map<String, Object> statusData = Map.of(
                "messageId", messageId,
                "status", status,
                "userId", userId,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, statusData);
            notificationsSent.incrementAndGet();
            log.debug("Sent message status update for message: {}", messageId);
        } catch (Exception e) {
            log.error("Error sending message status update for message: {}", messageId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendReadReceipt(String messageId, Long userId) {
        log.debug("Sending read receipt for message: {} from user: {}", messageId, userId);
        
        try {
            String destination = "/topic/message/" + messageId + "/read";
            Map<String, Object> readData = Map.of(
                "messageId", messageId,
                "userId", userId,
                "readAt", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, readData);
            notificationsSent.incrementAndGet();
            log.debug("Sent read receipt for message: {}", messageId);
        } catch (Exception e) {
            log.error("Error sending read receipt for message: {}", messageId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendUserOnlineStatus(Long userId, boolean isOnline) {
        log.debug("Sending user online status for user: {} - online: {}", userId, isOnline);
        
        try {
            String destination = "/topic/user/" + userId + "/status";
            Map<String, Object> statusData = Map.of(
                "userId", userId,
                "isOnline", isOnline,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, statusData);
            notificationsSent.incrementAndGet();
            log.debug("Sent user online status for user: {}", userId);
        } catch (Exception e) {
            log.error("Error sending user online status for user: {}", userId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendUserAwayStatus(Long userId, boolean isAway) {
        log.debug("Sending user away status for user: {} - away: {}", userId, isAway);
        
        try {
            String destination = "/topic/user/" + userId + "/away";
            Map<String, Object> awayData = Map.of(
                "userId", userId,
                "isAway", isAway,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, awayData);
            notificationsSent.incrementAndGet();
            log.debug("Sent user away status for user: {}", userId);
        } catch (Exception e) {
            log.error("Error sending user away status for user: {}", userId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendMessageReaction(String messageId, Long userId, String emoji, boolean isAdded) {
        log.debug("Sending message reaction for message: {} from user: {} - emoji: {} - added: {}", messageId, userId, emoji, isAdded);
        
        try {
            String destination = "/topic/message/" + messageId + "/reaction";
            Map<String, Object> reactionData = Map.of(
                "messageId", messageId,
                "userId", userId,
                "emoji", emoji,
                "isAdded", isAdded,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, reactionData);
            notificationsSent.incrementAndGet();
            log.debug("Sent message reaction for message: {}", messageId);
        } catch (Exception e) {
            log.error("Error sending message reaction for message: {}", messageId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendMessageEditNotification(String messageId, Long userId, String newContent) {
        log.debug("Sending message edit notification for message: {} from user: {}", messageId, userId);
        
        try {
            String destination = "/topic/message/" + messageId + "/edit";
            Map<String, Object> editData = Map.of(
                "messageId", messageId,
                "userId", userId,
                "newContent", newContent,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, editData);
            notificationsSent.incrementAndGet();
            log.debug("Sent message edit notification for message: {}", messageId);
        } catch (Exception e) {
            log.error("Error sending message edit notification for message: {}", messageId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendMessageDeleteNotification(String messageId, Long userId, boolean deleteForEveryone) {
        log.debug("Sending message delete notification for message: {} from user: {} - for everyone: {}", messageId, userId, deleteForEveryone);
        
        try {
            String destination = "/topic/message/" + messageId + "/delete";
            Map<String, Object> deleteData = Map.of(
                "messageId", messageId,
                "userId", userId,
                "deleteForEveryone", deleteForEveryone,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, deleteData);
            notificationsSent.incrementAndGet();
            log.debug("Sent message delete notification for message: {}", messageId);
        } catch (Exception e) {
            log.error("Error sending message delete notification for message: {}", messageId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendMessageForwardNotification(String originalMessageId, String newMessageId, Long fromUserId, Long toChatRoomId) {
        log.debug("Sending message forward notification from message: {} to message: {} in chat room: {}", originalMessageId, newMessageId, toChatRoomId);
        
        try {
            String destination = "/topic/chat/room/" + toChatRoomId + "/forward";
            Map<String, Object> forwardData = Map.of(
                "originalMessageId", originalMessageId,
                "newMessageId", newMessageId,
                "fromUserId", fromUserId,
                "toChatRoomId", toChatRoomId,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, forwardData);
            notificationsSent.incrementAndGet();
            log.debug("Sent message forward notification for message: {}", newMessageId);
        } catch (Exception e) {
            log.error("Error sending message forward notification for message: {}", newMessageId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendMessageStarNotification(String messageId, Long userId, boolean isStarred) {
        log.debug("Sending message star notification for message: {} from user: {} - starred: {}", messageId, userId, isStarred);
        
        try {
            String destination = "/topic/message/" + messageId + "/star";
            Map<String, Object> starData = Map.of(
                "messageId", messageId,
                "userId", userId,
                "isStarred", isStarred,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, starData);
            notificationsSent.incrementAndGet();
            log.debug("Sent message star notification for message: {}", messageId);
        } catch (Exception e) {
            log.error("Error sending message star notification for message: {}", messageId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendChatRoomUpdateNotification(Long chatRoomId, String updateType, Map<String, Object> updateData) {
        log.debug("Sending chat room update notification for chat room: {} - type: {}", chatRoomId, updateType);
        
        try {
            String destination = "/topic/chat/room/" + chatRoomId + "/update";
            Map<String, Object> notificationData = Map.of(
                "chatRoomId", chatRoomId,
                "updateType", updateType,
                "updateData", updateData,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, notificationData);
            notificationsSent.incrementAndGet();
            log.debug("Sent chat room update notification for chat room: {}", chatRoomId);
        } catch (Exception e) {
            log.error("Error sending chat room update notification for chat room: {}", chatRoomId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendChatRoomMemberAddedNotification(Long chatRoomId, Long addedUserId, Long addedByUserId) {
        log.debug("Sending chat room member added notification for chat room: {} - added user: {} by user: {}", chatRoomId, addedUserId, addedByUserId);
        
        try {
            String destination = "/topic/chat/room/" + chatRoomId + "/member/added";
            Map<String, Object> memberData = Map.of(
                "chatRoomId", chatRoomId,
                "addedUserId", addedUserId,
                "addedByUserId", addedByUserId,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, memberData);
            notificationsSent.incrementAndGet();
            log.debug("Sent chat room member added notification for chat room: {}", chatRoomId);
        } catch (Exception e) {
            log.error("Error sending chat room member added notification for chat room: {}", chatRoomId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendChatRoomMemberRemovedNotification(Long chatRoomId, Long removedUserId, Long removedByUserId) {
        log.debug("Sending chat room member removed notification for chat room: {} - removed user: {} by user: {}", chatRoomId, removedUserId, removedByUserId);
        
        try {
            String destination = "/topic/chat/room/" + chatRoomId + "/member/removed";
            Map<String, Object> memberData = Map.of(
                "chatRoomId", chatRoomId,
                "removedUserId", removedUserId,
                "removedByUserId", removedByUserId,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, memberData);
            notificationsSent.incrementAndGet();
            log.debug("Sent chat room member removed notification for chat room: {}", chatRoomId);
        } catch (Exception e) {
            log.error("Error sending chat room member removed notification for chat room: {}", chatRoomId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendChatRoomArchivedNotification(Long chatRoomId, Long userId, boolean isArchived) {
        log.debug("Sending chat room archived notification for chat room: {} by user: {} - archived: {}", chatRoomId, userId, isArchived);
        
        try {
            String destination = "/topic/chat/room/" + chatRoomId + "/archived";
            Map<String, Object> archiveData = Map.of(
                "chatRoomId", chatRoomId,
                "userId", userId,
                "isArchived", isArchived,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, archiveData);
            notificationsSent.incrementAndGet();
            log.debug("Sent chat room archived notification for chat room: {}", chatRoomId);
        } catch (Exception e) {
            log.error("Error sending chat room archived notification for chat room: {}", chatRoomId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendChatRoomMutedNotification(Long chatRoomId, Long userId, boolean isMuted) {
        log.debug("Sending chat room muted notification for chat room: {} by user: {} - muted: {}", chatRoomId, userId, isMuted);
        
        try {
            String destination = "/topic/chat/room/" + chatRoomId + "/muted";
            Map<String, Object> muteData = Map.of(
                "chatRoomId", chatRoomId,
                "userId", userId,
                "isMuted", isMuted,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, muteData);
            notificationsSent.incrementAndGet();
            log.debug("Sent chat room muted notification for chat room: {}", chatRoomId);
        } catch (Exception e) {
            log.error("Error sending chat room muted notification for chat room: {}", chatRoomId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendFamilyNotification(Long familyId, String notificationType, Map<String, Object> notificationData) {
        log.debug("Sending family notification for family: {} - type: {}", familyId, notificationType);
        
        try {
            String destination = "/topic/family/" + familyId + "/notification";
            Map<String, Object> data = Map.of(
                "familyId", familyId,
                "notificationType", notificationType,
                "notificationData", notificationData,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, data);
            notificationsSent.incrementAndGet();
            log.debug("Sent family notification for family: {}", familyId);
        } catch (Exception e) {
            log.error("Error sending family notification for family: {}", familyId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendStoryNotification(Long storyId, String notificationType, Map<String, Object> notificationData) {
        log.debug("Sending story notification for story: {} - type: {}", storyId, notificationType);
        
        try {
            String destination = "/topic/story/" + storyId + "/notification";
            Map<String, Object> data = Map.of(
                "storyId", storyId,
                "notificationType", notificationType,
                "notificationData", notificationData,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, data);
            notificationsSent.incrementAndGet();
            log.debug("Sent story notification for story: {}", storyId);
        } catch (Exception e) {
            log.error("Error sending story notification for story: {}", storyId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendEventNotification(Long eventId, String notificationType, Map<String, Object> notificationData) {
        log.debug("Sending event notification for event: {} - type: {}", eventId, notificationType);
        
        try {
            String destination = "/topic/event/" + eventId + "/notification";
            Map<String, Object> data = Map.of(
                "eventId", eventId,
                "notificationType", notificationType,
                "notificationData", notificationData,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, data);
            notificationsSent.incrementAndGet();
            log.debug("Sent event notification for event: {}", eventId);
        } catch (Exception e) {
            log.error("Error sending event notification for event: {}", eventId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendAIFeatureNotification(Long chatRoomId, String featureType, Map<String, Object> featureData) {
        log.debug("Sending AI feature notification for chat room: {} - feature: {}", chatRoomId, featureType);
        
        try {
            String destination = "/topic/chat/room/" + chatRoomId + "/ai/" + featureType;
            Map<String, Object> data = Map.of(
                "chatRoomId", chatRoomId,
                "featureType", featureType,
                "featureData", featureData,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, data);
            notificationsSent.incrementAndGet();
            log.debug("Sent AI feature notification for chat room: {}", chatRoomId);
        } catch (Exception e) {
            log.error("Error sending AI feature notification for chat room: {}", chatRoomId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendToneDetectionNotification(Long chatRoomId, String messageId, String toneColor, Double confidence) {
        log.debug("Sending tone detection notification for chat room: {} - message: {} - tone: {} - confidence: {}", chatRoomId, messageId, toneColor, confidence);
        
        try {
            String destination = "/topic/chat/room/" + chatRoomId + "/ai/tone";
            Map<String, Object> toneData = Map.of(
                "chatRoomId", chatRoomId,
                "messageId", messageId,
                "toneColor", toneColor,
                "confidence", confidence,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, toneData);
            notificationsSent.incrementAndGet();
            log.debug("Sent tone detection notification for chat room: {}", chatRoomId);
        } catch (Exception e) {
            log.error("Error sending tone detection notification for chat room: {}", chatRoomId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendVoiceEmotionNotification(Long chatRoomId, String messageId, String emotion, Double confidence) {
        log.debug("Sending voice emotion notification for chat room: {} - message: {} - emotion: {} - confidence: {}", chatRoomId, messageId, emotion, confidence);
        
        try {
            String destination = "/topic/chat/room/" + chatRoomId + "/ai/emotion";
            Map<String, Object> emotionData = Map.of(
                "chatRoomId", chatRoomId,
                "messageId", messageId,
                "emotion", emotion,
                "confidence", confidence,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, emotionData);
            notificationsSent.incrementAndGet();
            log.debug("Sent voice emotion notification for chat room: {}", chatRoomId);
        } catch (Exception e) {
            log.error("Error sending voice emotion notification for chat room: {}", chatRoomId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendMemoryTriggerNotification(Long chatRoomId, String messageId, List<String> memoryTriggers) {
        log.debug("Sending memory trigger notification for chat room: {} - message: {} - triggers: {}", chatRoomId, messageId, memoryTriggers);
        
        try {
            String destination = "/topic/chat/room/" + chatRoomId + "/ai/memory";
            Map<String, Object> memoryData = Map.of(
                "chatRoomId", chatRoomId,
                "messageId", messageId,
                "memoryTriggers", memoryTriggers,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, memoryData);
            notificationsSent.incrementAndGet();
            log.debug("Sent memory trigger notification for chat room: {}", chatRoomId);
        } catch (Exception e) {
            log.error("Error sending memory trigger notification for chat room: {}", chatRoomId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendPredictiveTextSuggestion(Long chatRoomId, Long userId, String suggestion) {
        log.debug("Sending predictive text suggestion for chat room: {} - user: {} - suggestion: {}", chatRoomId, userId, suggestion);
        
        try {
            String destination = "/queue/user/" + userId + "/ai/predictive";
            Map<String, Object> suggestionData = Map.of(
                "chatRoomId", chatRoomId,
                "userId", userId,
                "suggestion", suggestion,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, suggestionData);
            notificationsSent.incrementAndGet();
            log.debug("Sent predictive text suggestion for user: {}", userId);
        } catch (Exception e) {
            log.error("Error sending predictive text suggestion for user: {}", userId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendMessageProtectionNotification(String messageId, String protectionType, Map<String, Object> protectionData) {
        log.debug("Sending message protection notification for message: {} - type: {}", messageId, protectionType);
        
        try {
            String destination = "/topic/message/" + messageId + "/protection";
            Map<String, Object> data = Map.of(
                "messageId", messageId,
                "protectionType", protectionType,
                "protectionData", protectionData,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, data);
            notificationsSent.incrementAndGet();
            log.debug("Sent message protection notification for message: {}", messageId);
        } catch (Exception e) {
            log.error("Error sending message protection notification for message: {}", messageId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendScreenshotDetectionNotification(String messageId, Long userId) {
        log.debug("Sending screenshot detection notification for message: {} - user: {}", messageId, userId);
        
        try {
            String destination = "/topic/message/" + messageId + "/screenshot";
            Map<String, Object> screenshotData = Map.of(
                "messageId", messageId,
                "userId", userId,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, screenshotData);
            notificationsSent.incrementAndGet();
            log.debug("Sent screenshot detection notification for message: {}", messageId);
        } catch (Exception e) {
            log.error("Error sending screenshot detection notification for message: {}", messageId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendViewLimitReachedNotification(String messageId, Long userId) {
        log.debug("Sending view limit reached notification for message: {} - user: {}", messageId, userId);
        
        try {
            String destination = "/topic/message/" + messageId + "/viewlimit";
            Map<String, Object> viewLimitData = Map.of(
                "messageId", messageId,
                "userId", userId,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, viewLimitData);
            notificationsSent.incrementAndGet();
            log.debug("Sent view limit reached notification for message: {}", messageId);
        } catch (Exception e) {
            log.error("Error sending view limit reached notification for message: {}", messageId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendSelfDestructWarningNotification(String messageId, Long userId, int secondsRemaining) {
        log.debug("Sending self-destruct warning notification for message: {} - user: {} - seconds: {}", messageId, userId, secondsRemaining);
        
        try {
            String destination = "/queue/user/" + userId + "/message/" + messageId + "/warning";
            Map<String, Object> warningData = Map.of(
                "messageId", messageId,
                "userId", userId,
                "secondsRemaining", secondsRemaining,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, warningData);
            notificationsSent.incrementAndGet();
            log.debug("Sent self-destruct warning notification for message: {}", messageId);
        } catch (Exception e) {
            log.error("Error sending self-destruct warning notification for message: {}", messageId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendMessageExpiredNotification(String messageId, Long userId) {
        log.debug("Sending message expired notification for message: {} - user: {}", messageId, userId);
        
        try {
            String destination = "/topic/message/" + messageId + "/expired";
            Map<String, Object> expiredData = Map.of(
                "messageId", messageId,
                "userId", userId,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, expiredData);
            notificationsSent.incrementAndGet();
            log.debug("Sent message expired notification for message: {}", messageId);
        } catch (Exception e) {
            log.error("Error sending message expired notification for message: {}", messageId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendConnectionStatus(Long userId, String status, String message) {
        log.debug("Sending connection status to user: {} - status: {} - message: {}", userId, status, message);
        
        try {
            String destination = "/queue/user/" + userId + "/connection";
            Map<String, Object> statusData = Map.of(
                "userId", userId,
                "status", status,
                "message", message,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, statusData);
            notificationsSent.incrementAndGet();
            log.debug("Sent connection status to user: {}", userId);
        } catch (Exception e) {
            log.error("Error sending connection status to user: {}", userId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendErrorNotification(Long userId, String errorType, String errorMessage) {
        log.debug("Sending error notification to user: {} - type: {} - message: {}", userId, errorType, errorMessage);
        
        try {
            String destination = "/queue/user/" + userId + "/error";
            Map<String, Object> errorData = Map.of(
                "userId", userId,
                "errorType", errorType,
                "errorMessage", errorMessage,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, errorData);
            notificationsSent.incrementAndGet();
            log.debug("Sent error notification to user: {}", userId);
        } catch (Exception e) {
            log.error("Error sending error notification to user: {}", userId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void sendSystemNotification(Long userId, String notificationType, Map<String, Object> notificationData) {
        log.debug("Sending system notification to user: {} - type: {}", userId, notificationType);
        
        try {
            String destination = "/queue/user/" + userId + "/system";
            Map<String, Object> data = Map.of(
                "userId", userId,
                "notificationType", notificationType,
                "notificationData", notificationData,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, data);
            notificationsSent.incrementAndGet();
            log.debug("Sent system notification to user: {}", userId);
        } catch (Exception e) {
            log.error("Error sending system notification to user: {}", userId, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void broadcastToAll(String messageType, Map<String, Object> messageData) {
        log.debug("Broadcasting to all users - type: {}", messageType);
        
        try {
            String destination = "/topic/broadcast/" + messageType;
            Map<String, Object> data = Map.of(
                "messageType", messageType,
                "messageData", messageData,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, data);
            notificationsSent.incrementAndGet();
            log.debug("Broadcasted to all users - type: {}", messageType);
        } catch (Exception e) {
            log.error("Error broadcasting to all users - type: {}", messageType, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void broadcastToFamily(Long familyId, String messageType, Map<String, Object> messageData) {
        log.debug("Broadcasting to family: {} - type: {}", familyId, messageType);
        
        try {
            String destination = "/topic/family/" + familyId + "/broadcast";
            Map<String, Object> data = Map.of(
                "familyId", familyId,
                "messageType", messageType,
                "messageData", messageData,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, data);
            notificationsSent.incrementAndGet();
            log.debug("Broadcasted to family: {} - type: {}", familyId, messageType);
        } catch (Exception e) {
            log.error("Error broadcasting to family: {} - type: {}", familyId, messageType, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public void broadcastToChatRoom(Long chatRoomId, String messageType, Map<String, Object> messageData) {
        log.debug("Broadcasting to chat room: {} - type: {}", chatRoomId, messageType);
        
        try {
            String destination = "/topic/chat/room/" + chatRoomId + "/broadcast";
            Map<String, Object> data = Map.of(
                "chatRoomId", chatRoomId,
                "messageType", messageType,
                "messageData", messageData,
                "timestamp", System.currentTimeMillis()
            );
            messagingTemplate.convertAndSend(destination, data);
            notificationsSent.incrementAndGet();
            log.debug("Broadcasted to chat room: {} - type: {}", chatRoomId, messageType);
        } catch (Exception e) {
            log.error("Error broadcasting to chat room: {} - type: {}", chatRoomId, messageType, e);
            errorsOccurred.incrementAndGet();
        }
    }

    @Override
    public int getConnectedUsersCount() {
        return activeConnections.get();
    }

    @Override
    public List<Long> getConnectedUsersInRoom(Long chatRoomId) {
        // This would typically be implemented with proper connection tracking
        return List.of(); // Placeholder implementation
    }

    @Override
    public List<Long> getConnectedUsersInFamily(Long familyId) {
        // This would typically be implemented with proper connection tracking
        return List.of(); // Placeholder implementation
    }

    @Override
    public boolean isUserConnected(Long userId) {
        return userConnections.containsKey(userId);
    }

    @Override
    public boolean isUserConnectedToRoom(Long userId, Long chatRoomId) {
        // This would typically be implemented with proper connection tracking
        return isUserConnected(userId); // Placeholder implementation
    }

    @Override
    public Map<String, Object> getUserConnectionInfo(Long userId) {
        Map<String, Object> info = new HashMap<>();
        info.put("isConnected", isUserConnected(userId));
        info.put("connectionId", userConnections.get(userId));
        info.put("subscriptions", userSubscriptions.getOrDefault(userId, List.of()));
        return info;
    }

    @Override
    public void disconnectUser(Long userId) {
        log.debug("Disconnecting user: {}", userId);
        userConnections.remove(userId);
        userSubscriptions.remove(userId);
        activeConnections.decrementAndGet();
        log.info("Disconnected user: {}", userId);
    }

    @Override
    public void disconnectUserFromRoom(Long userId, Long chatRoomId) {
        log.debug("Disconnecting user: {} from chat room: {}", userId, chatRoomId);
        // This would typically be implemented with proper connection tracking
        log.info("Disconnected user: {} from chat room: {}", userId, chatRoomId);
    }

    @Override
    public void subscribeUserToRoom(Long userId, Long chatRoomId) {
        log.debug("Subscribing user: {} to chat room: {}", userId, chatRoomId);
        userSubscriptions.computeIfAbsent(userId, k -> new ArrayList<>()).add(chatRoomId);
        chatRoomSubscriptions.computeIfAbsent(chatRoomId, k -> new ArrayList<>()).add(userId);
        log.info("Subscribed user: {} to chat room: {}", userId, chatRoomId);
    }

    @Override
    public void unsubscribeUserFromRoom(Long userId, Long chatRoomId) {
        log.debug("Unsubscribing user: {} from chat room: {}", userId, chatRoomId);
        userSubscriptions.computeIfPresent(userId, (k, v) -> {
            v.remove(chatRoomId);
            return v.isEmpty() ? null : v;
        });
        chatRoomSubscriptions.computeIfPresent(chatRoomId, (k, v) -> {
            v.remove(userId);
            return v.isEmpty() ? null : v;
        });
        log.info("Unsubscribed user: {} from chat room: {}", userId, chatRoomId);
    }

    @Override
    public void subscribeUserToFamily(Long userId, Long familyId) {
        log.debug("Subscribing user: {} to family: {}", userId, familyId);
        userSubscriptions.computeIfAbsent(userId, k -> new ArrayList<>()).add(familyId);
        familySubscriptions.computeIfAbsent(familyId, k -> new ArrayList<>()).add(userId);
        log.info("Subscribed user: {} to family: {}", userId, familyId);
    }

    @Override
    public void unsubscribeUserFromFamily(Long userId, Long familyId) {
        log.debug("Unsubscribing user: {} from family: {}", userId, familyId);
        userSubscriptions.computeIfPresent(userId, (k, v) -> {
            v.remove(familyId);
            return v.isEmpty() ? null : v;
        });
        familySubscriptions.computeIfPresent(familyId, (k, v) -> {
            v.remove(userId);
            return v.isEmpty() ? null : v;
        });
        log.info("Unsubscribed user: {} from family: {}", userId, familyId);
    }

    @Override
    public void subscribeUserToStory(Long userId, Long storyId) {
        log.debug("Subscribing user: {} to story: {}", userId, storyId);
        userSubscriptions.computeIfAbsent(userId, k -> new ArrayList<>()).add(storyId);
        storySubscriptions.computeIfAbsent(storyId, k -> new ArrayList<>()).add(userId);
        log.info("Subscribed user: {} to story: {}", userId, storyId);
    }

    @Override
    public void unsubscribeUserFromStory(Long userId, Long storyId) {
        log.debug("Unsubscribing user: {} from story: {}", userId, storyId);
        userSubscriptions.computeIfPresent(userId, (k, v) -> {
            v.remove(storyId);
            return v.isEmpty() ? null : v;
        });
        storySubscriptions.computeIfPresent(storyId, (k, v) -> {
            v.remove(userId);
            return v.isEmpty() ? null : v;
        });
        log.info("Unsubscribed user: {} from story: {}", userId, storyId);
    }

    @Override
    public void subscribeUserToEvent(Long userId, Long eventId) {
        log.debug("Subscribing user: {} to event: {}", userId, eventId);
        userSubscriptions.computeIfAbsent(userId, k -> new ArrayList<>()).add(eventId);
        eventSubscriptions.computeIfAbsent(eventId, k -> new ArrayList<>()).add(userId);
        log.info("Subscribed user: {} to event: {}", userId, eventId);
    }

    @Override
    public void unsubscribeUserFromEvent(Long userId, Long eventId) {
        log.debug("Unsubscribing user: {} from event: {}", userId, eventId);
        userSubscriptions.computeIfPresent(userId, (k, v) -> {
            v.remove(eventId);
            return v.isEmpty() ? null : v;
        });
        eventSubscriptions.computeIfPresent(eventId, (k, v) -> {
            v.remove(userId);
            return v.isEmpty() ? null : v;
        });
        log.info("Unsubscribed user: {} from event: {}", userId, eventId);
    }

    @Override
    public Map<String, Object> getUserSubscriptionInfo(Long userId) {
        Map<String, Object> info = new HashMap<>();
        info.put("userId", userId);
        info.put("chatRooms", userSubscriptions.getOrDefault(userId, List.of()));
        info.put("families", familySubscriptions.entrySet().stream()
                .filter(entry -> entry.getValue().contains(userId))
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toList()));
        info.put("stories", storySubscriptions.entrySet().stream()
                .filter(entry -> entry.getValue().contains(userId))
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toList()));
        info.put("events", eventSubscriptions.entrySet().stream()
                .filter(entry -> entry.getValue().contains(userId))
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toList()));
        return info;
    }

    @Override
    public void cleanupInactiveConnections() {
        log.debug("Cleaning up inactive connections");
        // This would typically be implemented with proper connection tracking and timeout logic
        log.info("Cleaned up inactive connections");
    }

    @Override
    public WebSocketStats getWebSocketStats() {
        return new WebSocketStats(
                totalConnections.get(),
                activeConnections.get(),
                familySubscriptions.size(),
                storySubscriptions.size(),
                eventSubscriptions.size(),
                chatRoomSubscriptions.size(),
                messagesSent.get(),
                notificationsSent.get(),
                errorsOccurred.get()
        );
    }
}
