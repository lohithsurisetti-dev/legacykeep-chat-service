package com.legacykeep.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for message statistics.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageStats {

    private long totalMessages;
    private long textMessages;
    private long mediaMessages;
    private long voiceMessages;
    private long starredMessages;
    private long protectedMessages;
    private long messagesWithToneColor;
    private long messagesWithAIFeatures;
    private long messagesWithMedia;
    private long messagesWithLocation;
    private long messagesWithContact;
    private long storyMessages;
    private long memoryMessages;
    private long eventMessages;

    /**
     * Convert from service MessageStats to response DTO
     */
    public static MessageStats fromServiceStats(com.legacykeep.chat.service.MessageService.MessageStats serviceStats) {
        if (serviceStats == null) {
            return null;
        }
        
        return MessageStats.builder()
                .totalMessages(serviceStats.getTotalMessages())
                .textMessages(serviceStats.getTextMessages())
                .mediaMessages(serviceStats.getMediaMessages())
                .voiceMessages(serviceStats.getVoiceMessages())
                .starredMessages(serviceStats.getStarredMessages())
                .protectedMessages(serviceStats.getProtectedMessages())
                .messagesWithToneColor(serviceStats.getMessagesWithToneColor())
                .messagesWithAIFeatures(serviceStats.getMessagesWithAIFeatures())
                .messagesWithMedia(serviceStats.getMessagesWithMedia())
                .messagesWithLocation(serviceStats.getMessagesWithLocation())
                .messagesWithContact(serviceStats.getMessagesWithContact())
                .storyMessages(serviceStats.getStoryMessages())
                .memoryMessages(serviceStats.getMemoryMessages())
                .eventMessages(serviceStats.getEventMessages())
                .build();
    }
}
