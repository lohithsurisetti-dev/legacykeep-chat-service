package com.legacykeep.chat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for forwarding a message.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ForwardMessageRequest {
    
    /**
     * Target chat room IDs to forward the message to
     */
    @NotNull(message = "Target chat room IDs are required")
    private List<Long> targetChatRoomIds;
    
    /**
     * Additional message content to include with the forward
     */
    @Size(max = 1000, message = "Additional content must not exceed 1000 characters")
    private String additionalContent;
    
    /**
     * Whether to include the original message metadata
     */
    @Builder.Default
    private Boolean includeMetadata = true;
}
