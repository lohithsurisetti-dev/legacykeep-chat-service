package com.legacykeep.chat.entity;

import com.legacykeep.chat.enums.FilterType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a contact-specific content filter.
 * These filters apply only to messages from a specific contact.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Entity
@Table(name = "contact_filters")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactFilter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * User who owns this filter
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * Contact user ID that this filter applies to
     */
    @Column(name = "contact_user_id", nullable = false)
    private Long contactUserId;
    
    /**
     * The word or emoji to filter
     */
    @Column(name = "content", nullable = false, length = 255)
    private String content;
    
    /**
     * Type of filter (WORD, EMOJI, PHRASE)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "filter_type", nullable = false)
    private FilterType filterType;
    
    /**
     * Optional description of why this filter was added
     */
    @Column(name = "description", length = 500)
    private String description;
    
    /**
     * Whether the filter is active
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
