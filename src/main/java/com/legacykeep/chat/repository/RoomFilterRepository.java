package com.legacykeep.chat.repository;

import com.legacykeep.chat.entity.RoomFilter;
import com.legacykeep.chat.enums.FilterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for RoomFilter entity operations.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Repository
public interface RoomFilterRepository extends JpaRepository<RoomFilter, Long> {
    
    /**
     * Find all active filters for a room
     */
    List<RoomFilter> findByRoomIdAndIsActiveTrue(Long roomId);
    
    /**
     * Find all filters for a room (active and inactive)
     */
    List<RoomFilter> findByRoomId(Long roomId);
    
    /**
     * Find specific filter by room and content
     */
    Optional<RoomFilter> findByRoomIdAndContentAndFilterType(Long roomId, String content, FilterType filterType);
    
    /**
     * Check if a specific filter exists for a room
     */
    boolean existsByRoomIdAndContentAndFilterTypeAndIsActiveTrue(Long roomId, String content, FilterType filterType);
    
    /**
     * Find all active word filters for a room
     */
    @Query("SELECT rf FROM RoomFilter rf WHERE rf.roomId = :roomId AND rf.filterType = 'WORD' AND rf.isActive = true")
    List<RoomFilter> findActiveWordFiltersByRoomId(@Param("roomId") Long roomId);
    
    /**
     * Find all active emoji filters for a room
     */
    @Query("SELECT rf FROM RoomFilter rf WHERE rf.roomId = :roomId AND rf.filterType = 'EMOJI' AND rf.isActive = true")
    List<RoomFilter> findActiveEmojiFiltersByRoomId(@Param("roomId") Long roomId);
    
    /**
     * Count active filters for a room
     */
    long countByRoomIdAndIsActiveTrue(Long roomId);
    
    /**
     * Find filters created by a specific user in a room
     */
    List<RoomFilter> findByRoomIdAndCreatedByUserIdAndIsActiveTrue(Long roomId, Long createdByUserId);
}
