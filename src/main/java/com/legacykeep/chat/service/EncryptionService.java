package com.legacykeep.chat.service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Service for handling message encryption and decryption using AES-256-GCM.
 * 
 * This service provides secure encryption for chat messages with:
 * - AES-256-GCM encryption for authenticated encryption
 * - Random IV generation for each encryption operation
 * - Base64 encoding for safe storage and transmission
 * - Key generation and management
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
public interface EncryptionService {
    
    /**
     * Generate a new AES-256 encryption key
     * 
     * @return Base64 encoded encryption key
     */
    String generateEncryptionKey();
    
    /**
     * Encrypt a message using AES-256-GCM
     * 
     * @param plaintext The message content to encrypt
     * @param encryptionKey Base64 encoded encryption key
     * @return Encrypted message with IV prepended (Base64 encoded)
     */
    String encryptMessage(String plaintext, String encryptionKey);
    
    /**
     * Decrypt a message using AES-256-GCM
     * 
     * @param encryptedMessage Encrypted message with IV prepended (Base64 encoded)
     * @param encryptionKey Base64 encoded encryption key
     * @return Decrypted plaintext message
     * @throws IllegalArgumentException if decryption fails
     */
    String decryptMessage(String encryptedMessage, String encryptionKey);
    
    /**
     * Validate if a string is a valid encryption key
     * 
     * @param key The key to validate
     * @return true if valid, false otherwise
     */
    boolean isValidEncryptionKey(String key);
    
    /**
     * Generate a random IV for encryption
     * 
     * @return Base64 encoded IV
     */
    String generateIV();
}
