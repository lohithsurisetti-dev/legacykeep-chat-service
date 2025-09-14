package com.legacykeep.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for paginated message responses.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedMessageResponse {

    private List<MessageResponse> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int size;
    private boolean first;
    private boolean last;
}
