package com.legacykeep.chat.entity;

import com.legacykeep.chat.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Scheduled Message Entity (MongoDB Document)
 * 
 * Represents messages that are scheduled for future delivery
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Document(collection = "scheduled_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledMessage {

    @Id
    private String id;

    @Field("message_uuid")
    private String messageUuid;

    @Field("chat_room_id")
    private Long chatRoomId;

    @Field("sender_user_id")
    private Long senderUserId;

    @Field("content")
    private String content;

    @Field("message_type")
    private MessageType messageType;

    @Field("scheduled_for")
    private LocalDateTime scheduledFor;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("status")
    private ScheduledStatus status;

    @Field("retry_count")
    private Integer retryCount;

    @Field("max_retries")
    private Integer maxRetries;

    @Field("last_attempt")
    private LocalDateTime lastAttempt;

    @Field("error_message")
    private String errorMessage;

    @Field("metadata")
    private Map<String, Object> metadata;

    @Field("recurrence_pattern")
    private String recurrencePattern; // JSON string for recurrence rules

    @Field("is_recurring")
    private Boolean isRecurring;

    @Field("next_execution")
    private LocalDateTime nextExecution;

    @Field("end_date")
    private LocalDateTime endDate;

    public enum ScheduledStatus {
        PENDING,
        PROCESSING,
        SENT,
        FAILED,
        CANCELLED,
        EXPIRED
    }
}
