package com.legacykeep.chat.service.impl;

import com.legacykeep.chat.dto.request.AddFilterRequest;
import com.legacykeep.chat.dto.response.FilterResponse;
import com.legacykeep.chat.entity.UserFilter;
import com.legacykeep.chat.entity.ContactFilter;
import com.legacykeep.chat.entity.RoomFilter;
import com.legacykeep.chat.enums.FilterType;
import com.legacykeep.chat.repository.UserFilterRepository;
import com.legacykeep.chat.repository.ContactFilterRepository;
import com.legacykeep.chat.repository.RoomFilterRepository;
import com.legacykeep.chat.service.ContentFilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of ContentFilterService for managing content filters.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContentFilterServiceImpl implements ContentFilterService {
    
    private final UserFilterRepository userFilterRepository;
    private final ContactFilterRepository contactFilterRepository;
    private final RoomFilterRepository roomFilterRepository;
    
    // Global User Filters
    @Override
    @Transactional(readOnly = true)
    public List<FilterResponse> getUserFilters(Long userId) {
        log.debug("Getting user filters for user: {}", userId);
        List<UserFilter> filters = userFilterRepository.findByUserIdAndIsActiveTrue(userId);
        return filters.stream()
                .map(FilterResponse::fromUserFilter)
                .toList();
    }
    
    @Override
    public FilterResponse addUserFilter(Long userId, AddFilterRequest request) {
        log.info("Adding user filter for user: {} with content: {}", userId, request.getContent());
        
        // Check if filter already exists
        Optional<UserFilter> existingFilter = userFilterRepository.findByUserIdAndContentAndFilterType(
                userId, request.getContent(), request.getFilterType());
        
        UserFilter savedFilter;
        if (existingFilter.isPresent()) {
            // Reactivate if inactive
            UserFilter filter = existingFilter.get();
            if (!filter.getIsActive()) {
                filter.setIsActive(true);
                filter.setDescription(request.getDescription());
                savedFilter = userFilterRepository.save(filter);
                log.info("Reactivated existing user filter: {}", savedFilter.getId());
            } else {
                log.warn("User filter already exists for user: {} with content: {}", userId, request.getContent());
                throw new IllegalArgumentException("Filter already exists");
            }
        } else {
            // Create new filter
            UserFilter filter = UserFilter.builder()
                    .userId(userId)
                    .content(request.getContent())
                    .filterType(request.getFilterType())
                    .description(request.getDescription())
                    .isActive(true)
                    .build();
            
            savedFilter = userFilterRepository.save(filter);
            log.info("Created new user filter: {}", savedFilter.getId());
        }
        
        return FilterResponse.fromUserFilter(savedFilter);
    }
    
    @Override
    public void removeUserFilter(Long userId, Long filterId) {
        log.info("Removing user filter: {} for user: {}", filterId, userId);
        Optional<UserFilter> filter = userFilterRepository.findById(filterId);
        if (filter.isPresent() && filter.get().getUserId().equals(userId)) {
            userFilterRepository.deleteById(filterId);
            log.info("Deleted user filter: {}", filterId);
        } else {
            log.warn("User filter not found or access denied: {}", filterId);
            throw new IllegalArgumentException("Filter not found or access denied");
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isContentFilteredGlobally(Long userId, String content) {
        log.debug("Checking global filters for user: {} with content: {}", userId, content);
        
        // Check word filters
        List<UserFilter> wordFilters = userFilterRepository.findActiveWordFiltersByUserId(userId);
        for (UserFilter filter : wordFilters) {
            if (containsWord(content, filter.getContent())) {
                log.debug("Content filtered by global word filter: {}", filter.getContent());
                return true;
            }
        }
        
        // Check emoji filters
        List<UserFilter> emojiFilters = userFilterRepository.findActiveEmojiFiltersByUserId(userId);
        for (UserFilter filter : emojiFilters) {
            if (content.contains(filter.getContent())) {
                log.debug("Content filtered by global emoji filter: {}", filter.getContent());
                return true;
            }
        }
        
        return false;
    }
    
    // Contact-Specific Filters
    @Override
    @Transactional(readOnly = true)
    public List<FilterResponse> getContactFilters(Long userId) {
        log.debug("Getting all contact filters for user: {}", userId);
        List<ContactFilter> filters = contactFilterRepository.findByUserIdAndIsActiveTrue(userId);
        return filters.stream()
                .map(FilterResponse::fromContactFilter)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FilterResponse> getContactFilters(Long userId, Long contactUserId) {
        log.debug("Getting contact filters for user: {} and contact: {}", userId, contactUserId);
        List<ContactFilter> filters = contactFilterRepository.findByUserIdAndContactUserIdAndIsActiveTrue(userId, contactUserId);
        return filters.stream()
                .map(FilterResponse::fromContactFilter)
                .toList();
    }
    
    @Override
    public FilterResponse addContactFilter(Long userId, Long contactUserId, AddFilterRequest request) {
        log.info("Adding contact filter for user: {} and contact: {} with content: {}", userId, contactUserId, request.getContent());
        
        // Check if filter already exists
        Optional<ContactFilter> existingFilter = contactFilterRepository.findByUserIdAndContactUserIdAndContentAndFilterType(
                userId, contactUserId, request.getContent(), request.getFilterType());
        
        ContactFilter savedFilter;
        if (existingFilter.isPresent()) {
            // Reactivate if inactive
            ContactFilter filter = existingFilter.get();
            if (!filter.getIsActive()) {
                filter.setIsActive(true);
                filter.setDescription(request.getDescription());
                savedFilter = contactFilterRepository.save(filter);
                log.info("Reactivated existing contact filter: {}", savedFilter.getId());
            } else {
                log.warn("Contact filter already exists for user: {} and contact: {} with content: {}", userId, contactUserId, request.getContent());
                throw new IllegalArgumentException("Filter already exists");
            }
        } else {
            // Create new filter
            ContactFilter filter = ContactFilter.builder()
                    .userId(userId)
                    .contactUserId(contactUserId)
                    .content(request.getContent())
                    .filterType(request.getFilterType())
                    .description(request.getDescription())
                    .isActive(true)
                    .build();
            
            savedFilter = contactFilterRepository.save(filter);
            log.info("Created new contact filter: {}", savedFilter.getId());
        }
        
        return FilterResponse.fromContactFilter(savedFilter);
    }
    
    @Override
    public void removeContactFilter(Long userId, Long contactUserId, Long filterId) {
        log.info("Removing contact filter: {} for user: {} and contact: {}", filterId, userId, contactUserId);
        Optional<ContactFilter> filter = contactFilterRepository.findById(filterId);
        if (filter.isPresent() && filter.get().getUserId().equals(userId) && filter.get().getContactUserId().equals(contactUserId)) {
            contactFilterRepository.deleteById(filterId);
            log.info("Deleted contact filter: {}", filterId);
        } else {
            log.warn("Contact filter not found or access denied: {}", filterId);
            throw new IllegalArgumentException("Filter not found or access denied");
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isContentFilteredForContact(Long userId, Long contactUserId, String content) {
        log.debug("Checking contact filters for user: {} and contact: {} with content: {}", userId, contactUserId, content);
        
        // Check word filters
        List<ContactFilter> wordFilters = contactFilterRepository.findActiveWordFiltersByUserIdAndContactUserId(userId, contactUserId);
        for (ContactFilter filter : wordFilters) {
            if (containsWord(content, filter.getContent())) {
                log.debug("Content filtered by contact word filter: {}", filter.getContent());
                return true;
            }
        }
        
        // Check emoji filters
        List<ContactFilter> emojiFilters = contactFilterRepository.findActiveEmojiFiltersByUserIdAndContactUserId(userId, contactUserId);
        for (ContactFilter filter : emojiFilters) {
            if (content.contains(filter.getContent())) {
                log.debug("Content filtered by contact emoji filter: {}", filter.getContent());
                return true;
            }
        }
        
        return false;
    }
    
    // Room Filters
    @Override
    @Transactional(readOnly = true)
    public List<FilterResponse> getRoomFilters(Long roomId) {
        log.debug("Getting room filters for room: {}", roomId);
        List<RoomFilter> filters = roomFilterRepository.findByRoomIdAndIsActiveTrue(roomId);
        return filters.stream()
                .map(FilterResponse::fromRoomFilter)
                .toList();
    }
    
    @Override
    public FilterResponse addRoomFilter(Long roomId, Long createdByUserId, AddFilterRequest request) {
        log.info("Adding room filter for room: {} by user: {} with content: {}", roomId, createdByUserId, request.getContent());
        
        // Check if filter already exists
        Optional<RoomFilter> existingFilter = roomFilterRepository.findByRoomIdAndContentAndFilterType(
                roomId, request.getContent(), request.getFilterType());
        
        RoomFilter savedFilter;
        if (existingFilter.isPresent()) {
            // Reactivate if inactive
            RoomFilter filter = existingFilter.get();
            if (!filter.getIsActive()) {
                filter.setIsActive(true);
                filter.setDescription(request.getDescription());
                savedFilter = roomFilterRepository.save(filter);
                log.info("Reactivated existing room filter: {}", savedFilter.getId());
            } else {
                log.warn("Room filter already exists for room: {} with content: {}", roomId, request.getContent());
                throw new IllegalArgumentException("Filter already exists");
            }
        } else {
            // Create new filter
            RoomFilter filter = RoomFilter.builder()
                    .roomId(roomId)
                    .createdByUserId(createdByUserId)
                    .content(request.getContent())
                    .filterType(request.getFilterType())
                    .description(request.getDescription())
                    .isActive(true)
                    .build();
            
            savedFilter = roomFilterRepository.save(filter);
            log.info("Created new room filter: {}", savedFilter.getId());
        }
        
        return FilterResponse.fromRoomFilter(savedFilter);
    }
    
    @Override
    public void removeRoomFilter(Long roomId, Long filterId) {
        log.info("Removing room filter: {} for room: {}", filterId, roomId);
        Optional<RoomFilter> filter = roomFilterRepository.findById(filterId);
        if (filter.isPresent() && filter.get().getRoomId().equals(roomId)) {
            roomFilterRepository.deleteById(filterId);
            log.info("Deleted room filter: {}", filterId);
        } else {
            log.warn("Room filter not found or access denied: {}", filterId);
            throw new IllegalArgumentException("Filter not found or access denied");
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isContentFilteredInRoom(Long roomId, String content) {
        log.debug("Checking room filters for room: {} with content: {}", roomId, content);
        
        // Check word filters
        List<RoomFilter> wordFilters = roomFilterRepository.findActiveWordFiltersByRoomId(roomId);
        for (RoomFilter filter : wordFilters) {
            if (containsWord(content, filter.getContent())) {
                log.debug("Content filtered by room word filter: {}", filter.getContent());
                return true;
            }
        }
        
        // Check emoji filters
        List<RoomFilter> emojiFilters = roomFilterRepository.findActiveEmojiFiltersByRoomId(roomId);
        for (RoomFilter filter : emojiFilters) {
            if (content.contains(filter.getContent())) {
                log.debug("Content filtered by room emoji filter: {}", filter.getContent());
                return true;
            }
        }
        
        return false;
    }
    
    // Combined Filtering Logic
    @Override
    @Transactional(readOnly = true)
    public boolean shouldFilterMessage(Long senderUserId, Long receiverUserId, Long roomId, String content) {
        log.debug("Checking if message should be filtered for sender: {}, receiver: {}, room: {}", senderUserId, receiverUserId, roomId);
        
        // Priority 1: Global filters (receiver's global filters)
        if (isContentFilteredGlobally(receiverUserId, content)) {
            log.debug("Message filtered by global filters");
            return true;
        }
        
        // Priority 2: Contact filters (receiver's filters for sender)
        if (isContentFilteredForContact(receiverUserId, senderUserId, content)) {
            log.debug("Message filtered by contact filters");
            return true;
        }
        
        // Priority 3: Room filters (if in a group)
        if (roomId != null && isContentFilteredInRoom(roomId, content)) {
            log.debug("Message filtered by room filters");
            return true;
        }
        
        return false;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FilterResponse> getApplicableFilters(Long senderUserId, Long receiverUserId, Long roomId, String content) {
        log.debug("Getting applicable filters for sender: {}, receiver: {}, room: {}", senderUserId, receiverUserId, roomId);
        List<FilterResponse> applicableFilters = new ArrayList<>();
        
        // Check global filters
        List<UserFilter> globalFilters = userFilterRepository.findByUserIdAndIsActiveTrue(receiverUserId);
        for (UserFilter filter : globalFilters) {
            if (isContentMatchingFilter(content, filter.getContent(), filter.getFilterType())) {
                applicableFilters.add(FilterResponse.fromUserFilter(filter));
            }
        }
        
        // Check contact filters
        List<ContactFilter> contactFilters = contactFilterRepository.findByUserIdAndContactUserIdAndIsActiveTrue(receiverUserId, senderUserId);
        for (ContactFilter filter : contactFilters) {
            if (isContentMatchingFilter(content, filter.getContent(), filter.getFilterType())) {
                applicableFilters.add(FilterResponse.fromContactFilter(filter));
            }
        }
        
        // Check room filters
        if (roomId != null) {
            List<RoomFilter> roomFilters = roomFilterRepository.findByRoomIdAndIsActiveTrue(roomId);
            for (RoomFilter filter : roomFilters) {
                if (isContentMatchingFilter(content, filter.getContent(), filter.getFilterType())) {
                    applicableFilters.add(FilterResponse.fromRoomFilter(filter));
                }
            }
        }
        
        return applicableFilters;
    }
    
    // Helper Methods
    private boolean containsWord(String content, String word) {
        if (content == null || word == null) return false;
        
        // Case-insensitive word boundary matching
        String lowerContent = content.toLowerCase();
        String lowerWord = word.toLowerCase();
        
        // Check for exact word match with word boundaries
        return lowerContent.matches(".*\\b" + java.util.regex.Pattern.quote(lowerWord) + "\\b.*");
    }
    
    private boolean isContentMatchingFilter(String content, String filterContent, FilterType filterType) {
        if (content == null || filterContent == null) return false;
        
        switch (filterType) {
            case WORD:
                return containsWord(content, filterContent);
            case EMOJI:
                return content.contains(filterContent);
            case PHRASE:
                return content.toLowerCase().contains(filterContent.toLowerCase());
            default:
                return false;
        }
    }
}
