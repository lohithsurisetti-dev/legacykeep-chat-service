package com.legacykeep.chat.repository.postgres;

import com.legacykeep.chat.entity.FamilyPassword;
import com.legacykeep.chat.entity.FamilyPassword.PasswordStatus;
import com.legacykeep.chat.entity.FamilyPassword.PasswordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for FamilyPassword entity.
 * Provides data access methods for family passwords.
 */
@Repository
public interface FamilyPasswordRepository extends JpaRepository<FamilyPassword, Long> {

    /**
     * Find passwords by family ID
     */
    List<FamilyPassword> findByFamilyId(Long familyId);

    /**
     * Find passwords by family ID with pagination
     */
    Page<FamilyPassword> findByFamilyId(Long familyId, Pageable pageable);

    /**
     * Find passwords by password type
     */
    List<FamilyPassword> findByPasswordType(PasswordType passwordType);

    /**
     * Find passwords by password type with pagination
     */
    Page<FamilyPassword> findByPasswordType(PasswordType passwordType, Pageable pageable);

    /**
     * Find passwords by status
     */
    List<FamilyPassword> findByStatus(PasswordStatus status);

    /**
     * Find passwords by status with pagination
     */
    Page<FamilyPassword> findByStatus(PasswordStatus status, Pageable pageable);

    /**
     * Find passwords by family ID and password type
     */
    List<FamilyPassword> findByFamilyIdAndPasswordType(Long familyId, PasswordType passwordType);

    /**
     * Find passwords by family ID and password type with pagination
     */
    Page<FamilyPassword> findByFamilyIdAndPasswordType(Long familyId, PasswordType passwordType, Pageable pageable);

    /**
     * Find passwords by family ID and status
     */
    List<FamilyPassword> findByFamilyIdAndStatus(Long familyId, PasswordStatus status);

    /**
     * Find passwords by family ID and status with pagination
     */
    Page<FamilyPassword> findByFamilyIdAndStatus(Long familyId, PasswordStatus status, Pageable pageable);

    /**
     * Find passwords by password type and status
     */
    List<FamilyPassword> findByPasswordTypeAndStatus(PasswordType passwordType, PasswordStatus status);

    /**
     * Find passwords by password type and status with pagination
     */
    Page<FamilyPassword> findByPasswordTypeAndStatus(PasswordType passwordType, PasswordStatus status, Pageable pageable);

    /**
     * Find passwords by created by user ID
     */
    List<FamilyPassword> findByCreatedByUserId(Long createdByUserId);

    /**
     * Find passwords by created by user ID with pagination
     */
    Page<FamilyPassword> findByCreatedByUserId(Long createdByUserId, Pageable pageable);

    /**
     * Find passwords by name
     */
    List<FamilyPassword> findByPasswordName(String passwordName);

    /**
     * Find passwords by name with pagination
     */
    Page<FamilyPassword> findByPasswordName(String passwordName, Pageable pageable);

    /**
     * Find passwords by name (case insensitive)
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE LOWER(fp.passwordName) = LOWER(:passwordName)")
    List<FamilyPassword> findByPasswordNameIgnoreCase(@Param("passwordName") String passwordName);

    /**
     * Find passwords by name (case insensitive) with pagination
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE LOWER(fp.passwordName) = LOWER(:passwordName)")
    Page<FamilyPassword> findByPasswordNameIgnoreCase(@Param("passwordName") String passwordName, Pageable pageable);

    /**
     * Find active passwords
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE fp.status = 'ACTIVE'")
    List<FamilyPassword> findActivePasswords();

    /**
     * Find active passwords with pagination
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE fp.status = 'ACTIVE'")
    Page<FamilyPassword> findActivePasswords(Pageable pageable);

    /**
     * Find active passwords for a family
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE fp.familyId = :familyId AND fp.status = 'ACTIVE'")
    List<FamilyPassword> findActivePasswordsForFamily(@Param("familyId") Long familyId);

    /**
     * Find active passwords for a family with pagination
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE fp.familyId = :familyId AND fp.status = 'ACTIVE'")
    Page<FamilyPassword> findActivePasswordsForFamily(@Param("familyId") Long familyId, Pageable pageable);

    /**
     * Find expired passwords
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE fp.status = 'EXPIRED' OR (fp.expiresAt IS NOT NULL AND fp.expiresAt < :now)")
    List<FamilyPassword> findExpiredPasswords(@Param("now") LocalDateTime now);

    /**
     * Find expired passwords with pagination
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE fp.status = 'EXPIRED' OR (fp.expiresAt IS NOT NULL AND fp.expiresAt < :now)")
    Page<FamilyPassword> findExpiredPasswords(@Param("now") LocalDateTime now, Pageable pageable);

    /**
     * Find passwords expiring soon
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE fp.expiresAt IS NOT NULL AND fp.expiresAt BETWEEN :now AND :soon")
    List<FamilyPassword> findPasswordsExpiringSoon(@Param("now") LocalDateTime now, @Param("soon") LocalDateTime soon);

    /**
     * Find passwords expiring soon with pagination
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE fp.expiresAt IS NOT NULL AND fp.expiresAt BETWEEN :now AND :soon")
    Page<FamilyPassword> findPasswordsExpiringSoon(@Param("now") LocalDateTime now, @Param("soon") LocalDateTime soon, Pageable pageable);

    /**
     * Find shared passwords
     */
    List<FamilyPassword> findByIsSharedTrue();

    /**
     * Find shared passwords with pagination
     */
    Page<FamilyPassword> findByIsSharedTrue(Pageable pageable);

