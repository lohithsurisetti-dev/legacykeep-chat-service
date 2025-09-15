package com.legacykeep.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * Message Edit History Entity (MongoDB Document)
 * 
 * Tracks the edit history of messages for version control
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Document(collection = "message_edit_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageEditHistory {

    @Id
    private String id;

    @Field("message_id")
    private String messageId;

    @Field("version")
    private Integer version;

    @Field("previous_content")
    private String previousContent;

    @Field("new_content")
    private String newContent;

    @Field("edited_by_user_id")
    private Long editedByUserId;

    @Field("edit_reason")
    private String editReason;

    @Field("edit_timestamp")
    private LocalDateTime editTimestamp;

    @Field("is_current_version")
    private Boolean isCurrentVersion;

    @Field("edit_type")
    private EditType editType;

    @Field("metadata")
    private String metadata; // JSON string for additional metadata

    public enum EditType {
        CONTENT_EDIT,
        MEDIA_EDIT,
        FORMATTING_EDIT,
        TRANSLATION_EDIT,
        CORRECTION_EDIT
    }
}
