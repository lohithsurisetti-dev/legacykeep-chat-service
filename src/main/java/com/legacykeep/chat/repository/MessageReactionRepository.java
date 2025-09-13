package com.legacykeep.chat.repository;

import com.legacykeep.chat.entity.MessageReaction;
import com.legacykeep.chat.entity.MessageReaction.ReactionType;
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
 * Repository interface for MessageReaction entity.
 * Provides data access methods for message reactions.
 */
@Repository
public interface MessageReactionRepository extends JpaRepository<MessageReaction, Long> {

    /**
     * Find reaction by message ID, user ID, and reaction type
     */
    Optional<MessageReaction> findByMessageIdAndUserIdAndReactionType(String messageId, Long userId, ReactionType reactionType);

    /**
     * Find reactions by message ID
     */
    List<MessageReaction> findByMessageId(String messageId);

    /**
     * Find reactions by message ID with pagination
     */
    Page<MessageReaction> findByMessageId(String messageId, Pageable pageable);

    /**
     * Find reactions by user ID
     */
    List<MessageReaction> findByUserId(Long userId);

    /**
     * Find reactions by user ID with pagination
     */
    Page<MessageReaction> findByUserId(Long userId, Pageable pageable);

    /**
     * Find reactions by reaction type
     */
    List<MessageReaction> findByReactionType(ReactionType reactionType);

    /**
     * Find reactions by reaction type with pagination
     */
    Page<MessageReaction> findByReactionType(ReactionType reactionType, Pageable pageable);

    /**
     * Find reactions by message ID and reaction type
     */
    List<MessageReaction> findByMessageIdAndReactionType(String messageId, ReactionType reactionType);

    /**
     * Find reactions by user ID and reaction type
     */
    List<MessageReaction> findByUserIdAndReactionType(Long userId, ReactionType reactionType);

    /**
     * Find reactions by message ID and user ID
     */
    List<MessageReaction> findByMessageIdAndUserId(String messageId, Long userId);

    /**
     * Find family-specific reactions
     */
    @Query("SELECT mr FROM MessageReaction mr WHERE mr.reactionType IN " +
           "('BLESSING', 'PRIDE', 'GRATITUDE', 'MEMORY', 'WISDOM', 'TRADITION', 'RESPECT', 'HONOR', 'LEGACY', 'HERITAGE')")
    List<MessageReaction> findFamilySpecificReactions();

    /**
     * Find family-specific reactions with pagination
     */
    @Query("SELECT mr FROM MessageReaction mr WHERE mr.reactionType IN " +
           "('BLESSING', 'PRIDE', 'GRATITUDE', 'MEMORY', 'WISDOM', 'TRADITION', 'RESPECT', 'HONOR', 'LEGACY', 'HERITAGE')")
    Page<MessageReaction> findFamilySpecificReactions(Pageable pageable);

    /**
     * Find generational reactions
     */
    @Query("SELECT mr FROM MessageReaction mr WHERE mr.reactionType IN " +
           "('GRANDPARENT', 'PARENT', 'CHILD', 'SIBLING')")
    List<MessageReaction> findGenerationalReactions();

    /**
     * Find generational reactions with pagination
     */
    @Query("SELECT mr FROM MessageReaction mr WHERE mr.reactionType IN " +
           "('GRANDPARENT', 'PARENT', 'CHILD', 'SIBLING')")
    Page<MessageReaction> findGenerationalReactions(Pageable pageable);

    /**
     * Find cultural reactions
     */
    @Query("SELECT mr FROM MessageReaction mr WHERE mr.reactionType IN " +
           "('NAMASTE', 'OM', 'FESTIVAL', 'PRAYER', 'RITUAL')")
    List<MessageReaction> findCulturalReactions();

    /**
     * Find cultural reactions with pagination
     */
    @Query("SELECT mr FROM MessageReaction mr WHERE mr.reactionType IN " +
           "('NAMASTE', 'OM', 'FESTIVAL', 'PRAYER', 'RITUAL')")
    Page<MessageReaction> findCulturalReactions(Pageable pageable);

    /**
     * Find core emotional reactions
     */
    @Query("SELECT mr FROM MessageReaction mr WHERE mr.reactionType IN " +
           "('LIKE', 'LOVE', 'HEART', 'LAUGH', 'WOW', 'SAD', 'ANGRY')")
    List<MessageReaction> findCoreEmotionalReactions();

    /**
     * Find core emotional reactions with pagination
     */
    @Query("SELECT mr FROM MessageReaction mr WHERE mr.reactionType IN " +
           "('LIKE', 'LOVE', 'HEART', 'LAUGH', 'WOW', 'SAD', 'ANGRY')")
    Page<MessageReaction> findCoreEmotionalReactions(Pageable pageable);

    /**
     * Find high intensity reactions
     */
    List<MessageReaction> findByIntensityGreaterThanEqual(Integer intensity);

    /**
     * Find high intensity reactions with pagination
     */
    Page<MessageReaction> findByIntensityGreaterThanEqual(Integer intensity, Pageable pageable);

    /**
     * Find low intensity reactions
     */
    List<MessageReaction> findByIntensityLessThanEqual(Integer intensity);

