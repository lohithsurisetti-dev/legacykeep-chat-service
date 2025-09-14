package com.legacykeep.chat.repository;

import com.legacykeep.chat.entity.UserFilter;
import com.legacykeep.chat.enums.FilterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UserFilter entity operations.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Repository
public interface UserFilterRepository extends JpaRepository<UserFilter, Long> {
    
    /**
     * Find all active filters for a user
     */
    List<UserFilter> findByUserIdAndIsActiveTrue(Long userId);
    
    /**
     * Find all filters for a user (active and inactive)
     */
    List<UserFilter> findByUserId(Long userId);
    
    /**
     * Find specific filter by user and content
     */
    Optional<UserFilter> findByUserIdAndContentAndFilterType(Long userId, String content, FilterType filterType);
    
    /**
     * Check if a specific filter exists for a user
     */
    boolean existsByUserIdAndContentAndFilterTypeAndIsActiveTrue(Long userId, String content, FilterType filterType);
    
    /**
     * Find all active word filters for a user
     */
    @Query("SELECT uf FROM UserFilter uf WHERE uf.userId = :userId AND uf.filterType = 'WORD' AND uf.isActive = true")
    List<UserFilter> findActiveWordFiltersByUserId(@Param("userId") Long userId);
    
    /**
     * Find all active emoji filters for a user
     */
    @Query("SELECT uf FROM UserFilter uf WHERE uf.userId = :userId AND uf.filterType = 'EMOJI' AND uf.isActive = true")
    List<UserFilter> findActiveEmojiFiltersByUserId(@Param("userId") Long userId);
    
    /**
     * Count active filters for a user
     */
    long countByUserIdAndIsActiveTrue(Long userId);
}
