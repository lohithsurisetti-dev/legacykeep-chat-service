package com.legacykeep.chat.controller;

import com.legacykeep.chat.dto.ApiResponse;
import com.legacykeep.chat.dto.request.SendMessageRequest;
import com.legacykeep.chat.dto.request.EditMessageRequest;
import com.legacykeep.chat.dto.request.ForwardMessageRequest;
import com.legacykeep.chat.dto.request.ReactionRequest;
import com.legacykeep.chat.dto.response.MessageResponse;
import com.legacykeep.chat.dto.response.PaginatedMessageResponse;
import com.legacykeep.chat.dto.response.MessageStats;
import com.legacykeep.chat.entity.Message;
import com.legacykeep.chat.enums.MessageStatus;
import com.legacykeep.chat.enums.MessageType;
import com.legacykeep.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
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
    public ResponseEntity<ApiResponse<MessageResponse>> getMessageById(@PathVariable String id) {
        log.debug("Getting message by ID: {}", id);
        
        try {
            Optional<Message> message = messageService.getMessageById(id);
            if (message.isPresent()) {
                MessageResponse response = MessageResponse.fromEntity(message.get());
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
    public ResponseEntity<ApiResponse<MessageResponse>> getMessageByUuid(@PathVariable String uuid) {
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
            @PathVariable String id,
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
            @PathVariable String id,
            @RequestParam Long userId) {
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
            @PathVariable String id,
            @RequestParam Long userId) {
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
            @PathVariable String id,
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
            @PathVariable String id,
            @RequestParam Long userId) {
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
            @PathVariable String id,
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
            @PathVariable String id,
            @PathVariable String emoji,
            @RequestParam Long userId) {
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
            @PathVariable String id,
            @RequestParam Long userId) {
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
            @PathVariable Long chatRoomId,
            @RequestParam Long userId) {
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
            @PathVariable Long chatRoomId,
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
            @PathVariable Long chatRoomId,
            @PathVariable String messageId,
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
            @PathVariable Long chatRoomId,
            @PathVariable String messageId,
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
            @PathVariable Long senderId,
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
            @PathVariable MessageType type,
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
            @PathVariable MessageStatus status,
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
            @PathVariable Long userId,
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
            @PathVariable Long chatRoomId,
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
            @PathVariable Long storyId,
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
            @PathVariable Long memoryId,
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
            @PathVariable Long eventId,
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
            @RequestParam String content,
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
            @PathVariable Long chatRoomId,
            @RequestParam String content,
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
    public ResponseEntity<ApiResponse<MessageStats>> getMessageStatsForUser(@PathVariable Long userId) {
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
    public ResponseEntity<ApiResponse<MessageStats>> getMessageStatsForRoom(@PathVariable Long chatRoomId) {
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
    public ResponseEntity<ApiResponse<MessageResponse>> getLatestMessageInRoom(@PathVariable Long chatRoomId) {
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
}
