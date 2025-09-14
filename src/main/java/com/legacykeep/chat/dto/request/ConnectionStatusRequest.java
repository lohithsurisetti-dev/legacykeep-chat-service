package com.legacykeep.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for connection status.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionStatusRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Status is required")
    private String status;

    private String message;
}
