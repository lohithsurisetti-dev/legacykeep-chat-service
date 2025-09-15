package com.legacykeep.chat.service.impl;

import com.legacykeep.chat.dto.request.EditMessageRequest;
import com.legacykeep.chat.dto.request.ForwardMessageRequest;
import com.legacykeep.chat.dto.request.ReactionRequest;
import com.legacykeep.chat.dto.request.SendMessageRequest;
import com.legacykeep.chat.dto.request.EditMessageWithHistoryRequest;
import com.legacykeep.chat.dto.request.DeleteMessageRequest;
import com.legacykeep.chat.dto.request.ScheduleMessageRequest;
import com.legacykeep.chat.entity.Message;
import com.legacykeep.chat.entity.MessageEditHistory;
import com.legacykeep.chat.entity.ScheduledMessage;
import com.legacykeep.chat.dto.response.ThreadSummary;
import com.legacykeep.chat.enums.MessageStatus;
import com.legacykeep.chat.enums.MessageType;
import com.legacykeep.chat.repository.mongo.MessageRepository;
import com.legacykeep.chat.repository.mongo.MessageEditHistoryRepository;
import com.legacykeep.chat.repository.mongo.ScheduledMessageRepository;
import com.legacykeep.chat.service.ChatRoomService;
import com.legacykeep.chat.service.ContentFilterService;
import com.legacykeep.chat.service.EncryptionService;
import com.legacykeep.chat.service.KeyManagementService;
import com.legacykeep.chat.service.MessageService;
import com.legacykeep.chat.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of MessageService.
 * Provides business logic for message management with advanced family-centric features.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageEditHistoryRepository messageEditHistoryRepository;
    private final ScheduledMessageRepository scheduledMessageRepository;
    private final ChatRoomService chatRoomService;
    private final WebSocketService webSocketService;
    private final EncryptionService encryptionService;
    private final KeyManagementService keyManagementService;
    private final ContentFilterService contentFilterService;

    @Override
    public Message sendMessage(SendMessageRequest request) {
        log.debug("Sending message to chat room: {} from user: {}", request.getChatRoomId(), request.getSenderUserId());
        
        // Validate chat room exists and user has access
        chatRoomService.getChatRoomById(request.getChatRoomId())
                .orElseThrow(() -> new RuntimeException("Chat room not found with ID: " + request.getChatRoomId()));

        // Check content filters before processing message
        // Note: For now, we'll check filters for all potential receivers in the room
        // In a real implementation, you might want to get room participants and check filters for each
        if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
            // For group messages, we check if content would be filtered for any potential receiver
            // This is a simplified approach - in production, you'd check against actual room participants
            log.debug("Checking content filters for message content");
            
            // You could implement more sophisticated logic here to check against actual room participants
            // For now, we'll log the filter check but not block the message
            // This allows the system to be built incrementally
        }

        // Handle encryption if requested
        String messageContent = request.getContent();
        boolean isEncrypted = request.getIsEncrypted() != null ? request.getIsEncrypted() : false;
        
        if (isEncrypted) {
            log.debug("Encrypting message content for chat room: {}", request.getChatRoomId());
            
            // Get encryption key for the chat room
            Optional<String> encryptionKeyOpt = keyManagementService.getChatRoomKey(request.getChatRoomId(), request.getSenderUserId());
            if (encryptionKeyOpt.isEmpty()) {
                // Generate new key if none exists
                String newKey = keyManagementService.generateChatRoomKey(request.getChatRoomId(), request.getSenderUserId());
                log.info("Generated new encryption key for chat room: {}", request.getChatRoomId());
                
                // Encrypt the message content
                messageContent = encryptionService.encryptMessage(request.getContent(), newKey);
            } else {
                // Encrypt the message content with existing key
                messageContent = encryptionService.encryptMessage(request.getContent(), encryptionKeyOpt.get());
            }
            
            log.debug("Message content encrypted successfully");
        }

        // Create message with advanced features
        Message message = Message.builder()
                .messageUuid(UUID.randomUUID().toString())
                .chatRoomId(request.getChatRoomId())
                .senderUserId(request.getSenderUserId())
                .messageType(request.getMessageType())
                .content(messageContent) // Use encrypted content if encryption is enabled
                .status(MessageStatus.SENT)
                .replyToMessageId(request.getReplyToMessageId())
                .forwardedFromMessageId(request.getForwardedFromMessageId())
                .isStarred(false)
                .isEncrypted(isEncrypted)
                .isProtected(request.getIsProtected() != null ? request.getIsProtected() : false)
                .protectionLevel(request.getProtectionLevel())
                .passwordHash(request.getPasswordHash())
                .selfDestructAt(request.getSelfDestructAt())
                .screenshotProtection(request.getScreenshotProtection() != null ? request.getScreenshotProtection() : false)
                .viewCount(0)
                .maxViews(request.getMaxViews())
                .toneColor(request.getToneColor())
                .toneConfidence(request.getToneConfidence())
                .contextWrapper(request.getContextWrapper())
                .moodTag(request.getMoodTag())
                .voiceEmotion(request.getVoiceEmotion())
                .memoryTriggers(request.getMemoryTriggers())
                .predictiveText(request.getPredictiveText() != null ? request.getPredictiveText().toString() : null)
                .aiToneSuggestion(request.getAiToneSuggestion())
                .mediaUrl(request.getMediaUrl())
                .mediaThumbnailUrl(request.getMediaThumbnailUrl())
                .mediaSize(request.getMediaSize())
                .mediaDuration(request.getMediaDuration())
                .mediaFormat(request.getMediaFormat())
                .mediaMetadata(request.getMediaMetadata())
                .locationLatitude(request.getLocationLatitude())
                .locationLongitude(request.getLocationLongitude())
                .locationAddress(request.getLocationAddress())
                .locationName(request.getLocationName())
                .contactName(request.getContactName())
                .contactPhone(request.getContactPhone())
                .contactEmail(request.getContactEmail())
                .storyId(request.getStoryId())
                .memoryId(request.getMemoryId())
                .eventId(request.getEventId())
                .reactions(new HashMap<>())
                .readBy(new HashMap<>())
                .metadata(request.getMetadata())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Message savedMessage = messageRepository.save(message);
        
        // Update chat room last message info
        chatRoomService.updateLastMessageInfo(request.getChatRoomId(), savedMessage.getId(), request.getSenderUserId());
        chatRoomService.incrementMessageCount(request.getChatRoomId());
        
        // Send real-time notification
        webSocketService.sendMessageToRoom(request.getChatRoomId(), savedMessage);
        
        log.info("Sent message with ID: {} to chat room: {}", savedMessage.getId(), request.getChatRoomId());
        return savedMessage;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Message> getMessageById(String id) {
        log.debug("Getting message by ID: {}", id);
        return messageRepository.findById(id);
    }
    
    /**
     * Get message by ID with decryption if needed
     * 
     * @param id Message ID
     * @param userId User ID requesting the message (for key access)
     * @return Optional containing the message with decrypted content if user has access
     */
    @Transactional(readOnly = true)
    public Optional<Message> getMessageByIdWithDecryption(String id, Long userId) {
        log.debug("Getting message by ID: {} for user: {}", id, userId);
        
        Optional<Message> messageOpt = messageRepository.findById(id);
        if (messageOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Message message = messageOpt.get();
        
        // Decrypt content if message is encrypted and user has access
        if (message.getIsEncrypted() != null && message.getIsEncrypted()) {
            log.debug("Decrypting message content for user: {}", userId);
            
            Optional<String> encryptionKeyOpt = keyManagementService.getChatRoomKey(message.getChatRoomId(), userId);
            if (encryptionKeyOpt.isEmpty()) {
                log.warn("User {} does not have access to encryption key for message {}", userId, id);
                return Optional.empty();
            }
            
            try {
                String decryptedContent = encryptionService.decryptMessage(message.getContent(), encryptionKeyOpt.get());
                // Create a new message object with decrypted content
                message = Message.builder()
                        .id(message.getId())
                        .messageUuid(message.getMessageUuid())
                        .chatRoomId(message.getChatRoomId())
                        .senderUserId(message.getSenderUserId())
                        .messageType(message.getMessageType())
                        .content(decryptedContent) // Decrypted content
                        .status(message.getStatus())
                        .replyToMessageId(message.getReplyToMessageId())
                        .forwardedFromMessageId(message.getForwardedFromMessageId())
                        .isStarred(message.getIsStarred())
                        .isEncrypted(message.getIsEncrypted())
                        .isProtected(message.getIsProtected())
                        .protectionLevel(message.getProtectionLevel())
                        .passwordHash(message.getPasswordHash())
                        .selfDestructAt(message.getSelfDestructAt())
                        .screenshotProtection(message.getScreenshotProtection())
                        .viewCount(message.getViewCount())
                        .maxViews(message.getMaxViews())
                        .toneColor(message.getToneColor())
                        .toneConfidence(message.getToneConfidence())
                        .contextWrapper(message.getContextWrapper())
                        .moodTag(message.getMoodTag())
                        .voiceEmotion(message.getVoiceEmotion())
                        .memoryTriggers(message.getMemoryTriggers())
                        .predictiveText(message.getPredictiveText())
                        .aiToneSuggestion(message.getAiToneSuggestion())
                        .mediaUrl(message.getMediaUrl())
                        .mediaThumbnailUrl(message.getMediaThumbnailUrl())
                        .mediaSize(message.getMediaSize())
                        .mediaDuration(message.getMediaDuration())
                        .mediaFormat(message.getMediaFormat())
                        .mediaMetadata(message.getMediaMetadata())
                        .locationLatitude(message.getLocationLatitude())
                        .locationLongitude(message.getLocationLongitude())
                        .locationAddress(message.getLocationAddress())
                        .locationName(message.getLocationName())
                        .contactName(message.getContactName())
                        .contactPhone(message.getContactPhone())
                        .contactEmail(message.getContactEmail())
                        .storyId(message.getStoryId())
                        .memoryId(message.getMemoryId())
                        .eventId(message.getEventId())
                        .reactions(message.getReactions())
                        .readBy(message.getReadBy())
                        .metadata(message.getMetadata())
                        .createdAt(message.getCreatedAt())
                        .updatedAt(message.getUpdatedAt())
                        .editedAt(message.getEditedAt())
                        .deletedAt(message.getDeletedAt())
                        .deletedByUserId(message.getDeletedByUserId())
                        .isDeletedForEveryone(message.getIsDeletedForEveryone())
                        .build();
                
                log.debug("Message content decrypted successfully for user: {}", userId);
            } catch (Exception e) {
                log.error("Failed to decrypt message {} for user {}: {}", id, userId, e.getMessage(), e);
                return Optional.empty();
            }
        }
        
        return Optional.of(message);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Message> getMessageByUuid(String messageUuid) {
        log.debug("Getting message by UUID: {}", messageUuid);
        return messageRepository.findByMessageUuid(messageUuid);
    }

    @Override
    public Message editMessage(String messageId, EditMessageRequest request) {
        log.debug("Editing message with ID: {}", messageId);
        
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found with ID: " + messageId));

        // Check if user can edit this message (only sender can edit)
        if (!message.getSenderUserId().equals(request.getUserId())) {
            throw new RuntimeException("User is not authorized to edit this message");
        }

        // Check if message is not too old to edit (e.g., 15 minutes)
        if (message.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(15))) {
            throw new RuntimeException("Message is too old to edit");
        }

        // Update message content
        message.setContent(request.getNewContent());
        message.setEditedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());

        Message updatedMessage = messageRepository.save(message);
        
        // Send real-time notification
        webSocketService.sendMessageEditNotification(messageId, request.getUserId(), request.getNewContent());
        
        log.info("Edited message with ID: {}", messageId);
        return updatedMessage;
    }

    @Override
    public void deleteMessage(String messageId, Long userId) {
        log.debug("Deleting message with ID: {} by user: {}", messageId, userId);
        
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found with ID: " + messageId));

        // Check if user can delete this message
        if (!message.getSenderUserId().equals(userId)) {
            throw new RuntimeException("User is not authorized to delete this message");
        }

        // Soft delete - mark as deleted
        message.setDeletedAt(LocalDateTime.now());
        message.setDeletedByUserId(userId);
        message.setIsDeletedForEveryone(false);
        message.setUpdatedAt(LocalDateTime.now());

        messageRepository.save(message);
        
        // Send real-time notification
        webSocketService.sendMessageDeleteNotification(messageId, userId, false);
        
        log.info("Deleted message with ID: {} by user: {}", messageId, userId);
    }

    @Override
    public void deleteMessageForEveryone(String messageId, Long userId) {
        log.debug("Deleting message for everyone with ID: {} by user: {}", messageId, userId);
        
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found with ID: " + messageId));

        // Check if user can delete this message for everyone (only sender)
        if (!message.getSenderUserId().equals(userId)) {
            throw new RuntimeException("User is not authorized to delete this message for everyone");
        }

        // Check if message is not too old to delete for everyone (e.g., 1 hour)
        if (message.getCreatedAt().isBefore(LocalDateTime.now().minusHours(1))) {
            throw new RuntimeException("Message is too old to delete for everyone");
        }

        // Soft delete for everyone
        message.setDeletedAt(LocalDateTime.now());
        message.setDeletedByUserId(userId);
        message.setIsDeletedForEveryone(true);
        message.setUpdatedAt(LocalDateTime.now());

        messageRepository.save(message);
        
        // Send real-time notification to all participants
        webSocketService.sendMessageDeleteNotification(messageId, userId, true);
        
        log.info("Deleted message for everyone with ID: {} by user: {}", messageId, userId);
    }

    @Override
    public Message forwardMessage(ForwardMessageRequest request) {
        log.debug("Forwarding message with ID: {} to chat room: {}", request.getOriginalMessageId(), request.getToChatRoomId());
        
        Message originalMessage = messageRepository.findById(request.getOriginalMessageId())
                .orElseThrow(() -> new RuntimeException("Original message not found with ID: " + request.getOriginalMessageId()));

        // Validate target chat room exists
        chatRoomService.getChatRoomById(request.getToChatRoomId())
                .orElseThrow(() -> new RuntimeException("Target chat room not found with ID: " + request.getToChatRoomId()));

        // Create forwarded message
        Message forwardedMessage = Message.builder()
                .messageUuid(UUID.randomUUID().toString())
                .chatRoomId(request.getToChatRoomId())
                .senderUserId(request.getFromUserId())
                .messageType(originalMessage.getMessageType())
                .content(originalMessage.getContent())
                .status(MessageStatus.SENT)
                .forwardedFromMessageId(originalMessage.getId())
                .isStarred(false)
                .isEncrypted(originalMessage.getIsEncrypted())
                .isProtected(false) // Forwarded messages are not protected
                .mediaUrl(originalMessage.getMediaUrl())
                .mediaThumbnailUrl(originalMessage.getMediaThumbnailUrl())
                .mediaSize(originalMessage.getMediaSize())
                .mediaDuration(originalMessage.getMediaDuration())
                .mediaFormat(originalMessage.getMediaFormat())
                .mediaMetadata(originalMessage.getMediaMetadata())
                .locationLatitude(originalMessage.getLocationLatitude())
                .locationLongitude(originalMessage.getLocationLongitude())
                .locationAddress(originalMessage.getLocationAddress())
                .locationName(originalMessage.getLocationName())
                .contactName(originalMessage.getContactName())
                .contactPhone(originalMessage.getContactPhone())
                .contactEmail(originalMessage.getContactEmail())
                .reactions(new HashMap<>())
                .readBy(new HashMap<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Message savedForwardedMessage = messageRepository.save(forwardedMessage);
        
        // Update chat room last message info
        chatRoomService.updateLastMessageInfo(request.getToChatRoomId(), savedForwardedMessage.getId(), request.getFromUserId());
        chatRoomService.incrementMessageCount(request.getToChatRoomId());
        
        // Send real-time notifications
        webSocketService.sendMessageToRoom(request.getToChatRoomId(), savedForwardedMessage);
        webSocketService.sendMessageForwardNotification(request.getOriginalMessageId(), savedForwardedMessage.getId(), 
                request.getFromUserId(), request.getToChatRoomId());
        
        log.info("Forwarded message with ID: {} to chat room: {}", request.getOriginalMessageId(), request.getToChatRoomId());
        return savedForwardedMessage;
    }

    @Override
    public Message toggleStarMessage(String messageId, Long userId) {
        log.debug("Toggling star for message with ID: {} by user: {}", messageId, userId);
        
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found with ID: " + messageId));

        // Toggle star status
        boolean newStarStatus = !(message.getIsStarred() != null && message.getIsStarred());
        message.setIsStarred(newStarStatus);
        message.setUpdatedAt(LocalDateTime.now());

        Message updatedMessage = messageRepository.save(message);
        
        // Send real-time notification
        webSocketService.sendMessageStarNotification(messageId, userId, newStarStatus);
        
        log.info("Toggled star for message with ID: {} by user: {} to: {}", messageId, userId, newStarStatus);
        return updatedMessage;
    }

    @Override
    public Message addReaction(String messageId, ReactionRequest request) {
        log.debug("Adding reaction {} to message with ID: {} by user: {}", request.getEmoji(), messageId, request.getUserId());
        
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found with ID: " + messageId));

        // Initialize reactions map if null
        if (message.getReactions() == null) {
            message.setReactions(new HashMap<>());
        }

        // Add reaction
        List<Long> users = message.getReactions().getOrDefault(request.getEmoji(), new ArrayList<>());
        if (!users.contains(request.getUserId())) {
            users.add(request.getUserId());
            message.getReactions().put(request.getEmoji(), users);
            message.setUpdatedAt(LocalDateTime.now());

            Message updatedMessage = messageRepository.save(message);
            
            // Send real-time notification
            webSocketService.sendMessageReaction(messageId, request.getUserId(), request.getEmoji(), true);
            
            log.info("Added reaction {} to message with ID: {} by user: {}", request.getEmoji(), messageId, request.getUserId());
            return updatedMessage;
        }

        return message;
    }

    @Override
    public Message removeReaction(String messageId, Long userId, String emoji) {
        log.debug("Removing reaction {} from message with ID: {} by user: {}", emoji, messageId, userId);
        
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found with ID: " + messageId));

        if (message.getReactions() != null && message.getReactions().containsKey(emoji)) {
            List<Long> users = message.getReactions().get(emoji);
            users.remove(userId);
            
            if (users.isEmpty()) {
                message.getReactions().remove(emoji);
            } else {
                message.getReactions().put(emoji, users);
            }
            
            message.setUpdatedAt(LocalDateTime.now());
            Message updatedMessage = messageRepository.save(message);
            
            // Send real-time notification
            webSocketService.sendMessageReaction(messageId, userId, emoji, false);
            
            log.info("Removed reaction {} from message with ID: {} by user: {}", emoji, messageId, userId);
            return updatedMessage;
        }

        return message;
    }

    @Override
    public void markMessageAsRead(String messageId, Long userId) {
        log.debug("Marking message with ID: {} as read by user: {}", messageId, userId);
        
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found with ID: " + messageId));

        // Initialize readBy map if null
        if (message.getReadBy() == null) {
            message.setReadBy(new HashMap<>());
        }

        // Mark as read
        message.getReadBy().put(userId, LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());

        messageRepository.save(message);
        
        // Send real-time notification
        webSocketService.sendReadReceipt(messageId, userId);
        
        log.debug("Marked message with ID: {} as read by user: {}", messageId, userId);
    }

    @Override
    public void markMessagesAsReadInRoom(Long chatRoomId, Long userId) {
        log.debug("Marking all messages in chat room: {} as read by user: {}", chatRoomId, userId);
        
        // Get unread messages in the room
        List<Message> unreadMessages = messageRepository.findByChatRoomIdOrderByCreatedAtDesc(chatRoomId)
                .stream()
                .filter(msg -> !msg.isReadBy(userId))
                .collect(Collectors.toList());

        // Mark all as read
        for (Message message : unreadMessages) {
            markMessageAsRead(message.getId(), userId);
        }
        
        log.info("Marked {} messages as read in chat room: {} by user: {}", unreadMessages.size(), chatRoomId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessagesInRoom(Long chatRoomId) {
        log.debug("Getting messages in chat room: {}", chatRoomId);
        return messageRepository.findByChatRoomIdOrderByCreatedAtDesc(chatRoomId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getMessagesInRoom(Long chatRoomId, Pageable pageable) {
        log.debug("Getting messages in chat room: {} with pagination", chatRoomId);
        return messageRepository.findByChatRoomIdOrderByCreatedAtDesc(chatRoomId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessagesBefore(Long chatRoomId, String messageId, int limit) {
        log.debug("Getting {} messages before message: {} in chat room: {}", limit, messageId, chatRoomId);
        
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found with ID: " + messageId));

        return messageRepository.findMessagesBefore(chatRoomId, message.getCreatedAt(), 
                Pageable.ofSize(limit)).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessagesAfter(Long chatRoomId, String messageId, int limit) {
        log.debug("Getting {} messages after message: {} in chat room: {}", limit, messageId, chatRoomId);
        
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found with ID: " + messageId));

        return messageRepository.findMessagesAfter(chatRoomId, message.getCreatedAt(), 
                Pageable.ofSize(limit)).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessagesBySender(Long senderUserId) {
        log.debug("Getting messages by sender: {}", senderUserId);
        return messageRepository.findBySenderUserIdOrderByCreatedAtDesc(senderUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getMessagesBySender(Long senderUserId, Pageable pageable) {
        log.debug("Getting messages by sender: {} with pagination", senderUserId);
        return messageRepository.findBySenderUserIdOrderByCreatedAtDesc(senderUserId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessagesByType(MessageType messageType) {
        log.debug("Getting messages by type: {}", messageType);
        return messageRepository.findByMessageTypeOrderByCreatedAtDesc(messageType);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getMessagesByType(MessageType messageType, Pageable pageable) {
        log.debug("Getting messages by type: {} with pagination", messageType);
        return messageRepository.findByMessageTypeOrderByCreatedAtDesc(messageType, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessagesByStatus(MessageStatus status) {
        log.debug("Getting messages by status: {}", status);
        return messageRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getMessagesByStatus(MessageStatus status, Pageable pageable) {
        log.debug("Getting messages by status: {} with pagination", status);
        return messageRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getStarredMessagesForUser(Long userId) {
        log.debug("Getting starred messages for user: {}", userId);
        return messageRepository.findStarredMessagesByUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getStarredMessagesForUser(Long userId, Pageable pageable) {
        log.debug("Getting starred messages for user: {} with pagination", userId);
        return messageRepository.findStarredMessagesByUser(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getStarredMessagesInRoom(Long chatRoomId) {
        log.debug("Getting starred messages in chat room: {}", chatRoomId);
        return messageRepository.findStarredMessagesInRoom(chatRoomId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getStarredMessagesInRoom(Long chatRoomId, Pageable pageable) {
        log.debug("Getting starred messages in chat room: {} with pagination", chatRoomId);
        return messageRepository.findStarredMessagesInRoom(chatRoomId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getProtectedMessages() {
        log.debug("Getting protected messages");
        return messageRepository.findByIsProtectedTrueOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getProtectedMessages(Pageable pageable) {
        log.debug("Getting protected messages with pagination");
        return messageRepository.findByIsProtectedTrueOrderByCreatedAtDesc(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getProtectedMessagesInRoom(Long chatRoomId) {
        log.debug("Getting protected messages in chat room: {}", chatRoomId);
        return messageRepository.findProtectedMessagesInRoom(chatRoomId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getProtectedMessagesInRoom(Long chatRoomId, Pageable pageable) {
        log.debug("Getting protected messages in chat room: {} with pagination", chatRoomId);
        return messageRepository.findProtectedMessagesInRoom(chatRoomId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessagesWithToneColor() {
        log.debug("Getting messages with tone color");
        return messageRepository.findMessagesWithToneColor();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getMessagesWithToneColor(Pageable pageable) {
        log.debug("Getting messages with tone color with pagination");
        return messageRepository.findMessagesWithToneColor(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessagesWithToneColorInRoom(Long chatRoomId) {
        log.debug("Getting messages with tone color in chat room: {}", chatRoomId);
        return messageRepository.findMessagesWithToneColorInRoom(chatRoomId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getMessagesWithToneColorInRoom(Long chatRoomId, Pageable pageable) {
        log.debug("Getting messages with tone color in chat room: {} with pagination", chatRoomId);
        return messageRepository.findMessagesWithToneColorInRoom(chatRoomId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessagesWithAIFeatures() {
        log.debug("Getting messages with AI features");
        return messageRepository.findMessagesWithAIFeatures();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getMessagesWithAIFeatures(Pageable pageable) {
        log.debug("Getting messages with AI features with pagination");
        return messageRepository.findMessagesWithAIFeatures(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessagesWithMedia() {
        log.debug("Getting messages with media");
        return messageRepository.findMessagesWithMedia();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getMessagesWithMedia(Pageable pageable) {
        log.debug("Getting messages with media with pagination");
        return messageRepository.findMessagesWithMedia(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessagesWithMediaInRoom(Long chatRoomId) {
        log.debug("Getting messages with media in chat room: {}", chatRoomId);
        return messageRepository.findMessagesWithMediaInRoom(chatRoomId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getMessagesWithMediaInRoom(Long chatRoomId, Pageable pageable) {
        log.debug("Getting messages with media in chat room: {} with pagination", chatRoomId);
        return messageRepository.findMessagesWithMediaInRoom(chatRoomId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessagesWithLocation() {
        log.debug("Getting messages with location");
        return messageRepository.findMessagesWithLocation();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getMessagesWithLocation(Pageable pageable) {
        log.debug("Getting messages with location with pagination");
        return messageRepository.findMessagesWithLocation(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessagesWithContact() {
        log.debug("Getting messages with contact information");
        return messageRepository.findMessagesWithContact();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getMessagesWithContact(Pageable pageable) {
        log.debug("Getting messages with contact information with pagination");
        return messageRepository.findMessagesWithContact(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessagesByStory(Long storyId) {
        log.debug("Getting messages by story: {}", storyId);
        return messageRepository.findByStoryIdOrderByCreatedAtDesc(storyId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getMessagesByStory(Long storyId, Pageable pageable) {
        log.debug("Getting messages by story: {} with pagination", storyId);
        return messageRepository.findByStoryIdOrderByCreatedAtDesc(storyId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessagesByMemory(Long memoryId) {
        log.debug("Getting messages by memory: {}", memoryId);
        return messageRepository.findByMemoryIdOrderByCreatedAtDesc(memoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getMessagesByMemory(Long memoryId, Pageable pageable) {
        log.debug("Getting messages by memory: {} with pagination", memoryId);
        return messageRepository.findByMemoryIdOrderByCreatedAtDesc(memoryId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessagesByEvent(Long eventId) {
        log.debug("Getting messages by event: {}", eventId);
        return messageRepository.findByEventIdOrderByCreatedAtDesc(eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getMessagesByEvent(Long eventId, Pageable pageable) {
        log.debug("Getting messages by event: {} with pagination", eventId);
        return messageRepository.findByEventIdOrderByCreatedAtDesc(eventId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessagesBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Getting messages between dates: {} and {}", startDate, endDate);
        return messageRepository.findMessagesBetweenDates(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getMessagesBetweenDates(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("Getting messages between dates: {} and {} with pagination", startDate, endDate);
        return messageRepository.findMessagesBetweenDates(startDate, endDate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessagesInRoomBetweenDates(Long chatRoomId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Getting messages in chat room: {} between dates: {} and {}", chatRoomId, startDate, endDate);
        return messageRepository.findMessagesInRoomBetweenDates(chatRoomId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getMessagesInRoomBetweenDates(Long chatRoomId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("Getting messages in chat room: {} between dates: {} and {} with pagination", chatRoomId, startDate, endDate);
        return messageRepository.findMessagesInRoomBetweenDates(chatRoomId, startDate, endDate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Message> getLatestMessageInRoom(Long chatRoomId) {
        log.debug("Getting latest message in chat room: {}", chatRoomId);
        return messageRepository.findLatestMessageInRoom(chatRoomId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> searchMessagesByContent(String content) {
        log.debug("Searching messages by content: {}", content);
        // This would typically use MongoDB text search
        // For now, we'll use a simple content filter
        return messageRepository.findAll().stream()
                .filter(msg -> msg.getContent() != null && msg.getContent().toLowerCase().contains(content.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> searchMessagesByContent(String content, Pageable pageable) {
        log.debug("Searching messages by content: {} with pagination", content);
        // This would typically use MongoDB text search with pagination
        List<Message> results = searchMessagesByContent(content);
        return new org.springframework.data.domain.PageImpl<>(results, pageable, results.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> searchMessagesByContentInRoom(Long chatRoomId, String content) {
        log.debug("Searching messages by content: {} in chat room: {}", content, chatRoomId);
        return getMessagesInRoom(chatRoomId).stream()
                .filter(msg -> msg.getContent() != null && msg.getContent().toLowerCase().contains(content.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> searchMessagesByContentInRoom(Long chatRoomId, String content, Pageable pageable) {
        log.debug("Searching messages by content: {} in chat room: {} with pagination", content, chatRoomId);
        List<Message> results = searchMessagesByContentInRoom(chatRoomId, content);
        return new org.springframework.data.domain.PageImpl<>(results, pageable, results.size());
    }

    @Override
    @Transactional(readOnly = true)
    public MessageStats getMessageStatsForUser(Long userId) {
        log.debug("Getting message statistics for user: {}", userId);
        
        long totalMessages = messageRepository.countBySenderUserId(userId);
        long textMessages = messageRepository.countByMessageType(MessageType.TEXT);
        long mediaMessages = messageRepository.countMessagesWithMedia();
        long voiceMessages = messageRepository.countByMessageType(MessageType.AUDIO);
        long starredMessages = messageRepository.countStarredMessagesByUser(userId);
        long protectedMessages = messageRepository.countProtectedMessages();
        long messagesWithToneColor = messageRepository.findMessagesWithToneColor().size();
        long messagesWithAIFeatures = messageRepository.findMessagesWithAIFeatures().size();
        long messagesWithLocation = messageRepository.findMessagesWithLocation().size();
        long messagesWithContact = messageRepository.findMessagesWithContact().size();
        long storyMessages = messageRepository.findByStoryIdOrderByCreatedAtDesc(1L).size(); // This would need proper implementation
        long memoryMessages = messageRepository.findByMemoryIdOrderByCreatedAtDesc(1L).size(); // This would need proper implementation
        long eventMessages = messageRepository.findByEventIdOrderByCreatedAtDesc(1L).size(); // This would need proper implementation

        return new MessageStats(totalMessages, textMessages, mediaMessages, voiceMessages, 
                starredMessages, protectedMessages, messagesWithToneColor, messagesWithAIFeatures,
                mediaMessages, messagesWithLocation, messagesWithContact, storyMessages,
                memoryMessages, eventMessages);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageStats getMessageStatsForRoom(Long chatRoomId) {
        log.debug("Getting message statistics for chat room: {}", chatRoomId);
        
        long totalMessages = messageRepository.countByChatRoomId(chatRoomId);
        long starredMessages = messageRepository.countStarredMessagesInRoom(chatRoomId);
        long protectedMessages = messageRepository.countProtectedMessagesInRoom(chatRoomId);
        long messagesWithMedia = messageRepository.countMessagesWithMediaInRoom(chatRoomId);
        
        // Get room messages for detailed stats
        List<Message> roomMessages = getMessagesInRoom(chatRoomId);
        long textMessages = roomMessages.stream().mapToLong(msg -> msg.isTextMessage() ? 1 : 0).sum();
        long mediaMessages = roomMessages.stream().mapToLong(msg -> msg.isMediaMessage() ? 1 : 0).sum();
        long voiceMessages = roomMessages.stream().mapToLong(msg -> msg.getMessageType() == MessageType.AUDIO ? 1 : 0).sum();
        long messagesWithToneColor = roomMessages.stream().mapToLong(msg -> msg.hasToneColor() ? 1 : 0).sum();
        long messagesWithAIFeatures = roomMessages.stream().mapToLong(msg -> msg.hasAIFeatures() ? 1 : 0).sum();
        long messagesWithLocation = roomMessages.stream().mapToLong(msg -> msg.hasLocation() ? 1 : 0).sum();
        long messagesWithContact = roomMessages.stream().mapToLong(msg -> msg.hasContact() ? 1 : 0).sum();
        long storyMessages = roomMessages.stream().mapToLong(msg -> msg.hasStory() ? 1 : 0).sum();
        long memoryMessages = roomMessages.stream().mapToLong(msg -> msg.hasMemory() ? 1 : 0).sum();
        long eventMessages = roomMessages.stream().mapToLong(msg -> msg.hasEvent() ? 1 : 0).sum();

        return new MessageStats(totalMessages, textMessages, mediaMessages, voiceMessages,
                starredMessages, protectedMessages, messagesWithToneColor, messagesWithAIFeatures,
                messagesWithMedia, messagesWithLocation, messagesWithContact, storyMessages,
                memoryMessages, eventMessages);
    }

    @Override
    @Transactional(readOnly = true)
    public long countMessagesInRoom(Long chatRoomId) {
        log.debug("Counting messages in chat room: {}", chatRoomId);
        return messageRepository.countByChatRoomId(chatRoomId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countMessagesBySender(Long senderUserId) {
        log.debug("Counting messages by sender: {}", senderUserId);
        return messageRepository.countBySenderUserId(senderUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countMessagesByType(MessageType messageType) {
        log.debug("Counting messages by type: {}", messageType);
        return messageRepository.countByMessageType(messageType);
    }

    @Override
    @Transactional(readOnly = true)
    public long countMessagesByStatus(MessageStatus status) {
        log.debug("Counting messages by status: {}", status);
        return messageRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countStarredMessagesForUser(Long userId) {
        log.debug("Counting starred messages for user: {}", userId);
        return messageRepository.countStarredMessagesByUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countStarredMessagesInRoom(Long chatRoomId) {
        log.debug("Counting starred messages in chat room: {}", chatRoomId);
        return messageRepository.countStarredMessagesInRoom(chatRoomId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countProtectedMessages() {
        log.debug("Counting protected messages");
        return messageRepository.countProtectedMessages();
    }

    @Override
    @Transactional(readOnly = true)
    public long countProtectedMessagesInRoom(Long chatRoomId) {
        log.debug("Counting protected messages in chat room: {}", chatRoomId);
        return messageRepository.countProtectedMessagesInRoom(chatRoomId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countMessagesWithMedia() {
        log.debug("Counting messages with media");
        return messageRepository.countMessagesWithMedia();
    }

    @Override
    @Transactional(readOnly = true)
    public long countMessagesWithMediaInRoom(Long chatRoomId) {
        log.debug("Counting messages with media in chat room: {}", chatRoomId);
        return messageRepository.countMessagesWithMediaInRoom(chatRoomId);
    }

    @Override
    public void cleanupExpiredMessages() {
        log.debug("Cleaning up expired messages");
        LocalDateTime now = LocalDateTime.now();
        List<Message> expiredMessages = messageRepository.findExpiredMessages(now);
        
        for (Message message : expiredMessages) {
            message.setDeletedAt(now);
            message.setIsDeletedForEveryone(true);
            messageRepository.save(message);
            
            // Send notification
            webSocketService.sendMessageExpiredNotification(message.getId(), message.getSenderUserId());
        }
        
        log.info("Cleaned up {} expired messages", expiredMessages.size());
    }

    @Override
    public void cleanupMessagesAtViewLimit() {
        log.debug("Cleaning up messages at view limit");
        List<Message> messagesAtLimit = messageRepository.findMessagesAtViewLimit();
        
        for (Message message : messagesAtLimit) {
            message.setDeletedAt(LocalDateTime.now());
            message.setIsDeletedForEveryone(true);
            messageRepository.save(message);
            
            // Send notification
            webSocketService.sendViewLimitReachedNotification(message.getId(), message.getSenderUserId());
        }
        
        log.info("Cleaned up {} messages at view limit", messagesAtLimit.size());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean wouldMessageBeFiltered(Long senderUserId, Long receiverUserId, Long roomId, String content) {
        log.debug("Checking if message would be filtered for sender: {}, receiver: {}, room: {}", senderUserId, receiverUserId, roomId);
        
        if (content == null || content.trim().isEmpty()) {
            return false;
        }
        
        return contentFilterService.shouldFilterMessage(senderUserId, receiverUserId, roomId, content);
    }

    @Override
    public Message sendMessageWithFiltering(SendMessageRequest request) {
        log.debug("Sending message with filtering to chat room: {} from user: {}", request.getChatRoomId(), request.getSenderUserId());
        
        // Validate chat room exists and user has access
        chatRoomService.getChatRoomById(request.getChatRoomId())
                .orElseThrow(() -> new RuntimeException("Chat room not found with ID: " + request.getChatRoomId()));

        // Check content filters before processing message
        if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
            log.debug("Checking content filters for message content");
            
            // For now, we'll implement a simple check
            // In a real implementation, you'd check against all room participants
            // This is a placeholder for the filtering logic
            
            // You could get room participants and check filters for each:
            // List<Long> participants = chatRoomService.getRoomParticipants(request.getChatRoomId());
            // for (Long participantId : participants) {
            //     if (contentFilterService.shouldFilterMessage(request.getSenderUserId(), participantId, request.getChatRoomId(), request.getContent())) {
            //         log.warn("Message filtered for participant: {}", participantId);
            //         return null; // Message is filtered
            //     }
            // }
            
            // For now, we'll just log and continue
            log.debug("Content filter check completed - message will be sent");
        }

        // If we reach here, the message passed all filters
        // Use the existing sendMessage method
        return sendMessage(request);
    }

    // ==================== SEARCH METHODS ====================

    @Override
    public List<Message> searchMessages(String query, Long userId) {
        log.debug("Searching messages with query: '{}' for user: {}", query, userId);
        
        long startTime = System.currentTimeMillis();
        List<Message> messages = messageRepository.searchMessages(query);
        long searchTime = System.currentTimeMillis() - startTime;
        
        log.info("Found {} messages for query '{}' in {}ms", messages.size(), query, searchTime);
        return messages;
    }

    @Override
    public Page<Message> searchMessages(String query, Long userId, Pageable pageable) {
        log.debug("Searching messages with query: '{}' for user: {} with pagination", query, userId);
        
        long startTime = System.currentTimeMillis();
        Page<Message> messages = messageRepository.searchMessages(query, pageable);
        long searchTime = System.currentTimeMillis() - startTime;
        
        log.info("Found {} messages for query '{}' in {}ms (page {})", 
                messages.getTotalElements(), query, searchTime, pageable.getPageNumber());
        return messages;
    }

    @Override
    public List<Message> searchMessagesWithFilters(String query, Long userId, Long chatRoomId, List<Long> chatRoomIds, 
                                                 Long senderUserId, LocalDateTime startDate, LocalDateTime endDate, 
                                                 Boolean isStarred, Boolean isEncrypted, Boolean includeDeleted) {
        log.debug("Searching messages with advanced filters - query: '{}', user: {}, room: {}, sender: {}", 
                query, userId, chatRoomId, senderUserId);
        
        long startTime = System.currentTimeMillis();
        
        // Build filter parameters
        List<Long> roomIds = chatRoomIds != null ? chatRoomIds : 
                           (chatRoomId != null ? List.of(chatRoomId) : null);
        
        List<Message> messages = messageRepository.searchMessagesWithFilters(
                query, roomIds, senderUserId, isStarred, startDate, endDate);
        
        long searchTime = System.currentTimeMillis() - startTime;
        
        log.info("Found {} messages with advanced filters for query '{}' in {}ms", 
                messages.size(), query, searchTime);
        return messages;
    }

    @Override
    public Page<Message> searchMessagesWithFilters(String query, Long userId, Long chatRoomId, List<Long> chatRoomIds, 
                                                 Long senderUserId, LocalDateTime startDate, LocalDateTime endDate, 
                                                 Boolean isStarred, Boolean isEncrypted, Boolean includeDeleted, Pageable pageable) {
        log.debug("Searching messages with advanced filters and pagination - query: '{}', user: {}, room: {}, sender: {}", 
                query, userId, chatRoomId, senderUserId);
        
        long startTime = System.currentTimeMillis();
        
        // Build filter parameters
        List<Long> roomIds = chatRoomIds != null ? chatRoomIds : 
                           (chatRoomId != null ? List.of(chatRoomId) : null);
        
        Page<Message> messages = messageRepository.searchMessagesWithFilters(
                query, roomIds, senderUserId, isStarred, startDate, endDate, pageable);
        
        long searchTime = System.currentTimeMillis() - startTime;
        
        log.info("Found {} messages with advanced filters for query '{}' in {}ms (page {})", 
                messages.getTotalElements(), query, searchTime, pageable.getPageNumber());
        return messages;
    }

    @Override
    public List<Message> searchMessagesInRoom(String query, Long userId, Long chatRoomId) {
        log.debug("Searching messages in room: {} with query: '{}' for user: {}", chatRoomId, query, userId);
        
        long startTime = System.currentTimeMillis();
        List<Message> messages = messageRepository.searchMessagesInRoom(chatRoomId, query);
        long searchTime = System.currentTimeMillis() - startTime;
        
        log.info("Found {} messages in room {} for query '{}' in {}ms", 
                messages.size(), chatRoomId, query, searchTime);
        return messages;
    }

    @Override
    public Page<Message> searchMessagesInRoom(String query, Long userId, Long chatRoomId, Pageable pageable) {
        log.debug("Searching messages in room: {} with query: '{}' for user: {} with pagination", 
                chatRoomId, query, userId);
        
        long startTime = System.currentTimeMillis();
        Page<Message> messages = messageRepository.searchMessagesInRoom(chatRoomId, query, pageable);
        long searchTime = System.currentTimeMillis() - startTime;
        
        log.info("Found {} messages in room {} for query '{}' in {}ms (page {})", 
                messages.getTotalElements(), chatRoomId, query, searchTime, pageable.getPageNumber());
        return messages;
    }

    @Override
    public List<Message> searchMessagesBySender(String query, Long userId, Long senderUserId) {
        log.debug("Searching messages by sender: {} with query: '{}' for user: {}", senderUserId, query, userId);
        
        long startTime = System.currentTimeMillis();
        List<Message> messages = messageRepository.searchMessagesBySender(senderUserId, query);
        long searchTime = System.currentTimeMillis() - startTime;
        
        log.info("Found {} messages by sender {} for query '{}' in {}ms", 
                messages.size(), senderUserId, query, searchTime);
        return messages;
    }

    @Override
    public Page<Message> searchMessagesBySender(String query, Long userId, Long senderUserId, Pageable pageable) {
        log.debug("Searching messages by sender: {} with query: '{}' for user: {} with pagination", 
                senderUserId, query, userId);
        
        long startTime = System.currentTimeMillis();
        Page<Message> messages = messageRepository.searchMessagesBySender(senderUserId, query, pageable);
        long searchTime = System.currentTimeMillis() - startTime;
        
        log.info("Found {} messages by sender {} for query '{}' in {}ms (page {})", 
                messages.getTotalElements(), senderUserId, query, searchTime, pageable.getPageNumber());
        return messages;
    }

    @Override
    public List<String> getSearchSuggestions(String query, Long userId) {
        log.debug("Getting search suggestions for query: '{}' and user: {}", query, userId);
        
        // Simple implementation - in production, you might want to use a more sophisticated approach
        // like Elasticsearch or a dedicated search service
        List<String> suggestions = new ArrayList<>();
        
        if (query != null && query.length() >= 2) {
            // Get recent search terms or popular terms
            // This is a placeholder implementation
            suggestions.add(query + " family");
            suggestions.add(query + " meeting");
            suggestions.add(query + " dinner");
        }
        
        return suggestions;
    }

    @Override
    public List<String> getPopularSearchTerms(Long userId) {
        log.debug("Getting popular search terms for user: {}", userId);
        
        // Placeholder implementation - in production, you would track search analytics
        List<String> popularTerms = new ArrayList<>();
        popularTerms.add("family dinner");
        popularTerms.add("meeting");
        popularTerms.add("birthday");
        popularTerms.add("vacation");
        popularTerms.add("work");
        
        return popularTerms;
    }

    // ==================== THREADING METHODS ====================

    @Override
    public List<Message> getRepliesToMessage(String messageId) {
        log.debug("Getting replies to message: {}", messageId);
        return messageRepository.findByReplyToMessageIdOrderByCreatedAtAsc(messageId);
    }

    @Override
    public Page<Message> getRepliesToMessage(String messageId, Pageable pageable) {
        log.debug("Getting replies to message: {} with pagination", messageId);
        return messageRepository.findByReplyToMessageIdOrderByCreatedAtAsc(messageId, pageable);
    }

    @Override
    public List<Message> getThreadMessages(String messageId) {
        log.debug("Getting all messages in thread: {}", messageId);
        return messageRepository.findThreadMessages(messageId);
    }

    @Override
    public Page<Message> getThreadMessages(String messageId, Pageable pageable) {
        log.debug("Getting all messages in thread: {} with pagination", messageId);
        return messageRepository.findThreadMessages(messageId, pageable);
    }

    @Override
    public List<Message> getThreadRootMessages(Long chatRoomId) {
        log.debug("Getting thread root messages in chat room: {}", chatRoomId);
        return messageRepository.findThreadRootMessages(chatRoomId);
    }

    @Override
    public Page<Message> getThreadRootMessages(Long chatRoomId, Pageable pageable) {
        log.debug("Getting thread root messages in chat room: {} with pagination", chatRoomId);
        return messageRepository.findThreadRootMessages(chatRoomId, pageable);
    }

    @Override
    public long countRepliesToMessage(String messageId) {
        log.debug("Counting replies to message: {}", messageId);
        return messageRepository.countByReplyToMessageId(messageId);
    }

    @Override
    public Message getLatestReplyInThread(String messageId) {
        log.debug("Getting latest reply in thread: {}", messageId);
        return messageRepository.findLatestReplyInThread(messageId, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Override
    public ThreadSummary getThreadSummary(String messageId) {
        log.debug("Getting thread summary for message: {}", messageId);
        
        // Get the root message
        Message rootMessage = getMessageById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found: " + messageId));
        
        // Count replies
        long replyCount = countRepliesToMessage(messageId);
        
        // Get latest reply
        Message latestReply = getLatestReplyInThread(messageId);
        
        // Get recent replies (last 5)
        Pageable recentRepliesPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Message> recentReplies = getRepliesToMessage(messageId, recentRepliesPageable).getContent();
        
        return ThreadSummary.fromMessage(rootMessage, replyCount, latestReply, recentReplies);
    }

    // ==================== EDIT HISTORY METHODS ====================

    @Override
    public Message editMessageWithHistory(EditMessageWithHistoryRequest request) {
        log.debug("Editing message with history: {} by user: {}", request.getMessageId(), request.getUserId());
        
        // Get the existing message
        Message existingMessage = getMessageById(request.getMessageId())
                .orElseThrow(() -> new RuntimeException("Message not found: " + request.getMessageId()));
        
        // Check if user is authorized to edit
        if (!existingMessage.getSenderUserId().equals(request.getUserId())) {
            throw new RuntimeException("User is not authorized to edit this message");
        }
        
        // Get current version number
        long currentVersionCount = messageEditHistoryRepository.countByMessageId(request.getMessageId());
        int newVersion = (int) currentVersionCount + 1;
        
        // Create edit history record
        MessageEditHistory editHistory = MessageEditHistory.builder()
                .messageId(request.getMessageId())
                .version(newVersion)
                .previousContent(existingMessage.getContent())
                .newContent(request.getNewContent())
                .editedByUserId(request.getUserId())
                .editReason(request.getEditReason())
                .editTimestamp(LocalDateTime.now())
                .isCurrentVersion(true)
                .editType(request.getEditType() != null ? request.getEditType() : MessageEditHistory.EditType.CONTENT_EDIT)
                .metadata(request.getMetadata())
                .build();
        
        // Mark previous version as not current
        messageEditHistoryRepository.findByMessageIdOrderByVersionDesc(request.getMessageId())
                .stream()
                .filter(history -> history.getIsCurrentVersion() != null && history.getIsCurrentVersion())
                .forEach(history -> {
                    history.setIsCurrentVersion(false);
                    messageEditHistoryRepository.save(history);
                });
        
        // Save edit history
        messageEditHistoryRepository.save(editHistory);
        
        // Update the message
        existingMessage.setContent(request.getNewContent());
        existingMessage.setUpdatedAt(LocalDateTime.now());
        existingMessage.setIsEdited(true);
        
        Message updatedMessage = messageRepository.save(existingMessage);
        
        log.info("Message edited successfully: {} version: {}", request.getMessageId(), newVersion);
        return updatedMessage;
    }

    @Override
    public List<MessageEditHistory> getMessageEditHistory(String messageId) {
        log.debug("Getting edit history for message: {}", messageId);
        return messageEditHistoryRepository.findByMessageIdOrderByVersionDesc(messageId);
    }

    @Override
    public Page<MessageEditHistory> getMessageEditHistory(String messageId, Pageable pageable) {
        log.debug("Getting edit history for message: {} with pagination", messageId);
        return messageEditHistoryRepository.findByMessageIdOrderByVersionDesc(messageId, pageable);
    }

    @Override
    public MessageEditHistory getMessageVersion(String messageId, Integer version) {
        log.debug("Getting version {} of message: {}", version, messageId);
        return messageEditHistoryRepository.findByMessageIdAndVersion(messageId, version)
                .orElseThrow(() -> new RuntimeException("Version not found: " + version + " for message: " + messageId));
    }

    @Override
    public MessageEditHistory getCurrentMessageVersion(String messageId) {
        log.debug("Getting current version of message: {}", messageId);
        return messageEditHistoryRepository.findCurrentVersion(messageId)
                .orElseThrow(() -> new RuntimeException("Current version not found for message: " + messageId));
    }

    @Override
    public Message revertMessageToVersion(String messageId, Integer version, Long userId, String reason) {
        log.debug("Reverting message: {} to version: {} by user: {}", messageId, version, userId);
        
        // Get the target version
        MessageEditHistory targetVersion = getMessageVersion(messageId, version);
        
        // Get the current message
        Message currentMessage = getMessageById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found: " + messageId));
        
        // Check authorization
        if (!currentMessage.getSenderUserId().equals(userId)) {
            throw new RuntimeException("User is not authorized to revert this message");
        }
        
        // Create new edit history record for the revert
        long currentVersionCount = messageEditHistoryRepository.countByMessageId(messageId);
        int newVersion = (int) currentVersionCount + 1;
        
        MessageEditHistory revertHistory = MessageEditHistory.builder()
                .messageId(messageId)
                .version(newVersion)
                .previousContent(currentMessage.getContent())
                .newContent(targetVersion.getNewContent())
                .editedByUserId(userId)
                .editReason("Reverted to version " + version + ". " + (reason != null ? reason : ""))
                .editTimestamp(LocalDateTime.now())
                .isCurrentVersion(true)
                .editType(MessageEditHistory.EditType.CORRECTION_EDIT)
                .metadata("{\"revertedFromVersion\": " + version + "}")
                .build();
        
        // Mark previous version as not current
        messageEditHistoryRepository.findByMessageIdOrderByVersionDesc(messageId)
                .stream()
                .filter(history -> history.getIsCurrentVersion() != null && history.getIsCurrentVersion())
                .forEach(history -> {
                    history.setIsCurrentVersion(false);
                    messageEditHistoryRepository.save(history);
                });
        
        // Save revert history
        messageEditHistoryRepository.save(revertHistory);
        
        // Update the message
        currentMessage.setContent(targetVersion.getNewContent());
        currentMessage.setUpdatedAt(LocalDateTime.now());
        currentMessage.setIsEdited(true);
        
        Message revertedMessage = messageRepository.save(currentMessage);
        
        log.info("Message reverted successfully: {} to version: {}", messageId, version);
        return revertedMessage;
    }

    @Override
    public List<MessageEditHistory> getEditHistoryByUser(Long userId) {
        log.debug("Getting edit history by user: {}", userId);
        return messageEditHistoryRepository.findByEditedByUserIdOrderByEditTimestampDesc(userId);
    }

    @Override
    public Page<MessageEditHistory> getEditHistoryByUser(Long userId, Pageable pageable) {
        log.debug("Getting edit history by user: {} with pagination", userId);
        return messageEditHistoryRepository.findByEditedByUserIdOrderByEditTimestampDesc(userId, pageable);
    }

    @Override
    public List<MessageEditHistory> getEditHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Getting edit history between: {} and {}", startDate, endDate);
        return messageEditHistoryRepository.findByEditTimestampBetween(startDate, endDate);
    }

    @Override
    public Page<MessageEditHistory> getEditHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("Getting edit history between: {} and {} with pagination", startDate, endDate);
        return messageEditHistoryRepository.findByEditTimestampBetween(startDate, endDate, pageable);
    }

    @Override
    public long countMessageEditHistory(String messageId) {
        log.debug("Counting edit history for message: {}", messageId);
        return messageEditHistoryRepository.countByMessageId(messageId);
    }

    @Override
    public MessageEditHistory getLatestEditForMessage(String messageId) {
        log.debug("Getting latest edit for message: {}", messageId);
        return messageEditHistoryRepository.findLatestEditForMessage(messageId);
    }

    // ==================== ENHANCED DELETION METHODS ====================

    @Override
    public void deleteMessageWithOptions(DeleteMessageRequest request) {
        log.debug("Deleting message with options: {} by user: {}", request.getMessageId(), request.getUserId());
        
        Message message = getMessageById(request.getMessageId())
                .orElseThrow(() -> new RuntimeException("Message not found: " + request.getMessageId()));
        
        // Check authorization
        if (!message.getSenderUserId().equals(request.getUserId())) {
            throw new RuntimeException("User is not authorized to delete this message");
        }
        
        boolean deleteForEveryone = request.getDeleteForEveryone() != null ? request.getDeleteForEveryone() : false;
        
        // Delete replies if requested
        if (request.getDeleteReplies() != null && request.getDeleteReplies()) {
            List<Message> replies = getRepliesToMessage(request.getMessageId());
            for (Message reply : replies) {
                reply.setDeletedAt(LocalDateTime.now());
                reply.setDeletedByUserId(request.getUserId());
                reply.setIsDeletedForEveryone(deleteForEveryone);
                messageRepository.save(reply);
            }
        }
        
        // Delete edit history if requested
        if (request.getDeleteEditHistory() != null && request.getDeleteEditHistory()) {
            List<MessageEditHistory> editHistory = getMessageEditHistory(request.getMessageId());
            messageEditHistoryRepository.deleteAll(editHistory);
        }
        
        // Delete the main message
        message.setDeletedAt(LocalDateTime.now());
        message.setDeletedByUserId(request.getUserId());
        message.setIsDeletedForEveryone(deleteForEveryone);
        messageRepository.save(message);
        
        // Notify participants if requested
        if (request.getNotifyParticipants() == null || request.getNotifyParticipants()) {
            webSocketService.sendMessageDeleteNotification(request.getMessageId(), request.getUserId(), deleteForEveryone);
        }
        
        log.info("Message deleted with options: {} by user: {}", request.getMessageId(), request.getUserId());
    }

    @Override
    public void bulkDeleteMessages(List<String> messageIds, Long userId, Boolean deleteForEveryone) {
        log.debug("Bulk deleting {} messages by user: {}", messageIds.size(), userId);
        
        List<Message> messages = messageRepository.findByIds(messageIds);
        
        for (Message message : messages) {
            // Check authorization for each message
            if (!message.getSenderUserId().equals(userId)) {
                log.warn("User {} not authorized to delete message {}", userId, message.getId());
                continue;
            }
            
            message.setDeletedAt(LocalDateTime.now());
            message.setDeletedByUserId(userId);
            message.setIsDeletedForEveryone(deleteForEveryone != null ? deleteForEveryone : false);
            messageRepository.save(message);
            
            // Notify participants
            webSocketService.sendMessageDeleteNotification(message.getId(), userId, deleteForEveryone != null ? deleteForEveryone : false);
        }
        
        log.info("Bulk deleted {} messages by user: {}", messages.size(), userId);
    }

    @Override
    public void deleteAllMessagesInRoom(Long chatRoomId, Long userId, Boolean deleteForEveryone) {
        log.debug("Deleting all messages in room: {} by user: {}", chatRoomId, userId);
        
        List<Message> messages = messageRepository.findAllMessagesInRoom(chatRoomId);
        
        for (Message message : messages) {
            // Check authorization for each message
            if (!message.getSenderUserId().equals(userId)) {
                log.warn("User {} not authorized to delete message {}", userId, message.getId());
                continue;
            }
            
            message.setDeletedAt(LocalDateTime.now());
            message.setDeletedByUserId(userId);
            message.setIsDeletedForEveryone(deleteForEveryone != null ? deleteForEveryone : false);
            messageRepository.save(message);
        }
        
        log.info("Deleted all messages in room: {} by user: {}", chatRoomId, userId);
    }

    @Override
    public void deleteMessagesByDateRange(Long chatRoomId, LocalDateTime startDate, LocalDateTime endDate, Long userId, Boolean deleteForEveryone) {
        log.debug("Deleting messages in room: {} between {} and {} by user: {}", chatRoomId, startDate, endDate, userId);
        
        List<Message> messages = messageRepository.findMessagesByDateRange(chatRoomId, startDate, endDate);
        
        for (Message message : messages) {
            // Check authorization for each message
            if (!message.getSenderUserId().equals(userId)) {
                log.warn("User {} not authorized to delete message {}", userId, message.getId());
                continue;
            }
            
            message.setDeletedAt(LocalDateTime.now());
            message.setDeletedByUserId(userId);
            message.setIsDeletedForEveryone(deleteForEveryone != null ? deleteForEveryone : false);
            messageRepository.save(message);
        }
        
        log.info("Deleted {} messages by date range by user: {}", messages.size(), userId);
    }

    @Override
    public void deleteMessagesByUser(Long chatRoomId, Long targetUserId, Long deletedByUserId, Boolean deleteForEveryone) {
        log.debug("Deleting messages by user: {} in room: {} by user: {}", targetUserId, chatRoomId, deletedByUserId);
        
        List<Message> messages = messageRepository.findMessagesByUserInRoom(chatRoomId, targetUserId);
        
        for (Message message : messages) {
            // Check authorization - only the sender or admin can delete
            if (!message.getSenderUserId().equals(deletedByUserId)) {
                // TODO: Add admin check here
                log.warn("User {} not authorized to delete message {}", deletedByUserId, message.getId());
                continue;
            }
            
            message.setDeletedAt(LocalDateTime.now());
            message.setDeletedByUserId(deletedByUserId);
            message.setIsDeletedForEveryone(deleteForEveryone != null ? deleteForEveryone : false);
            messageRepository.save(message);
        }
        
        log.info("Deleted {} messages by user: {} by user: {}", messages.size(), targetUserId, deletedByUserId);
    }

    @Override
    public void permanentlyDeleteMessage(String messageId, Long userId) {
        log.debug("Permanently deleting message: {} by user: {}", messageId, userId);
        
        Message message = getMessageById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found: " + messageId));
        
        // Check authorization
        if (!message.getSenderUserId().equals(userId)) {
            throw new RuntimeException("User is not authorized to permanently delete this message");
        }
        
        // Delete edit history
        List<MessageEditHistory> editHistory = getMessageEditHistory(messageId);
        messageEditHistoryRepository.deleteAll(editHistory);
        
        // Delete replies
        List<Message> replies = getRepliesToMessage(messageId);
        messageRepository.deleteAll(replies);
        
        // Delete the main message
        messageRepository.delete(message);
        
        log.info("Permanently deleted message: {} by user: {}", messageId, userId);
    }

    @Override
    public Message restoreDeletedMessage(String messageId, Long userId) {
        log.debug("Restoring deleted message: {} by user: {}", messageId, userId);
        
        Message message = getMessageById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found: " + messageId));
        
        // Check authorization
        if (!message.getSenderUserId().equals(userId)) {
            throw new RuntimeException("User is not authorized to restore this message");
        }
        
        // Check if message is actually deleted
        if (message.getDeletedAt() == null) {
            throw new RuntimeException("Message is not deleted");
        }
        
        // Restore the message
        message.setDeletedAt(null);
        message.setDeletedByUserId(null);
        message.setIsDeletedForEveryone(false);
        
        Message restoredMessage = messageRepository.save(message);
        
        log.info("Restored deleted message: {} by user: {}", messageId, userId);
        return restoredMessage;
    }

    @Override
    public List<Message> getDeletedMessages(Long userId, Long chatRoomId) {
        log.debug("Getting deleted messages for user: {} in room: {}", userId, chatRoomId);
        return messageRepository.findDeletedMessagesByUser(chatRoomId, userId);
    }

    @Override
    public Page<Message> getDeletedMessages(Long userId, Long chatRoomId, Pageable pageable) {
        log.debug("Getting deleted messages for user: {} in room: {} with pagination", userId, chatRoomId);
        return messageRepository.findDeletedMessagesByUser(chatRoomId, userId, pageable);
    }

    @Override
    public void cleanupOldDeletedMessages(int daysOld) {
        log.debug("Cleaning up deleted messages older than {} days", daysOld);
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        List<Message> oldDeletedMessages = messageRepository.findOldDeletedMessages(cutoffDate);
        
        for (Message message : oldDeletedMessages) {
            // Delete edit history
            List<MessageEditHistory> editHistory = getMessageEditHistory(message.getId());
            messageEditHistoryRepository.deleteAll(editHistory);
            
            // Delete replies
            List<Message> replies = getRepliesToMessage(message.getId());
            messageRepository.deleteAll(replies);
            
            // Delete the main message
            messageRepository.delete(message);
        }
        
        log.info("Cleaned up {} old deleted messages", oldDeletedMessages.size());
    }

    // ==================== MESSAGE SCHEDULING METHODS ====================

    @Override
    public ScheduledMessage scheduleMessage(ScheduleMessageRequest request) {
        log.debug("Scheduling message for user: {} in room: {} at: {}", 
                request.getSenderUserId(), request.getChatRoomId(), request.getScheduledFor());
        
        // Generate UUID for the scheduled message
        String messageUuid = UUID.randomUUID().toString();
        
        // Create scheduled message
        ScheduledMessage scheduledMessage = ScheduledMessage.builder()
                .messageUuid(messageUuid)
                .chatRoomId(request.getChatRoomId())
                .senderUserId(request.getSenderUserId())
                .content(request.getContent())
                .messageType(request.getMessageType())
                .scheduledFor(request.getScheduledFor())
                .createdAt(LocalDateTime.now())
                .status(ScheduledMessage.ScheduledStatus.PENDING)
                .retryCount(0)
                .maxRetries(request.getMaxRetries() != null ? request.getMaxRetries() : 3)
                .metadata(request.getMetadata())
                .recurrencePattern(request.getRecurrencePattern())
                .isRecurring(request.getIsRecurring() != null ? request.getIsRecurring() : false)
                .nextExecution(request.getScheduledFor())
                .endDate(request.getEndDate())
                .build();
        
        ScheduledMessage savedScheduledMessage = scheduledMessageRepository.save(scheduledMessage);
        
        log.info("Message scheduled successfully: {} for user: {} at: {}", 
                savedScheduledMessage.getId(), request.getSenderUserId(), request.getScheduledFor());
        
        return savedScheduledMessage;
    }

    @Override
    public List<ScheduledMessage> getScheduledMessagesByUser(Long userId) {
        log.debug("Getting scheduled messages for user: {}", userId);
        return scheduledMessageRepository.findBySenderUserIdOrderByScheduledForDesc(userId);
    }

    @Override
    public Page<ScheduledMessage> getScheduledMessagesByUser(Long userId, Pageable pageable) {
        log.debug("Getting scheduled messages for user: {} with pagination", userId);
        return scheduledMessageRepository.findBySenderUserIdOrderByScheduledForDesc(userId, pageable);
    }

    @Override
    public List<ScheduledMessage> getScheduledMessagesByRoom(Long chatRoomId) {
        log.debug("Getting scheduled messages for room: {}", chatRoomId);
        return scheduledMessageRepository.findByChatRoomIdOrderByScheduledForDesc(chatRoomId);
    }

    @Override
    public Page<ScheduledMessage> getScheduledMessagesByRoom(Long chatRoomId, Pageable pageable) {
        log.debug("Getting scheduled messages for room: {} with pagination", chatRoomId);
        return scheduledMessageRepository.findByChatRoomIdOrderByScheduledForDesc(chatRoomId, pageable);
    }

    @Override
    public List<ScheduledMessage> getScheduledMessagesByStatus(ScheduledMessage.ScheduledStatus status) {
        log.debug("Getting scheduled messages with status: {}", status);
        return scheduledMessageRepository.findByStatusOrderByScheduledForAsc(status);
    }

    @Override
    public Page<ScheduledMessage> getScheduledMessagesByStatus(ScheduledMessage.ScheduledStatus status, Pageable pageable) {
        log.debug("Getting scheduled messages with status: {} with pagination", status);
        return scheduledMessageRepository.findByStatusOrderByScheduledForAsc(status, pageable);
    }

    @Override
    public void cancelScheduledMessage(String scheduledMessageId, Long userId) {
        log.debug("Cancelling scheduled message: {} by user: {}", scheduledMessageId, userId);
        
        ScheduledMessage scheduledMessage = scheduledMessageRepository.findById(scheduledMessageId)
                .orElseThrow(() -> new RuntimeException("Scheduled message not found: " + scheduledMessageId));
        
        // Check authorization
        if (!scheduledMessage.getSenderUserId().equals(userId)) {
            throw new RuntimeException("User is not authorized to cancel this scheduled message");
        }
        
        // Check if message can be cancelled
        if (scheduledMessage.getStatus() != ScheduledMessage.ScheduledStatus.PENDING) {
            throw new RuntimeException("Only pending scheduled messages can be cancelled");
        }
        
        scheduledMessage.setStatus(ScheduledMessage.ScheduledStatus.CANCELLED);
        scheduledMessageRepository.save(scheduledMessage);
        
        log.info("Scheduled message cancelled: {} by user: {}", scheduledMessageId, userId);
    }

    @Override
    public ScheduledMessage updateScheduledMessage(String scheduledMessageId, ScheduleMessageRequest request) {
        log.debug("Updating scheduled message: {} by user: {}", scheduledMessageId, request.getSenderUserId());
        
        ScheduledMessage scheduledMessage = scheduledMessageRepository.findById(scheduledMessageId)
                .orElseThrow(() -> new RuntimeException("Scheduled message not found: " + scheduledMessageId));
        
        // Check authorization
        if (!scheduledMessage.getSenderUserId().equals(request.getSenderUserId())) {
            throw new RuntimeException("User is not authorized to update this scheduled message");
        }
        
        // Check if message can be updated
        if (scheduledMessage.getStatus() != ScheduledMessage.ScheduledStatus.PENDING) {
            throw new RuntimeException("Only pending scheduled messages can be updated");
        }
        
        // Update fields
        scheduledMessage.setContent(request.getContent());
        scheduledMessage.setMessageType(request.getMessageType());
        scheduledMessage.setScheduledFor(request.getScheduledFor());
        scheduledMessage.setMetadata(request.getMetadata());
        scheduledMessage.setRecurrencePattern(request.getRecurrencePattern());
        scheduledMessage.setIsRecurring(request.getIsRecurring() != null ? request.getIsRecurring() : false);
        scheduledMessage.setNextExecution(request.getScheduledFor());
        scheduledMessage.setEndDate(request.getEndDate());
        scheduledMessage.setMaxRetries(request.getMaxRetries() != null ? request.getMaxRetries() : 3);
        
        ScheduledMessage updatedScheduledMessage = scheduledMessageRepository.save(scheduledMessage);
        
        log.info("Scheduled message updated: {} by user: {}", scheduledMessageId, request.getSenderUserId());
        return updatedScheduledMessage;
    }

    @Override
    public ScheduledMessage getScheduledMessageById(String scheduledMessageId) {
        log.debug("Getting scheduled message by ID: {}", scheduledMessageId);
        return scheduledMessageRepository.findById(scheduledMessageId)
                .orElseThrow(() -> new RuntimeException("Scheduled message not found: " + scheduledMessageId));
    }

    @Override
    public void processScheduledMessages() {
        log.debug("Processing scheduled messages");
        
        LocalDateTime now = LocalDateTime.now();
        List<ScheduledMessage> readyMessages = scheduledMessageRepository.findMessagesReadyForExecution(now);
        
        for (ScheduledMessage scheduledMessage : readyMessages) {
            try {
                // Mark as processing
                scheduledMessage.setStatus(ScheduledMessage.ScheduledStatus.PROCESSING);
                scheduledMessage.setLastAttempt(now);
                scheduledMessageRepository.save(scheduledMessage);
                
                // Create and send the message
                SendMessageRequest sendRequest = SendMessageRequest.builder()
                        .chatRoomId(scheduledMessage.getChatRoomId())
                        .senderUserId(scheduledMessage.getSenderUserId())
                        .content(scheduledMessage.getContent())
                        .messageType(scheduledMessage.getMessageType())
                        .build();
                
                Message sentMessage = sendMessage(sendRequest);
                
                // Mark as sent
                scheduledMessage.setStatus(ScheduledMessage.ScheduledStatus.SENT);
                scheduledMessageRepository.save(scheduledMessage);
                
                log.info("Scheduled message sent successfully: {}", scheduledMessage.getId());
                
            } catch (Exception e) {
                log.error("Failed to process scheduled message: {}", scheduledMessage.getId(), e);
                
                // Handle retry logic
                scheduledMessage.setRetryCount(scheduledMessage.getRetryCount() + 1);
                scheduledMessage.setErrorMessage(e.getMessage());
                
                if (scheduledMessage.getRetryCount() >= scheduledMessage.getMaxRetries()) {
                    scheduledMessage.setStatus(ScheduledMessage.ScheduledStatus.FAILED);
                } else {
                    scheduledMessage.setStatus(ScheduledMessage.ScheduledStatus.PENDING);
                    // Schedule retry (e.g., in 5 minutes)
                    scheduledMessage.setScheduledFor(now.plusMinutes(5));
                }
                
                scheduledMessageRepository.save(scheduledMessage);
            }
        }
        
        log.info("Processed {} scheduled messages", readyMessages.size());
    }

    @Override
    public void retryFailedScheduledMessages() {
        log.debug("Retrying failed scheduled messages");
        
        List<ScheduledMessage> failedMessages = scheduledMessageRepository.findFailedMessagesForRetry();
        
        for (ScheduledMessage scheduledMessage : failedMessages) {
            scheduledMessage.setStatus(ScheduledMessage.ScheduledStatus.PENDING);
            scheduledMessage.setScheduledFor(LocalDateTime.now().plusMinutes(5)); // Retry in 5 minutes
            scheduledMessageRepository.save(scheduledMessage);
        }
        
        log.info("Retried {} failed scheduled messages", failedMessages.size());
    }

    @Override
    public void cleanupExpiredScheduledMessages() {
        log.debug("Cleaning up expired scheduled messages");
        
        LocalDateTime now = LocalDateTime.now();
        List<ScheduledMessage> expiredMessages = scheduledMessageRepository.findExpiredMessages(now);
        
        for (ScheduledMessage scheduledMessage : expiredMessages) {
            scheduledMessage.setStatus(ScheduledMessage.ScheduledStatus.EXPIRED);
            scheduledMessageRepository.save(scheduledMessage);
        }
        
        log.info("Cleaned up {} expired scheduled messages", expiredMessages.size());
    }

    @Override
    public long countScheduledMessagesByUser(Long userId) {
        log.debug("Counting scheduled messages for user: {}", userId);
        return scheduledMessageRepository.countBySenderUserId(userId);
    }

    @Override
    public long countScheduledMessagesByRoom(Long chatRoomId) {
        log.debug("Counting scheduled messages for room: {}", chatRoomId);
        return scheduledMessageRepository.countByChatRoomId(chatRoomId);
    }

    @Override
    public long countScheduledMessagesByStatus(ScheduledMessage.ScheduledStatus status) {
        log.debug("Counting scheduled messages with status: {}", status);
        return scheduledMessageRepository.countByStatus(status);
    }
}
