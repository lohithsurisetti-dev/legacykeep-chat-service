package com.legacykeep.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for WebSocket statistics.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketStats {

    private int totalConnections;
    private int activeConnections;
    private int familySubscriptions;
    private int storySubscriptions;
    private int eventSubscriptions;
    private int chatRoomSubscriptions;
    private long messagesSent;
    private long notificationsSent;
    private long errorsOccurred;

    /**
     * Convert from service WebSocketStats to response DTO
     */
    public static WebSocketStats fromServiceStats(com.legacykeep.chat.service.WebSocketService.WebSocketStats serviceStats) {
        if (serviceStats == null) {
            return null;
        }
        
        return WebSocketStats.builder()
                .totalConnections(serviceStats.getTotalConnections())
                .activeConnections(serviceStats.getActiveConnections())
                .familySubscriptions(serviceStats.getFamilySubscriptions())
                .storySubscriptions(serviceStats.getStorySubscriptions())
                .eventSubscriptions(serviceStats.getEventSubscriptions())
                .chatRoomSubscriptions(serviceStats.getChatRoomSubscriptions())
                .messagesSent(serviceStats.getMessagesSent())
                .notificationsSent(serviceStats.getNotificationsSent())
                .errorsOccurred(serviceStats.getErrorsOccurred())
                .build();
    }
}
