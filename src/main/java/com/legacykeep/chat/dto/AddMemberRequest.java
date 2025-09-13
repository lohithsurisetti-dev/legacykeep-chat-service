package com.legacykeep.chat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.legacykeep.chat.enums.ParticipantRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for adding members to a chat room.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddMemberRequest {
    
    /**
     * User IDs to add to the chat room
     */
    @NotNull(message = "User IDs are required")
    private List<Long> userIds;
    
    /**
     * Role to assign to the new members
     */
    @Builder.Default
    private ParticipantRole role = ParticipantRole.MEMBER;
    
    /**
     * Whether to send invitation notifications
     */
    @Builder.Default
    private Boolean sendNotification = true;
}
