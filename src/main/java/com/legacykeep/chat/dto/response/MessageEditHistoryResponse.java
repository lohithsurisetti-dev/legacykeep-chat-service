package com.legacykeep.chat.dto.response;

import com.legacykeep.chat.entity.MessageEditHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for message edit history
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageEditHistoryResponse {

    private String id;
    
    private String messageId;
    
    private Integer version;
    
    private String previousContent;
    
    private String newContent;
    
    private Long editedByUserId;
    
    private String editReason;
    
    private LocalDateTime editTimestamp;
    
    private Boolean isCurrentVersion;
    
    private MessageEditHistory.EditType editType;
    
    private String metadata;
    
    public static MessageEditHistoryResponse fromEntity(MessageEditHistory history) {
        return MessageEditHistoryResponse.builder()
                .id(history.getId())
                .messageId(history.getMessageId())
                .version(history.getVersion())
                .previousContent(history.getPreviousContent())
                .newContent(history.getNewContent())
                .editedByUserId(history.getEditedByUserId())
                .editReason(history.getEditReason())
                .editTimestamp(history.getEditTimestamp())
                .isCurrentVersion(history.getIsCurrentVersion())
                .editType(history.getEditType())
                .metadata(history.getMetadata())
                .build();
    }
}
