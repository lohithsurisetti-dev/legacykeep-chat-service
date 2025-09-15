package com.legacykeep.chat.dto.response;

import com.legacykeep.chat.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for message search results
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchMessagesResponse {

    private List<MessageResponse> messages;
    
    private long totalElements;
    
    private int totalPages;
    
    private int currentPage;
    
    private int size;
    
    private boolean first;
    
    private boolean last;
    
    private String query;
    
    private long searchTimeMs;
    
    private List<String> highlightedSnippets;
    
    public static SearchMessagesResponse fromMessages(List<Message> messages, String query, long searchTimeMs) {
        List<MessageResponse> messageResponses = messages.stream()
                .map(MessageResponse::fromEntity)
                .toList();
        
        return SearchMessagesResponse.builder()
                .messages(messageResponses)
                .query(query)
                .searchTimeMs(searchTimeMs)
                .build();
    }
}
