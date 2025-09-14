package com.legacykeep.chat.dto.response;

import com.legacykeep.chat.enums.FilterType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for content filter information.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterResponse {
    
    private Long id;
    private Long userId;
    private Long contactUserId; // For contact filters
    private Long roomId; // For room filters
    private Long createdByUserId; // For room filters
    private String content;
    private FilterType filterType;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Create FilterResponse from UserFilter entity
     */
    public static FilterResponse fromUserFilter(com.legacykeep.chat.entity.UserFilter filter) {
        return FilterResponse.builder()
                .id(filter.getId())
                .userId(filter.getUserId())
                .content(filter.getContent())
                .filterType(filter.getFilterType())
                .description(filter.getDescription())
                .isActive(filter.getIsActive())
                .createdAt(filter.getCreatedAt())
                .updatedAt(filter.getUpdatedAt())
                .build();
    }
    
    /**
     * Create FilterResponse from ContactFilter entity
     */
    public static FilterResponse fromContactFilter(com.legacykeep.chat.entity.ContactFilter filter) {
        return FilterResponse.builder()
                .id(filter.getId())
                .userId(filter.getUserId())
                .contactUserId(filter.getContactUserId())
                .content(filter.getContent())
                .filterType(filter.getFilterType())
                .description(filter.getDescription())
                .isActive(filter.getIsActive())
                .createdAt(filter.getCreatedAt())
                .updatedAt(filter.getUpdatedAt())
                .build();
    }
    
    /**
     * Create FilterResponse from RoomFilter entity
     */
    public static FilterResponse fromRoomFilter(com.legacykeep.chat.entity.RoomFilter filter) {
        return FilterResponse.builder()
                .id(filter.getId())
                .roomId(filter.getRoomId())
                .createdByUserId(filter.getCreatedByUserId())
                .content(filter.getContent())
                .filterType(filter.getFilterType())
                .description(filter.getDescription())
                .isActive(filter.getIsActive())
                .createdAt(filter.getCreatedAt())
                .updatedAt(filter.getUpdatedAt())
                .build();
    }
}
