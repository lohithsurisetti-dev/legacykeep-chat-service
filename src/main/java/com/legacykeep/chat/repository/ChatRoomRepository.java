package com.legacykeep.chat.repository;

import com.legacykeep.chat.entity.ChatRoom;
import com.legacykeep.chat.enums.ChatRoomStatus;
import com.legacykeep.chat.enums.ChatRoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for ChatRoom entity.
 * Provides data access methods for chat rooms.
 */
@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /**
     * Find chat room by UUID
     */
    Optional<ChatRoom> findByRoomUuid(UUID roomUuid);

    /**
     * Find chat rooms by type
     */
    List<ChatRoom> findByRoomType(ChatRoomType roomType);

    /**
     * Find chat rooms by type with pagination
     */
    Page<ChatRoom> findByRoomType(ChatRoomType roomType, Pageable pageable);

    /**
     * Find chat rooms by status
     */
    List<ChatRoom> findByStatus(ChatRoomStatus status);

    /**
     * Find chat rooms by status with pagination
     */
    Page<ChatRoom> findByStatus(ChatRoomStatus status, Pageable pageable);

    /**
     * Find chat rooms by type and status
     */
    List<ChatRoom> findByRoomTypeAndStatus(ChatRoomType roomType, ChatRoomStatus status);

    /**
     * Find chat rooms by type and status with pagination
     */
    Page<ChatRoom> findByRoomTypeAndStatus(ChatRoomType roomType, ChatRoomStatus status, Pageable pageable);

    /**
     * Find chat rooms created by a specific user
     */
    List<ChatRoom> findByCreatedByUserId(Long createdByUserId);

    /**
     * Find chat rooms created by a specific user with pagination
     */
    Page<ChatRoom> findByCreatedByUserId(Long createdByUserId, Pageable pageable);

    /**
     * Find chat rooms by family ID
     */
    List<ChatRoom> findByFamilyId(Long familyId);

    /**
     * Find chat rooms by family ID with pagination
     */
    Page<ChatRoom> findByFamilyId(Long familyId, Pageable pageable);

    /**
     * Find chat rooms by story ID
     */
    List<ChatRoom> findByStoryId(Long storyId);

    /**
     * Find chat rooms by event ID
     */
    List<ChatRoom> findByEventId(Long eventId);

    /**
     * Find individual chat room between two users
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.roomType = 'INDIVIDUAL' AND " +
           "EXISTS (SELECT cp1 FROM ChatParticipant cp1 WHERE cp1.chatRoomId = cr.id AND cp1.userId = :user1Id) AND " +
           "EXISTS (SELECT cp2 FROM ChatParticipant cp2 WHERE cp2.chatRoomId = cr.id AND cp2.userId = :user2Id) AND " +
           "cr.participantCount = 2")
    Optional<ChatRoom> findIndividualChatBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    /**
     * Find chat rooms where user is a participant
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE EXISTS " +
           "(SELECT cp FROM ChatParticipant cp WHERE cp.chatRoomId = cr.id AND cp.userId = :userId AND cp.status = 'ACTIVE')")
    List<ChatRoom> findChatRoomsByParticipant(@Param("userId") Long userId);

    /**
     * Find chat rooms where user is a participant with pagination
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE EXISTS " +
           "(SELECT cp FROM ChatParticipant cp WHERE cp.chatRoomId = cr.id AND cp.userId = :userId AND cp.status = 'ACTIVE')")
    Page<ChatRoom> findChatRoomsByParticipant(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find active chat rooms where user is a participant
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.status = 'ACTIVE' AND EXISTS " +
           "(SELECT cp FROM ChatParticipant cp WHERE cp.chatRoomId = cr.id AND cp.userId = :userId AND cp.status = 'ACTIVE')")
    List<ChatRoom> findActiveChatRoomsByParticipant(@Param("userId") Long userId);

    /**
     * Find chat rooms with recent activity
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.lastMessageAt >= :since ORDER BY cr.lastMessageAt DESC")
    List<ChatRoom> findChatRoomsWithRecentActivity(@Param("since") LocalDateTime since);

    /**
     * Find chat rooms with recent activity for a specific user
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.lastMessageAt >= :since AND EXISTS " +
           "(SELECT cp FROM ChatParticipant cp WHERE cp.chatRoomId = cr.id AND cp.userId = :userId AND cp.status = 'ACTIVE') " +
           "ORDER BY cr.lastMessageAt DESC")
    List<ChatRoom> findChatRoomsWithRecentActivityForUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    /**
     * Find archived chat rooms for a user
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.isArchived = true AND EXISTS " +
           "(SELECT cp FROM ChatParticipant cp WHERE cp.chatRoomId = cr.id AND cp.userId = :userId)")
    List<ChatRoom> findArchivedChatRoomsForUser(@Param("userId") Long userId);

    /**
     * Find muted chat rooms for a user
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.isMuted = true AND EXISTS " +
           "(SELECT cp FROM ChatParticipant cp WHERE cp.chatRoomId = cr.id AND cp.userId = :userId)")
    List<ChatRoom> findMutedChatRoomsForUser(@Param("userId") Long userId);

    /**
     * Find encrypted chat rooms
     */
    List<ChatRoom> findByIsEncryptedTrue();

    /**
     * Find chat rooms by name (case insensitive)
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE LOWER(cr.roomName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<ChatRoom> findByRoomNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Find chat rooms by name with pagination
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE LOWER(cr.roomName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<ChatRoom> findByRoomNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    /**
     * Count chat rooms for a user
     */
    @Query("SELECT COUNT(cr) FROM ChatRoom cr WHERE EXISTS " +
           "(SELECT cp FROM ChatParticipant cp WHERE cp.chatRoomId = cr.id AND cp.userId = :userId)")
    long countChatRoomsForUser(@Param("userId") Long userId);

    /**
     * Count active chat rooms for a user
     */
    @Query("SELECT COUNT(cr) FROM ChatRoom cr WHERE cr.status = 'ACTIVE' AND EXISTS " +
           "(SELECT cp FROM ChatParticipant cp WHERE cp.chatRoomId = cr.id AND cp.userId = :userId AND cp.status = 'ACTIVE')")
    long countActiveChatRoomsForUser(@Param("userId") Long userId);

    /**
     * Check if chat room exists between two users
     */
    @Query("SELECT COUNT(cr) > 0 FROM ChatRoom cr WHERE cr.roomType = 'INDIVIDUAL' AND " +
           "EXISTS (SELECT cp1 FROM ChatParticipant cp1 WHERE cp1.chatRoomId = cr.id AND cp1.userId = :user1Id) AND " +
           "EXISTS (SELECT cp2 FROM ChatParticipant cp2 WHERE cp2.chatRoomId = cr.id AND cp2.userId = :user2Id) AND " +
           "cr.participantCount = 2")
    boolean existsIndividualChatBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    /**
     * Find chat rooms created after a specific date
     */
    List<ChatRoom> findByCreatedAtAfter(LocalDateTime createdAt);

    /**
     * Find chat rooms updated after a specific date
     */
    List<ChatRoom> findByUpdatedAtAfter(LocalDateTime updatedAt);

    /**
     * Find chat rooms with no recent activity
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.lastMessageAt IS NULL OR cr.lastMessageAt < :before")
    List<ChatRoom> findInactiveChatRooms(@Param("before") LocalDateTime before);
}
