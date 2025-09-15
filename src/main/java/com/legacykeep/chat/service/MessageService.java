package com.legacykeep.chat.service;

import com.legacykeep.chat.dto.request.EditMessageRequest;
import com.legacykeep.chat.dto.request.ForwardMessageRequest;
import com.legacykeep.chat.dto.request.ReactionRequest;
import com.legacykeep.chat.dto.request.SendMessageRequest;
import com.legacykeep.chat.dto.request.EditMessageWithHistoryRequest;
import com.legacykeep.chat.dto.request.DeleteMessageRequest;
import com.legacykeep.chat.dto.request.ScheduleMessageRequest;
import com.legacykeep.chat.entity.Message;
import com.legacykeep.chat.entity.MessageEditHistory;
import com.legacykeep.chat.entity.ScheduledMessage;
import com.legacykeep.chat.dto.response.ThreadSummary;
import com.legacykeep.chat.enums.MessageStatus;
import com.legacykeep.chat.enums.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Message operations.
 * Provides business logic for message management with advanced family-centric features.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public interface MessageService {

    /**
     * Send a new message
     */
    Message sendMessage(SendMessageRequest request);

    /**
     * Get message by ID
     */
    Optional<Message> getMessageById(String id);
    
    /**
     * Get message by ID with decryption if needed
     * 
     * @param id Message ID
     * @param userId User ID requesting the message (for key access)
     * @return Optional containing the message with decrypted content if user has access
     */
    Optional<Message> getMessageByIdWithDecryption(String id, Long userId);

    /**
     * Get message by UUID
     */
    Optional<Message> getMessageByUuid(String messageUuid);

    /**
     * Edit a message
     */
    Message editMessage(String messageId, EditMessageRequest request);

    /**
     * Delete a message (soft delete)
     */
    void deleteMessage(String messageId, Long userId);

    /**
     * Delete message for everyone
     */
    void deleteMessageForEveryone(String messageId, Long userId);

    /**
     * Forward a message
     */
    Message forwardMessage(ForwardMessageRequest request);

    /**
     * Star/unstar a message
     */
    Message toggleStarMessage(String messageId, Long userId);

    /**
     * Add reaction to message
     */
    Message addReaction(String messageId, ReactionRequest request);

    /**
     * Remove reaction from message
     */
    Message removeReaction(String messageId, Long userId, String emoji);

    /**
     * Mark message as read
     */
    void markMessageAsRead(String messageId, Long userId);

    /**
     * Mark messages as read in chat room
     */
    void markMessagesAsReadInRoom(Long chatRoomId, Long userId);

    /**
     * Get messages in a chat room
     */
    List<Message> getMessagesInRoom(Long chatRoomId);

    /**
     * Get messages in a chat room with pagination
     */
    Page<Message> getMessagesInRoom(Long chatRoomId, Pageable pageable);

    /**
     * Get messages in a chat room before a specific message
     */
    List<Message> getMessagesBefore(Long chatRoomId, String messageId, int limit);

    /**
     * Get messages in a chat room after a specific message
     */
    List<Message> getMessagesAfter(Long chatRoomId, String messageId, int limit);

    /**
     * Get messages by sender
     */
    List<Message> getMessagesBySender(Long senderUserId);

    /**
     * Get messages by sender with pagination
     */
    Page<Message> getMessagesBySender(Long senderUserId, Pageable pageable);

    /**
     * Get messages by type
     */
    List<Message> getMessagesByType(MessageType messageType);

    /**
     * Get messages by type with pagination
     */
    Page<Message> getMessagesByType(MessageType messageType, Pageable pageable);

    /**
     * Get messages by status
     */
    List<Message> getMessagesByStatus(MessageStatus status);

    /**
     * Get messages by status with pagination
     */
    Page<Message> getMessagesByStatus(MessageStatus status, Pageable pageable);

    /**
     * Get starred messages for a user
     */
    List<Message> getStarredMessagesForUser(Long userId);

    /**
     * Get starred messages for a user with pagination
     */
    Page<Message> getStarredMessagesForUser(Long userId, Pageable pageable);

    /**
     * Get starred messages in a chat room
     */
    List<Message> getStarredMessagesInRoom(Long chatRoomId);

    /**
     * Get starred messages in a chat room with pagination
     */
    Page<Message> getStarredMessagesInRoom(Long chatRoomId, Pageable pageable);

    /**
     * Get protected messages
     */
    List<Message> getProtectedMessages();

    /**
     * Get protected messages with pagination
     */
    Page<Message> getProtectedMessages(Pageable pageable);

    /**
     * Get protected messages in a chat room
     */
    List<Message> getProtectedMessagesInRoom(Long chatRoomId);

    /**
     * Get protected messages in a chat room with pagination
     */
    Page<Message> getProtectedMessagesInRoom(Long chatRoomId, Pageable pageable);

    /**
     * Get messages with tone color
     */
    List<Message> getMessagesWithToneColor();

    /**
     * Get messages with tone color with pagination
     */
    Page<Message> getMessagesWithToneColor(Pageable pageable);

    /**
     * Get messages with tone color in a chat room
     */
    List<Message> getMessagesWithToneColorInRoom(Long chatRoomId);

    /**
     * Get messages with tone color in a chat room with pagination
     */
    Page<Message> getMessagesWithToneColorInRoom(Long chatRoomId, Pageable pageable);

    /**
     * Get messages with AI features
     */
    List<Message> getMessagesWithAIFeatures();

    /**
     * Get messages with AI features with pagination
     */
    Page<Message> getMessagesWithAIFeatures(Pageable pageable);

    /**
     * Get messages with media
     */
    List<Message> getMessagesWithMedia();

    /**
     * Get messages with media with pagination
     */
    Page<Message> getMessagesWithMedia(Pageable pageable);

    /**
     * Get messages with media in a chat room
     */
    List<Message> getMessagesWithMediaInRoom(Long chatRoomId);

    /**
     * Get messages with media in a chat room with pagination
     */
    Page<Message> getMessagesWithMediaInRoom(Long chatRoomId, Pageable pageable);

    /**
     * Get messages with location
     */
    List<Message> getMessagesWithLocation();

    /**
     * Get messages with location with pagination
     */
    Page<Message> getMessagesWithLocation(Pageable pageable);

    /**
     * Get messages with contact information
     */
    List<Message> getMessagesWithContact();

    /**
     * Get messages with contact information with pagination
     */
    Page<Message> getMessagesWithContact(Pageable pageable);

    /**
     * Get messages associated with a story
     */
    List<Message> getMessagesByStory(Long storyId);

    /**
     * Get messages associated with a story with pagination
     */
    Page<Message> getMessagesByStory(Long storyId, Pageable pageable);

    /**
     * Get messages associated with a memory
     */
    List<Message> getMessagesByMemory(Long memoryId);

    /**
     * Get messages associated with a memory with pagination
     */
    Page<Message> getMessagesByMemory(Long memoryId, Pageable pageable);

    /**
     * Get messages associated with an event
     */
    List<Message> getMessagesByEvent(Long eventId);

    /**
     * Get messages associated with an event with pagination
     */
    Page<Message> getMessagesByEvent(Long eventId, Pageable pageable);

    /**
     * Get messages created between two dates
     */
    List<Message> getMessagesBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get messages created between two dates with pagination
     */
    Page<Message> getMessagesBetweenDates(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Get messages in a chat room created between two dates
     */
    List<Message> getMessagesInRoomBetweenDates(Long chatRoomId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get messages in a chat room created between two dates with pagination
     */
    Page<Message> getMessagesInRoomBetweenDates(Long chatRoomId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Get the latest message in a chat room
     */
    Optional<Message> getLatestMessageInRoom(Long chatRoomId);

    /**
     * Search messages by content
     */
    List<Message> searchMessagesByContent(String content);

    /**
     * Search messages by content with pagination
     */
    Page<Message> searchMessagesByContent(String content, Pageable pageable);

    /**
     * Search messages by content in a chat room
     */
    List<Message> searchMessagesByContentInRoom(Long chatRoomId, String content);

    /**
     * Search messages by content in a chat room with pagination
     */
    Page<Message> searchMessagesByContentInRoom(Long chatRoomId, String content, Pageable pageable);

    /**
     * Get message statistics for a user
     */
    MessageStats getMessageStatsForUser(Long userId);

    /**
     * Get message statistics for a chat room
     */
    MessageStats getMessageStatsForRoom(Long chatRoomId);

    /**
     * Count messages in a chat room
     */
    long countMessagesInRoom(Long chatRoomId);

    /**
     * Count messages by sender
     */
    long countMessagesBySender(Long senderUserId);

    /**
     * Count messages by type
     */
    long countMessagesByType(MessageType messageType);

    /**
     * Count messages by status
     */
    long countMessagesByStatus(MessageStatus status);

    /**
     * Count starred messages for a user
     */
    long countStarredMessagesForUser(Long userId);

    /**
     * Count starred messages in a chat room
     */
    long countStarredMessagesInRoom(Long chatRoomId);

    /**
     * Count protected messages
     */
    long countProtectedMessages();

    /**
     * Count protected messages in a chat room
     */
    long countProtectedMessagesInRoom(Long chatRoomId);

    /**
     * Count messages with media
     */
    long countMessagesWithMedia();

    /**
     * Count messages with media in a chat room
     */
    long countMessagesWithMediaInRoom(Long chatRoomId);

    /**
     * Clean up expired messages
     */
    void cleanupExpiredMessages();

    /**
     * Clean up messages that have reached view limit
     */
    void cleanupMessagesAtViewLimit();

    /**
     * Message statistics DTO
     */
    class MessageStats {
        private long totalMessages;
        private long textMessages;
        private long mediaMessages;
        private long voiceMessages;
        private long starredMessages;
        private long protectedMessages;
        private long messagesWithToneColor;
        private long messagesWithAIFeatures;
        private long messagesWithMedia;
        private long messagesWithLocation;
        private long messagesWithContact;
        private long storyMessages;
        private long memoryMessages;
        private long eventMessages;

        // Constructors
        public MessageStats() {}

        public MessageStats(long totalMessages, long textMessages, long mediaMessages, 
                          long voiceMessages, long starredMessages, long protectedMessages,
                          long messagesWithToneColor, long messagesWithAIFeatures, 
                          long messagesWithMedia, long messagesWithLocation, 
                          long messagesWithContact, long storyMessages, 
                          long memoryMessages, long eventMessages) {
            this.totalMessages = totalMessages;
            this.textMessages = textMessages;
            this.mediaMessages = mediaMessages;
            this.voiceMessages = voiceMessages;
            this.starredMessages = starredMessages;
            this.protectedMessages = protectedMessages;
            this.messagesWithToneColor = messagesWithToneColor;
            this.messagesWithAIFeatures = messagesWithAIFeatures;
            this.messagesWithMedia = messagesWithMedia;
            this.messagesWithLocation = messagesWithLocation;
            this.messagesWithContact = messagesWithContact;
            this.storyMessages = storyMessages;
            this.memoryMessages = memoryMessages;
            this.eventMessages = eventMessages;
        }

        // Getters and Setters
        public long getTotalMessages() { return totalMessages; }
        public void setTotalMessages(long totalMessages) { this.totalMessages = totalMessages; }

        public long getTextMessages() { return textMessages; }
        public void setTextMessages(long textMessages) { this.textMessages = textMessages; }

        public long getMediaMessages() { return mediaMessages; }
        public void setMediaMessages(long mediaMessages) { this.mediaMessages = mediaMessages; }

        public long getVoiceMessages() { return voiceMessages; }
        public void setVoiceMessages(long voiceMessages) { this.voiceMessages = voiceMessages; }

        public long getStarredMessages() { return starredMessages; }
        public void setStarredMessages(long starredMessages) { this.starredMessages = starredMessages; }

        public long getProtectedMessages() { return protectedMessages; }
        public void setProtectedMessages(long protectedMessages) { this.protectedMessages = protectedMessages; }

        public long getMessagesWithToneColor() { return messagesWithToneColor; }
        public void setMessagesWithToneColor(long messagesWithToneColor) { this.messagesWithToneColor = messagesWithToneColor; }

        public long getMessagesWithAIFeatures() { return messagesWithAIFeatures; }
        public void setMessagesWithAIFeatures(long messagesWithAIFeatures) { this.messagesWithAIFeatures = messagesWithAIFeatures; }

        public long getMessagesWithMedia() { return messagesWithMedia; }
        public void setMessagesWithMedia(long messagesWithMedia) { this.messagesWithMedia = messagesWithMedia; }

        public long getMessagesWithLocation() { return messagesWithLocation; }
        public void setMessagesWithLocation(long messagesWithLocation) { this.messagesWithLocation = messagesWithLocation; }

        public long getMessagesWithContact() { return messagesWithContact; }
        public void setMessagesWithContact(long messagesWithContact) { this.messagesWithContact = messagesWithContact; }

        public long getStoryMessages() { return storyMessages; }
        public void setStoryMessages(long storyMessages) { this.storyMessages = storyMessages; }

        public long getMemoryMessages() { return memoryMessages; }
        public void setMemoryMessages(long memoryMessages) { this.memoryMessages = memoryMessages; }

        public long getEventMessages() { return eventMessages; }
        public void setEventMessages(long eventMessages) { this.eventMessages = eventMessages; }
    }

    /**
     * Check if a message would be filtered for a specific user
     */
    boolean wouldMessageBeFiltered(Long senderUserId, Long receiverUserId, Long roomId, String content);

    /**
     * Send a message with content filtering applied
     * Returns null if message is filtered, otherwise returns the sent message
     */
    Message sendMessageWithFiltering(SendMessageRequest request);

    // ==================== SEARCH METHODS ====================

    /**
     * Search messages with full-text search
     */
    List<Message> searchMessages(String query, Long userId);

    /**
     * Search messages with full-text search and pagination
     */
    Page<Message> searchMessages(String query, Long userId, Pageable pageable);

    /**
     * Search messages with advanced filters
     */
    List<Message> searchMessagesWithFilters(String query, Long userId, Long chatRoomId, List<Long> chatRoomIds, 
                                          Long senderUserId, LocalDateTime startDate, LocalDateTime endDate, 
                                          Boolean isStarred, Boolean isEncrypted, Boolean includeDeleted);

    /**
     * Search messages with advanced filters and pagination
     */
    Page<Message> searchMessagesWithFilters(String query, Long userId, Long chatRoomId, List<Long> chatRoomIds, 
                                          Long senderUserId, LocalDateTime startDate, LocalDateTime endDate, 
                                          Boolean isStarred, Boolean isEncrypted, Boolean includeDeleted, Pageable pageable);

    /**
     * Search messages in specific chat room
     */
    List<Message> searchMessagesInRoom(String query, Long userId, Long chatRoomId);

    /**
     * Search messages in specific chat room with pagination
     */
    Page<Message> searchMessagesInRoom(String query, Long userId, Long chatRoomId, Pageable pageable);

    /**
     * Search messages by specific sender
     */
    List<Message> searchMessagesBySender(String query, Long userId, Long senderUserId);

    /**
     * Search messages by specific sender with pagination
     */
    Page<Message> searchMessagesBySender(String query, Long userId, Long senderUserId, Pageable pageable);

    /**
     * Get search suggestions based on query
     */
    List<String> getSearchSuggestions(String query, Long userId);

    /**
     * Get popular search terms
     */
    List<String> getPopularSearchTerms(Long userId);

    // ==================== THREADING METHODS ====================

    /**
     * Get all replies to a specific message
     */
    List<Message> getRepliesToMessage(String messageId);

    /**
     * Get all replies to a specific message with pagination
     */
    Page<Message> getRepliesToMessage(String messageId, Pageable pageable);

    /**
     * Get all messages in a thread (original message + all replies)
     */
    List<Message> getThreadMessages(String messageId);

    /**
     * Get all messages in a thread with pagination
     */
    Page<Message> getThreadMessages(String messageId, Pageable pageable);

    /**
     * Get thread root messages (messages that are not replies)
     */
    List<Message> getThreadRootMessages(Long chatRoomId);

    /**
     * Get thread root messages with pagination
     */
    Page<Message> getThreadRootMessages(Long chatRoomId, Pageable pageable);

    /**
     * Count replies to a specific message
     */
    long countRepliesToMessage(String messageId);

    /**
     * Get latest reply in a thread
     */
    Message getLatestReplyInThread(String messageId);

    /**
     * Get thread summary with reply count
     */
    ThreadSummary getThreadSummary(String messageId);

    // ==================== EDIT HISTORY METHODS ====================

    /**
     * Edit a message with history tracking
     */
    Message editMessageWithHistory(EditMessageWithHistoryRequest request);

    /**
     * Get edit history for a message
     */
    List<MessageEditHistory> getMessageEditHistory(String messageId);

    /**
     * Get edit history for a message with pagination
     */
    Page<MessageEditHistory> getMessageEditHistory(String messageId, Pageable pageable);

    /**
     * Get specific version of a message
     */
    MessageEditHistory getMessageVersion(String messageId, Integer version);

    /**
     * Get current version of a message
     */
    MessageEditHistory getCurrentMessageVersion(String messageId);

    /**
     * Revert message to a specific version
     */
    Message revertMessageToVersion(String messageId, Integer version, Long userId, String reason);

    /**
     * Get edit history by user
     */
    List<MessageEditHistory> getEditHistoryByUser(Long userId);

    /**
     * Get edit history by user with pagination
     */
    Page<MessageEditHistory> getEditHistoryByUser(Long userId, Pageable pageable);

    /**
     * Get edit history within date range
     */
    List<MessageEditHistory> getEditHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get edit history within date range with pagination
     */
    Page<MessageEditHistory> getEditHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Count edit history for a message
     */
    long countMessageEditHistory(String messageId);

    /**
     * Get latest edit for a message
     */
    MessageEditHistory getLatestEditForMessage(String messageId);

    // ==================== ENHANCED DELETION METHODS ====================

    /**
     * Delete message with enhanced options
     */
    void deleteMessageWithOptions(DeleteMessageRequest request);

    /**
     * Bulk delete messages
     */
    void bulkDeleteMessages(List<String> messageIds, Long userId, Boolean deleteForEveryone);

    /**
     * Delete all messages in a chat room
     */
    void deleteAllMessagesInRoom(Long chatRoomId, Long userId, Boolean deleteForEveryone);

    /**
     * Delete messages by date range
     */
    void deleteMessagesByDateRange(Long chatRoomId, LocalDateTime startDate, LocalDateTime endDate, Long userId, Boolean deleteForEveryone);

    /**
     * Delete messages by user
     */
    void deleteMessagesByUser(Long chatRoomId, Long targetUserId, Long deletedByUserId, Boolean deleteForEveryone);

    /**
     * Permanently delete message (hard delete)
     */
    void permanentlyDeleteMessage(String messageId, Long userId);

    /**
     * Restore deleted message
     */
    Message restoreDeletedMessage(String messageId, Long userId);

    /**
     * Get deleted messages for a user
     */
    List<Message> getDeletedMessages(Long userId, Long chatRoomId);

    /**
     * Get deleted messages with pagination
     */
    Page<Message> getDeletedMessages(Long userId, Long chatRoomId, Pageable pageable);

    /**
     * Cleanup old deleted messages
     */
    void cleanupOldDeletedMessages(int daysOld);

    // ==================== MESSAGE SCHEDULING METHODS ====================

    /**
     * Schedule a message for future delivery
     */
    ScheduledMessage scheduleMessage(ScheduleMessageRequest request);

    /**
     * Get scheduled messages for a user
     */
    List<ScheduledMessage> getScheduledMessagesByUser(Long userId);

    /**
     * Get scheduled messages for a user with pagination
     */
    Page<ScheduledMessage> getScheduledMessagesByUser(Long userId, Pageable pageable);

    /**
     * Get scheduled messages for a chat room
     */
    List<ScheduledMessage> getScheduledMessagesByRoom(Long chatRoomId);

    /**
     * Get scheduled messages for a chat room with pagination
     */
    Page<ScheduledMessage> getScheduledMessagesByRoom(Long chatRoomId, Pageable pageable);

    /**
     * Get scheduled messages by status
     */
    List<ScheduledMessage> getScheduledMessagesByStatus(ScheduledMessage.ScheduledStatus status);

    /**
     * Get scheduled messages by status with pagination
     */
    Page<ScheduledMessage> getScheduledMessagesByStatus(ScheduledMessage.ScheduledStatus status, Pageable pageable);

    /**
     * Cancel a scheduled message
     */
    void cancelScheduledMessage(String scheduledMessageId, Long userId);

    /**
     * Update a scheduled message
     */
    ScheduledMessage updateScheduledMessage(String scheduledMessageId, ScheduleMessageRequest request);

    /**
     * Get scheduled message by ID
     */
    ScheduledMessage getScheduledMessageById(String scheduledMessageId);

    /**
     * Process scheduled messages (called by scheduler)
     */
    void processScheduledMessages();

    /**
     * Retry failed scheduled messages
     */
    void retryFailedScheduledMessages();

    /**
     * Cleanup expired scheduled messages
     */
    void cleanupExpiredScheduledMessages();

    /**
     * Count scheduled messages by user
     */
    long countScheduledMessagesByUser(Long userId);

    /**
     * Count scheduled messages by chat room
     */
    long countScheduledMessagesByRoom(Long chatRoomId);

    /**
     * Count scheduled messages by status
     */
    long countScheduledMessagesByStatus(ScheduledMessage.ScheduledStatus status);
}