    /**
     * Find low intensity reactions with pagination
     */
    Page<MessageReaction> findByIntensityLessThanEqual(Integer intensity, Pageable pageable);

    /**
     * Find anonymous reactions
     */
    List<MessageReaction> findByIsAnonymousTrue();

    /**
     * Find private reactions
     */
    List<MessageReaction> findByIsPrivateTrue();

    /**
     * Find visible reactions (not private)
     */
    List<MessageReaction> findByIsPrivateFalse();

    /**
     * Find visible reactions with pagination
     */
    Page<MessageReaction> findByIsPrivateFalse(Pageable pageable);

    /**
     * Find reactions with recent activity
     */
    @Query("SELECT mr FROM MessageReaction mr WHERE mr.createdAt >= :since")
    List<MessageReaction> findReactionsWithRecentActivity(@Param("since") LocalDateTime since);

    /**
     * Find reactions with recent activity for a specific user
     */
    @Query("SELECT mr FROM MessageReaction mr WHERE mr.userId = :userId AND mr.createdAt >= :since")
    List<MessageReaction> findRecentReactionsForUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    /**
     * Find reactions with recent activity for a specific message
     */
    @Query("SELECT mr FROM MessageReaction mr WHERE mr.messageId = :messageId AND mr.createdAt >= :since")
    List<MessageReaction> findRecentReactionsForMessage(@Param("messageId") String messageId, @Param("since") LocalDateTime since);

    /**
     * Count reactions by message ID
     */
    long countByMessageId(String messageId);

    /**
     * Count reactions by message ID and reaction type
     */
    long countByMessageIdAndReactionType(String messageId, ReactionType reactionType);

    /**
     * Count reactions by user ID
     */
    long countByUserId(Long userId);

    /**
     * Count reactions by reaction type
     */
    long countByReactionType(ReactionType reactionType);

    /**
     * Count family-specific reactions
     */
    @Query("SELECT COUNT(mr) FROM MessageReaction mr WHERE mr.reactionType IN " +
           "('BLESSING', 'PRIDE', 'GRATITUDE', 'MEMORY', 'WISDOM', 'TRADITION', 'RESPECT', 'HONOR', 'LEGACY', 'HERITAGE')")
    long countFamilySpecificReactions();

    /**
     * Count generational reactions
     */
    @Query("SELECT COUNT(mr) FROM MessageReaction mr WHERE mr.reactionType IN " +
           "('GRANDPARENT', 'PARENT', 'CHILD', 'SIBLING')")
    long countGenerationalReactions();

    /**
     * Count cultural reactions
     */
    @Query("SELECT COUNT(mr) FROM MessageReaction mr WHERE mr.reactionType IN " +
           "('NAMASTE', 'OM', 'FESTIVAL', 'PRAYER', 'RITUAL')")
    long countCulturalReactions();

    /**
     * Count core emotional reactions
     */
    @Query("SELECT COUNT(mr) FROM MessageReaction mr WHERE mr.reactionType IN " +
           "('LIKE', 'LOVE', 'HEART', 'LAUGH', 'WOW', 'SAD', 'ANGRY')")
    long countCoreEmotionalReactions();

    /**
     * Count high intensity reactions
     */
    long countByIntensityGreaterThanEqual(Integer intensity);

    /**
     * Count anonymous reactions
     */
    long countByIsAnonymousTrue();

    /**
     * Count private reactions
     */
    long countByIsPrivateTrue();

    /**
     * Count visible reactions
     */
    long countByIsPrivateFalse();

    /**
     * Check if user has reacted to a message
     */
    boolean existsByMessageIdAndUserId(String messageId, Long userId);

    /**
     * Check if user has reacted to a message with specific reaction type
     */
    boolean existsByMessageIdAndUserIdAndReactionType(String messageId, Long userId, ReactionType reactionType);

    /**
     * Find reactions created after a specific date
     */
    List<MessageReaction> findByCreatedAtAfter(LocalDateTime createdAt);

    /**
     * Find reactions updated after a specific date
     */
    List<MessageReaction> findByUpdatedAtAfter(LocalDateTime updatedAt);

    /**
     * Find reactions by generation level
     */
    List<MessageReaction> findByGenerationLevel(Integer generationLevel);

    /**
     * Find reactions by generation level with pagination
     */
    Page<MessageReaction> findByGenerationLevel(Integer generationLevel, Pageable pageable);

    /**
     * Find reactions with specific generation level range
     */
    @Query("SELECT mr FROM MessageReaction mr WHERE mr.generationLevel >= :minLevel AND mr.generationLevel <= :maxLevel")
    List<MessageReaction> findReactionsByGenerationLevelRange(@Param("minLevel") Integer minLevel, @Param("maxLevel") Integer maxLevel);

    /**
     * Find reactions with specific generation level range with pagination
     */
    @Query("SELECT mr FROM MessageReaction mr WHERE mr.generationLevel >= :minLevel AND mr.generationLevel <= :maxLevel")
    Page<MessageReaction> findReactionsByGenerationLevelRange(@Param("minLevel") Integer minLevel, @Param("maxLevel") Integer maxLevel, Pageable pageable);
}
