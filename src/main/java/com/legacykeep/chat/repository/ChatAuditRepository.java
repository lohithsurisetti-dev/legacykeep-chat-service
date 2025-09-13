package com.legacykeep.chat.repository;

import com.legacykeep.chat.entity.ChatAudit;
import com.legacykeep.chat.enums.AuditAction;
import com.legacykeep.chat.enums.AuditEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for ChatAudit entity.
 * Provides data access methods for chat audit logs.
 */
@Repository
public interface ChatAuditRepository extends JpaRepository<ChatAudit, Long> {

    /**
     * Find audit logs by user ID
     */
    List<ChatAudit> findByUserId(Long userId);

    /**
     * Find audit logs by user ID with pagination
     */
    Page<ChatAudit> findByUserId(Long userId, Pageable pageable);

    /**
     * Find audit logs by chat room ID
     */
    List<ChatAudit> findByChatRoomId(Long chatRoomId);

    /**
     * Find audit logs by chat room ID with pagination
     */
    Page<ChatAudit> findByChatRoomId(Long chatRoomId, Pageable pageable);

    /**
     * Find audit logs by message ID
     */
    List<ChatAudit> findByMessageId(String messageId);

    /**
     * Find audit logs by message ID with pagination
     */
    Page<ChatAudit> findByMessageId(String messageId, Pageable pageable);

    /**
     * Find audit logs by entity type
     */
    List<ChatAudit> findByEntityType(AuditEntity entityType);

    /**
     * Find audit logs by entity type with pagination
     */
    Page<ChatAudit> findByEntityType(AuditEntity entityType, Pageable pageable);

    /**
     * Find audit logs by action
     */
    List<ChatAudit> findByAction(AuditAction action);

    /**
     * Find audit logs by action with pagination
     */
    Page<ChatAudit> findByAction(AuditAction action, Pageable pageable);

    /**
     * Find audit logs by entity type and action
     */
    List<ChatAudit> findByEntityTypeAndAction(AuditEntity entityType, AuditAction action);

    /**
     * Find audit logs by entity type and action with pagination
     */
    Page<ChatAudit> findByEntityTypeAndAction(AuditEntity entityType, AuditAction action, Pageable pageable);

    /**
     * Find audit logs by user ID and entity type
     */
    List<ChatAudit> findByUserIdAndEntityType(Long userId, AuditEntity entityType);

    /**
     * Find audit logs by user ID and entity type with pagination
     */
    Page<ChatAudit> findByUserIdAndEntityType(Long userId, AuditEntity entityType, Pageable pageable);

    /**
     * Find audit logs by user ID and action
     */
    List<ChatAudit> findByUserIdAndAction(Long userId, AuditAction action);

    /**
     * Find audit logs by user ID and action with pagination
     */
    Page<ChatAudit> findByUserIdAndAction(Long userId, AuditAction action, Pageable pageable);

    /**
     * Find audit logs by chat room ID and entity type
     */
    List<ChatAudit> findByChatRoomIdAndEntityType(Long chatRoomId, AuditEntity entityType);

    /**
     * Find audit logs by chat room ID and entity type with pagination
     */
    Page<ChatAudit> findByChatRoomIdAndEntityType(Long chatRoomId, AuditEntity entityType, Pageable pageable);

    /**
     * Find audit logs by chat room ID and action
     */
    List<ChatAudit> findByChatRoomIdAndAction(Long chatRoomId, AuditAction action);

    /**
     * Find audit logs by chat room ID and action with pagination
     */
    Page<ChatAudit> findByChatRoomIdAndAction(Long chatRoomId, AuditAction action, Pageable pageable);

    /**
     * Find successful audit logs
     */
    List<ChatAudit> findByIsSuccessfulTrue();

    /**
     * Find successful audit logs with pagination
     */
    Page<ChatAudit> findByIsSuccessfulTrue(Pageable pageable);

    /**
     * Find failed audit logs
     */
    List<ChatAudit> findByIsSuccessfulFalse();

    /**
     * Find failed audit logs with pagination
     */
    Page<ChatAudit> findByIsSuccessfulFalse(Pageable pageable);

    /**
     * Find audit logs with errors
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.isSuccessful = false OR ca.errorMessage IS NOT NULL")
    List<ChatAudit> findAuditLogsWithErrors();

    /**
     * Find audit logs with errors with pagination
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.isSuccessful = false OR ca.errorMessage IS NOT NULL")
    Page<ChatAudit> findAuditLogsWithErrors(Pageable pageable);

    /**
     * Find audit logs by IP address
     */
    List<ChatAudit> findByIpAddress(String ipAddress);

