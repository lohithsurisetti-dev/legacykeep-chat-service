package com.legacykeep.chat.dto.response;

import com.legacykeep.chat.entity.ChatRoom;
import com.legacykeep.chat.enums.ChatRoomStatus;
import com.legacykeep.chat.enums.ChatRoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for ChatRoom.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {

    private Long id;
    private String chatRoomUuid;
    private String name;
    private String description;
    private ChatRoomType type;
    private ChatRoomStatus status;
    private Long createdByUserId;
    private Long familyId;
    private Long storyId;
    private Long eventId;
    private List<Long> participants;
    private Long lastMessageId;
    private Long lastMessageSenderId;
    private LocalDateTime lastMessageAt;
    private Integer messageCount;
    private Boolean isArchived;
    private LocalDateTime archivedAt;
    private Long archivedByUserId;
    private Boolean isMuted;
    private LocalDateTime mutedAt;
    private Long mutedByUserId;
    private Boolean isEncrypted;
    private String encryptionKey;
    private Boolean allowMediaSharing;
    private Boolean allowFileSharing;
    private Boolean allowLocationSharing;
    private Boolean allowContactSharing;
    private Integer maxParticipants;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Convert ChatRoom entity to ChatRoomResponse DTO.
     */
    public static ChatRoomResponse fromEntity(ChatRoom chatRoom) {
        if (chatRoom == null) {
            return null;
        }

        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .chatRoomUuid(chatRoom.getRoomUuid() != null ? chatRoom.getRoomUuid().toString() : null)
                .name(chatRoom.getRoomName())
                .description(chatRoom.getRoomDescription())
                .type(chatRoom.getRoomType())
                .status(chatRoom.getStatus())
                .createdByUserId(chatRoom.getCreatedByUserId())
                .familyId(chatRoom.getFamilyId())
                .storyId(chatRoom.getStoryId())
                .eventId(chatRoom.getEventId())
                .participants(null) // Field doesn't exist in entity
                .lastMessageId(chatRoom.getLastMessageId())
                .lastMessageSenderId(chatRoom.getLastMessageByUserId())
                .lastMessageAt(chatRoom.getLastMessageAt())
                .messageCount(chatRoom.getMessageCount() != null ? chatRoom.getMessageCount().intValue() : 0)
                .isArchived(chatRoom.getIsArchived())
                .archivedAt(null) // Field doesn't exist in entity
                .archivedByUserId(null) // Field doesn't exist in entity
                .isMuted(chatRoom.getIsMuted())
                .mutedAt(null) // Field doesn't exist in entity
                .mutedByUserId(null) // Field doesn't exist in entity
                .isEncrypted(chatRoom.getIsEncrypted())
                .encryptionKey(null) // Field doesn't exist in entity
                .allowMediaSharing(null) // Field doesn't exist in entity
                .allowFileSharing(null) // Field doesn't exist in entity
                .allowLocationSharing(null) // Field doesn't exist in entity
                .allowContactSharing(null) // Field doesn't exist in entity
                .maxParticipants(null) // Field doesn't exist in entity
                .metadata(chatRoom.getMetadata())
                .createdAt(chatRoom.getCreatedAt())
                .updatedAt(chatRoom.getUpdatedAt())
                .build();
    }
}
