package com.legacykeep.chat.service;

import com.legacykeep.chat.dto.request.AddFilterRequest;
import com.legacykeep.chat.dto.response.FilterResponse;

import java.util.List;

/**
 * Service interface for content filtering operations.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public interface ContentFilterService {
    
    // Global User Filters
    /**
     * Get all active global filters for a user
     */
    List<FilterResponse> getUserFilters(Long userId);
    
    /**
     * Add a global filter for a user
     */
    FilterResponse addUserFilter(Long userId, AddFilterRequest request);
    
    /**
     * Remove a global filter
     */
    void removeUserFilter(Long userId, Long filterId);
    
    /**
     * Check if content should be filtered for a user globally
     */
    boolean isContentFilteredGlobally(Long userId, String content);
    
    // Contact-Specific Filters
    /**
     * Get all contact filters for a user
     */
    List<FilterResponse> getContactFilters(Long userId);
    
    /**
     * Get filters for a specific contact
     */
    List<FilterResponse> getContactFilters(Long userId, Long contactUserId);
    
    /**
     * Add a contact-specific filter
     */
    FilterResponse addContactFilter(Long userId, Long contactUserId, AddFilterRequest request);
    
    /**
     * Remove a contact filter
     */
    void removeContactFilter(Long userId, Long contactUserId, Long filterId);
    
    /**
     * Check if content should be filtered for a specific contact
     */
    boolean isContentFilteredForContact(Long userId, Long contactUserId, String content);
    
    // Room Filters
    /**
     * Get all room filters for a room
     */
    List<FilterResponse> getRoomFilters(Long roomId);
    
    /**
     * Add a room filter
     */
    FilterResponse addRoomFilter(Long roomId, Long createdByUserId, AddFilterRequest request);
    
    /**
     * Remove a room filter
     */
    void removeRoomFilter(Long roomId, Long filterId);
    
    /**
     * Check if content should be filtered in a room
     */
    boolean isContentFilteredInRoom(Long roomId, String content);
    
    // Combined Filtering Logic
    /**
     * Check if a message should be filtered based on all applicable filters
     */
    boolean shouldFilterMessage(Long senderUserId, Long receiverUserId, Long roomId, String content);
    
    /**
     * Get all applicable filters for a message
     */
    List<FilterResponse> getApplicableFilters(Long senderUserId, Long receiverUserId, Long roomId, String content);
}
