package com.legacykeep.chat.controller;

import com.legacykeep.chat.dto.ApiResponse;
import com.legacykeep.chat.dto.request.CreateChatRoomRequest;
import com.legacykeep.chat.dto.request.UpdateChatRoomRequest;
import com.legacykeep.chat.dto.request.AddParticipantRequest;
import com.legacykeep.chat.dto.request.RemoveParticipantRequest;
import com.legacykeep.chat.dto.request.ArchiveChatRoomRequest;
import com.legacykeep.chat.dto.request.MuteChatRoomRequest;
import com.legacykeep.chat.dto.request.UpdateChatRoomSettingsRequest;
import com.legacykeep.chat.dto.response.ChatRoomResponse;
import com.legacykeep.chat.dto.response.PaginatedChatRoomResponse;
import com.legacykeep.chat.entity.ChatRoom;
import com.legacykeep.chat.enums.ChatRoomStatus;
import com.legacykeep.chat.enums.ChatRoomType;
import com.legacykeep.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for Chat Room management.
 * Provides comprehensive APIs for family-centric chat room operations.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/chat-rooms")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * Create a new chat room
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ChatRoomResponse>> createChatRoom(@Valid @RequestBody CreateChatRoomRequest request) {
        log.info("Creating chat room: {} by user: {}", request.getName(), request.getCreatedByUserId());
        
        try {
            ChatRoom chatRoom = chatRoomService.createChatRoom(request);
            ChatRoomResponse response = ChatRoomResponse.fromEntity(chatRoom);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<ChatRoomResponse>builder()
                            .success(true)
                            .message("Chat room created successfully")
                            .data(response)
                            .build());
        } catch (Exception e) {
            log.error("Error creating chat room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to create chat room: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get chat room by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> getChatRoomById(@PathVariable("id") Long id) {
        log.debug("Getting chat room by ID: {}", id);
        
        try {
            Optional<ChatRoom> chatRoom = chatRoomService.getChatRoomById(id);
            if (chatRoom.isPresent()) {
                ChatRoomResponse response = ChatRoomResponse.fromEntity(chatRoom.get());
                return ResponseEntity.ok(ApiResponse.<ChatRoomResponse>builder()
                        .success(true)
                        .message("Chat room retrieved successfully")
                        .data(response)
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<ChatRoomResponse>builder()
                                .success(false)
                                .message("Chat room not found with ID: " + id)
                                .build());
            }
        } catch (Exception e) {
            log.error("Error getting chat room by ID: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to retrieve chat room: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get chat room by UUID
     */
    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> getChatRoomByUuid(@PathVariable("uuid") String uuid) {
        log.debug("Getting chat room by UUID: {}", uuid);
        
        try {
            Optional<ChatRoom> chatRoom = chatRoomService.getChatRoomByUuid(UUID.fromString(uuid));
            if (chatRoom.isPresent()) {
                ChatRoomResponse response = ChatRoomResponse.fromEntity(chatRoom.get());
                return ResponseEntity.ok(ApiResponse.<ChatRoomResponse>builder()
                        .success(true)
                        .message("Chat room retrieved successfully")
                        .data(response)
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<ChatRoomResponse>builder()
                                .success(false)
                                .message("Chat room not found with UUID: " + uuid)
                                .build());
            }
        } catch (Exception e) {
            log.error("Error getting chat room by UUID: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to retrieve chat room: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Update chat room
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> updateChatRoom(
            @PathVariable("id") Long id, 
            @Valid @RequestBody UpdateChatRoomRequest request) {
        log.info("Updating chat room: {} by user: {}", id, request.getUpdatedByUserId());
        
        try {
            ChatRoom updatedChatRoom = chatRoomService.updateChatRoom(id, request);
            ChatRoomResponse response = ChatRoomResponse.fromEntity(updatedChatRoom);
            
            return ResponseEntity.ok(ApiResponse.<ChatRoomResponse>builder()
                    .success(true)
                    .message("Chat room updated successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error updating chat room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to update chat room: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Delete chat room
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteChatRoom(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        log.info("Deleting chat room: {} by user: {}", id, userId);
        
        try {
            chatRoomService.deleteChatRoom(id);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Chat room deleted successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error deleting chat room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to delete chat room: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get all chat rooms with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedChatRoomResponse>> getAllChatRooms(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting all chat rooms with pagination");
        
        try {
            Page<ChatRoom> chatRooms = chatRoomService.getAllChatRooms(pageable);
            List<ChatRoomResponse> responses = chatRooms.getContent().stream()
                    .map(ChatRoomResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedChatRoomResponse paginatedResponse = PaginatedChatRoomResponse.builder()
                    .content(responses)
                    .totalElements(chatRooms.getTotalElements())
                    .totalPages(chatRooms.getTotalPages())
                    .currentPage(chatRooms.getNumber())
                    .size(chatRooms.getSize())
                    .first(chatRooms.isFirst())
                    .last(chatRooms.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedChatRoomResponse>builder()
                    .success(true)
                    .message("Chat rooms retrieved successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error getting all chat rooms: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to retrieve chat rooms: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get chat rooms by type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<PaginatedChatRoomResponse>> getChatRoomsByType(
            @PathVariable("type") ChatRoomType type,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting chat rooms by type: {}", type);
        
        try {
            Page<ChatRoom> chatRooms = chatRoomService.getChatRoomsByType(type, pageable);
            List<ChatRoomResponse> responses = chatRooms.getContent().stream()
                    .map(ChatRoomResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedChatRoomResponse paginatedResponse = PaginatedChatRoomResponse.builder()
                    .content(responses)
                    .totalElements(chatRooms.getTotalElements())
                    .totalPages(chatRooms.getTotalPages())
                    .currentPage(chatRooms.getNumber())
                    .size(chatRooms.getSize())
                    .first(chatRooms.isFirst())
                    .last(chatRooms.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedChatRoomResponse>builder()
                    .success(true)
                    .message("Chat rooms retrieved successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error getting chat rooms by type: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to retrieve chat rooms: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get chat rooms by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<PaginatedChatRoomResponse>> getChatRoomsByStatus(
            @PathVariable("status") ChatRoomStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting chat rooms by status: {}", status);
        
        try {
            Page<ChatRoom> chatRooms = chatRoomService.getChatRoomsByStatus(status, pageable);
            List<ChatRoomResponse> responses = chatRooms.getContent().stream()
                    .map(ChatRoomResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedChatRoomResponse paginatedResponse = PaginatedChatRoomResponse.builder()
                    .content(responses)
                    .totalElements(chatRooms.getTotalElements())
                    .totalPages(chatRooms.getTotalPages())
                    .currentPage(chatRooms.getNumber())
                    .size(chatRooms.getSize())
                    .first(chatRooms.isFirst())
                    .last(chatRooms.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedChatRoomResponse>builder()
                    .success(true)
                    .message("Chat rooms retrieved successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error getting chat rooms by status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to retrieve chat rooms: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get chat rooms by creator
     */
    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<ApiResponse<PaginatedChatRoomResponse>> getChatRoomsByCreator(
            @PathVariable("creatorId") Long creatorId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting chat rooms by creator: {}", creatorId);
        
        try {
            Page<ChatRoom> chatRooms = chatRoomService.getChatRoomsByCreator(creatorId, pageable);
            List<ChatRoomResponse> responses = chatRooms.getContent().stream()
                    .map(ChatRoomResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedChatRoomResponse paginatedResponse = PaginatedChatRoomResponse.builder()
                    .content(responses)
                    .totalElements(chatRooms.getTotalElements())
                    .totalPages(chatRooms.getTotalPages())
                    .currentPage(chatRooms.getNumber())
                    .size(chatRooms.getSize())
                    .first(chatRooms.isFirst())
                    .last(chatRooms.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedChatRoomResponse>builder()
                    .success(true)
                    .message("Chat rooms retrieved successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error getting chat rooms by creator: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to retrieve chat rooms: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get chat rooms by family
     */
    @GetMapping("/family/{familyId}")
    public ResponseEntity<ApiResponse<PaginatedChatRoomResponse>> getChatRoomsByFamily(
            @PathVariable("familyId") Long familyId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting chat rooms by family: {}", familyId);
        
        try {
            Page<ChatRoom> chatRooms = chatRoomService.getChatRoomsByFamily(familyId, pageable);
            List<ChatRoomResponse> responses = chatRooms.getContent().stream()
                    .map(ChatRoomResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedChatRoomResponse paginatedResponse = PaginatedChatRoomResponse.builder()
                    .content(responses)
                    .totalElements(chatRooms.getTotalElements())
                    .totalPages(chatRooms.getTotalPages())
                    .currentPage(chatRooms.getNumber())
                    .size(chatRooms.getSize())
                    .first(chatRooms.isFirst())
                    .last(chatRooms.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedChatRoomResponse>builder()
                    .success(true)
                    .message("Chat rooms retrieved successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error getting chat rooms by family: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to retrieve chat rooms: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get chat rooms by story
     */
    @GetMapping("/story/{storyId}")
    public ResponseEntity<ApiResponse<PaginatedChatRoomResponse>> getChatRoomsByStory(
            @PathVariable("storyId") Long storyId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting chat rooms by story: {}", storyId);
        
        try {
            Page<ChatRoom> chatRooms = chatRoomService.getChatRoomsByStory(storyId, pageable);
            List<ChatRoomResponse> responses = chatRooms.getContent().stream()
                    .map(ChatRoomResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedChatRoomResponse paginatedResponse = PaginatedChatRoomResponse.builder()
                    .content(responses)
                    .totalElements(chatRooms.getTotalElements())
                    .totalPages(chatRooms.getTotalPages())
                    .currentPage(chatRooms.getNumber())
                    .size(chatRooms.getSize())
                    .first(chatRooms.isFirst())
                    .last(chatRooms.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedChatRoomResponse>builder()
                    .success(true)
                    .message("Chat rooms retrieved successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error getting chat rooms by story: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to retrieve chat rooms: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get chat rooms by event
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<PaginatedChatRoomResponse>> getChatRoomsByEvent(
            @PathVariable("eventId") Long eventId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting chat rooms by event: {}", eventId);
        
        try {
            Page<ChatRoom> chatRooms = chatRoomService.getChatRoomsByEvent(eventId, pageable);
            List<ChatRoomResponse> responses = chatRooms.getContent().stream()
                    .map(ChatRoomResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedChatRoomResponse paginatedResponse = PaginatedChatRoomResponse.builder()
                    .content(responses)
                    .totalElements(chatRooms.getTotalElements())
                    .totalPages(chatRooms.getTotalPages())
                    .currentPage(chatRooms.getNumber())
                    .size(chatRooms.getSize())
                    .first(chatRooms.isFirst())
                    .last(chatRooms.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedChatRoomResponse>builder()
                    .success(true)
                    .message("Chat rooms retrieved successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error getting chat rooms by event: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to retrieve chat rooms: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get chat rooms by participant
     */
    @GetMapping("/participant/{userId}")
    public ResponseEntity<ApiResponse<PaginatedChatRoomResponse>> getChatRoomsByParticipant(
            @PathVariable("userId") Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting chat rooms by participant: {}", userId);
        
        try {
            Page<ChatRoom> chatRooms = chatRoomService.getChatRoomsByParticipant(userId, pageable);
            List<ChatRoomResponse> responses = chatRooms.getContent().stream()
                    .map(ChatRoomResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedChatRoomResponse paginatedResponse = PaginatedChatRoomResponse.builder()
                    .content(responses)
                    .totalElements(chatRooms.getTotalElements())
                    .totalPages(chatRooms.getTotalPages())
                    .currentPage(chatRooms.getNumber())
                    .size(chatRooms.getSize())
                    .first(chatRooms.isFirst())
                    .last(chatRooms.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedChatRoomResponse>builder()
                    .success(true)
                    .message("Chat rooms retrieved successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error getting chat rooms by participant: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to retrieve chat rooms: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get recent chat rooms
     */
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<PaginatedChatRoomResponse>> getRecentChatRooms(
            @RequestParam(defaultValue = "7") int days,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting recent chat rooms for last {} days", days);
        
        try {
            Page<ChatRoom> chatRooms = chatRoomService.getRecentChatRooms(days, pageable);
            List<ChatRoomResponse> responses = chatRooms.getContent().stream()
                    .map(ChatRoomResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedChatRoomResponse paginatedResponse = PaginatedChatRoomResponse.builder()
                    .content(responses)
                    .totalElements(chatRooms.getTotalElements())
                    .totalPages(chatRooms.getTotalPages())
                    .currentPage(chatRooms.getNumber())
                    .size(chatRooms.getSize())
                    .first(chatRooms.isFirst())
                    .last(chatRooms.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedChatRoomResponse>builder()
                    .success(true)
                    .message("Recent chat rooms retrieved successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error getting recent chat rooms: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to retrieve recent chat rooms: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get archived chat rooms
     */
    @GetMapping("/archived")
    public ResponseEntity<ApiResponse<PaginatedChatRoomResponse>> getArchivedChatRooms(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting archived chat rooms");
        
        try {
            Page<ChatRoom> chatRooms = chatRoomService.getArchivedChatRooms(pageable);
            List<ChatRoomResponse> responses = chatRooms.getContent().stream()
                    .map(ChatRoomResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedChatRoomResponse paginatedResponse = PaginatedChatRoomResponse.builder()
                    .content(responses)
                    .totalElements(chatRooms.getTotalElements())
                    .totalPages(chatRooms.getTotalPages())
                    .currentPage(chatRooms.getNumber())
                    .size(chatRooms.getSize())
                    .first(chatRooms.isFirst())
                    .last(chatRooms.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedChatRoomResponse>builder()
                    .success(true)
                    .message("Archived chat rooms retrieved successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error getting archived chat rooms: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to retrieve archived chat rooms: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get muted chat rooms
     */
    @GetMapping("/muted")
    public ResponseEntity<ApiResponse<PaginatedChatRoomResponse>> getMutedChatRooms(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting muted chat rooms");
        
        try {
            Page<ChatRoom> chatRooms = chatRoomService.getMutedChatRooms(pageable);
            List<ChatRoomResponse> responses = chatRooms.getContent().stream()
                    .map(ChatRoomResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedChatRoomResponse paginatedResponse = PaginatedChatRoomResponse.builder()
                    .content(responses)
                    .totalElements(chatRooms.getTotalElements())
                    .totalPages(chatRooms.getTotalPages())
                    .currentPage(chatRooms.getNumber())
                    .size(chatRooms.getSize())
                    .first(chatRooms.isFirst())
                    .last(chatRooms.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedChatRoomResponse>builder()
                    .success(true)
                    .message("Muted chat rooms retrieved successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error getting muted chat rooms: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to retrieve muted chat rooms: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get encrypted chat rooms
     */
    @GetMapping("/encrypted")
    public ResponseEntity<ApiResponse<PaginatedChatRoomResponse>> getEncryptedChatRooms(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting encrypted chat rooms");
        
        try {
            Page<ChatRoom> chatRooms = chatRoomService.getEncryptedChatRooms(pageable);
            List<ChatRoomResponse> responses = chatRooms.getContent().stream()
                    .map(ChatRoomResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedChatRoomResponse paginatedResponse = PaginatedChatRoomResponse.builder()
                    .content(responses)
                    .totalElements(chatRooms.getTotalElements())
                    .totalPages(chatRooms.getTotalPages())
                    .currentPage(chatRooms.getNumber())
                    .size(chatRooms.getSize())
                    .first(chatRooms.isFirst())
                    .last(chatRooms.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedChatRoomResponse>builder()
                    .success(true)
                    .message("Encrypted chat rooms retrieved successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error getting encrypted chat rooms: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to retrieve encrypted chat rooms: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Search chat rooms by name
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PaginatedChatRoomResponse>> searchChatRoomsByName(
            @RequestParam("name") String name,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Searching chat rooms by name: {}", name);
        
        try {
            Page<ChatRoom> chatRooms = chatRoomService.searchChatRoomsByName(name, pageable);
            List<ChatRoomResponse> responses = chatRooms.getContent().stream()
                    .map(ChatRoomResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedChatRoomResponse paginatedResponse = PaginatedChatRoomResponse.builder()
                    .content(responses)
                    .totalElements(chatRooms.getTotalElements())
                    .totalPages(chatRooms.getTotalPages())
                    .currentPage(chatRooms.getNumber())
                    .size(chatRooms.getSize())
                    .first(chatRooms.isFirst())
                    .last(chatRooms.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedChatRoomResponse>builder()
                    .success(true)
                    .message("Chat rooms search completed successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error searching chat rooms by name: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to search chat rooms: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Add participant to chat room
     */
    @PostMapping("/{id}/participants")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> addParticipant(
            @PathVariable("id") Long id,
            @Valid @RequestBody AddParticipantRequest request) {
        log.info("Adding participant {} to chat room: {} by user: {}", request.getUserId(), id, request.getAddedByUserId());
        
        try {
            ChatRoom updatedChatRoom = chatRoomService.addParticipant(id, request);
            ChatRoomResponse response = ChatRoomResponse.fromEntity(updatedChatRoom);
            
            return ResponseEntity.ok(ApiResponse.<ChatRoomResponse>builder()
                    .success(true)
                    .message("Participant added successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error adding participant to chat room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to add participant: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Remove participant from chat room
     */
    @DeleteMapping("/{id}/participants/{userId}")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> removeParticipant(
            @PathVariable("id") Long id,
            @PathVariable("userId") Long userId,
            @RequestParam("removedByUserId") Long removedByUserId) {
        log.info("Removing participant {} from chat room: {} by user: {}", userId, id, removedByUserId);
        
        try {
            RemoveParticipantRequest request = RemoveParticipantRequest.builder()
                    .userId(userId)
                    .removedByUserId(removedByUserId)
                    .build();
            
            ChatRoom updatedChatRoom = chatRoomService.removeParticipant(id, request);
            ChatRoomResponse response = ChatRoomResponse.fromEntity(updatedChatRoom);
            
            return ResponseEntity.ok(ApiResponse.<ChatRoomResponse>builder()
                    .success(true)
                    .message("Participant removed successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error removing participant from chat room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to remove participant: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Archive chat room
     */
    @PostMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> archiveChatRoom(
            @PathVariable("id") Long id,
            @Valid @RequestBody ArchiveChatRoomRequest request) {
        log.info("Archiving chat room: {} by user: {}", id, request.getArchivedByUserId());
        
        try {
            ChatRoom updatedChatRoom = chatRoomService.archiveChatRoom(id, request);
            ChatRoomResponse response = ChatRoomResponse.fromEntity(updatedChatRoom);
            
            return ResponseEntity.ok(ApiResponse.<ChatRoomResponse>builder()
                    .success(true)
                    .message("Chat room archived successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error archiving chat room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to archive chat room: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Unarchive chat room
     */
    @PostMapping("/{id}/unarchive")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> unarchiveChatRoom(
            @PathVariable("id") Long id,
            @RequestParam("userId") Long userId) {
        log.info("Unarchiving chat room: {} by user: {}", id, userId);
        
        try {
            ChatRoom updatedChatRoom = chatRoomService.unarchiveChatRoom(id, userId);
            ChatRoomResponse response = ChatRoomResponse.fromEntity(updatedChatRoom);
            
            return ResponseEntity.ok(ApiResponse.<ChatRoomResponse>builder()
                    .success(true)
                    .message("Chat room unarchived successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error unarchiving chat room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to unarchive chat room: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Mute chat room
     */
    @PostMapping("/{id}/mute")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> muteChatRoom(
            @PathVariable("id") Long id,
            @Valid @RequestBody MuteChatRoomRequest request) {
        log.info("Muting chat room: {} by user: {}", id, request.getMutedByUserId());
        
        try {
            ChatRoom updatedChatRoom = chatRoomService.muteChatRoom(id, request);
            ChatRoomResponse response = ChatRoomResponse.fromEntity(updatedChatRoom);
            
            return ResponseEntity.ok(ApiResponse.<ChatRoomResponse>builder()
                    .success(true)
                    .message("Chat room muted successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error muting chat room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to mute chat room: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Unmute chat room
     */
    @PostMapping("/{id}/unmute")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> unmuteChatRoom(
            @PathVariable("id") Long id,
            @RequestParam("userId") Long userId) {
        log.info("Unmuting chat room: {} by user: {}", id, userId);
        
        try {
            ChatRoom updatedChatRoom = chatRoomService.unmuteChatRoom(id, userId);
            ChatRoomResponse response = ChatRoomResponse.fromEntity(updatedChatRoom);
            
            return ResponseEntity.ok(ApiResponse.<ChatRoomResponse>builder()
                    .success(true)
                    .message("Chat room unmuted successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error unmuting chat room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to unmute chat room: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Update chat room settings
     */
    @PutMapping("/{id}/settings")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> updateChatRoomSettings(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateChatRoomSettingsRequest request) {
        log.info("Updating chat room settings for: {} by user: {}", id, request.getUpdatedByUserId());
        
        try {
            ChatRoom updatedChatRoom = chatRoomService.updateChatRoomSettings(id, request);
            ChatRoomResponse response = ChatRoomResponse.fromEntity(updatedChatRoom);
            
            return ResponseEntity.ok(ApiResponse.<ChatRoomResponse>builder()
                    .success(true)
                    .message("Chat room settings updated successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error updating chat room settings: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to update chat room settings: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get chat room statistics
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<ApiResponse<ChatRoomService.ChatRoomStats>> getChatRoomStats(@PathVariable("id") Long id) {
        log.debug("Getting chat room statistics for: {}", id);
        
        try {
            ChatRoomService.ChatRoomStats stats = chatRoomService.getChatRoomStats(id);
            return ResponseEntity.ok(ApiResponse.<ChatRoomService.ChatRoomStats>builder()
                    .success(true)
                    .message("Chat room statistics retrieved successfully")
                    .data(stats)
                    .build());
        } catch (Exception e) {
            log.error("Error getting chat room statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ChatRoomService.ChatRoomStats>builder()
                            .success(false)
                            .message("Failed to retrieve chat room statistics: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Check if individual chat exists
     */
    @GetMapping("/individual/check")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> checkIndividualChat(
            @RequestParam("user1Id") Long user1Id,
            @RequestParam("user2Id") Long user2Id) {
        log.debug("Checking individual chat between users: {} and {}", user1Id, user2Id);
        
        try {
            boolean chatExists = chatRoomService.checkIndividualChat(user1Id, user2Id);
            if (chatExists) {
                return ResponseEntity.ok(ApiResponse.<ChatRoomResponse>builder()
                        .success(true)
                        .message("Individual chat exists between these users")
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<ChatRoomResponse>builder()
                                .success(false)
                                .message("Individual chat not found")
                                .build());
            }
        } catch (Exception e) {
            log.error("Error checking individual chat: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ChatRoomResponse>builder()
                            .success(false)
                            .message("Failed to check individual chat: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get chat room participants
     */
    @GetMapping("/{id}/participants")
    public ResponseEntity<ApiResponse<List<Long>>> getChatRoomParticipants(@PathVariable("id") Long id) {
        log.debug("Getting participants for chat room: {}", id);
        
        try {
            List<Long> participants = chatRoomService.getChatRoomParticipants(id);
            return ResponseEntity.ok(ApiResponse.<List<Long>>builder()
                    .success(true)
                    .message("Chat room participants retrieved successfully")
                    .data(participants)
                    .build());
        } catch (Exception e) {
            log.error("Error getting chat room participants: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<Long>>builder()
                            .success(false)
                            .message("Failed to retrieve chat room participants: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get chat room count
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getChatRoomCount() {
        log.debug("Getting total chat room count");
        
        try {
            long count = chatRoomService.getChatRoomCount();
            return ResponseEntity.ok(ApiResponse.<Long>builder()
                    .success(true)
                    .message("Chat room count retrieved successfully")
                    .data(count)
                    .build());
        } catch (Exception e) {
            log.error("Error getting chat room count: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Long>builder()
                            .success(false)
                            .message("Failed to retrieve chat room count: " + e.getMessage())
                            .build());
        }
    }
}
