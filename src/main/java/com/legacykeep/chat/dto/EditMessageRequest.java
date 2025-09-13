package com.legacykeep.chat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for editing a message.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EditMessageRequest {
    
    /**
     * Updated message content
     */
    @Size(max = 10000, message = "Message content must not exceed 10000 characters")
    private String content;
    
    // Advanced Features - Tone & Context
    /**
     * Updated tone color for the message
     */
    @Size(max = 7, message = "Tone color must be a valid hex color (7 characters)")
    private String toneColor;
    
    /**
     * Updated contextual wrapper for the message
     */
    @Size(max = 500, message = "Context wrapper must not exceed 500 characters")
    private String contextWrapper;
    
    /**
     * Updated mood tag for the message
     */
    @Size(max = 50, message = "Mood tag must not exceed 50 characters")
    private String moodTag;
    
    /**
     * Updated message metadata
     */
    private Map<String, Object> metadata;
}
