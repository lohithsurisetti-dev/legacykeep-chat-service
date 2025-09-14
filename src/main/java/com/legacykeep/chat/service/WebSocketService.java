package com.legacykeep.chat.service;

import com.legacykeep.chat.entity.Message;
import com.legacykeep.chat.enums.MessageType;

import java.util.List;
import java.util.Map;

/**
 * Service interface for WebSocket operations.
 * Provides real-time messaging capabilities with family-centric features.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public interface WebSocketService {

    /**
     * Send message to chat room subscribers
     */
    void sendMessageToRoom(Long chatRoomId, Message message);

    /**
     * Send message to specific user
     */
    void sendMessageToUser(Long userId, Message message);

    /**
     * Send typing indicator to chat room
     */
    void sendTypingIndicator(Long chatRoomId, Long userId, boolean isTyping);

    /**
     * Send message status update
     */
    void sendMessageStatusUpdate(String messageId, String status, Long userId);

    /**
     * Send read receipt
     */
    void sendReadReceipt(String messageId, Long userId);

    /**
     * Send user online status
     */
    void sendUserOnlineStatus(Long userId, boolean isOnline);

    /**
     * Send user away status
     */
    void sendUserAwayStatus(Long userId, boolean isAway);

    /**
     * Send message reaction
     */
    void sendMessageReaction(String messageId, Long userId, String emoji, boolean isAdded);

    /**
     * Send message edit notification
     */
    void sendMessageEditNotification(String messageId, Long userId, String newContent);

    /**
     * Send message delete notification
     */
    void sendMessageDeleteNotification(String messageId, Long userId, boolean deleteForEveryone);

    /**
     * Send message forward notification
     */
    void sendMessageForwardNotification(String originalMessageId, String newMessageId, Long fromUserId, Long toChatRoomId);

    /**
     * Send message star notification
     */
    void sendMessageStarNotification(String messageId, Long userId, boolean isStarred);

    /**
     * Send chat room update notification
     */
    void sendChatRoomUpdateNotification(Long chatRoomId, String updateType, Map<String, Object> updateData);

    /**
     * Send chat room member added notification
     */
    void sendChatRoomMemberAddedNotification(Long chatRoomId, Long addedUserId, Long addedByUserId);

    /**
     * Send chat room member removed notification
     */
    void sendChatRoomMemberRemovedNotification(Long chatRoomId, Long removedUserId, Long removedByUserId);

    /**
     * Send chat room archived notification
     */
    void sendChatRoomArchivedNotification(Long chatRoomId, Long userId, boolean isArchived);

    /**
     * Send chat room muted notification
     */
    void sendChatRoomMutedNotification(Long chatRoomId, Long userId, boolean isMuted);

    /**
     * Send family notification
     */
    void sendFamilyNotification(Long familyId, String notificationType, Map<String, Object> notificationData);

    /**
     * Send story notification
     */
    void sendStoryNotification(Long storyId, String notificationType, Map<String, Object> notificationData);

    /**
     * Send event notification
     */
    void sendEventNotification(Long eventId, String notificationType, Map<String, Object> notificationData);

    /**
     * Send AI feature notification
     */
    void sendAIFeatureNotification(Long chatRoomId, String featureType, Map<String, Object> featureData);

    /**
     * Send tone detection notification
     */
    void sendToneDetectionNotification(Long chatRoomId, String messageId, String toneColor, Double confidence);

    /**
     * Send voice emotion notification
     */
    void sendVoiceEmotionNotification(Long chatRoomId, String messageId, String emotion, Double confidence);

    /**
     * Send memory trigger notification
     */
    void sendMemoryTriggerNotification(Long chatRoomId, String messageId, List<String> memoryTriggers);

    /**
     * Send predictive text suggestion
     */
    void sendPredictiveTextSuggestion(Long chatRoomId, Long userId, String suggestion);

    /**
     * Send message protection notification
     */
    void sendMessageProtectionNotification(String messageId, String protectionType, Map<String, Object> protectionData);

    /**
     * Send screenshot detection notification
     */
    void sendScreenshotDetectionNotification(String messageId, Long userId);

    /**
     * Send view limit reached notification
     */
    void sendViewLimitReachedNotification(String messageId, Long userId);

    /**
     * Send self-destruct warning notification
     */
    void sendSelfDestructWarningNotification(String messageId, Long userId, int secondsRemaining);

    /**
     * Send message expired notification
     */
    void sendMessageExpiredNotification(String messageId, Long userId);

    /**
     * Send connection status to user
     */
    void sendConnectionStatus(Long userId, String status, String message);

    /**
     * Send error notification to user
     */
    void sendErrorNotification(Long userId, String errorType, String errorMessage);

    /**
     * Send system notification
     */
    void sendSystemNotification(Long userId, String notificationType, Map<String, Object> notificationData);

    /**
     * Broadcast to all connected users
     */
    void broadcastToAll(String messageType, Map<String, Object> messageData);

    /**
     * Broadcast to family members
     */
    void broadcastToFamily(Long familyId, String messageType, Map<String, Object> messageData);

    /**
     * Broadcast to chat room participants
     */
    void broadcastToChatRoom(Long chatRoomId, String messageType, Map<String, Object> messageData);

    /**
     * Get connected users count
     */
    int getConnectedUsersCount();

    /**
     * Get connected users in chat room
     */
    List<Long> getConnectedUsersInRoom(Long chatRoomId);

    /**
     * Get connected users in family
     */
    List<Long> getConnectedUsersInFamily(Long familyId);

    /**
     * Check if user is connected
     */
    boolean isUserConnected(Long userId);

    /**
     * Check if user is connected to chat room
     */
    boolean isUserConnectedToRoom(Long userId, Long chatRoomId);

    /**
     * Get user connection info
     */
    Map<String, Object> getUserConnectionInfo(Long userId);

    /**
     * Disconnect user
     */
    void disconnectUser(Long userId);

    /**
     * Disconnect user from chat room
     */
    void disconnectUserFromRoom(Long userId, Long chatRoomId);

    /**
     * Subscribe user to chat room
     */
    void subscribeUserToRoom(Long userId, Long chatRoomId);

    /**
     * Unsubscribe user from chat room
     */
    void unsubscribeUserFromRoom(Long userId, Long chatRoomId);

    /**
     * Subscribe user to family notifications
     */
    void subscribeUserToFamily(Long userId, Long familyId);

    /**
     * Unsubscribe user from family notifications
     */
    void unsubscribeUserFromFamily(Long userId, Long familyId);

    /**
     * Subscribe user to story notifications
     */
    void subscribeUserToStory(Long userId, Long storyId);

    /**
     * Unsubscribe user from story notifications
     */
    void unsubscribeUserFromStory(Long userId, Long storyId);

    /**
     * Subscribe user to event notifications
     */
    void subscribeUserToEvent(Long userId, Long eventId);

    /**
     * Unsubscribe user from event notifications
     */
    void unsubscribeUserFromEvent(Long userId, Long eventId);

    /**
     * Get subscription info for user
     */
    Map<String, Object> getUserSubscriptionInfo(Long userId);

    /**
     * Clean up inactive connections
     */
    void cleanupInactiveConnections();

    /**
     * Get WebSocket statistics
     */
    WebSocketStats getWebSocketStats();

    /**
     * WebSocket statistics DTO
     */
    class WebSocketStats {
        private int totalConnections;
        private int activeConnections;
        private int familySubscriptions;
        private int storySubscriptions;
        private int eventSubscriptions;
        private int chatRoomSubscriptions;
        private long messagesSent;
        private long notificationsSent;
        private long errorsOccurred;

        // Constructors
        public WebSocketStats() {}

        public WebSocketStats(int totalConnections, int activeConnections, int familySubscriptions,
                            int storySubscriptions, int eventSubscriptions, int chatRoomSubscriptions,
                            long messagesSent, long notificationsSent, long errorsOccurred) {
            this.totalConnections = totalConnections;
            this.activeConnections = activeConnections;
            this.familySubscriptions = familySubscriptions;
            this.storySubscriptions = storySubscriptions;
            this.eventSubscriptions = eventSubscriptions;
            this.chatRoomSubscriptions = chatRoomSubscriptions;
            this.messagesSent = messagesSent;
            this.notificationsSent = notificationsSent;
            this.errorsOccurred = errorsOccurred;
        }

        // Getters and Setters
        public int getTotalConnections() { return totalConnections; }
        public void setTotalConnections(int totalConnections) { this.totalConnections = totalConnections; }

        public int getActiveConnections() { return activeConnections; }
        public void setActiveConnections(int activeConnections) { this.activeConnections = activeConnections; }

        public int getFamilySubscriptions() { return familySubscriptions; }
        public void setFamilySubscriptions(int familySubscriptions) { this.familySubscriptions = familySubscriptions; }

        public int getStorySubscriptions() { return storySubscriptions; }
        public void setStorySubscriptions(int storySubscriptions) { this.storySubscriptions = storySubscriptions; }

        public int getEventSubscriptions() { return eventSubscriptions; }
        public void setEventSubscriptions(int eventSubscriptions) { this.eventSubscriptions = eventSubscriptions; }

        public int getChatRoomSubscriptions() { return chatRoomSubscriptions; }
        public void setChatRoomSubscriptions(int chatRoomSubscriptions) { this.chatRoomSubscriptions = chatRoomSubscriptions; }

        public long getMessagesSent() { return messagesSent; }
        public void setMessagesSent(long messagesSent) { this.messagesSent = messagesSent; }

        public long getNotificationsSent() { return notificationsSent; }
        public void setNotificationsSent(long notificationsSent) { this.notificationsSent = notificationsSent; }

        public long getErrorsOccurred() { return errorsOccurred; }
        public void setErrorsOccurred(long errorsOccurred) { this.errorsOccurred = errorsOccurred; }
    }
}
