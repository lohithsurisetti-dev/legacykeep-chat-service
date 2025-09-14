package com.legacykeep.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for adding a participant to a chat room.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddParticipantRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Added by user ID is required")
    private Long addedByUserId;
}
