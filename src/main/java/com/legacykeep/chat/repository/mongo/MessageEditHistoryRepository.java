package com.legacykeep.chat.repository.mongo;

import com.legacykeep.chat.entity.MessageEditHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for MessageEditHistory entity (MongoDB).
 * Provides data access methods for message edit history.
 */
@Repository
public interface MessageEditHistoryRepository extends MongoRepository<MessageEditHistory, String> {

    /**
     * Find edit history for a specific message
     */
    List<MessageEditHistory> findByMessageIdOrderByVersionDesc(String messageId);

    /**
     * Find edit history for a specific message with pagination
     */
    Page<MessageEditHistory> findByMessageIdOrderByVersionDesc(String messageId, Pageable pageable);

    /**
     * Find current version of a message
     */
    @Query("{ 'messageId': ?0, 'isCurrentVersion': true }")
    Optional<MessageEditHistory> findCurrentVersion(String messageId);

    /**
     * Find specific version of a message
     */
    @Query("{ 'messageId': ?0, 'version': ?1 }")
    Optional<MessageEditHistory> findByMessageIdAndVersion(String messageId, Integer version);

    /**
     * Find edit history by user
     */
    List<MessageEditHistory> findByEditedByUserIdOrderByEditTimestampDesc(Long editedByUserId);

    /**
     * Find edit history by user with pagination
     */
    Page<MessageEditHistory> findByEditedByUserIdOrderByEditTimestampDesc(Long editedByUserId, Pageable pageable);

    /**
     * Find edit history within date range
     */
    @Query("{ 'editTimestamp': { $gte: ?0, $lte: ?1 } }")
    List<MessageEditHistory> findByEditTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find edit history within date range with pagination
     */
    @Query("{ 'editTimestamp': { $gte: ?0, $lte: ?1 } }")
    Page<MessageEditHistory> findByEditTimestampBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find edit history by edit type
     */
    List<MessageEditHistory> findByEditTypeOrderByEditTimestampDesc(MessageEditHistory.EditType editType);

    /**
     * Find edit history by edit type with pagination
     */
    Page<MessageEditHistory> findByEditTypeOrderByEditTimestampDesc(MessageEditHistory.EditType editType, Pageable pageable);

    /**
     * Count edit history for a message
     */
    long countByMessageId(String messageId);

    /**
     * Find latest edit for a message
     */
    @Query("{ 'messageId': ?0 }")
    MessageEditHistory findLatestEditForMessage(String messageId);

    /**
     * Find edit history for multiple messages
     */
    @Query("{ 'messageId': { $in: ?0 } }")
    List<MessageEditHistory> findByMessageIdIn(List<String> messageIds);

    /**
     * Find edit history for multiple messages with pagination
     */
    @Query("{ 'messageId': { $in: ?0 } }")
    Page<MessageEditHistory> findByMessageIdIn(List<String> messageIds, Pageable pageable);
}
