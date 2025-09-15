package com.legacykeep.chat.repository.mongo;

import com.legacykeep.chat.entity.ScheduledMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ScheduledMessage entity (MongoDB).
 * Provides data access methods for scheduled messages.
 */
@Repository
public interface ScheduledMessageRepository extends MongoRepository<ScheduledMessage, String> {

    /**
     * Find scheduled messages by status
     */
    List<ScheduledMessage> findByStatusOrderByScheduledForAsc(ScheduledMessage.ScheduledStatus status);

    /**
     * Find scheduled messages by status with pagination
     */
    Page<ScheduledMessage> findByStatusOrderByScheduledForAsc(ScheduledMessage.ScheduledStatus status, Pageable pageable);

    /**
     * Find scheduled messages ready for execution
     */
    @Query("{ 'status': 'PENDING', 'scheduledFor': { $lte: ?0 } }")
    List<ScheduledMessage> findMessagesReadyForExecution(LocalDateTime now);

    /**
     * Find scheduled messages by user
     */
    List<ScheduledMessage> findBySenderUserIdOrderByScheduledForDesc(Long senderUserId);

    /**
     * Find scheduled messages by user with pagination
     */
    Page<ScheduledMessage> findBySenderUserIdOrderByScheduledForDesc(Long senderUserId, Pageable pageable);

    /**
     * Find scheduled messages by chat room
     */
    List<ScheduledMessage> findByChatRoomIdOrderByScheduledForDesc(Long chatRoomId);

    /**
     * Find scheduled messages by chat room with pagination
     */
    Page<ScheduledMessage> findByChatRoomIdOrderByScheduledForDesc(Long chatRoomId, Pageable pageable);

    /**
     * Find scheduled messages by user and chat room
     */
    List<ScheduledMessage> findBySenderUserIdAndChatRoomIdOrderByScheduledForDesc(Long senderUserId, Long chatRoomId);

    /**
     * Find scheduled messages by user and chat room with pagination
     */
    Page<ScheduledMessage> findBySenderUserIdAndChatRoomIdOrderByScheduledForDesc(Long senderUserId, Long chatRoomId, Pageable pageable);

    /**
     * Find scheduled messages by date range
     */
    @Query("{ 'scheduledFor': { $gte: ?0, $lte: ?1 } }")
    List<ScheduledMessage> findByScheduledForBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find scheduled messages by date range with pagination
     */
    @Query("{ 'scheduledFor': { $gte: ?0, $lte: ?1 } }")
    Page<ScheduledMessage> findByScheduledForBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find recurring messages
     */
    List<ScheduledMessage> findByIsRecurringTrueAndStatusOrderByNextExecutionAsc(ScheduledMessage.ScheduledStatus status);

    /**
     * Find expired scheduled messages
     */
    @Query("{ 'status': 'PENDING', 'scheduledFor': { $lt: ?0 } }")
    List<ScheduledMessage> findExpiredMessages(LocalDateTime now);

    /**
     * Find failed messages that can be retried
     */
    @Query("{ 'status': 'FAILED', 'retryCount': { $lt: '$maxRetries' } }")
    List<ScheduledMessage> findFailedMessagesForRetry();

    /**
     * Count scheduled messages by status
     */
    long countByStatus(ScheduledMessage.ScheduledStatus status);

    /**
     * Count scheduled messages by user
     */
    long countBySenderUserId(Long senderUserId);

    /**
     * Count scheduled messages by chat room
     */
    long countByChatRoomId(Long chatRoomId);

    /**
     * Find scheduled message by UUID
     */
    Optional<ScheduledMessage> findByMessageUuid(String messageUuid);

    /**
     * Delete scheduled messages by status
     */
    void deleteByStatus(ScheduledMessage.ScheduledStatus status);

    /**
     * Delete scheduled messages by user
     */
    void deleteBySenderUserId(Long senderUserId);

    /**
     * Delete scheduled messages by chat room
     */
    void deleteByChatRoomId(Long chatRoomId);
}
