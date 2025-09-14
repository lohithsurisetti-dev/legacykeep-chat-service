package com.legacykeep.chat.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for adding a reaction to a message.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReactionRequest {
    
    /**
     * User ID adding the reaction
     */
    private Long userId;
    
    /**
     * Reaction emoji or type
     */
    @NotBlank(message = "Reaction is required")
    @Size(max = 10, message = "Reaction must not exceed 10 characters")
    private String emoji;
    
    /**
     * Reaction type (EMOJI, CUSTOM, STICKER, GIF, ANIMATION)
     */
    @Size(max = 20, message = "Reaction type must not exceed 20 characters")
    private String reactionType;
    
    /**
     * Additional reaction data
     */
    private Object reactionData;
}
