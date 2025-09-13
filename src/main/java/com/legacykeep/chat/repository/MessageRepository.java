package com.legacykeep.chat.repository;

import com.legacykeep.chat.entity.Message;
import com.legacykeep.chat.enums.MessageStatus;
import com.legacykeep.chat.enums.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Message entity (MongoDB).
 * Provides data access methods for messages.
 */
@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    /**
     * Find message by UUID
     */
    Optional<Message> findByMessageUuid(String messageUuid);

    /**
     * Find messages by chat room ID
     */
    List<Message> findByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);

    /**
     * Find messages by chat room ID with pagination
     */
    Page<Message> findByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId, Pageable pageable);

    /**
     * Find messages by sender user ID
     */
    List<Message> findBySenderUserIdOrderByCreatedAtDesc(Long senderUserId);

    /**
     * Find messages by sender user ID with pagination
     */
    Page<Message> findBySenderUserIdOrderByCreatedAtDesc(Long senderUserId, Pageable pageable);

    /**
     * Find messages by chat room ID and sender user ID
     */
    List<Message> findByChatRoomIdAndSenderUserIdOrderByCreatedAtDesc(Long chatRoomId, Long senderUserId);

    /**
     * Find messages by chat room ID and sender user ID with pagination
     */
    Page<Message> findByChatRoomIdAndSenderUserIdOrderByCreatedAtDesc(Long chatRoomId, Long senderUserId, Pageable pageable);

    /**
     * Find messages by message type
     */
    List<Message> findByMessageTypeOrderByCreatedAtDesc(MessageType messageType);

    /**
     * Find messages by message type with pagination
     */
    Page<Message> findByMessageTypeOrderByCreatedAtDesc(MessageType messageType, Pageable pageable);

    /**
     * Find messages by status
     */
    List<Message> findByStatusOrderByCreatedAtDesc(MessageStatus status);

    /**
     * Find messages by status with pagination
     */
    Page<Message> findByStatusOrderByCreatedAtDesc(MessageStatus status, Pageable pageable);

    /**
     * Find messages by chat room ID and message type
     */
    List<Message> findByChatRoomIdAndMessageTypeOrderByCreatedAtDesc(Long chatRoomId, MessageType messageType);

    /**
     * Find messages by chat room ID and message type with pagination
     */
    Page<Message> findByChatRoomIdAndMessageTypeOrderByCreatedAtDesc(Long chatRoomId, MessageType messageType, Pageable pageable);

    /**
     * Find messages by chat room ID and status
     */
    List<Message> findByChatRoomIdAndStatusOrderByCreatedAtDesc(Long chatRoomId, MessageStatus status);

    /**
     * Find messages by chat room ID and status with pagination
     */
    Page<Message> findByChatRoomIdAndStatusOrderByCreatedAtDesc(Long chatRoomId, MessageStatus status, Pageable pageable);

    /**
     * Find starred messages
     */
    List<Message> findByIsStarredTrueOrderByCreatedAtDesc();

    /**
     * Find starred messages with pagination
     */
    Page<Message> findByIsStarredTrueOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find starred messages for a specific user
     */
    @Query("{ 'isStarred': true, 'senderUserId': ?0 }")
    List<Message> findStarredMessagesByUser(Long senderUserId);

    /**
     * Find starred messages for a specific user with pagination
     */
    @Query("{ 'isStarred': true, 'senderUserId': ?0 }")
    Page<Message> findStarredMessagesByUser(Long senderUserId, Pageable pageable);

    /**
     * Find starred messages in a specific chat room
     */
    @Query("{ 'isStarred': true, 'chatRoomId': ?0 }")
    List<Message> findStarredMessagesInRoom(Long chatRoomId);

    /**
     * Find starred messages in a specific chat room with pagination
     */
    @Query("{ 'isStarred': true, 'chatRoomId': ?0 }")
    Page<Message> findStarredMessagesInRoom(Long chatRoomId, Pageable pageable);

    /**
     * Find encrypted messages
     */
    List<Message> findByIsEncryptedTrueOrderByCreatedAtDesc();

    /**
     * Find protected messages
     */
    List<Message> findByIsProtectedTrueOrderByCreatedAtDesc();

    /**
     * Find protected messages with pagination
     */
    Page<Message> findByIsProtectedTrueOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find protected messages in a specific chat room
     */
    @Query("{ 'isProtected': true, 'chatRoomId': ?0 }")
    List<Message> findProtectedMessagesInRoom(Long chatRoomId);

    /**
     * Find protected messages in a specific chat room with pagination
     */
    @Query("{ 'isProtected': true, 'chatRoomId': ?0 }")
    Page<Message> findProtectedMessagesInRoom(Long chatRoomId, Pageable pageable);

    /**
     * Find messages with tone color
     */
    @Query("{ 'toneColor': { $exists: true, $ne: null } }")
    List<Message> findMessagesWithToneColor();

    /**
     * Find messages with tone color with pagination
     */
    @Query("{ 'toneColor': { $exists: true, $ne: null } }")
    Page<Message> findMessagesWithToneColor(Pageable pageable);

    /**
     * Find messages with tone color in a specific chat room
     */
    @Query("{ 'toneColor': { $exists: true, $ne: null }, 'chatRoomId': ?0 }")
    List<Message> findMessagesWithToneColorInRoom(Long chatRoomId);

    /**
     * Find messages with tone color in a specific chat room with pagination
     */
    @Query("{ 'toneColor': { $exists: true, $ne: null }, 'chatRoomId': ?0 }")
    Page<Message> findMessagesWithToneColorInRoom(Long chatRoomId, Pageable pageable);

    /**
     * Find messages with AI features
     */
    @Query("{ $or: [ { 'voiceEmotion': { $exists: true, $ne: null } }, { 'memoryTriggers': { $exists: true, $ne: [] } }, { 'predictiveText': { $exists: true, $ne: null } }, { 'aiToneSuggestion': { $exists: true, $ne: null } } ] }")
    List<Message> findMessagesWithAIFeatures();

    /**
     * Find messages with AI features with pagination
     */
    @Query("{ $or: [ { 'voiceEmotion': { $exists: true, $ne: null } }, { 'memoryTriggers': { $exists: true, $ne: [] } }, { 'predictiveText': { $exists: true, $ne: null } }, { 'aiToneSuggestion': { $exists: true, $ne: null } } ] }")
    Page<Message> findMessagesWithAIFeatures(Pageable pageable);

    /**
     * Find messages with media
     */
    @Query("{ 'mediaUrl': { $exists: true, $ne: null } }")
    List<Message> findMessagesWithMedia();

    /**
     * Find messages with media with pagination
     */
    @Query("{ 'mediaUrl': { $exists: true, $ne: null } }")
    Page<Message> findMessagesWithMedia(Pageable pageable);

    /**
     * Find messages with media in a specific chat room
     */
    @Query("{ 'mediaUrl': { $exists: true, $ne: null }, 'chatRoomId': ?0 }")
    List<Message> findMessagesWithMediaInRoom(Long chatRoomId);

    /**
     * Find messages with media in a specific chat room with pagination
     */
    @Query("{ 'mediaUrl': { $exists: true, $ne: null }, 'chatRoomId': ?0 }")
    Page<Message> findMessagesWithMediaInRoom(Long chatRoomId, Pageable pageable);

    /**
     * Find messages with location
     */
    @Query("{ 'locationLatitude': { $exists: true, $ne: null }, 'locationLongitude': { $exists: true, $ne: null } }")
    List<Message> findMessagesWithLocation();

    /**
     * Find messages with location with pagination
     */
    @Query("{ 'locationLatitude': { $exists: true, $ne: null }, 'locationLongitude': { $exists: true, $ne: null } }")
    Page<Message> findMessagesWithLocation(Pageable pageable);

    /**
     * Find messages with contact information
     */
    @Query("{ 'contactName': { $exists: true, $ne: null } }")
    List<Message> findMessagesWithContact();

    /**
     * Find messages with contact information with pagination
     */
    @Query("{ 'contactName': { $exists: true, $ne: null } }")
    Page<Message> findMessagesWithContact(Pageable pageable);

    /**
     * Find messages associated with a story
     */
    List<Message> findByStoryIdOrderByCreatedAtDesc(Long storyId);

    /**
     * Find messages associated with a story with pagination
     */
    Page<Message> findByStoryIdOrderByCreatedAtDesc(Long storyId, Pageable pageable);

    /**
     * Find messages associated with a memory
     */
    List<Message> findByMemoryIdOrderByCreatedAtDesc(Long memoryId);

    /**
     * Find messages associated with a memory with pagination
     */
    Page<Message> findByMemoryIdOrderByCreatedAtDesc(Long memoryId, Pageable pageable);

    /**
     * Find messages associated with an event
     */
    List<Message> findByEventIdOrderByCreatedAtDesc(Long eventId);

    /**
     * Find messages associated with an event with pagination
     */
    Page<Message> findByEventIdOrderByCreatedAtDesc(Long eventId, Pageable pageable);

    /**
     * Find messages created after a specific date
     */
    List<Message> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime createdAt);

    /**
     * Find messages created after a specific date with pagination
     */
    Page<Message> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime createdAt, Pageable pageable);

    /**
     * Find messages created between two dates
     */
    @Query("{ 'createdAt': { $gte: ?0, $lte: ?1 } }")
    List<Message> findMessagesBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find messages created between two dates with pagination
     */
    @Query("{ 'createdAt': { $gte: ?0, $lte: ?1 } }")
    Page<Message> findMessagesBetweenDates(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find messages in a chat room created between two dates
     */
    @Query("{ 'chatRoomId': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    List<Message> findMessagesInRoomBetweenDates(Long chatRoomId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find messages in a chat room created between two dates with pagination
     */
    @Query("{ 'chatRoomId': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    Page<Message> findMessagesInRoomBetweenDates(Long chatRoomId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find messages that have expired (self-destructed)
     */
    @Query("{ 'selfDestructAt': { $exists: true, $ne: null, $lt: ?0 } }")
    List<Message> findExpiredMessages(LocalDateTime now);

    /**
     * Find messages that will expire soon
     */
    @Query("{ 'selfDestructAt': { $exists: true, $ne: null, $gte: ?0, $lte: ?1 } }")
    List<Message> findMessagesExpiringSoon(LocalDateTime now, LocalDateTime soon);

    /**
     * Find messages that have reached view limit
     */
    @Query("{ 'maxViews': { $exists: true, $ne: null }, 'viewCount': { $gte: '$maxViews' } }")
    List<Message> findMessagesAtViewLimit();

    /**
     * Count messages in a chat room
     */
    long countByChatRoomId(Long chatRoomId);

    /**
     * Count messages by sender user ID
     */
    long countBySenderUserId(Long senderUserId);

    /**
     * Count messages by message type
     */
    long countByMessageType(MessageType messageType);

    /**
     * Count messages by status
     */
    long countByStatus(MessageStatus status);

    /**
     * Count starred messages
     */
    @Query(value = "{ 'isStarred': true }", count = true)
    long countStarredMessages();

    /**
     * Count starred messages for a specific user
     */
    @Query(value = "{ 'isStarred': true, 'senderUserId': ?0 }", count = true)
    long countStarredMessagesByUser(Long senderUserId);

    /**
     * Count starred messages in a specific chat room
     */
    @Query(value = "{ 'isStarred': true, 'chatRoomId': ?0 }", count = true)
    long countStarredMessagesInRoom(Long chatRoomId);

    /**
     * Count protected messages
     */
    @Query(value = "{ 'isProtected': true }", count = true)
    long countProtectedMessages();

    /**
     * Count protected messages in a specific chat room
     */
    @Query(value = "{ 'isProtected': true, 'chatRoomId': ?0 }", count = true)
    long countProtectedMessagesInRoom(Long chatRoomId);

    /**
     * Count messages with media
     */
    @Query(value = "{ 'mediaUrl': { $exists: true, $ne: null } }", count = true)
    long countMessagesWithMedia();

    /**
     * Count messages with media in a specific chat room
     */
    @Query(value = "{ 'mediaUrl': { $exists: true, $ne: null }, 'chatRoomId': ?0 }", count = true)
    long countMessagesWithMediaInRoom(Long chatRoomId);

    /**
     * Find the latest message in a chat room
     */
    @Query(value = "{ 'chatRoomId': ?0 }", sort = "{ 'createdAt': -1 }")
    Optional<Message> findLatestMessageInRoom(Long chatRoomId);

    /**
     * Find messages before a specific message
     */
    @Query("{ 'chatRoomId': ?0, 'createdAt': { $lt: ?1 } }")
    List<Message> findMessagesBefore(Long chatRoomId, LocalDateTime before);

    /**
     * Find messages before a specific message with pagination
     */
    @Query("{ 'chatRoomId': ?0, 'createdAt': { $lt: ?1 } }")
    Page<Message> findMessagesBefore(Long chatRoomId, LocalDateTime before, Pageable pageable);

    /**
     * Find messages after a specific message
     */
    @Query("{ 'chatRoomId': ?0, 'createdAt': { $gt: ?1 } }")
    List<Message> findMessagesAfter(Long chatRoomId, LocalDateTime after);

    /**
     * Find messages after a specific message with pagination
     */
    @Query("{ 'chatRoomId': ?0, 'createdAt': { $gt: ?1 } }")
    Page<Message> findMessagesAfter(Long chatRoomId, LocalDateTime after, Pageable pageable);
}
