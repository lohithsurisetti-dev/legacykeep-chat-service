package com.legacykeep.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API Response DTO for Chat Service.
 * 
 * Provides consistent response structure across all chat endpoints.
 * Follows the project's established API response pattern.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    /**
     * Response status (success/error)
     */
    private String status;
    
    /**
     * Response message
     */
    private String message;
    
    /**
     * Response data payload
     */
    private T data;
    
    /**
     * Timestamp of the response
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;
    
    /**
     * Error details (if any)
     */
    private String error;
    
    /**
     * HTTP status code
     */
    private Integer statusCode;
    
    /**
     * Request path (for debugging)
     */
    private String path;
    
    /**
     * Create a successful response with data.
     * 
     * @param data Response data
     * @param message Success message
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .statusCode(200)
                .build();
    }
    
    /**
     * Create a successful response without data.
     * 
     * @param message Success message
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .status("success")
                .message(message)
                .timestamp(LocalDateTime.now())
                .statusCode(200)
                .build();
    }
    
    /**
     * Create an error response.
     * 
     * @param message Error message
     * @param error Error details
     * @param statusCode HTTP status code
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> error(String message, String error, Integer statusCode) {
        return ApiResponse.<T>builder()
                .status("error")
                .message(message)
                .error(error)
                .timestamp(LocalDateTime.now())
                .statusCode(statusCode)
                .build();
    }
    
    /**
     * Create a validation error response.
     * 
     * @param message Error message
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> validationError(String message) {
        return error(message, "Validation failed", 400);
    }
    
    /**
     * Create an unauthorized error response.
     * 
     * @param message Error message
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return error(message, "Unauthorized access", 401);
    }
    
    /**
     * Create a forbidden error response.
     * 
     * @param message Error message
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return error(message, "Access forbidden", 403);
    }
    
    /**
     * Create a not found error response.
     * 
     * @param message Error message
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return error(message, "Resource not found", 404);
    }
    
    /**
     * Create an internal server error response.
     * 
     * @param message Error message
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> internalError(String message) {
        return error(message, "Internal server error", 500);
    }
    
    /**
     * Create a chat-specific error response for room not found.
     * 
     * @param roomId Chat room ID
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> chatRoomNotFound(Long roomId) {
        return error("Chat room not found", "Chat room with ID " + roomId + " does not exist", 404);
    }
    
    /**
     * Create a chat-specific error response for user not in room.
     * 
     * @param userId User ID
     * @param roomId Chat room ID
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> userNotInRoom(Long userId, Long roomId) {
        return error("User not in room", "User " + userId + " is not a member of chat room " + roomId, 403);
    }
    
    /**
     * Create a chat-specific error response for message not found.
     * 
     * @param messageId Message ID
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> messageNotFound(String messageId) {
        return error("Message not found", "Message with ID " + messageId + " does not exist", 404);
    }
    
    /**
     * Create a chat-specific error response for permission denied.
     * 
     * @param action Action attempted
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> permissionDenied(String action) {
        return error("Permission denied", "You don't have permission to " + action, 403);
    }
}
