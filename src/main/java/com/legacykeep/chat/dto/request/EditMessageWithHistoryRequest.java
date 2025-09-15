package com.legacykeep.chat.dto.request;

import com.legacykeep.chat.entity.MessageEditHistory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for editing a message with history tracking
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditMessageWithHistoryRequest {

    @NotBlank(message = "Message ID is required")
    private String messageId;

    @NotBlank(message = "New content is required")
    @Size(max = 10000, message = "Content must not exceed 10000 characters")
    private String newContent;

    @NotNull(message = "User ID is required")
    private Long userId;

    @Size(max = 500, message = "Edit reason must not exceed 500 characters")
    private String editReason;

    private MessageEditHistory.EditType editType;

    private String metadata; // JSON string for additional metadata
}