    /**
     * Find audit logs by IP address with pagination
     */
    Page<ChatAudit> findByIpAddress(String ipAddress, Pageable pageable);

    /**
     * Find audit logs by session ID
     */
    List<ChatAudit> findBySessionId(String sessionId);

    /**
     * Find audit logs by session ID with pagination
     */
    Page<ChatAudit> findBySessionId(String sessionId, Pageable pageable);

    /**
     * Find audit logs by request ID
     */
    List<ChatAudit> findByRequestId(String requestId);

    /**
     * Find audit logs by request ID with pagination
     */
    Page<ChatAudit> findByRequestId(String requestId, Pageable pageable);

    /**
     * Find audit logs by family ID
     */
    List<ChatAudit> findByFamilyId(Long familyId);

    /**
     * Find audit logs by family ID with pagination
     */
    Page<ChatAudit> findByFamilyId(Long familyId, Pageable pageable);

    /**
     * Find audit logs by story ID
     */
    List<ChatAudit> findByStoryId(Long storyId);

    /**
     * Find audit logs by story ID with pagination
     */
    Page<ChatAudit> findByStoryId(Long storyId, Pageable pageable);

    /**
     * Find audit logs by event ID
     */
    List<ChatAudit> findByEventId(Long eventId);

    /**
     * Find audit logs by event ID with pagination
     */
    Page<ChatAudit> findByEventId(Long eventId, Pageable pageable);

    /**
     * Find audit logs with recent activity
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.createdAt >= :since")
    List<ChatAudit> findAuditLogsWithRecentActivity(@Param("since") LocalDateTime since);

    /**
     * Find audit logs with recent activity with pagination
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.createdAt >= :since")
    Page<ChatAudit> findAuditLogsWithRecentActivity(@Param("since") LocalDateTime since, Pageable pageable);

    /**
     * Find audit logs with recent activity for a specific user
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.userId = :userId AND ca.createdAt >= :since")
    List<ChatAudit> findRecentAuditLogsForUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    /**
     * Find audit logs with recent activity for a specific user with pagination
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.userId = :userId AND ca.createdAt >= :since")
    Page<ChatAudit> findRecentAuditLogsForUser(@Param("userId") Long userId, @Param("since") LocalDateTime since, Pageable pageable);

    /**
     * Find audit logs with recent activity for a specific chat room
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.chatRoomId = :chatRoomId AND ca.createdAt >= :since")
    List<ChatAudit> findRecentAuditLogsForChatRoom(@Param("chatRoomId") Long chatRoomId, @Param("since") LocalDateTime since);

    /**
     * Find audit logs with recent activity for a specific chat room with pagination
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.chatRoomId = :chatRoomId AND ca.createdAt >= :since")
    Page<ChatAudit> findRecentAuditLogsForChatRoom(@Param("chatRoomId") Long chatRoomId, @Param("since") LocalDateTime since, Pageable pageable);

    /**
     * Find security-related audit logs
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.entityType = 'SECURITY' OR ca.action IN ('LOGIN', 'LOGOUT', 'AUTHENTICATE', 'AUTHORIZE', 'ACCESS_DENIED', 'SUSPICIOUS_ACTIVITY')")
    List<ChatAudit> findSecurityAuditLogs();

    /**
     * Find security-related audit logs with pagination
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.entityType = 'SECURITY' OR ca.action IN ('LOGIN', 'LOGOUT', 'AUTHENTICATE', 'AUTHORIZE', 'ACCESS_DENIED', 'SUSPICIOUS_ACTIVITY')")
    Page<ChatAudit> findSecurityAuditLogs(Pageable pageable);

    /**
     * Find system-related audit logs
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.entityType = 'SYSTEM' OR ca.action IN ('SYSTEM_START', 'SYSTEM_STOP', 'SYSTEM_ERROR', 'SYSTEM_WARNING')")
    List<ChatAudit> findSystemAuditLogs();

    /**
     * Find system-related audit logs with pagination
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.entityType = 'SYSTEM' OR ca.action IN ('SYSTEM_START', 'SYSTEM_STOP', 'SYSTEM_ERROR', 'SYSTEM_WARNING')")
    Page<ChatAudit> findSystemAuditLogs(Pageable pageable);

    /**
     * Find message-related audit logs
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.entityType = 'MESSAGE' OR ca.action IN ('SEND_MESSAGE', 'RECEIVE_MESSAGE', 'READ_MESSAGE', 'EDIT_MESSAGE', 'DELETE_MESSAGE', 'FORWARD_MESSAGE', 'STAR_MESSAGE', 'UNSTAR_MESSAGE')")
    List<ChatAudit> findMessageAuditLogs();

    /**
     * Find message-related audit logs with pagination
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.entityType = 'MESSAGE' OR ca.action IN ('SEND_MESSAGE', 'RECEIVE_MESSAGE', 'READ_MESSAGE', 'EDIT_MESSAGE', 'DELETE_MESSAGE', 'FORWARD_MESSAGE', 'STAR_MESSAGE', 'UNSTAR_MESSAGE')")
    Page<ChatAudit> findMessageAuditLogs(Pageable pageable);

    /**
     * Find audit logs by execution time range
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.executionTimeMs >= :minTime AND ca.executionTimeMs <= :maxTime")
    List<ChatAudit> findAuditLogsByExecutionTimeRange(@Param("minTime") Long minTime, @Param("maxTime") Long maxTime);

    /**
     * Find audit logs by execution time range with pagination
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.executionTimeMs >= :minTime AND ca.executionTimeMs <= :maxTime")
    Page<ChatAudit> findAuditLogsByExecutionTimeRange(@Param("minTime") Long minTime, @Param("maxTime") Long maxTime, Pageable pageable);

    /**
     * Find slow audit logs (execution time > threshold)
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.executionTimeMs > :threshold")
    List<ChatAudit> findSlowAuditLogs(@Param("threshold") Long threshold);

    /**
     * Find slow audit logs with pagination
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.executionTimeMs > :threshold")
    Page<ChatAudit> findSlowAuditLogs(@Param("threshold") Long threshold, Pageable pageable);

    /**
     * Count audit logs by user ID
     */
    long countByUserId(Long userId);

