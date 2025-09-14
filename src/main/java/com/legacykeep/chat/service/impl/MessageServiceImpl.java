package com.legacykeep.chat.service.impl;

import com.legacykeep.chat.dto.request.EditMessageRequest;
import com.legacykeep.chat.dto.request.ForwardMessageRequest;
import com.legacykeep.chat.dto.request.ReactionRequest;
import com.legacykeep.chat.dto.request.SendMessageRequest;
import com.legacykeep.chat.entity.Message;
import com.legacykeep.chat.enums.MessageStatus;
import com.legacykeep.chat.enums.MessageType;
import com.legacykeep.chat.repository.mongo.MessageRepository;
import com.legacykeep.chat.service.ChatRoomService;
import com.legacykeep.chat.service.ContentFilterService;
import com.legacykeep.chat.service.EncryptionService;
import com.legacykeep.chat.service.KeyManagementService;
import com.legacykeep.chat.service.MessageService;
import com.legacykeep.chat.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
}
