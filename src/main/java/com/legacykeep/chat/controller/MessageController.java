package com.legacykeep.chat.controller;

import com.legacykeep.chat.dto.ApiResponse;
import com.legacykeep.chat.dto.request.SendMessageRequest;
import com.legacykeep.chat.dto.request.EditMessageRequest;
import com.legacykeep.chat.dto.request.ForwardMessageRequest;
import com.legacykeep.chat.dto.request.ReactionRequest;
import com.legacykeep.chat.dto.request.SearchMessagesRequest;
import com.legacykeep.chat.dto.request.EditMessageWithHistoryRequest;
import com.legacykeep.chat.dto.request.DeleteMessageRequest;
import com.legacykeep.chat.dto.request.ScheduleMessageRequest;
import com.legacykeep.chat.dto.response.MessageResponse;
import com.legacykeep.chat.dto.response.PaginatedMessageResponse;
import com.legacykeep.chat.dto.response.MessageStats;
import com.legacykeep.chat.dto.response.SearchMessagesResponse;
import com.legacykeep.chat.dto.response.ThreadSummary;
import com.legacykeep.chat.dto.response.MessageEditHistoryResponse;
import com.legacykeep.chat.dto.response.ScheduledMessageResponse;
import com.legacykeep.chat.entity.Message;
import com.legacykeep.chat.entity.MessageEditHistory;
import com.legacykeep.chat.entity.ScheduledMessage;
import com.legacykeep.chat.enums.MessageStatus;
import com.legacykeep.chat.enums.MessageType;
import com.legacykeep.chat.service.MessageService;
import com.legacykeep.chat.service.ContentFilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for Message management.
 * Provides comprehensive APIs for family-centric messaging operations.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;
    private final ContentFilterService contentFilterService;

    /**
     * Send a new message
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        log.info("Sending message to chat room: {} from user: {}", request.getChatRoomId(), request.getSenderUserId());
        
        try {
            Message message = messageService.sendMessage(request);
            MessageResponse response = MessageResponse.fromEntity(message);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "Message sent successfully"));
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to send message: " + e.getMessage(), e.getMessage(), 500));
        }
    }

    /**
     * Get message by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MessageResponse>> getMessageById(
            @PathVariable("id") String id,
            @RequestParam(value = "userId", required = false) Long userId) {
        log.debug("Getting message by ID: {} for user: {}", id, userId);
        
        try {
            Optional<Message> message = messageService.getMessageById(id);
            if (message.isPresent()) {
                MessageResponse response;
                
                // If userId is provided, check for filtering
                if (userId != null) {
                    Message msg = message.get();
                    boolean isFiltered = messageService.wouldMessageBeFiltered(
                        msg.getSenderUserId(), userId, msg.getChatRoomId(), msg.getContent());
                    
                    if (isFiltered) {
                        // Get filter reasons
                        List<com.legacykeep.chat.dto.response.FilterResponse> applicableFilters = 
                            contentFilterService.getApplicableFilters(
                                msg.getSenderUserId(), userId, msg.getChatRoomId(), msg.getContent());
                        
                        List<String> filteredReasons = applicableFilters.stream()
                            .map(filter -> "Contains: \"" + filter.getContent() + "\" (" + filter.getFilterType() + ")")
                            .toList();
                        
                        List<String> filterTypes = applicableFilters.stream()
                            .map(filter -> filter.getFilterType().toString())
                            .toList();
                        
                        response = MessageResponse.fromEntityWithFilters(msg, userId, true, filteredReasons, filterTypes);
                    } else {
                        response = MessageResponse.fromEntityWithFilters(msg, userId, false, null, null);
                    }
                } else {
                    // No userId provided, return normal response
                    response = MessageResponse.fromEntity(message.get());
                }
                
                return ResponseEntity.ok(ApiResponse.success(response, "Message retrieved successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("Message not found with ID: " + id));
            }
        } catch (Exception e) {
            log.error("Error getting message by ID: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve message: " + e.getMessage(), e.getMessage(), 500));
        }
    }

    /**
     * Get message by UUID
     */
    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<ApiResponse<MessageResponse>> getMessageByUuid(@PathVariable("uuid") String uuid) {
        log.debug("Getting message by UUID: {}", uuid);
        
        try {
            Optional<Message> message = messageService.getMessageByUuid(uuid);
            if (message.isPresent()) {
                MessageResponse response = MessageResponse.fromEntity(message.get());
                return ResponseEntity.ok(ApiResponse.success(response, "Message retrieved successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("Message not found with UUID: " + uuid));
            }
        } catch (Exception e) {
            log.error("Error getting message by UUID: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve message: " + e.getMessage(), e.getMessage(), 500));
        }
    }

    /**
     * Edit a message
     */
    @PutMapping("/{id}/edit")
    public ResponseEntity<ApiResponse<MessageResponse>> editMessage(
            @PathVariable("id") String id,
            @Valid @RequestBody EditMessageRequest request) {
        log.info("Editing message: {} by user: {}", id, request.getUserId());
        
        try {
            Message updatedMessage = messageService.editMessage(id, request);
            MessageResponse response = MessageResponse.fromEntity(updatedMessage);
            
            return ResponseEntity.ok(ApiResponse.success(response, "Message edited successfully"));
        } catch (Exception e) {
            log.error("Error editing message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to edit message: " + e.getMessage(), e.getMessage(), 500));
        }
    }

    /**
     * Delete a message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(
            @PathVariable("id") String id,
            @RequestParam("userId") Long userId) {
        log.info("Deleting message: {} by user: {}", id, userId);
        
        try {
            messageService.deleteMessage(id, userId);
            return ResponseEntity.ok(ApiResponse.success("Message deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete message: " + e.getMessage(), e.getMessage(), 500));
        }
    }

    /**
     * Delete a message for everyone
     */
    @DeleteMapping("/{id}/everyone")
    public ResponseEntity<ApiResponse<Void>> deleteMessageForEveryone(
            @PathVariable("id") String id,
            @RequestParam("userId") Long userId) {
        log.info("Deleting message for everyone: {} by user: {}", id, userId);
        
        try {
            messageService.deleteMessageForEveryone(id, userId);
            return ResponseEntity.ok(ApiResponse.success("Message deleted for everyone successfully"));
        } catch (Exception e) {
            log.error("Error deleting message for everyone: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete message for everyone: " + e.getMessage(), e.getMessage(), 500));
        }
    }

    /**
     * Forward a message
     */
    @PostMapping("/{id}/forward")
    public ResponseEntity<ApiResponse<MessageResponse>> forwardMessage(
            @PathVariable("id") String id,
            @Valid @RequestBody ForwardMessageRequest request) {
        log.info("Forwarding message: {} to chat room: {}", id, request.getToChatRoomId());
        
        try {
            request.setOriginalMessageId(id);
            Message forwardedMessage = messageService.forwardMessage(request);
            MessageResponse response = MessageResponse.fromEntity(forwardedMessage);
            
            return ResponseEntity.ok(ApiResponse.success(response, "Message forwarded successfully"));
        } catch (Exception e) {
            log.error("Error forwarding message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to forward message: " + e.getMessage(), e.getMessage(), 500));
        }
    }

    /**
     * Toggle star on a message
     */
    @PostMapping("/{id}/star")
    public ResponseEntity<ApiResponse<MessageResponse>> toggleStarMessage(
            @PathVariable("id") String id,
            @RequestParam("userId") Long userId) {
        log.info("Toggling star for message: {} by user: {}", id, userId);
        
        try {
            Message updatedMessage = messageService.toggleStarMessage(id, userId);
            MessageResponse response = MessageResponse.fromEntity(updatedMessage);
            
            return ResponseEntity.ok(ApiResponse.<MessageResponse>builder()
                    .status("success")
                    .message("Message star toggled successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error toggling star for message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<MessageResponse>builder()
                            .status("error")
                            .message("Failed to toggle star: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Add reaction to a message
     */
    @PostMapping("/{id}/reactions")
    public ResponseEntity<ApiResponse<MessageResponse>> addReaction(
            @PathVariable("id") String id,
            @Valid @RequestBody ReactionRequest request) {
        log.info("Adding reaction {} to message: {} by user: {}", request.getEmoji(), id, request.getUserId());
        
        try {
            Message updatedMessage = messageService.addReaction(id, request);
            MessageResponse response = MessageResponse.fromEntity(updatedMessage);
            
            return ResponseEntity.ok(ApiResponse.<MessageResponse>builder()
                    .status("success")
                    .message("Reaction added successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error adding reaction to message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<MessageResponse>builder()
                            .status("error")
                            .message("Failed to add reaction: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Remove reaction from a message
     */
    @DeleteMapping("/{id}/reactions/{emoji}")
    public ResponseEntity<ApiResponse<MessageResponse>> removeReaction(
            @PathVariable("id") String id,
            @PathVariable("emoji") String emoji,
            @RequestParam("userId") Long userId) {
        log.info("Removing reaction {} from message: {} by user: {}", emoji, id, userId);
        
        try {
            Message updatedMessage = messageService.removeReaction(id, userId, emoji);
            MessageResponse response = MessageResponse.fromEntity(updatedMessage);
            
            return ResponseEntity.ok(ApiResponse.<MessageResponse>builder()
                    .status("success")
                    .message("Reaction removed successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error removing reaction from message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<MessageResponse>builder()
                            .status("error")
                            .message("Failed to remove reaction: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Mark message as read
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markMessageAsRead(
            @PathVariable("id") String id,
            @RequestParam("userId") Long userId) {
        log.debug("Marking message: {} as read by user: {}", id, userId);
        
        try {
            messageService.markMessageAsRead(id, userId);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status("success")
                    .message("Message marked as read successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error marking message as read: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .status("error")
                            .message("Failed to mark message as read: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Mark all messages in a room as read
     */
    @PostMapping("/room/{chatRoomId}/read-all")
    public ResponseEntity<ApiResponse<Void>> markMessagesAsReadInRoom(
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam("userId") Long userId) {
        log.debug("Marking all messages in chat room: {} as read by user: {}", chatRoomId, userId);
        
        try {
            messageService.markMessagesAsReadInRoom(chatRoomId, userId);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status("success")
                    .message("All messages marked as read successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error marking messages as read in room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .status("error")
                            .message("Failed to mark messages as read: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get messages in a chat room
     */
    @GetMapping("/room/{chatRoomId}")
    public ResponseEntity<ApiResponse<PaginatedMessageResponse>> getMessagesInRoom(
            @PathVariable("chatRoomId") Long chatRoomId,
            @PageableDefault(size = 50) Pageable pageable) {
        log.debug("Getting messages in chat room: {}", chatRoomId);
        
        try {
            Page<Message> messages = messageService.getMessagesInRoom(chatRoomId, pageable);
            List<MessageResponse> responses = messages.getContent().stream()
                    .map(MessageResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedMessageResponse paginatedResponse = PaginatedMessageResponse.builder()
                    .content(responses)
                    .totalElements(messages.getTotalElements())
                    .totalPages(messages.getTotalPages())
                    .currentPage(messages.getNumber())
                    .size(messages.getSize())
                    .first(messages.isFirst())
                    .last(messages.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.success(paginatedResponse, "Messages retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting messages in room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve messages: " + e.getMessage(), e.getMessage(), 500));
        }
    }

    /**
     * Get messages before a specific message
     */
    @GetMapping("/room/{chatRoomId}/before/{messageId}")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessagesBefore(
            @PathVariable("chatRoomId") Long chatRoomId,
            @PathVariable("messageId") String messageId,
            @RequestParam(defaultValue = "20") int limit) {
        log.debug("Getting {} messages before message: {} in chat room: {}", limit, messageId, chatRoomId);
        
        try {
            List<Message> messages = messageService.getMessagesBefore(chatRoomId, messageId, limit);
            List<MessageResponse> responses = messages.stream()
                    .map(MessageResponse::fromEntity)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(responses, "Messages retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting messages before: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve messages: " + e.getMessage(), e.getMessage(), 500));
        }
    }

    /**
     * Get messages after a specific message
     */
    @GetMapping("/room/{chatRoomId}/after/{messageId}")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessagesAfter(
            @PathVariable("chatRoomId") Long chatRoomId,
            @PathVariable("messageId") String messageId,
            @RequestParam(defaultValue = "20") int limit) {
        log.debug("Getting {} messages after message: {} in chat room: {}", limit, messageId, chatRoomId);
        
        try {
            List<Message> messages = messageService.getMessagesAfter(chatRoomId, messageId, limit);
            List<MessageResponse> responses = messages.stream()
                    .map(MessageResponse::fromEntity)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(responses, "Messages retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting messages after: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve messages: " + e.getMessage(), e.getMessage(), 500));
        }
    }

    /**
     * Get messages by sender
     */
    @GetMapping("/sender/{senderId}")
    public ResponseEntity<ApiResponse<PaginatedMessageResponse>> getMessagesBySender(
            @PathVariable("senderId") Long senderId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting messages by sender: {}", senderId);
        
        try {
            Page<Message> messages = messageService.getMessagesBySender(senderId, pageable);
            List<MessageResponse> responses = messages.getContent().stream()
                    .map(MessageResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedMessageResponse paginatedResponse = PaginatedMessageResponse.builder()
                    .content(responses)
                    .totalElements(messages.getTotalElements())
                    .totalPages(messages.getTotalPages())
                    .currentPage(messages.getNumber())
                    .size(messages.getSize())
                    .first(messages.isFirst())
                    .last(messages.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.success(paginatedResponse, "Messages retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting messages by sender: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve messages: " + e.getMessage(), e.getMessage(), 500));
        }
    }

    /**
     * Get messages by type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<PaginatedMessageResponse>> getMessagesByType(
            @PathVariable("type") MessageType type,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting messages by type: {}", type);
        
        try {
            Page<Message> messages = messageService.getMessagesByType(type, pageable);
            List<MessageResponse> responses = messages.getContent().stream()
                    .map(MessageResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedMessageResponse paginatedResponse = PaginatedMessageResponse.builder()
                    .content(responses)
                    .totalElements(messages.getTotalElements())
                    .totalPages(messages.getTotalPages())
                    .currentPage(messages.getNumber())
                    .size(messages.getSize())
                    .first(messages.isFirst())
                    .last(messages.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.success(paginatedResponse, "Messages retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting messages by type: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve messages: " + e.getMessage(), e.getMessage(), 500));
        }
    }

    /**
     * Get messages by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<PaginatedMessageResponse>> getMessagesByStatus(
            @PathVariable("status") MessageStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting messages by status: {}", status);
        
        try {
            Page<Message> messages = messageService.getMessagesByStatus(status, pageable);
            List<MessageResponse> responses = messages.getContent().stream()
                    .map(MessageResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedMessageResponse paginatedResponse = PaginatedMessageResponse.builder()
                    .content(responses)
                    .totalElements(messages.getTotalElements())
                    .totalPages(messages.getTotalPages())
                    .currentPage(messages.getNumber())
                    .size(messages.getSize())
                    .first(messages.isFirst())
                    .last(messages.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.success(paginatedResponse, "Messages retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting messages by status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve messages: " + e.getMessage(), e.getMessage(), 500));
        }
    }

    /**
     * Get starred messages for a user
     */
    @GetMapping("/starred/user/{userId}")
    public ResponseEntity<ApiResponse<PaginatedMessageResponse>> getStarredMessagesForUser(
            @PathVariable("userId") Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting starred messages for user: {}", userId);
        
        try {
            Page<Message> messages = messageService.getStarredMessagesForUser(userId, pageable);
            List<MessageResponse> responses = messages.getContent().stream()
                    .map(MessageResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedMessageResponse paginatedResponse = PaginatedMessageResponse.builder()
                    .content(responses)
                    .totalElements(messages.getTotalElements())
                    .totalPages(messages.getTotalPages())
                    .currentPage(messages.getNumber())
                    .size(messages.getSize())
                    .first(messages.isFirst())
                    .last(messages.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedMessageResponse>builder()
                    .status("success")
                    .message("Starred messages retrieved successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error getting starred messages for user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedMessageResponse>builder()
                            .status("error")
                            .message("Failed to retrieve starred messages: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get starred messages in a room
     */
    @GetMapping("/starred/room/{chatRoomId}")
    public ResponseEntity<ApiResponse<PaginatedMessageResponse>> getStarredMessagesInRoom(
            @PathVariable("chatRoomId") Long chatRoomId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting starred messages in chat room: {}", chatRoomId);
        
        try {
            Page<Message> messages = messageService.getStarredMessagesInRoom(chatRoomId, pageable);
            List<MessageResponse> responses = messages.getContent().stream()
                    .map(MessageResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedMessageResponse paginatedResponse = PaginatedMessageResponse.builder()
                    .content(responses)
                    .totalElements(messages.getTotalElements())
                    .totalPages(messages.getTotalPages())
                    .currentPage(messages.getNumber())
                    .size(messages.getSize())
                    .first(messages.isFirst())
                    .last(messages.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedMessageResponse>builder()
                    .status("success")
                    .message("Starred messages retrieved successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error getting starred messages in room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedMessageResponse>builder()
                            .status("error")
                            .message("Failed to retrieve starred messages: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get protected messages
     */
    @GetMapping("/protected")
    public ResponseEntity<ApiResponse<PaginatedMessageResponse>> getProtectedMessages(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting protected messages");
        
        try {
            Page<Message> messages = messageService.getProtectedMessages(pageable);
            List<MessageResponse> responses = messages.getContent().stream()
                    .map(MessageResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedMessageResponse paginatedResponse = PaginatedMessageResponse.builder()
                    .content(responses)
                    .totalElements(messages.getTotalElements())
                    .totalPages(messages.getTotalPages())
                    .currentPage(messages.getNumber())
                    .size(messages.getSize())
                    .first(messages.isFirst())
                    .last(messages.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedMessageResponse>builder()
                    .status("success")
                    .message("Protected messages retrieved successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error getting protected messages: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedMessageResponse>builder()
                            .status("error")
                            .message("Failed to retrieve protected messages: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get messages with tone color
     */
    @GetMapping("/tone-color")
    public ResponseEntity<ApiResponse<PaginatedMessageResponse>> getMessagesWithToneColor(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting messages with tone color");
        
        try {
            Page<Message> messages = messageService.getMessagesWithToneColor(pageable);
            List<MessageResponse> responses = messages.getContent().stream()
                    .map(MessageResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedMessageResponse paginatedResponse = PaginatedMessageResponse.builder()
                    .content(responses)
                    .totalElements(messages.getTotalElements())
                    .totalPages(messages.getTotalPages())
                    .currentPage(messages.getNumber())
                    .size(messages.getSize())
                    .first(messages.isFirst())
                    .last(messages.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedMessageResponse>builder()
                    .status("success")
                    .message("Messages with tone color retrieved successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error getting messages with tone color: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedMessageResponse>builder()
                            .status("error")
                            .message("Failed to retrieve messages with tone color: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get messages with AI features
     */
    @GetMapping("/ai-features")
    public ResponseEntity<ApiResponse<PaginatedMessageResponse>> getMessagesWithAIFeatures(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting messages with AI features");
        
        try {
            Page<Message> messages = messageService.getMessagesWithAIFeatures(pageable);
            List<MessageResponse> responses = messages.getContent().stream()
                    .map(MessageResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedMessageResponse paginatedResponse = PaginatedMessageResponse.builder()
                    .content(responses)
                    .totalElements(messages.getTotalElements())
                    .totalPages(messages.getTotalPages())
                    .currentPage(messages.getNumber())
                    .size(messages.getSize())
                    .first(messages.isFirst())
                    .last(messages.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedMessageResponse>builder()
                    .status("success")
                    .message("Messages with AI features retrieved successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error getting messages with AI features: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedMessageResponse>builder()
                            .status("error")
                            .message("Failed to retrieve messages with AI features: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get messages with media
     */
    @GetMapping("/media")
    public ResponseEntity<ApiResponse<PaginatedMessageResponse>> getMessagesWithMedia(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting messages with media");
        
        try {
            Page<Message> messages = messageService.getMessagesWithMedia(pageable);
            List<MessageResponse> responses = messages.getContent().stream()
                    .map(MessageResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedMessageResponse paginatedResponse = PaginatedMessageResponse.builder()
                    .content(responses)
                    .totalElements(messages.getTotalElements())
                    .totalPages(messages.getTotalPages())
                    .currentPage(messages.getNumber())
                    .size(messages.getSize())
                    .first(messages.isFirst())
                    .last(messages.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedMessageResponse>builder()
                    .status("success")
                    .message("Messages with media retrieved successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error getting messages with media: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedMessageResponse>builder()
                            .status("error")
                            .message("Failed to retrieve messages with media: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get messages by story
     */
    @GetMapping("/story/{storyId}")
    public ResponseEntity<ApiResponse<PaginatedMessageResponse>> getMessagesByStory(
            @PathVariable("storyId") Long storyId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting messages by story: {}", storyId);
        
        try {
            Page<Message> messages = messageService.getMessagesByStory(storyId, pageable);
            List<MessageResponse> responses = messages.getContent().stream()
                    .map(MessageResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedMessageResponse paginatedResponse = PaginatedMessageResponse.builder()
                    .content(responses)
                    .totalElements(messages.getTotalElements())
                    .totalPages(messages.getTotalPages())
                    .currentPage(messages.getNumber())
                    .size(messages.getSize())
                    .first(messages.isFirst())
                    .last(messages.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedMessageResponse>builder()
                    .status("success")
                    .message("Messages by story retrieved successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error getting messages by story: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedMessageResponse>builder()
                            .status("error")
                            .message("Failed to retrieve messages by story: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get messages by memory
     */
    @GetMapping("/memory/{memoryId}")
    public ResponseEntity<ApiResponse<PaginatedMessageResponse>> getMessagesByMemory(
            @PathVariable("memoryId") Long memoryId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting messages by memory: {}", memoryId);
        
        try {
            Page<Message> messages = messageService.getMessagesByMemory(memoryId, pageable);
            List<MessageResponse> responses = messages.getContent().stream()
                    .map(MessageResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedMessageResponse paginatedResponse = PaginatedMessageResponse.builder()
                    .content(responses)
                    .totalElements(messages.getTotalElements())
                    .totalPages(messages.getTotalPages())
                    .currentPage(messages.getNumber())
                    .size(messages.getSize())
                    .first(messages.isFirst())
                    .last(messages.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedMessageResponse>builder()
                    .status("success")
                    .message("Messages by memory retrieved successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error getting messages by memory: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedMessageResponse>builder()
                            .status("error")
                            .message("Failed to retrieve messages by memory: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get messages by event
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<PaginatedMessageResponse>> getMessagesByEvent(
            @PathVariable("eventId") Long eventId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Getting messages by event: {}", eventId);
        
        try {
            Page<Message> messages = messageService.getMessagesByEvent(eventId, pageable);
            List<MessageResponse> responses = messages.getContent().stream()
                    .map(MessageResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedMessageResponse paginatedResponse = PaginatedMessageResponse.builder()
                    .content(responses)
                    .totalElements(messages.getTotalElements())
                    .totalPages(messages.getTotalPages())
                    .currentPage(messages.getNumber())
                    .size(messages.getSize())
                    .first(messages.isFirst())
                    .last(messages.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedMessageResponse>builder()
                    .status("success")
                    .message("Messages by event retrieved successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error getting messages by event: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedMessageResponse>builder()
                            .status("error")
                            .message("Failed to retrieve messages by event: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Search messages by content
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PaginatedMessageResponse>> searchMessagesByContent(
            @RequestParam("content") String content,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Searching messages by content: {}", content);
        
        try {
            Page<Message> messages = messageService.searchMessagesByContent(content, pageable);
            List<MessageResponse> responses = messages.getContent().stream()
                    .map(MessageResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedMessageResponse paginatedResponse = PaginatedMessageResponse.builder()
                    .content(responses)
                    .totalElements(messages.getTotalElements())
                    .totalPages(messages.getTotalPages())
                    .currentPage(messages.getNumber())
                    .size(messages.getSize())
                    .first(messages.isFirst())
                    .last(messages.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedMessageResponse>builder()
                    .status("success")
                    .message("Message search completed successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error searching messages by content: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedMessageResponse>builder()
                            .status("error")
                            .message("Failed to search messages: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Search messages by content in a specific room
     */
    @GetMapping("/room/{chatRoomId}/search")
    public ResponseEntity<ApiResponse<PaginatedMessageResponse>> searchMessagesByContentInRoom(
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam("content") String content,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Searching messages by content: {} in chat room: {}", content, chatRoomId);
        
        try {
            Page<Message> messages = messageService.searchMessagesByContentInRoom(chatRoomId, content, pageable);
            List<MessageResponse> responses = messages.getContent().stream()
                    .map(MessageResponse::fromEntity)
                    .collect(Collectors.toList());
            
            PaginatedMessageResponse paginatedResponse = PaginatedMessageResponse.builder()
                    .content(responses)
                    .totalElements(messages.getTotalElements())
                    .totalPages(messages.getTotalPages())
                    .currentPage(messages.getNumber())
                    .size(messages.getSize())
                    .first(messages.isFirst())
                    .last(messages.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedMessageResponse>builder()
                    .status("success")
                    .message("Message search in room completed successfully")
                    .data(paginatedResponse)
                    .build());
        } catch (Exception e) {
            log.error("Error searching messages by content in room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedMessageResponse>builder()
                            .status("error")
                            .message("Failed to search messages in room: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get message statistics for a user
     */
    @GetMapping("/stats/user/{userId}")
    public ResponseEntity<ApiResponse<MessageStats>> getMessageStatsForUser(@PathVariable("userId") Long userId) {
        log.debug("Getting message statistics for user: {}", userId);
        
        try {
            com.legacykeep.chat.service.MessageService.MessageStats serviceStats = messageService.getMessageStatsForUser(userId);
            MessageStats stats = MessageStats.fromServiceStats(serviceStats);
            return ResponseEntity.ok(ApiResponse.success(stats, "Message statistics retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting message statistics for user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve message statistics: " + e.getMessage(), e.getMessage(), 500));
        }
    }

    /**
     * Get message statistics for a room
     */
    @GetMapping("/stats/room/{chatRoomId}")
    public ResponseEntity<ApiResponse<MessageStats>> getMessageStatsForRoom(@PathVariable("chatRoomId") Long chatRoomId) {
        log.debug("Getting message statistics for chat room: {}", chatRoomId);
        
        try {
            com.legacykeep.chat.service.MessageService.MessageStats serviceStats = messageService.getMessageStatsForRoom(chatRoomId);
            MessageStats stats = MessageStats.fromServiceStats(serviceStats);
            return ResponseEntity.ok(ApiResponse.success(stats, "Message statistics retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting message statistics for room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve message statistics: " + e.getMessage(), e.getMessage(), 500));
        }
    }

    /**
     * Get latest message in a room
     */
    @GetMapping("/room/{chatRoomId}/latest")
    public ResponseEntity<ApiResponse<MessageResponse>> getLatestMessageInRoom(@PathVariable("chatRoomId") Long chatRoomId) {
        log.debug("Getting latest message in chat room: {}", chatRoomId);
        
        try {
            Optional<Message> message = messageService.getLatestMessageInRoom(chatRoomId);
            if (message.isPresent()) {
                MessageResponse response = MessageResponse.fromEntity(message.get());
                return ResponseEntity.ok(ApiResponse.<MessageResponse>builder()
                        .status("success")
                        .message("Latest message retrieved successfully")
                        .data(response)
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<MessageResponse>builder()
                                .status("error")
                                .message("No messages found in chat room")
                                .build());
            }
        } catch (Exception e) {
            log.error("Error getting latest message in room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<MessageResponse>builder()
                            .status("error")
                            .message("Failed to retrieve latest message: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get message counts
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getMessageCounts(
            @RequestParam(required = false) Long chatRoomId,
            @RequestParam(required = false) Long senderId,
            @RequestParam(required = false) MessageType messageType,
            @RequestParam(required = false) MessageStatus status) {
        log.debug("Getting message counts");
        
        try {
            Map<String, Long> counts = new java.util.HashMap<>();
            
            if (chatRoomId != null) {
                counts.put("chatRoom", messageService.countMessagesInRoom(chatRoomId));
            }
            if (senderId != null) {
                counts.put("sender", messageService.countMessagesBySender(senderId));
            }
            if (messageType != null) {
                counts.put("type", messageService.countMessagesByType(messageType));
            }
            if (status != null) {
                counts.put("status", messageService.countMessagesByStatus(status));
            }
            
            return ResponseEntity.ok(ApiResponse.<Map<String, Long>>builder()
                    .status("success")
                    .message("Message counts retrieved successfully")
                    .data(counts)
                    .build());
        } catch (Exception e) {
            log.error("Error getting message counts: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Map<String, Long>>builder()
                            .status("error")
                            .message("Failed to retrieve message counts: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Cleanup expired messages
     */
    @PostMapping("/cleanup/expired")
    public ResponseEntity<ApiResponse<Void>> cleanupExpiredMessages() {
        log.info("Cleaning up expired messages");
        
        try {
            messageService.cleanupExpiredMessages();
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status("success")
                    .message("Expired messages cleaned up successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error cleaning up expired messages: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .status("error")
                            .message("Failed to cleanup expired messages: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Cleanup messages at view limit
     */
    @PostMapping("/cleanup/view-limit")
    public ResponseEntity<ApiResponse<Void>> cleanupMessagesAtViewLimit() {
        log.info("Cleaning up messages at view limit");
        
        try {
            messageService.cleanupMessagesAtViewLimit();
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status("success")
                    .message("Messages at view limit cleaned up successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error cleaning up messages at view limit: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .status("error")
                            .message("Failed to cleanup messages at view limit: " + e.getMessage())
                            .build());
        }
    }

    // ==================== SEARCH ENDPOINTS ====================

    /**
     * Search messages with full-text search
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<SearchMessagesResponse>> searchMessages(
            @Valid @RequestBody SearchMessagesRequest request) {
        log.info("Searching messages with query: '{}' for user: {}", request.getQuery(), request.getUserId());
        
        try {
            long startTime = System.currentTimeMillis();
            
            // Create pageable if pagination is requested
            Pageable pageable = null;
            if (request.getPage() != null && request.getSize() != null) {
                Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
                if (request.getSortBy() != null) {
                    Sort.Direction direction = "asc".equalsIgnoreCase(request.getSortDirection()) ? 
                            Sort.Direction.ASC : Sort.Direction.DESC;
                    sort = Sort.by(direction, request.getSortBy());
                }
                pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
            }
            
            List<Message> messages;
            long totalElements = 0;
            int totalPages = 0;
            int currentPage = 0;
            int size = 0;
            boolean first = true;
            boolean last = true;
            
            if (pageable != null) {
                // Use paginated search
                Page<Message> messagePage = messageService.searchMessagesWithFilters(
                        request.getQuery(), request.getUserId(), request.getChatRoomId(), 
                        request.getChatRoomIds(), request.getSenderUserId(), 
                        request.getStartDate(), request.getEndDate(), 
                        request.getIsStarred(), request.getIsEncrypted(), 
                        request.getIncludeDeleted(), pageable);
                
                messages = messagePage.getContent();
                totalElements = messagePage.getTotalElements();
                totalPages = messagePage.getTotalPages();
                currentPage = messagePage.getNumber();
                size = messagePage.getSize();
                first = messagePage.isFirst();
                last = messagePage.isLast();
            } else {
                // Use non-paginated search
                messages = messageService.searchMessagesWithFilters(
                        request.getQuery(), request.getUserId(), request.getChatRoomId(), 
                        request.getChatRoomIds(), request.getSenderUserId(), 
                        request.getStartDate(), request.getEndDate(), 
                        request.getIsStarred(), request.getIsEncrypted(), 
                        request.getIncludeDeleted());
                
                totalElements = messages.size();
                totalPages = 1;
                currentPage = 0;
                size = messages.size();
                first = true;
                last = true;
            }
            
            long searchTime = System.currentTimeMillis() - startTime;
            
            SearchMessagesResponse response = SearchMessagesResponse.builder()
                    .messages(messages.stream().map(MessageResponse::fromEntity).toList())
                    .totalElements(totalElements)
                    .totalPages(totalPages)
                    .currentPage(currentPage)
                    .size(size)
                    .first(first)
                    .last(last)
                    .query(request.getQuery())
                    .searchTimeMs(searchTime)
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<SearchMessagesResponse>builder()
                    .status("success")
                    .message("Search completed successfully")
                    .data(response)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error searching messages: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<SearchMessagesResponse>builder()
                            .status("error")
                            .message("Failed to search messages: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Search messages in specific chat room
     */
    @GetMapping("/search/room/{chatRoomId}")
    public ResponseEntity<ApiResponse<SearchMessagesResponse>> searchMessagesInRoom(
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam("query") String query,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("Searching messages in room: {} with query: '{}' for user: {}", chatRoomId, query, userId);
        
        try {
            long startTime = System.currentTimeMillis();
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Message> messagePage = messageService.searchMessagesInRoom(query, userId, chatRoomId, pageable);
            
            long searchTime = System.currentTimeMillis() - startTime;
            
            SearchMessagesResponse response = SearchMessagesResponse.builder()
                    .messages(messagePage.getContent().stream().map(MessageResponse::fromEntity).toList())
                    .totalElements(messagePage.getTotalElements())
                    .totalPages(messagePage.getTotalPages())
                    .currentPage(messagePage.getNumber())
                    .size(messagePage.getSize())
                    .first(messagePage.isFirst())
                    .last(messagePage.isLast())
                    .query(query)
                    .searchTimeMs(searchTime)
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<SearchMessagesResponse>builder()
                    .status("success")
                    .message("Room search completed successfully")
                    .data(response)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error searching messages in room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<SearchMessagesResponse>builder()
                            .status("error")
                            .message("Failed to search messages in room: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Search messages by specific sender
     */
    @GetMapping("/search/sender/{senderUserId}")
    public ResponseEntity<ApiResponse<SearchMessagesResponse>> searchMessagesBySender(
            @PathVariable("senderUserId") Long senderUserId,
            @RequestParam("query") String query,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("Searching messages by sender: {} with query: '{}' for user: {}", senderUserId, query, userId);
        
        try {
            long startTime = System.currentTimeMillis();
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Message> messagePage = messageService.searchMessagesBySender(query, userId, senderUserId, pageable);
            
            long searchTime = System.currentTimeMillis() - startTime;
            
            SearchMessagesResponse response = SearchMessagesResponse.builder()
                    .messages(messagePage.getContent().stream().map(MessageResponse::fromEntity).toList())
                    .totalElements(messagePage.getTotalElements())
                    .totalPages(messagePage.getTotalPages())
                    .currentPage(messagePage.getNumber())
                    .size(messagePage.getSize())
                    .first(messagePage.isFirst())
                    .last(messagePage.isLast())
                    .query(query)
                    .searchTimeMs(searchTime)
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<SearchMessagesResponse>builder()
                    .status("success")
                    .message("Sender search completed successfully")
                    .data(response)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error searching messages by sender: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<SearchMessagesResponse>builder()
                            .status("error")
                            .message("Failed to search messages by sender: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get search suggestions
     */
    @GetMapping("/search/suggestions")
    public ResponseEntity<ApiResponse<List<String>>> getSearchSuggestions(
            @RequestParam("query") String query,
            @RequestParam("userId") Long userId) {
        log.debug("Getting search suggestions for query: '{}' and user: {}", query, userId);
        
        try {
            List<String> suggestions = messageService.getSearchSuggestions(query, userId);
            
            return ResponseEntity.ok(ApiResponse.<List<String>>builder()
                    .status("success")
                    .message("Search suggestions retrieved successfully")
                    .data(suggestions)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error getting search suggestions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<String>>builder()
                            .status("error")
                            .message("Failed to get search suggestions: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get popular search terms
     */
    @GetMapping("/search/popular")
    public ResponseEntity<ApiResponse<List<String>>> getPopularSearchTerms(
            @RequestParam("userId") Long userId) {
        log.debug("Getting popular search terms for user: {}", userId);
        
        try {
            List<String> popularTerms = messageService.getPopularSearchTerms(userId);
            
            return ResponseEntity.ok(ApiResponse.<List<String>>builder()
                    .status("success")
                    .message("Popular search terms retrieved successfully")
                    .data(popularTerms)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error getting popular search terms: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<String>>builder()
                            .status("error")
                            .message("Failed to get popular search terms: " + e.getMessage())
                            .build());
        }
    }

    // ==================== THREADING ENDPOINTS ====================

    /**
     * Get all replies to a specific message
     */
    @GetMapping("/{id}/replies")
    public ResponseEntity<ApiResponse<PaginatedMessageResponse>> getRepliesToMessage(
            @PathVariable("id") String messageId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("Getting replies to message: {}", messageId);
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
            Page<Message> messagePage = messageService.getRepliesToMessage(messageId, pageable);
            
            PaginatedMessageResponse response = PaginatedMessageResponse.builder()
                    .content(messagePage.getContent().stream().map(MessageResponse::fromEntity).toList())
                    .totalElements(messagePage.getTotalElements())
                    .totalPages(messagePage.getTotalPages())
                    .currentPage(messagePage.getNumber())
                    .size(messagePage.getSize())
                    .first(messagePage.isFirst())
                    .last(messagePage.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedMessageResponse>builder()
                    .status("success")
                    .message("Replies retrieved successfully")
                    .data(response)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error getting replies to message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedMessageResponse>builder()
                            .status("error")
                            .message("Failed to get replies: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get all messages in a thread
     */
    @GetMapping("/{id}/thread")
    public ResponseEntity<ApiResponse<PaginatedMessageResponse>> getThreadMessages(
            @PathVariable("id") String messageId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size) {
        log.info("Getting thread messages for: {}", messageId);
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
            Page<Message> messagePage = messageService.getThreadMessages(messageId, pageable);
            
            PaginatedMessageResponse response = PaginatedMessageResponse.builder()
                    .content(messagePage.getContent().stream().map(MessageResponse::fromEntity).toList())
                    .totalElements(messagePage.getTotalElements())
                    .totalPages(messagePage.getTotalPages())
                    .currentPage(messagePage.getNumber())
                    .size(messagePage.getSize())
                    .first(messagePage.isFirst())
                    .last(messagePage.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedMessageResponse>builder()
                    .status("success")
                    .message("Thread messages retrieved successfully")
                    .data(response)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error getting thread messages: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedMessageResponse>builder()
                            .status("error")
                            .message("Failed to get thread messages: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get thread root messages (messages that are not replies)
     */
    @GetMapping("/threads/room/{chatRoomId}")
    public ResponseEntity<ApiResponse<PaginatedMessageResponse>> getThreadRootMessages(
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("Getting thread root messages for chat room: {}", chatRoomId);
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Message> messagePage = messageService.getThreadRootMessages(chatRoomId, pageable);
            
            PaginatedMessageResponse response = PaginatedMessageResponse.builder()
                    .content(messagePage.getContent().stream().map(MessageResponse::fromEntity).toList())
                    .totalElements(messagePage.getTotalElements())
                    .totalPages(messagePage.getTotalPages())
                    .currentPage(messagePage.getNumber())
                    .size(messagePage.getSize())
                    .first(messagePage.isFirst())
                    .last(messagePage.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedMessageResponse>builder()
                    .status("success")
                    .message("Thread root messages retrieved successfully")
                    .data(response)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error getting thread root messages: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedMessageResponse>builder()
                            .status("error")
                            .message("Failed to get thread root messages: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get thread summary with reply count and latest reply
     */
    @GetMapping("/{id}/thread/summary")
    public ResponseEntity<ApiResponse<ThreadSummary>> getThreadSummary(
            @PathVariable("id") String messageId) {
        log.info("Getting thread summary for message: {}", messageId);
        
        try {
            ThreadSummary summary = messageService.getThreadSummary(messageId);
            
            return ResponseEntity.ok(ApiResponse.<ThreadSummary>builder()
                    .status("success")
                    .message("Thread summary retrieved successfully")
                    .data(summary)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error getting thread summary: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ThreadSummary>builder()
                            .status("error")
                            .message("Failed to get thread summary: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Count replies to a specific message
     */
    @GetMapping("/{id}/replies/count")
    public ResponseEntity<ApiResponse<Long>> countRepliesToMessage(
            @PathVariable("id") String messageId) {
        log.debug("Counting replies to message: {}", messageId);
        
        try {
            long replyCount = messageService.countRepliesToMessage(messageId);
            
            return ResponseEntity.ok(ApiResponse.<Long>builder()
                    .status("success")
                    .message("Reply count retrieved successfully")
                    .data(replyCount)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error counting replies: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Long>builder()
                            .status("error")
                            .message("Failed to count replies: " + e.getMessage())
                            .build());
        }
    }

    // ==================== EDIT HISTORY ENDPOINTS ====================

    /**
     * Edit a message with history tracking
     */
    @PutMapping("/edit-with-history")
    public ResponseEntity<ApiResponse<MessageResponse>> editMessageWithHistory(
            @Valid @RequestBody EditMessageWithHistoryRequest request) {
        log.info("Editing message with history: {} by user: {}", request.getMessageId(), request.getUserId());
        
        try {
            Message updatedMessage = messageService.editMessageWithHistory(request);
            
            return ResponseEntity.ok(ApiResponse.<MessageResponse>builder()
                    .status("success")
                    .message("Message edited successfully with history tracking")
                    .data(MessageResponse.fromEntity(updatedMessage))
                    .build());
                    
        } catch (Exception e) {
            log.error("Error editing message with history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<MessageResponse>builder()
                            .status("error")
                            .message("Failed to edit message: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get edit history for a message
     */
    @GetMapping("/{id}/edit-history")
    public ResponseEntity<ApiResponse<List<MessageEditHistoryResponse>>> getMessageEditHistory(
            @PathVariable("id") String messageId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("Getting edit history for message: {}", messageId);
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "version"));
            Page<MessageEditHistory> historyPage = messageService.getMessageEditHistory(messageId, pageable);
            
            List<MessageEditHistoryResponse> historyResponses = historyPage.getContent()
                    .stream()
                    .map(MessageEditHistoryResponse::fromEntity)
                    .toList();
            
            return ResponseEntity.ok(ApiResponse.<List<MessageEditHistoryResponse>>builder()
                    .status("success")
                    .message("Edit history retrieved successfully")
                    .data(historyResponses)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error getting edit history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<MessageEditHistoryResponse>>builder()
                            .status("error")
                            .message("Failed to get edit history: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get specific version of a message
     */
    @GetMapping("/{id}/version/{version}")
    public ResponseEntity<ApiResponse<MessageEditHistoryResponse>> getMessageVersion(
            @PathVariable("id") String messageId,
            @PathVariable("version") Integer version) {
        log.info("Getting version {} of message: {}", version, messageId);
        
        try {
            MessageEditHistory history = messageService.getMessageVersion(messageId, version);
            
            return ResponseEntity.ok(ApiResponse.<MessageEditHistoryResponse>builder()
                    .status("success")
                    .message("Message version retrieved successfully")
                    .data(MessageEditHistoryResponse.fromEntity(history))
                    .build());
                    
        } catch (Exception e) {
            log.error("Error getting message version: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<MessageEditHistoryResponse>builder()
                            .status("error")
                            .message("Failed to get message version: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get current version of a message
     */
    @GetMapping("/{id}/current-version")
    public ResponseEntity<ApiResponse<MessageEditHistoryResponse>> getCurrentMessageVersion(
            @PathVariable("id") String messageId) {
        log.info("Getting current version of message: {}", messageId);
        
        try {
            MessageEditHistory history = messageService.getCurrentMessageVersion(messageId);
            
            return ResponseEntity.ok(ApiResponse.<MessageEditHistoryResponse>builder()
                    .status("success")
                    .message("Current message version retrieved successfully")
                    .data(MessageEditHistoryResponse.fromEntity(history))
                    .build());
                    
        } catch (Exception e) {
            log.error("Error getting current message version: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<MessageEditHistoryResponse>builder()
                            .status("error")
                            .message("Failed to get current message version: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Revert message to a specific version
     */
    @PostMapping("/{id}/revert/{version}")
    public ResponseEntity<ApiResponse<MessageResponse>> revertMessageToVersion(
            @PathVariable("id") String messageId,
            @PathVariable("version") Integer version,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "reason", required = false) String reason) {
        log.info("Reverting message: {} to version: {} by user: {}", messageId, version, userId);
        
        try {
            Message revertedMessage = messageService.revertMessageToVersion(messageId, version, userId, reason);
            
            return ResponseEntity.ok(ApiResponse.<MessageResponse>builder()
                    .status("success")
                    .message("Message reverted successfully")
                    .data(MessageResponse.fromEntity(revertedMessage))
                    .build());
                    
        } catch (Exception e) {
            log.error("Error reverting message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<MessageResponse>builder()
                            .status("error")
                            .message("Failed to revert message: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get edit history by user
     */
    @GetMapping("/edit-history/user/{userId}")
    public ResponseEntity<ApiResponse<List<MessageEditHistoryResponse>>> getEditHistoryByUser(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("Getting edit history by user: {}", userId);
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "editTimestamp"));
            Page<MessageEditHistory> historyPage = messageService.getEditHistoryByUser(userId, pageable);
            
            List<MessageEditHistoryResponse> historyResponses = historyPage.getContent()
                    .stream()
                    .map(MessageEditHistoryResponse::fromEntity)
                    .toList();
            
            return ResponseEntity.ok(ApiResponse.<List<MessageEditHistoryResponse>>builder()
                    .status("success")
                    .message("User edit history retrieved successfully")
                    .data(historyResponses)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error getting user edit history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<MessageEditHistoryResponse>>builder()
                            .status("error")
                            .message("Failed to get user edit history: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Count edit history for a message
     */
    @GetMapping("/{id}/edit-history/count")
    public ResponseEntity<ApiResponse<Long>> countMessageEditHistory(
            @PathVariable("id") String messageId) {
        log.debug("Counting edit history for message: {}", messageId);
        
        try {
            long count = messageService.countMessageEditHistory(messageId);
            
            return ResponseEntity.ok(ApiResponse.<Long>builder()
                    .status("success")
                    .message("Edit history count retrieved successfully")
                    .data(count)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error counting edit history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Long>builder()
                            .status("error")
                            .message("Failed to count edit history: " + e.getMessage())
                            .build());
        }
    }

    // ==================== ENHANCED DELETION ENDPOINTS ====================

    /**
     * Delete message with enhanced options
     */
    @DeleteMapping("/delete-with-options")
    public ResponseEntity<ApiResponse<Void>> deleteMessageWithOptions(
            @Valid @RequestBody DeleteMessageRequest request) {
        log.info("Deleting message with options: {} by user: {}", request.getMessageId(), request.getUserId());
        
        try {
            messageService.deleteMessageWithOptions(request);
            
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status("success")
                    .message("Message deleted successfully with options")
                    .build());
                    
        } catch (Exception e) {
            log.error("Error deleting message with options: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .status("error")
                            .message("Failed to delete message: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Bulk delete messages
     */
    @DeleteMapping("/bulk-delete")
    public ResponseEntity<ApiResponse<Void>> bulkDeleteMessages(
            @RequestParam("messageIds") List<String> messageIds,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "deleteForEveryone", defaultValue = "false") Boolean deleteForEveryone) {
        log.info("Bulk deleting {} messages by user: {}", messageIds.size(), userId);
        
        try {
            messageService.bulkDeleteMessages(messageIds, userId, deleteForEveryone);
            
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status("success")
                    .message("Messages deleted successfully")
                    .build());
                    
        } catch (Exception e) {
            log.error("Error bulk deleting messages: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .status("error")
                            .message("Failed to bulk delete messages: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Delete all messages in a chat room
     */
    @DeleteMapping("/room/{chatRoomId}/all")
    public ResponseEntity<ApiResponse<Void>> deleteAllMessagesInRoom(
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "deleteForEveryone", defaultValue = "false") Boolean deleteForEveryone) {
        log.info("Deleting all messages in room: {} by user: {}", chatRoomId, userId);
        
        try {
            messageService.deleteAllMessagesInRoom(chatRoomId, userId, deleteForEveryone);
            
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status("success")
                    .message("All messages in room deleted successfully")
                    .build());
                    
        } catch (Exception e) {
            log.error("Error deleting all messages in room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .status("error")
                            .message("Failed to delete all messages: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Delete messages by date range
     */
    @DeleteMapping("/room/{chatRoomId}/by-date-range")
    public ResponseEntity<ApiResponse<Void>> deleteMessagesByDateRange(
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "deleteForEveryone", defaultValue = "false") Boolean deleteForEveryone) {
        log.info("Deleting messages in room: {} between {} and {} by user: {}", chatRoomId, startDate, endDate, userId);
        
        try {
            messageService.deleteMessagesByDateRange(chatRoomId, startDate, endDate, userId, deleteForEveryone);
            
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status("success")
                    .message("Messages deleted successfully by date range")
                    .build());
                    
        } catch (Exception e) {
            log.error("Error deleting messages by date range: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .status("error")
                            .message("Failed to delete messages by date range: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Permanently delete message
     */
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<ApiResponse<Void>> permanentlyDeleteMessage(
            @PathVariable("id") String messageId,
            @RequestParam("userId") Long userId) {
        log.info("Permanently deleting message: {} by user: {}", messageId, userId);
        
        try {
            messageService.permanentlyDeleteMessage(messageId, userId);
            
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status("success")
                    .message("Message permanently deleted successfully")
                    .build());
                    
        } catch (Exception e) {
            log.error("Error permanently deleting message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .status("error")
                            .message("Failed to permanently delete message: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Restore deleted message
     */
    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<MessageResponse>> restoreDeletedMessage(
            @PathVariable("id") String messageId,
            @RequestParam("userId") Long userId) {
        log.info("Restoring deleted message: {} by user: {}", messageId, userId);
        
        try {
            Message restoredMessage = messageService.restoreDeletedMessage(messageId, userId);
            
            return ResponseEntity.ok(ApiResponse.<MessageResponse>builder()
                    .status("success")
                    .message("Message restored successfully")
                    .data(MessageResponse.fromEntity(restoredMessage))
                    .build());
                    
        } catch (Exception e) {
            log.error("Error restoring deleted message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<MessageResponse>builder()
                            .status("error")
                            .message("Failed to restore message: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get deleted messages for a user
     */
    @GetMapping("/deleted/room/{chatRoomId}")
    public ResponseEntity<ApiResponse<PaginatedMessageResponse>> getDeletedMessages(
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("Getting deleted messages for user: {} in room: {}", userId, chatRoomId);
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "deletedAt"));
            Page<Message> messagePage = messageService.getDeletedMessages(userId, chatRoomId, pageable);
            
            PaginatedMessageResponse response = PaginatedMessageResponse.builder()
                    .content(messagePage.getContent().stream().map(MessageResponse::fromEntity).toList())
                    .totalElements(messagePage.getTotalElements())
                    .totalPages(messagePage.getTotalPages())
                    .currentPage(messagePage.getNumber())
                    .size(messagePage.getSize())
                    .first(messagePage.isFirst())
                    .last(messagePage.isLast())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<PaginatedMessageResponse>builder()
                    .status("success")
                    .message("Deleted messages retrieved successfully")
                    .data(response)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error getting deleted messages: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<PaginatedMessageResponse>builder()
                            .status("error")
                            .message("Failed to get deleted messages: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Cleanup old deleted messages
     */
    @PostMapping("/cleanup")
    public ResponseEntity<ApiResponse<Void>> cleanupOldDeletedMessages(
            @RequestParam("daysOld") int daysOld) {
        log.info("Cleaning up deleted messages older than {} days", daysOld);
        
        try {
            messageService.cleanupOldDeletedMessages(daysOld);
            
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status("success")
                    .message("Old deleted messages cleaned up successfully")
                    .build());
                    
        } catch (Exception e) {
            log.error("Error cleaning up old deleted messages: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .status("error")
                            .message("Failed to cleanup old deleted messages: " + e.getMessage())
                            .build());
        }
    }

    // ==================== MESSAGE SCHEDULING ENDPOINTS ====================

    /**
     * Schedule a message for future delivery
     */
    @PostMapping("/schedule")
    public ResponseEntity<ApiResponse<ScheduledMessageResponse>> scheduleMessage(
            @Valid @RequestBody ScheduleMessageRequest request) {
        log.info("Scheduling message for user: {} in room: {} at: {}", 
                request.getSenderUserId(), request.getChatRoomId(), request.getScheduledFor());
        
        try {
            ScheduledMessage scheduledMessage = messageService.scheduleMessage(request);
            
            return ResponseEntity.ok(ApiResponse.<ScheduledMessageResponse>builder()
                    .status("success")
                    .message("Message scheduled successfully")
                    .data(ScheduledMessageResponse.fromEntity(scheduledMessage))
                    .build());
                    
        } catch (Exception e) {
            log.error("Error scheduling message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ScheduledMessageResponse>builder()
                            .status("error")
                            .message("Failed to schedule message: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get scheduled messages for a user
     */
    @GetMapping("/scheduled/user/{userId}")
    public ResponseEntity<ApiResponse<List<ScheduledMessageResponse>>> getScheduledMessagesByUser(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("Getting scheduled messages for user: {}", userId);
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "scheduledFor"));
            Page<ScheduledMessage> scheduledPage = messageService.getScheduledMessagesByUser(userId, pageable);
            
            List<ScheduledMessageResponse> scheduledResponses = scheduledPage.getContent()
                    .stream()
                    .map(ScheduledMessageResponse::fromEntity)
                    .toList();
            
            return ResponseEntity.ok(ApiResponse.<List<ScheduledMessageResponse>>builder()
                    .status("success")
                    .message("Scheduled messages retrieved successfully")
                    .data(scheduledResponses)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error getting scheduled messages by user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<ScheduledMessageResponse>>builder()
                            .status("error")
                            .message("Failed to get scheduled messages: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get scheduled messages for a chat room
     */
    @GetMapping("/scheduled/room/{chatRoomId}")
    public ResponseEntity<ApiResponse<List<ScheduledMessageResponse>>> getScheduledMessagesByRoom(
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("Getting scheduled messages for room: {}", chatRoomId);
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "scheduledFor"));
            Page<ScheduledMessage> scheduledPage = messageService.getScheduledMessagesByRoom(chatRoomId, pageable);
            
            List<ScheduledMessageResponse> scheduledResponses = scheduledPage.getContent()
                    .stream()
                    .map(ScheduledMessageResponse::fromEntity)
                    .toList();
            
            return ResponseEntity.ok(ApiResponse.<List<ScheduledMessageResponse>>builder()
                    .status("success")
                    .message("Scheduled messages retrieved successfully")
                    .data(scheduledResponses)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error getting scheduled messages by room: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<ScheduledMessageResponse>>builder()
                            .status("error")
                            .message("Failed to get scheduled messages: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get scheduled message by ID
     */
    @GetMapping("/scheduled/{id}")
    public ResponseEntity<ApiResponse<ScheduledMessageResponse>> getScheduledMessageById(
            @PathVariable("id") String scheduledMessageId) {
        log.info("Getting scheduled message by ID: {}", scheduledMessageId);
        
        try {
            ScheduledMessage scheduledMessage = messageService.getScheduledMessageById(scheduledMessageId);
            
            return ResponseEntity.ok(ApiResponse.<ScheduledMessageResponse>builder()
                    .status("success")
                    .message("Scheduled message retrieved successfully")
                    .data(ScheduledMessageResponse.fromEntity(scheduledMessage))
                    .build());
                    
        } catch (Exception e) {
            log.error("Error getting scheduled message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ScheduledMessageResponse>builder()
                            .status("error")
                            .message("Failed to get scheduled message: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Cancel a scheduled message
     */
    @DeleteMapping("/scheduled/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelScheduledMessage(
            @PathVariable("id") String scheduledMessageId,
            @RequestParam("userId") Long userId) {
        log.info("Cancelling scheduled message: {} by user: {}", scheduledMessageId, userId);
        
        try {
            messageService.cancelScheduledMessage(scheduledMessageId, userId);
            
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status("success")
                    .message("Scheduled message cancelled successfully")
                    .build());
                    
        } catch (Exception e) {
            log.error("Error cancelling scheduled message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .status("error")
                            .message("Failed to cancel scheduled message: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Update a scheduled message
     */
    @PutMapping("/scheduled/{id}")
    public ResponseEntity<ApiResponse<ScheduledMessageResponse>> updateScheduledMessage(
            @PathVariable("id") String scheduledMessageId,
            @Valid @RequestBody ScheduleMessageRequest request) {
        log.info("Updating scheduled message: {} by user: {}", scheduledMessageId, request.getSenderUserId());
        
        try {
            ScheduledMessage updatedScheduledMessage = messageService.updateScheduledMessage(scheduledMessageId, request);
            
            return ResponseEntity.ok(ApiResponse.<ScheduledMessageResponse>builder()
                    .status("success")
                    .message("Scheduled message updated successfully")
                    .data(ScheduledMessageResponse.fromEntity(updatedScheduledMessage))
                    .build());
                    
        } catch (Exception e) {
            log.error("Error updating scheduled message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ScheduledMessageResponse>builder()
                            .status("error")
                            .message("Failed to update scheduled message: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Process scheduled messages (admin endpoint)
     */
    @PostMapping("/scheduled/process")
    public ResponseEntity<ApiResponse<Void>> processScheduledMessages() {
        log.info("Processing scheduled messages");
        
        try {
            messageService.processScheduledMessages();
            
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status("success")
                    .message("Scheduled messages processed successfully")
                    .build());
                    
        } catch (Exception e) {
            log.error("Error processing scheduled messages: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .status("error")
                            .message("Failed to process scheduled messages: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Retry failed scheduled messages (admin endpoint)
     */
    @PostMapping("/scheduled/retry")
    public ResponseEntity<ApiResponse<Void>> retryFailedScheduledMessages() {
        log.info("Retrying failed scheduled messages");
        
        try {
            messageService.retryFailedScheduledMessages();
            
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status("success")
                    .message("Failed scheduled messages retried successfully")
                    .build());
                    
        } catch (Exception e) {
            log.error("Error retrying failed scheduled messages: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .status("error")
                            .message("Failed to retry scheduled messages: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Cleanup expired scheduled messages (admin endpoint)
     */
    @PostMapping("/scheduled/cleanup")
    public ResponseEntity<ApiResponse<Void>> cleanupExpiredScheduledMessages() {
        log.info("Cleaning up expired scheduled messages");
        
        try {
            messageService.cleanupExpiredScheduledMessages();
            
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status("success")
                    .message("Expired scheduled messages cleaned up successfully")
                    .build());
                    
        } catch (Exception e) {
            log.error("Error cleaning up expired scheduled messages: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .status("error")
                            .message("Failed to cleanup expired scheduled messages: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get scheduled message statistics
     */
    @GetMapping("/scheduled/stats")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getScheduledMessageStats(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "chatRoomId", required = false) Long chatRoomId) {
        log.info("Getting scheduled message statistics for user: {}, room: {}", userId, chatRoomId);
        
        try {
            Map<String, Long> stats = new HashMap<>();
            
            if (userId != null) {
                stats.put("userScheduled", messageService.countScheduledMessagesByUser(userId));
            }
            
            if (chatRoomId != null) {
                stats.put("roomScheduled", messageService.countScheduledMessagesByRoom(chatRoomId));
            }
            
            stats.put("pending", messageService.countScheduledMessagesByStatus(ScheduledMessage.ScheduledStatus.PENDING));
            stats.put("processing", messageService.countScheduledMessagesByStatus(ScheduledMessage.ScheduledStatus.PROCESSING));
            stats.put("sent", messageService.countScheduledMessagesByStatus(ScheduledMessage.ScheduledStatus.SENT));
            stats.put("failed", messageService.countScheduledMessagesByStatus(ScheduledMessage.ScheduledStatus.FAILED));
            stats.put("cancelled", messageService.countScheduledMessagesByStatus(ScheduledMessage.ScheduledStatus.CANCELLED));
            stats.put("expired", messageService.countScheduledMessagesByStatus(ScheduledMessage.ScheduledStatus.EXPIRED));
            
            return ResponseEntity.ok(ApiResponse.<Map<String, Long>>builder()
                    .status("success")
                    .message("Scheduled message statistics retrieved successfully")
                    .data(stats)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error getting scheduled message statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Map<String, Long>>builder()
                            .status("error")
                            .message("Failed to get scheduled message statistics: " + e.getMessage())
                            .build());
        }
    }
}
