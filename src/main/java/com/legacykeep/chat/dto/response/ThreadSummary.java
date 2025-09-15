package com.legacykeep.chat.dto.response;

import com.legacykeep.chat.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Thread Summary DTO for message threading
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadSummary {

    private String threadId;
    
    private MessageResponse rootMessage;
    
    private long replyCount;
    
    private MessageResponse latestReply;
    
    private LocalDateTime latestReplyAt;
    
    private List<MessageResponse> recentReplies;
    
    private boolean hasMoreReplies;
    
    private long totalReplies;
    
    public static ThreadSummary fromMessage(Message rootMessage, long replyCount, 
                                          Message latestReply, List<Message> recentReplies) {
        return ThreadSummary.builder()
                .threadId(rootMessage.getId())
                .rootMessage(MessageResponse.fromEntity(rootMessage))
                .replyCount(replyCount)
                .latestReply(latestReply != null ? MessageResponse.fromEntity(latestReply) : null)
                .latestReplyAt(latestReply != null ? latestReply.getCreatedAt() : null)
                .recentReplies(recentReplies.stream().map(MessageResponse::fromEntity).toList())
                .hasMoreReplies(replyCount > recentReplies.size())
                .totalReplies(replyCount)
                .build();
    }
}