    /**
     * Count audit logs by chat room ID
     */
    long countByChatRoomId(Long chatRoomId);

    /**
     * Count audit logs by entity type
     */
    long countByEntityType(AuditEntity entityType);

    /**
     * Count audit logs by action
     */
    long countByAction(AuditAction action);

    /**
     * Count successful audit logs
     */
    long countByIsSuccessfulTrue();

    /**
     * Count failed audit logs
     */
    long countByIsSuccessfulFalse();

    /**
     * Count security-related audit logs
     */
    @Query("SELECT COUNT(ca) FROM ChatAudit ca WHERE ca.entityType = 'SECURITY' OR ca.action IN ('LOGIN', 'LOGOUT', 'AUTHENTICATE', 'AUTHORIZE', 'ACCESS_DENIED', 'SUSPICIOUS_ACTIVITY')")
    long countSecurityAuditLogs();

    /**
     * Count system-related audit logs
     */
    @Query("SELECT COUNT(ca) FROM ChatAudit ca WHERE ca.entityType = 'SYSTEM' OR ca.action IN ('SYSTEM_START', 'SYSTEM_STOP', 'SYSTEM_ERROR', 'SYSTEM_WARNING')")
    long countSystemAuditLogs();

    /**
     * Count message-related audit logs
     */
    @Query("SELECT COUNT(ca) FROM ChatAudit ca WHERE ca.entityType = 'MESSAGE' OR ca.action IN ('SEND_MESSAGE', 'RECEIVE_MESSAGE', 'READ_MESSAGE', 'EDIT_MESSAGE', 'DELETE_MESSAGE', 'FORWARD_MESSAGE', 'STAR_MESSAGE', 'UNSTAR_MESSAGE')")
    long countMessageAuditLogs();

    /**
     * Find audit logs created after a specific date
     */
    List<ChatAudit> findByCreatedAtAfter(LocalDateTime createdAt);

    /**
     * Find audit logs created between two dates
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.createdAt >= :startDate AND ca.createdAt <= :endDate")
    List<ChatAudit> findAuditLogsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find audit logs created between two dates with pagination
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.createdAt >= :startDate AND ca.createdAt <= :endDate")
    Page<ChatAudit> findAuditLogsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    /**
     * Find audit logs by error code
     */
    List<ChatAudit> findByErrorCode(String errorCode);

    /**
     * Find audit logs by error code with pagination
     */
    Page<ChatAudit> findByErrorCode(String errorCode, Pageable pageable);

    /**
     * Find audit logs by error message containing
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.errorMessage LIKE CONCAT('%', :errorMessage, '%')")
    List<ChatAudit> findByErrorMessageContaining(@Param("errorMessage") String errorMessage);

    /**
     * Find audit logs by error message containing with pagination
     */
    @Query("SELECT ca FROM ChatAudit ca WHERE ca.errorMessage LIKE CONCAT('%', :errorMessage, '%')")
    Page<ChatAudit> findByErrorMessageContaining(@Param("errorMessage") String errorMessage, Pageable pageable);
}
