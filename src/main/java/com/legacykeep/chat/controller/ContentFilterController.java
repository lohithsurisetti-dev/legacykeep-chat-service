package com.legacykeep.chat.controller;

import com.legacykeep.chat.dto.ApiResponse;
import com.legacykeep.chat.dto.request.AddFilterRequest;
import com.legacykeep.chat.dto.response.FilterResponse;
import com.legacykeep.chat.service.ContentFilterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for content filtering operations.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/filters")
@RequiredArgsConstructor
@Slf4j
public class ContentFilterController {
    
    private final ContentFilterService contentFilterService;
    
    // Global User Filters
    /**
     * Get all global filters for a user
     */
    @GetMapping("/global")
    public ResponseEntity<ApiResponse<List<FilterResponse>>> getUserFilters(
            @RequestParam("userId") Long userId) {
        log.debug("Getting global filters for user: {}", userId);
        
        try {
            List<FilterResponse> filters = contentFilterService.getUserFilters(userId);
            return ResponseEntity.ok(ApiResponse.<List<FilterResponse>>builder()
                    .status("success")
                    .message("Global filters retrieved successfully")
                    .data(filters)
                    .build());
        } catch (Exception e) {
            log.error("Error getting global filters: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<FilterResponse>>builder()
                            .status("error")
                            .message("Failed to retrieve global filters: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Add a global filter for a user
     */
    @PostMapping("/global")
    public ResponseEntity<ApiResponse<FilterResponse>> addUserFilter(
            @RequestParam("userId") Long userId,
            @Valid @RequestBody AddFilterRequest request) {
        log.info("Adding global filter for user: {} with content: {}", userId, request.getContent());
        
        try {
            FilterResponse filter = contentFilterService.addUserFilter(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<FilterResponse>builder()
                            .status("success")
                            .message("Global filter added successfully")
                            .data(filter)
                            .build());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for adding global filter: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<FilterResponse>builder()
                            .status("error")
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error adding global filter: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<FilterResponse>builder()
                            .status("error")
                            .message("Failed to add global filter: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Remove a global filter
     */
    @DeleteMapping("/global/{filterId}")
    public ResponseEntity<ApiResponse<Void>> removeUserFilter(
            @PathVariable("filterId") Long filterId,
            @RequestParam("userId") Long userId) {
        log.info("Removing global filter: {} for user: {}", filterId, userId);
        
        try {
            contentFilterService.removeUserFilter(userId, filterId);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status("success")
                    .message("Global filter removed successfully")
                    .build());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for removing global filter: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Void>builder()
                            .status("error")
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error removing global filter: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .status("error")
                            .message("Failed to remove global filter: " + e.getMessage())
                            .build());
        }
    }
    
    // Contact-Specific Filters
    /**
     * Get all contact filters for a user
     */
    @GetMapping("/contacts")
    public ResponseEntity<ApiResponse<List<FilterResponse>>> getContactFilters(
            @RequestParam("userId") Long userId) {
        log.debug("Getting contact filters for user: {}", userId);
        
        try {
            List<FilterResponse> filters = contentFilterService.getContactFilters(userId);
            return ResponseEntity.ok(ApiResponse.<List<FilterResponse>>builder()
                    .status("success")
                    .message("Contact filters retrieved successfully")
                    .data(filters)
                    .build());
        } catch (Exception e) {
            log.error("Error getting contact filters: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<FilterResponse>>builder()
                            .status("error")
                            .message("Failed to retrieve contact filters: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Get filters for a specific contact
     */
    @GetMapping("/contacts/{contactUserId}")
    public ResponseEntity<ApiResponse<List<FilterResponse>>> getContactFilters(
            @PathVariable("contactUserId") Long contactUserId,
            @RequestParam("userId") Long userId) {
        log.debug("Getting contact filters for user: {} and contact: {}", userId, contactUserId);
        
        try {
            List<FilterResponse> filters = contentFilterService.getContactFilters(userId, contactUserId);
            return ResponseEntity.ok(ApiResponse.<List<FilterResponse>>builder()
                    .status("success")
                    .message("Contact filters retrieved successfully")
                    .data(filters)
                    .build());
        } catch (Exception e) {
            log.error("Error getting contact filters: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<FilterResponse>>builder()
                            .status("error")
                            .message("Failed to retrieve contact filters: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Add a contact-specific filter
     */
    @PostMapping("/contacts/{contactUserId}")
    public ResponseEntity<ApiResponse<FilterResponse>> addContactFilter(
            @PathVariable("contactUserId") Long contactUserId,
            @RequestParam("userId") Long userId,
            @Valid @RequestBody AddFilterRequest request) {
        log.info("Adding contact filter for user: {} and contact: {} with content: {}", userId, contactUserId, request.getContent());
        
        try {
            FilterResponse filter = contentFilterService.addContactFilter(userId, contactUserId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<FilterResponse>builder()
                            .status("success")
                            .message("Contact filter added successfully")
                            .data(filter)
                            .build());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for adding contact filter: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<FilterResponse>builder()
                            .status("error")
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error adding contact filter: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<FilterResponse>builder()
                            .status("error")
                            .message("Failed to add contact filter: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Remove a contact filter
     */
    @DeleteMapping("/contacts/{contactUserId}/{filterId}")
    public ResponseEntity<ApiResponse<Void>> removeContactFilter(
            @PathVariable("contactUserId") Long contactUserId,
            @PathVariable("filterId") Long filterId,
            @RequestParam("userId") Long userId) {
        log.info("Removing contact filter: {} for user: {} and contact: {}", filterId, userId, contactUserId);
        
        try {
            contentFilterService.removeContactFilter(userId, contactUserId, filterId);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status("success")
                    .message("Contact filter removed successfully")
                    .build());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for removing contact filter: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Void>builder()
                            .status("error")
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error removing contact filter: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .status("error")
                            .message("Failed to remove contact filter: " + e.getMessage())
                            .build());
        }
    }
    
    // Room Filters
    /**
     * Get all room filters for a room
     */
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ApiResponse<List<FilterResponse>>> getRoomFilters(
            @PathVariable("roomId") Long roomId) {
        log.debug("Getting room filters for room: {}", roomId);
        
        try {
            List<FilterResponse> filters = contentFilterService.getRoomFilters(roomId);
            return ResponseEntity.ok(ApiResponse.<List<FilterResponse>>builder()
                    .status("success")
                    .message("Room filters retrieved successfully")
                    .data(filters)
                    .build());
        } catch (Exception e) {
            log.error("Error getting room filters: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<FilterResponse>>builder()
                            .status("error")
                            .message("Failed to retrieve room filters: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Add a room filter
     */
    @PostMapping("/rooms/{roomId}")
    public ResponseEntity<ApiResponse<FilterResponse>> addRoomFilter(
            @PathVariable("roomId") Long roomId,
            @RequestParam("createdByUserId") Long createdByUserId,
            @Valid @RequestBody AddFilterRequest request) {
        log.info("Adding room filter for room: {} by user: {} with content: {}", roomId, createdByUserId, request.getContent());
        
        try {
            FilterResponse filter = contentFilterService.addRoomFilter(roomId, createdByUserId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<FilterResponse>builder()
                            .status("success")
                            .message("Room filter added successfully")
                            .data(filter)
                            .build());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for adding room filter: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<FilterResponse>builder()
                            .status("error")
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error adding room filter: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<FilterResponse>builder()
                            .status("error")
                            .message("Failed to add room filter: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Remove a room filter
     */
    @DeleteMapping("/rooms/{roomId}/{filterId}")
    public ResponseEntity<ApiResponse<Void>> removeRoomFilter(
            @PathVariable("roomId") Long roomId,
            @PathVariable("filterId") Long filterId) {
        log.info("Removing room filter: {} for room: {}", filterId, roomId);
        
        try {
            contentFilterService.removeRoomFilter(roomId, filterId);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status("success")
                    .message("Room filter removed successfully")
                    .build());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for removing room filter: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Void>builder()
                            .status("error")
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error removing room filter: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .status("error")
                            .message("Failed to remove room filter: " + e.getMessage())
                            .build());
        }
    }
    
    // Filter Testing
    /**
     * Test if content would be filtered
     */
    @PostMapping("/test")
    public ResponseEntity<ApiResponse<Boolean>> testFilter(
            @RequestParam("senderUserId") Long senderUserId,
            @RequestParam("receiverUserId") Long receiverUserId,
            @RequestParam(value = "roomId", required = false) Long roomId,
            @RequestParam("content") String content) {
        log.debug("Testing filter for sender: {}, receiver: {}, room: {}, content: {}", senderUserId, receiverUserId, roomId, content);
        
        try {
            boolean shouldFilter = contentFilterService.shouldFilterMessage(senderUserId, receiverUserId, roomId, content);
            return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("Filter test completed")
                    .data(shouldFilter)
                    .build());
        } catch (Exception e) {
            log.error("Error testing filter: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Boolean>builder()
                            .status("error")
                            .message("Failed to test filter: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Get applicable filters for content
     */
    @PostMapping("/applicable")
    public ResponseEntity<ApiResponse<List<FilterResponse>>> getApplicableFilters(
            @RequestParam("senderUserId") Long senderUserId,
            @RequestParam("receiverUserId") Long receiverUserId,
            @RequestParam(value = "roomId", required = false) Long roomId,
            @RequestParam("content") String content) {
        log.debug("Getting applicable filters for sender: {}, receiver: {}, room: {}, content: {}", senderUserId, receiverUserId, roomId, content);
        
        try {
            List<FilterResponse> filters = contentFilterService.getApplicableFilters(senderUserId, receiverUserId, roomId, content);
            return ResponseEntity.ok(ApiResponse.<List<FilterResponse>>builder()
                    .status("success")
                    .message("Applicable filters retrieved successfully")
                    .data(filters)
                    .build());
        } catch (Exception e) {
            log.error("Error getting applicable filters: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<FilterResponse>>builder()
                            .status("error")
                            .message("Failed to retrieve applicable filters: " + e.getMessage())
                            .build());
        }
    }
}
