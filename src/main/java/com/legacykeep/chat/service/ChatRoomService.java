package com.legacykeep.chat.service;

import com.legacykeep.chat.dto.request.CreateChatRoomRequest;
import com.legacykeep.chat.dto.request.UpdateChatRoomRequest;
import com.legacykeep.chat.entity.ChatRoom;
import com.legacykeep.chat.enums.ChatRoomStatus;
import com.legacykeep.chat.enums.ChatRoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for ChatRoom operations.
 * Provides business logic for chat room management with family-centric features.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public interface ChatRoomService {

    /**
     * Create a new chat room
     */
    ChatRoom createChatRoom(CreateChatRoomRequest request);

    /**
     * Get chat room by ID
     */
    Optional<ChatRoom> getChatRoomById(Long id);

    /**
     * Get chat room by UUID
     */
    Optional<ChatRoom> getChatRoomByUuid(UUID roomUuid);

    /**
     * Update chat room
     */
    ChatRoom updateChatRoom(Long id, UpdateChatRoomRequest request);

    /**
     * Delete chat room (soft delete)
     */
    void deleteChatRoom(Long id);

    /**
     * Archive chat room
     */
    ChatRoom archiveChatRoom(Long id);

    /**
     * Unarchive chat room
     */
    ChatRoom unarchiveChatRoom(Long id);

    /**
     * Mute chat room for user
     */
    ChatRoom muteChatRoom(Long id, Long userId);

    /**
     * Unmute chat room for user
     */
    ChatRoom unmuteChatRoom(Long id, Long userId);

    /**
     * Get chat rooms for a user
     */
    List<ChatRoom> getChatRoomsForUser(Long userId);

    /**
     * Get chat rooms for a user with pagination
     */
    Page<ChatRoom> getChatRoomsForUser(Long userId, Pageable pageable);

    /**
     * Get active chat rooms for a user
     */
    List<ChatRoom> getActiveChatRoomsForUser(Long userId);

    /**
     * Get archived chat rooms for a user
     */
    List<ChatRoom> getArchivedChatRoomsForUser(Long userId);

    /**
     * Get muted chat rooms for a user
     */
    List<ChatRoom> getMutedChatRoomsForUser(Long userId);

    /**
     * Get chat rooms by type
     */
    List<ChatRoom> getChatRoomsByType(ChatRoomType roomType);

    /**
     * Get chat rooms by type with pagination
     */
    Page<ChatRoom> getChatRoomsByType(ChatRoomType roomType, Pageable pageable);

    /**
     * Get chat rooms by status
     */
    List<ChatRoom> getChatRoomsByStatus(ChatRoomStatus status);

    /**
     * Get chat rooms by status with pagination
     */
    Page<ChatRoom> getChatRoomsByStatus(ChatRoomStatus status, Pageable pageable);

    /**
     * Get family chat rooms
     */
    List<ChatRoom> getFamilyChatRooms(Long familyId);

    /**
     * Get family chat rooms with pagination
     */
    Page<ChatRoom> getFamilyChatRooms(Long familyId, Pageable pageable);

    /**
     * Get story chat rooms
     */
    List<ChatRoom> getStoryChatRooms(Long storyId);

    /**
     * Get event chat rooms
     */
    List<ChatRoom> getEventChatRooms(Long eventId);

    /**
     * Get individual chat room between two users
     */
    Optional<ChatRoom> getIndividualChatBetweenUsers(Long user1Id, Long user2Id);

    /**
     * Create individual chat room between two users
     */
    ChatRoom createIndividualChatRoom(Long user1Id, Long user2Id);

    /**
     * Get chat rooms with recent activity
     */
    List<ChatRoom> getChatRoomsWithRecentActivity(int days);

    /**
     * Get chat rooms with recent activity for a user
     */
    List<ChatRoom> getChatRoomsWithRecentActivityForUser(Long userId, int days);

    /**
     * Search chat rooms by name
     */
    List<ChatRoom> searchChatRoomsByName(String name);

    /**
     * Search chat rooms by name with pagination
     */
    Page<ChatRoom> searchChatRoomsByName(String name, Pageable pageable);

    /**
     * Get chat room statistics for a user
     */
    ChatRoomStats getChatRoomStatsForUser(Long userId);

    /**
     * Check if chat room exists between two users
     */
    boolean existsIndividualChatBetweenUsers(Long user1Id, Long user2Id);

    /**
     * Get inactive chat rooms
     */
    List<ChatRoom> getInactiveChatRooms(int days);

    /**
     * Update last message info for chat room
     */
    void updateLastMessageInfo(Long chatRoomId, String messageId, Long senderUserId);

    /**
     * Increment message count for chat room
     */
    void incrementMessageCount(Long chatRoomId);

    /**
     * Update participant count for chat room
     */
    void updateParticipantCount(Long chatRoomId);

    /**
     * Get chat room by name (exact match)
     */
    Optional<ChatRoom> getChatRoomByName(String name);

    /**
     * Get encrypted chat rooms
     */
    List<ChatRoom> getEncryptedChatRooms();

    /**
     * Get chat rooms created by user
     */
    List<ChatRoom> getChatRoomsCreatedByUser(Long userId);

    /**
     * Get chat rooms created by user with pagination
     */
    Page<ChatRoom> getChatRoomsCreatedByUser(Long userId, Pageable pageable);

    /**
     * Count chat rooms for user
     */
    long countChatRoomsForUser(Long userId);

    /**
     * Count active chat rooms for user
     */
    long countActiveChatRoomsForUser(Long userId);

    // Additional methods needed by controllers
    /**
     * Get all chat rooms with pagination
     */
    Page<ChatRoom> getAllChatRooms(Pageable pageable);

    /**
     * Get chat rooms by creator with pagination
     */
    Page<ChatRoom> getChatRoomsByCreator(Long creatorId, Pageable pageable);

    /**
     * Get chat rooms by family with pagination
     */
    Page<ChatRoom> getChatRoomsByFamily(Long familyId, Pageable pageable);

    /**
     * Get chat rooms by story with pagination
     */
    Page<ChatRoom> getChatRoomsByStory(Long storyId, Pageable pageable);

    /**
     * Get chat rooms by event with pagination
     */
    Page<ChatRoom> getChatRoomsByEvent(Long eventId, Pageable pageable);

    /**
     * Get chat rooms by participant with pagination
     */
    Page<ChatRoom> getChatRoomsByParticipant(Long participantId, Pageable pageable);

    /**
     * Get recent chat rooms with pagination
     */
    Page<ChatRoom> getRecentChatRooms(int days, Pageable pageable);

    /**
     * Get archived chat rooms with pagination
     */
    Page<ChatRoom> getArchivedChatRooms(Pageable pageable);

    /**
     * Get muted chat rooms with pagination
     */
    Page<ChatRoom> getMutedChatRooms(Pageable pageable);

    /**
     * Get encrypted chat rooms with pagination
     */
    Page<ChatRoom> getEncryptedChatRooms(Pageable pageable);

    /**
     * Add participant to chat room
     */
    ChatRoom addParticipant(Long chatRoomId, com.legacykeep.chat.dto.request.AddParticipantRequest request);

    /**
     * Remove participant from chat room
     */
    ChatRoom removeParticipant(Long chatRoomId, com.legacykeep.chat.dto.request.RemoveParticipantRequest request);

    /**
     * Archive chat room with request
     */
    ChatRoom archiveChatRoom(Long chatRoomId, com.legacykeep.chat.dto.request.ArchiveChatRoomRequest request);

    /**
     * Unarchive chat room with user ID
     */
    ChatRoom unarchiveChatRoom(Long chatRoomId, Long userId);

    /**
     * Mute chat room with request
     */
    ChatRoom muteChatRoom(Long chatRoomId, com.legacykeep.chat.dto.request.MuteChatRoomRequest request);

    /**
     * Update chat room settings
     */
    ChatRoom updateChatRoomSettings(Long chatRoomId, com.legacykeep.chat.dto.request.UpdateChatRoomSettingsRequest request);

    /**
     * Get chat room statistics
     */
    ChatRoomStats getChatRoomStats(Long chatRoomId);

    /**
     * Check individual chat between users
     */
    boolean checkIndividualChat(Long user1Id, Long user2Id);

    /**
     * Get chat room participants
     */
    List<Long> getChatRoomParticipants(Long chatRoomId);

    /**
     * Get chat room count
     */
    long getChatRoomCount();

    /**
     * Chat room statistics DTO
     */
    class ChatRoomStats {
        private long totalChatRooms;
        private long activeChatRooms;
        private long archivedChatRooms;
        private long mutedChatRooms;
        private long individualChats;
        private long groupChats;
        private long familyGroups;
        private long storyChats;
        private long eventChats;
        private long encryptedChats;

        // Constructors
        public ChatRoomStats() {}

        public ChatRoomStats(long totalChatRooms, long activeChatRooms, long archivedChatRooms, 
                           long mutedChatRooms, long individualChats, long groupChats, 
                           long familyGroups, long storyChats, long eventChats, long encryptedChats) {
            this.totalChatRooms = totalChatRooms;
            this.activeChatRooms = activeChatRooms;
            this.archivedChatRooms = archivedChatRooms;
            this.mutedChatRooms = mutedChatRooms;
            this.individualChats = individualChats;
            this.groupChats = groupChats;
            this.familyGroups = familyGroups;
            this.storyChats = storyChats;
            this.eventChats = eventChats;
            this.encryptedChats = encryptedChats;
        }

        // Getters and Setters
        public long getTotalChatRooms() { return totalChatRooms; }
        public void setTotalChatRooms(long totalChatRooms) { this.totalChatRooms = totalChatRooms; }

        public long getActiveChatRooms() { return activeChatRooms; }
        public void setActiveChatRooms(long activeChatRooms) { this.activeChatRooms = activeChatRooms; }

        public long getArchivedChatRooms() { return archivedChatRooms; }
        public void setArchivedChatRooms(long archivedChatRooms) { this.archivedChatRooms = archivedChatRooms; }

        public long getMutedChatRooms() { return mutedChatRooms; }
        public void setMutedChatRooms(long mutedChatRooms) { this.mutedChatRooms = mutedChatRooms; }

        public long getIndividualChats() { return individualChats; }
        public void setIndividualChats(long individualChats) { this.individualChats = individualChats; }

        public long getGroupChats() { return groupChats; }
        public void setGroupChats(long groupChats) { this.groupChats = groupChats; }

        public long getFamilyGroups() { return familyGroups; }
        public void setFamilyGroups(long familyGroups) { this.familyGroups = familyGroups; }

        public long getStoryChats() { return storyChats; }
        public void setStoryChats(long storyChats) { this.storyChats = storyChats; }

        public long getEventChats() { return eventChats; }
        public void setEventChats(long eventChats) { this.eventChats = eventChats; }

        public long getEncryptedChats() { return encryptedChats; }
        public void setEncryptedChats(long encryptedChats) { this.encryptedChats = encryptedChats; }
    }
}
