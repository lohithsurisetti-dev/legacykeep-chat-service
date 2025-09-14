package com.legacykeep.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for removing a participant from a chat room.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemoveParticipantRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Removed by user ID is required")
    private Long removedByUserId;
}
