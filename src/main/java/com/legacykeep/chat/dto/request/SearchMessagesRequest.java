package com.legacykeep.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Request DTO for searching messages
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchMessagesRequest {

    @NotBlank(message = "Search query is required")
    private String query;

    @NotNull(message = "User ID is required")
    private Long userId;

    private Long chatRoomId;
    
    private List<Long> chatRoomIds;
    
    private Long senderUserId;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private Boolean isStarred;
    
    private Boolean isEncrypted;
    
    private Boolean includeDeleted;
    
    private Integer page;
    
    private Integer size;
    
    private String sortBy;
    
    private String sortDirection;
}
