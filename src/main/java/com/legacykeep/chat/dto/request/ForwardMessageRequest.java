package com.legacykeep.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

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
public class ForwardMessageRequest {

    @NotNull(message = "Original message ID is required")
    private String originalMessageId;

    @NotNull(message = "Target chat room ID is required")
    private Long toChatRoomId;

    @NotNull(message = "From user ID is required")
    private Long fromUserId;
}
