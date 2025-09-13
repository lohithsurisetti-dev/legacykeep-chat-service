package com.legacykeep.chat.repository.postgres;

import com.legacykeep.chat.entity.ChatParticipant;
import com.legacykeep.chat.enums.ParticipantRole;
import com.legacykeep.chat.enums.ParticipantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ChatParticipant entity.
 * Provides data access methods for chat participants.
 */
@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    /**
     * Find participant by chat room ID and user ID
     */
    Optional<ChatParticipant> findByChatRoomIdAndUserId(Long chatRoomId, Long userId);

    /**
     * Find participants by chat room ID
     */
    List<ChatParticipant> findByChatRoomId(Long chatRoomId);

    /**
     * Find participants by chat room ID with pagination
     */
    Page<ChatParticipant> findByChatRoomId(Long chatRoomId, Pageable pageable);

    /**
     * Find participants by user ID
     */
    List<ChatParticipant> findByUserId(Long userId);

    /**
     * Find participants by user ID with pagination
     */
    Page<ChatParticipant> findByUserId(Long userId, Pageable pageable);

    /**
     * Find participants by chat room ID and status
     */
    List<ChatParticipant> findByChatRoomIdAndStatus(Long chatRoomId, ParticipantStatus status);

    /**
     * Find participants by user ID and status
     */
    List<ChatParticipant> findByUserIdAndStatus(Long userId, ParticipantStatus status);

    /**
     * Find participants by role
     */
    List<ChatParticipant> findByRole(ParticipantRole role);

    /**
     * Find participants by role with pagination
     */
    Page<ChatParticipant> findByRole(ParticipantRole role, Pageable pageable);

    /**
     * Find participants by status
     */
    List<ChatParticipant> findByStatus(ParticipantStatus status);

    /**
     * Find participants by status with pagination
     */
    Page<ChatParticipant> findByStatus(ParticipantStatus status, Pageable pageable);

    /**
     * Find participants by chat room ID and role
     */
    List<ChatParticipant> findByChatRoomIdAndRole(Long chatRoomId, ParticipantRole role);

    /**
     * Find participants by user ID and role
     */
    List<ChatParticipant> findByUserIdAndRole(Long userId, ParticipantRole role);

    /**
     * Find active participants in a chat room
     */
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.chatRoomId = :chatRoomId AND cp.status = 'ACTIVE'")
    List<ChatParticipant> findActiveParticipantsInRoom(@Param("chatRoomId") Long chatRoomId);

    /**
     * Find active participants in a chat room with pagination
     */
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.chatRoomId = :chatRoomId AND cp.status = 'ACTIVE'")
    Page<ChatParticipant> findActiveParticipantsInRoom(@Param("chatRoomId") Long chatRoomId, Pageable pageable);

    /**
     * Find active chat rooms for a user
     */
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.userId = :userId AND cp.status = 'ACTIVE'")
    List<ChatParticipant> findActiveChatRoomsForUser(@Param("userId") Long userId);

    /**
     * Find active chat rooms for a user with pagination
     */
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.userId = :userId AND cp.status = 'ACTIVE'")
    Page<ChatParticipant> findActiveChatRoomsForUser(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find admins in a chat room
     */
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.chatRoomId = :chatRoomId AND cp.role IN ('ADMIN', 'SUPER_ADMIN')")
    List<ChatParticipant> findAdminsInRoom(@Param("chatRoomId") Long chatRoomId);

    /**
     * Find moderators in a chat room
     */
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.chatRoomId = :chatRoomId AND cp.role IN ('ADMIN', 'SUPER_ADMIN', 'MODERATOR')")
    List<ChatParticipant> findModeratorsInRoom(@Param("chatRoomId") Long chatRoomId);

    /**
     * Find participants with unread messages
     */
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.unreadCount > 0")
    List<ChatParticipant> findParticipantsWithUnreadMessages();

    /**
     * Find participants with unread messages for a specific user
     */
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.userId = :userId AND cp.unreadCount > 0")
    List<ChatParticipant> findUnreadMessagesForUser(@Param("userId") Long userId);

    /**
     * Find muted participants
     */
    List<ChatParticipant> findByIsMutedTrue();

    /**
     * Find muted participants for a specific user
     */
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.userId = :userId AND cp.isMuted = true")
    List<ChatParticipant> findMutedChatRoomsForUser(@Param("userId") Long userId);

    /**
     * Find archived participants
     */
    List<ChatParticipant> findByIsArchivedTrue();

    /**
     * Find archived participants for a specific user
     */
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.userId = :userId AND cp.isArchived = true")
    List<ChatParticipant> findArchivedChatRoomsForUser(@Param("userId") Long userId);

    /**
     * Find pinned participants
     */
    List<ChatParticipant> findByIsPinnedTrue();

    /**
     * Find pinned participants for a specific user
     */
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.userId = :userId AND cp.isPinned = true")
    List<ChatParticipant> findPinnedChatRoomsForUser(@Param("userId") Long userId);

    /**
     * Find participants with notifications enabled
     */
    List<ChatParticipant> findByIsNotificationsEnabledTrue();

    /**
     * Find participants with notifications enabled for a specific user
     */
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.userId = :userId AND cp.isNotificationsEnabled = true")
    List<ChatParticipant> findChatRoomsWithNotificationsForUser(@Param("userId") Long userId);

    /**
     * Find participants with recent activity
     */
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.lastActivityAt >= :since")
    List<ChatParticipant> findParticipantsWithRecentActivity(@Param("since") LocalDateTime since);

    /**
     * Find participants with recent activity for a specific user
     */
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.userId = :userId AND cp.lastActivityAt >= :since")
    List<ChatParticipant> findRecentActivityForUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    /**
     * Find participants who have read recent messages
     */
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.lastReadAt >= :since")
    List<ChatParticipant> findParticipantsWhoReadRecently(@Param("since") LocalDateTime since);

    /**
     * Find participants who have read recent messages for a specific user
     */
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.userId = :userId AND cp.lastReadAt >= :since")
    List<ChatParticipant> findRecentReadsForUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    /**
     * Count participants in a chat room
     */
    long countByChatRoomId(Long chatRoomId);

    /**
     * Count active participants in a chat room
     */
    @Query("SELECT COUNT(cp) FROM ChatParticipant cp WHERE cp.chatRoomId = :chatRoomId AND cp.status = 'ACTIVE'")
    long countActiveParticipantsInRoom(@Param("chatRoomId") Long chatRoomId);

    /**
     * Count chat rooms for a user
     */
    long countByUserId(Long userId);

    /**
     * Count active chat rooms for a user
     */
    @Query("SELECT COUNT(cp) FROM ChatParticipant cp WHERE cp.userId = :userId AND cp.status = 'ACTIVE'")
    long countActiveChatRoomsForUser(@Param("userId") Long userId);

    /**
     * Count unread messages for a user
     */
    @Query("SELECT SUM(cp.unreadCount) FROM ChatParticipant cp WHERE cp.userId = :userId")
    Long sumUnreadMessagesForUser(@Param("userId") Long userId);

    /**
     * Check if user is participant in chat room
     */
    boolean existsByChatRoomIdAndUserId(Long chatRoomId, Long userId);

    /**
     * Check if user is active participant in chat room
     */
    @Query("SELECT COUNT(cp) > 0 FROM ChatParticipant cp WHERE cp.chatRoomId = :chatRoomId AND cp.userId = :userId AND cp.status = 'ACTIVE'")
    boolean existsActiveParticipant(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

    /**
     * Check if user is admin in chat room
     */
    @Query("SELECT COUNT(cp) > 0 FROM ChatParticipant cp WHERE cp.chatRoomId = :chatRoomId AND cp.userId = :userId AND cp.role IN ('ADMIN', 'SUPER_ADMIN')")
    boolean existsAdminParticipant(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

    /**
     * Check if user is moderator in chat room
     */
    @Query("SELECT COUNT(cp) > 0 FROM ChatParticipant cp WHERE cp.chatRoomId = :chatRoomId AND cp.userId = :userId AND cp.role IN ('ADMIN', 'SUPER_ADMIN', 'MODERATOR')")
    boolean existsModeratorParticipant(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

    /**
     * Find participants created after a specific date
     */
    List<ChatParticipant> findByCreatedAtAfter(LocalDateTime createdAt);

    /**
     * Find participants updated after a specific date
     */
    List<ChatParticipant> findByUpdatedAtAfter(LocalDateTime updatedAt);

    /**
     * Find participants who joined after a specific date
     */
    List<ChatParticipant> findByJoinedAtAfter(LocalDateTime joinedAt);

    /**
     * Find participants who left after a specific date
     */
    List<ChatParticipant> findByLeftAtAfter(LocalDateTime leftAt);
}
