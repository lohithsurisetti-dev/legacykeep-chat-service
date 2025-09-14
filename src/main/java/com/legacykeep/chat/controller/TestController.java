package com.legacykeep.chat.controller;

import com.legacykeep.chat.dto.ApiResponse;
import com.legacykeep.chat.repository.postgres.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple test controller to verify database connectivity.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {

    private final ChatRoomRepository chatRoomRepository;

    @GetMapping("/db")
    public ResponseEntity<ApiResponse<String>> testDatabase() {
        try {
            // Try to get a simple count without any transaction management
            long count = chatRoomRepository.count();
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Database connection successful")
                    .data("Chat rooms count: " + count)
                    .build());
        } catch (Exception e) {
            log.error("Database test failed: {}", e.getMessage(), e);
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(false)
                    .message("Database test failed: " + e.getMessage())
                    .data(null)
                    .build());
        }
    }
}
