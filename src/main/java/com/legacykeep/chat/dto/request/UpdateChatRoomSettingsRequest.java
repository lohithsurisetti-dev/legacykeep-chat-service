package com.legacykeep.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for updating chat room settings.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChatRoomSettingsRequest {

    @NotNull(message = "Updated by user ID is required")
    private Long updatedByUserId;

    private String name;
    private String description;
    private Boolean isEncrypted;
    private String encryptionKey;
    private Boolean allowMediaSharing;
    private Boolean allowFileSharing;
    private Boolean allowLocationSharing;
    private Boolean allowContactSharing;
    private Integer maxParticipants;
    private String metadata;
}
