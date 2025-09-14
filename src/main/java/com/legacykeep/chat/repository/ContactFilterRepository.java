package com.legacykeep.chat.repository;

import com.legacykeep.chat.entity.ContactFilter;
import com.legacykeep.chat.enums.FilterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ContactFilter entity operations.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Repository
public interface ContactFilterRepository extends JpaRepository<ContactFilter, Long> {
    
    /**
     * Find all active contact filters for a user
     */
    List<ContactFilter> findByUserIdAndIsActiveTrue(Long userId);
    
    /**
     * Find all filters for a specific contact
     */
    List<ContactFilter> findByUserIdAndContactUserIdAndIsActiveTrue(Long userId, Long contactUserId);
    
    /**
     * Find specific filter by user, contact, and content
     */
    Optional<ContactFilter> findByUserIdAndContactUserIdAndContentAndFilterType(
            Long userId, Long contactUserId, String content, FilterType filterType);
    
    /**
     * Check if a specific filter exists for a contact
     */
    boolean existsByUserIdAndContactUserIdAndContentAndFilterTypeAndIsActiveTrue(
            Long userId, Long contactUserId, String content, FilterType filterType);
    
    /**
     * Find all active word filters for a specific contact
     */
    @Query("SELECT cf FROM ContactFilter cf WHERE cf.userId = :userId AND cf.contactUserId = :contactUserId AND cf.filterType = 'WORD' AND cf.isActive = true")
    List<ContactFilter> findActiveWordFiltersByUserIdAndContactUserId(@Param("userId") Long userId, @Param("contactUserId") Long contactUserId);
    
    /**
     * Find all active emoji filters for a specific contact
     */
    @Query("SELECT cf FROM ContactFilter cf WHERE cf.userId = :userId AND cf.contactUserId = :contactUserId AND cf.filterType = 'EMOJI' AND cf.isActive = true")
    List<ContactFilter> findActiveEmojiFiltersByUserIdAndContactUserId(@Param("userId") Long userId, @Param("contactUserId") Long contactUserId);
    
    /**
     * Count active filters for a specific contact
     */
    long countByUserIdAndContactUserIdAndIsActiveTrue(Long userId, Long contactUserId);
}
