package com.legacykeep.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for typing indicator.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypingIndicatorRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Chat room ID is required")
    private Long chatRoomId;

    @NotNull(message = "Is typing flag is required")
    private Boolean isTyping;
}