    /**
     * Find emergency passwords
     */
    List<FamilyPassword> findByIsEmergencyTrue();

    /**
     * Find emergency passwords with pagination
     */
    Page<FamilyPassword> findByIsEmergencyTrue(Pageable pageable);

    /**
     * Find temporary passwords
     */
    List<FamilyPassword> findByIsTemporaryTrue();

    /**
     * Find temporary passwords with pagination
     */
    Page<FamilyPassword> findByIsTemporaryTrue(Pageable pageable);

    /**
     * Find family-specific passwords
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE fp.passwordType IN ('FAMILY', 'STORY', 'EVENT', 'MEMORY')")
    List<FamilyPassword> findFamilySpecificPasswords();

    /**
     * Find family-specific passwords with pagination
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE fp.passwordType IN ('FAMILY', 'STORY', 'EVENT', 'MEMORY')")
    Page<FamilyPassword> findFamilySpecificPasswords(Pageable pageable);

    /**
     * Find emergency passwords
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE fp.passwordType = 'EMERGENCY'")
    List<FamilyPassword> findEmergencyPasswords();

    /**
     * Find emergency passwords with pagination
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE fp.passwordType = 'EMERGENCY'")
    Page<FamilyPassword> findEmergencyPasswords(Pageable pageable);

    /**
     * Find temporary passwords
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE fp.passwordType = 'TEMPORARY'")
    List<FamilyPassword> findTemporaryPasswords();

    /**
     * Find temporary passwords with pagination
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE fp.passwordType = 'TEMPORARY'")
    Page<FamilyPassword> findTemporaryPasswords(Pageable pageable);

    /**
     * Find passwords with recent usage
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE fp.lastUsedAt >= :since")
    List<FamilyPassword> findPasswordsWithRecentUsage(@Param("since") LocalDateTime since);

    /**
     * Find passwords with recent usage with pagination
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE fp.lastUsedAt >= :since")
    Page<FamilyPassword> findPasswordsWithRecentUsage(@Param("since") LocalDateTime since, Pageable pageable);

    /**
     * Find passwords with usage history
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE fp.usageCount > 0")
    List<FamilyPassword> findPasswordsWithUsageHistory();

    /**
     * Find passwords with usage history with pagination
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE fp.usageCount > 0")
    Page<FamilyPassword> findPasswordsWithUsageHistory(Pageable pageable);

    /**
     * Find passwords that have reached usage limit
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE fp.maxUsageCount IS NOT NULL AND fp.usageCount >= fp.maxUsageCount")
    List<FamilyPassword> findPasswordsAtUsageLimit();

    /**
     * Find passwords that have reached usage limit with pagination
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE fp.maxUsageCount IS NOT NULL AND fp.usageCount >= fp.maxUsageCount")
    Page<FamilyPassword> findPasswordsAtUsageLimit(Pageable pageable);

    /**
     * Count passwords by family ID
     */
    long countByFamilyId(Long familyId);

    /**
     * Count passwords by password type
     */
    long countByPasswordType(PasswordType passwordType);

    /**
     * Count passwords by status
     */
    long countByStatus(PasswordStatus status);

    /**
     * Count active passwords
     */
    @Query("SELECT COUNT(fp) FROM FamilyPassword fp WHERE fp.status = 'ACTIVE'")
    long countActivePasswords();

    /**
     * Count active passwords for a family
     */
    @Query("SELECT COUNT(fp) FROM FamilyPassword fp WHERE fp.familyId = :familyId AND fp.status = 'ACTIVE'")
    long countActivePasswordsForFamily(@Param("familyId") Long familyId);

    /**
     * Count expired passwords
     */
    @Query("SELECT COUNT(fp) FROM FamilyPassword fp WHERE fp.status = 'EXPIRED' OR (fp.expiresAt IS NOT NULL AND fp.expiresAt < :now)")
    long countExpiredPasswords(@Param("now") LocalDateTime now);

    /**
     * Count shared passwords
     */
    long countByIsSharedTrue();

    /**
     * Count emergency passwords
     */
    long countByIsEmergencyTrue();

    /**
     * Count temporary passwords
     */
    long countByIsTemporaryTrue();

    /**
     * Count family-specific passwords
     */
    @Query("SELECT COUNT(fp) FROM FamilyPassword fp WHERE fp.passwordType IN ('FAMILY', 'STORY', 'EVENT', 'MEMORY')")
    long countFamilySpecificPasswords();

    /**
     * Check if password exists by name and family ID
     */
    boolean existsByPasswordNameAndFamilyId(String passwordName, Long familyId);

    /**
     * Check if password exists by name and family ID (case insensitive)
     */
    @Query("SELECT COUNT(fp) > 0 FROM FamilyPassword fp WHERE LOWER(fp.passwordName) = LOWER(:passwordName) AND fp.familyId = :familyId")
    boolean existsByPasswordNameIgnoreCaseAndFamilyId(@Param("passwordName") String passwordName, @Param("familyId") Long familyId);

    /**
     * Find passwords created after a specific date
     */
    List<FamilyPassword> findByCreatedAtAfter(LocalDateTime createdAt);

    /**
     * Find passwords updated after a specific date
     */
    List<FamilyPassword> findByUpdatedAtAfter(LocalDateTime updatedAt);

    /**
     * Find passwords by name containing (case insensitive)
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE LOWER(fp.passwordName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<FamilyPassword> findByPasswordNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Find passwords by name containing (case insensitive) with pagination
     */
    @Query("SELECT fp FROM FamilyPassword fp WHERE LOWER(fp.passwordName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<FamilyPassword> findByPasswordNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
}
