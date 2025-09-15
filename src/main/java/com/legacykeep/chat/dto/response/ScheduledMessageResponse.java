package com.legacykeep.chat.dto.response;

import com.legacykeep.chat.entity.ScheduledMessage;
import com.legacykeep.chat.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for scheduled messages
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledMessageResponse {

    private String id;
    
    private String messageUuid;
    
    private Long chatRoomId;
    
    private Long senderUserId;
    
    private String content;
    
    private MessageType messageType;
    
    private LocalDateTime scheduledFor;
    
    private LocalDateTime createdAt;
    
    private ScheduledMessage.ScheduledStatus status;
    
    private Integer retryCount;
    
    private Integer maxRetries;
    
    private LocalDateTime lastAttempt;
    
    private String errorMessage;
    
    private Map<String, Object> metadata;
    
    private String recurrencePattern;
    
    private Boolean isRecurring;
    
    private LocalDateTime nextExecution;
    
    private LocalDateTime endDate;
    
    public static ScheduledMessageResponse fromEntity(ScheduledMessage scheduledMessage) {
        return ScheduledMessageResponse.builder()
                .id(scheduledMessage.getId())
                .messageUuid(scheduledMessage.getMessageUuid())
                .chatRoomId(scheduledMessage.getChatRoomId())
                .senderUserId(scheduledMessage.getSenderUserId())
                .content(scheduledMessage.getContent())
                .messageType(scheduledMessage.getMessageType())
                .scheduledFor(scheduledMessage.getScheduledFor())
                .createdAt(scheduledMessage.getCreatedAt())
                .status(scheduledMessage.getStatus())
                .retryCount(scheduledMessage.getRetryCount())
                .maxRetries(scheduledMessage.getMaxRetries())
                .lastAttempt(scheduledMessage.getLastAttempt())
                .errorMessage(scheduledMessage.getErrorMessage())
                .metadata(scheduledMessage.getMetadata())
                .recurrencePattern(scheduledMessage.getRecurrencePattern())
                .isRecurring(scheduledMessage.getIsRecurring())
                .nextExecution(scheduledMessage.getNextExecution())
                .endDate(scheduledMessage.getEndDate())
                .build();
    }
}
