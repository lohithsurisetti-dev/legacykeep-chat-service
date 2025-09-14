package com.legacykeep.chat.service.impl;

import com.legacykeep.chat.service.EncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Implementation of EncryptionService using AES-256-GCM encryption.
 * 
 * Features:
 * - AES-256-GCM for authenticated encryption
 * - 96-bit IV for each encryption operation
 * - 128-bit authentication tag
 * - Secure random number generation
 * - Base64 encoding for safe storage
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Slf4j
@Service
public class EncryptionServiceImpl implements EncryptionService {
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12; // 96 bits
    private static final int GCM_TAG_LENGTH = 16; // 128 bits
    private static final int AES_KEY_LENGTH = 256; // 256 bits
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    @Override
    public String generateEncryptionKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(AES_KEY_LENGTH);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            log.error("Failed to generate encryption key: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate encryption key", e);
        }
    }
    
    @Override
    public String encryptMessage(String plaintext, String encryptionKey) {
        if (plaintext == null || plaintext.isEmpty()) {
            throw new IllegalArgumentException("Plaintext cannot be null or empty");
        }
        if (!isValidEncryptionKey(encryptionKey)) {
            throw new IllegalArgumentException("Invalid encryption key");
        }
        
        try {
            // Decode the encryption key
            byte[] keyBytes = Base64.getDecoder().decode(encryptionKey);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
            
            // Generate random IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);
            
            // Initialize cipher for encryption
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);
            
            // Encrypt the plaintext
            byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedBytes = cipher.doFinal(plaintextBytes);
            
            // Combine IV and encrypted data
            byte[] combined = new byte[GCM_IV_LENGTH + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedBytes, 0, combined, GCM_IV_LENGTH, encryptedBytes.length);
            
            // Return Base64 encoded result
            String result = Base64.getEncoder().encodeToString(combined);
            log.debug("Successfully encrypted message (length: {})", plaintext.length());
            return result;
            
        } catch (Exception e) {
            log.error("Failed to encrypt message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to encrypt message", e);
        }
    }
    
    @Override
    public String decryptMessage(String encryptedMessage, String encryptionKey) {
        if (encryptedMessage == null || encryptedMessage.isEmpty()) {
            throw new IllegalArgumentException("Encrypted message cannot be null or empty");
        }
        if (!isValidEncryptionKey(encryptionKey)) {
            throw new IllegalArgumentException("Invalid encryption key");
        }
        
        try {
            // Decode the encryption key
            byte[] keyBytes = Base64.getDecoder().decode(encryptionKey);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
            
            // Decode the encrypted message
            byte[] combined = Base64.getDecoder().decode(encryptedMessage);
            
            // Extract IV and encrypted data
            if (combined.length < GCM_IV_LENGTH) {
                throw new IllegalArgumentException("Invalid encrypted message format");
            }
            
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encryptedBytes = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(combined, GCM_IV_LENGTH, encryptedBytes, 0, encryptedBytes.length);
            
            // Initialize cipher for decryption
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);
            
            // Decrypt the message
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            String result = new String(decryptedBytes, StandardCharsets.UTF_8);
            
            log.debug("Successfully decrypted message (length: {})", result.length());
            return result;
            
        } catch (Exception e) {
            log.error("Failed to decrypt message: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Failed to decrypt message: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean isValidEncryptionKey(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        
        try {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            // AES-256 requires 32 bytes (256 bits)
            return keyBytes.length == 32;
        } catch (Exception e) {
            log.debug("Invalid encryption key format: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public String generateIV() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        SECURE_RANDOM.nextBytes(iv);
        return Base64.getEncoder().encodeToString(iv);
    }
}
