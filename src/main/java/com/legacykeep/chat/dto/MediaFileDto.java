package com.legacykeep.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Media File DTO for API responses.
 * 
 * Represents media file information in API responses.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MediaFileDto {
    
    /**
     * Media file ID
     */
    private String id;
    
    /**
     * Associated message ID
     */
    private String messageId;
    
    /**
     * User ID who uploaded the file
     */
    private Long userId;
    
    /**
     * File type (image, video, audio, document)
     */
    private String fileType;
    
    /**
     * Original file name
     */
    private String fileName;
    
    /**
     * File size in bytes
     */
    private Long fileSize;
    
    /**
     * MIME type
     */
    private String mimeType;
    
    /**
     * Storage URL
     */
    private String storageUrl;
    
    /**
     * Thumbnail URL
     */
    private String thumbnailUrl;
    
    /**
     * Media metadata
     */
    private Map<String, Object> metadata;
    
    /**
     * Whether the file has been processed
     */
    private Boolean isProcessed;
    
    /**
     * File creation timestamp
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;
    
    // Computed fields
    /**
     * File size in human-readable format
     */
    private String fileSizeFormatted;
    
    /**
     * Whether the file is an image
     */
    private Boolean isImage;
    
    /**
     * Whether the file is a video
     */
    private Boolean isVideo;
    
    /**
     * Whether the file is an audio file
     */
    private Boolean isAudio;
    
    /**
     * Whether the file is a document
     */
    private Boolean isDocument;
}
