package com.legacykeep.chat.dto.response;

import com.legacykeep.chat.entity.Message;
import com.legacykeep.chat.enums.MessageStatus;
import com.legacykeep.chat.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for Message.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private String id;
    private String messageUuid;
    private Long chatRoomId;
    private Long senderUserId;
    private MessageType messageType;
    private String content;
    private MessageStatus status;
    private String replyToMessageId;
    private String forwardedFromMessageId;
    private Boolean isStarred;
    private Boolean isEncrypted;
    private Boolean isProtected;
    private String protectionLevel;
    private String passwordHash;
    private LocalDateTime selfDestructAt;
    private Boolean screenshotProtection;
    private Integer viewCount;
    private Integer maxViews;
    private String toneColor;
    private Double toneConfidence;
    private String contextWrapper;
    private String moodTag;
    private String voiceEmotion;
    private List<String> memoryTriggers;
    private String predictiveText;
    private String aiToneSuggestion;
    private String mediaUrl;
    private String mediaThumbnailUrl;
    private Long mediaSize;
    private Integer mediaDuration;
    private String mediaFormat;
    private String mediaMetadata;
    private Double locationLatitude;
    private Double locationLongitude;
    private String locationAddress;
    private String locationName;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private Long storyId;
    private Long memoryId;
    private Long eventId;
    private Map<String, List<Long>> reactions;
    private Map<Long, LocalDateTime> readBy;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime editedAt;
    private LocalDateTime deletedAt;
    private Long deletedByUserId;
    private Boolean isDeletedForEveryone;
    
    // Content Filtering Fields
    private Boolean isFiltered;
    private List<String> filteredReasons;
    private List<String> applicableFilterTypes;
    private String filterStatus; // "FILTERED", "ALLOWED", "OVERRIDDEN"

    /**
     * Convert Message entity to MessageResponse DTO.
     */
    public static MessageResponse fromEntity(Message message) {
        if (message == null) {
            return null;
        }

        return MessageResponse.builder()
                .id(message.getId())
                .messageUuid(message.getMessageUuid())
                .chatRoomId(message.getChatRoomId())
                .senderUserId(message.getSenderUserId())
                .messageType(message.getMessageType())
                .content(message.getContent())
                .status(message.getStatus())
                .replyToMessageId(message.getReplyToMessageId())
                .forwardedFromMessageId(message.getForwardedFromMessageId())
                .isStarred(message.getIsStarred())
                .isEncrypted(message.getIsEncrypted())
                .isProtected(message.getIsProtected())
                .protectionLevel(message.getProtectionLevel())
                .passwordHash(message.getPasswordHash())
                .selfDestructAt(message.getSelfDestructAt())
                .screenshotProtection(message.getScreenshotProtection())
                .viewCount(message.getViewCount())
                .maxViews(message.getMaxViews())
                .toneColor(message.getToneColor())
                .toneConfidence(message.getToneConfidence())
                .contextWrapper(message.getContextWrapper())
                .moodTag(message.getMoodTag())
                .voiceEmotion(message.getVoiceEmotion())
                .memoryTriggers(message.getMemoryTriggers())
                .predictiveText(message.getPredictiveText())
                .aiToneSuggestion(message.getAiToneSuggestion())
                .mediaUrl(message.getMediaUrl())
                .mediaThumbnailUrl(message.getMediaThumbnailUrl())
                .mediaSize(message.getMediaSize())
                .mediaDuration(message.getMediaDuration())
                .mediaFormat(message.getMediaFormat())
                .mediaMetadata(message.getMediaMetadata() != null ? message.getMediaMetadata().toString() : null)
                .locationLatitude(message.getLocationLatitude())
                .locationLongitude(message.getLocationLongitude())
                .locationAddress(message.getLocationAddress())
                .locationName(message.getLocationName())
                .contactName(message.getContactName())
                .contactPhone(message.getContactPhone())
                .contactEmail(message.getContactEmail())
                .storyId(message.getStoryId())
                .memoryId(message.getMemoryId())
                .eventId(message.getEventId())
                .reactions(message.getReactions())
                .readBy(message.getReadBy())
                .metadata(message.getMetadata() != null ? message.getMetadata().toString() : null)
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .editedAt(message.getEditedAt())
                .deletedAt(message.getDeletedAt())
                .deletedByUserId(message.getDeletedByUserId())
                .isDeletedForEveryone(message.getIsDeletedForEveryone())
                .build();
    }

    /**
     * Convert Message entity to MessageResponse DTO with filter information.
     */
    public static MessageResponse fromEntityWithFilters(Message message, Long requestingUserId, 
                                                       Boolean isFiltered, List<String> filteredReasons, 
                                                       List<String> applicableFilterTypes) {
        if (message == null) {
            return null;
        }

        MessageResponse response = fromEntity(message);
        
        // Set filter information
        response.setIsFiltered(isFiltered);
        response.setFilteredReasons(filteredReasons);
        response.setApplicableFilterTypes(applicableFilterTypes);
        
        // Set filter status
        if (isFiltered != null && isFiltered) {
            response.setFilterStatus("FILTERED");
            // Mask the content for filtered messages
            response.setContent("[Content Filtered]");
        } else {
            response.setFilterStatus("ALLOWED");
        }
        
        return response;
    }
}
