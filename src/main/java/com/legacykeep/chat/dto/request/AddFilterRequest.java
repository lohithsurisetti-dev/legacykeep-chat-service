package com.legacykeep.chat.dto.request;

import com.legacykeep.chat.enums.FilterType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for adding a content filter.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddFilterRequest {
    
    /**
     * The word or emoji to filter
     */
    @NotBlank(message = "Content is required")
    @Size(max = 255, message = "Content must not exceed 255 characters")
    private String content;
    
    /**
     * Type of filter (WORD, EMOJI, PHRASE)
     */
    @NotNull(message = "Filter type is required")
    private FilterType filterType;
    
    /**
     * Optional description of why this filter was added
     */
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
