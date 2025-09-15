package com.legacykeep.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for deleting messages
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteMessageRequest {

    @NotBlank(message = "Message ID is required")
    private String messageId;

    @NotNull(message = "User ID is required")
    private Long userId;

    private Boolean deleteForEveryone;

    private String reason;

    private Boolean deleteReplies;

    private Boolean deleteEditHistory;

    private Boolean notifyParticipants;
}
