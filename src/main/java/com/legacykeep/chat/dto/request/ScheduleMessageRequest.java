package com.legacykeep.chat.dto.request;

import com.legacykeep.chat.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Request DTO for scheduling messages
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleMessageRequest {

    @NotNull(message = "Chat room ID is required")
    private Long chatRoomId;

    @NotNull(message = "Sender user ID is required")
    private Long senderUserId;

    @NotBlank(message = "Content is required")
    @Size(max = 10000, message = "Content must not exceed 10000 characters")
    private String content;

    @NotNull(message = "Message type is required")
    private MessageType messageType;

    @NotNull(message = "Scheduled time is required")
    @Future(message = "Scheduled time must be in the future")
    private LocalDateTime scheduledFor;

    private String replyToMessageId;

    private List<String> mediaUrls;

    private String location;

    private String contactInfo;

    private String toneColor;

    private Boolean isEncrypted;

    private String password;

    private Boolean isStarred;

    private List<String> tags;

    private Map<String, Object> metadata;

    private String recurrencePattern; // JSON string for recurrence rules

    private Boolean isRecurring;

    private LocalDateTime endDate;

    private Integer maxRetries;
}
