package com.legacykeep.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for subscription management.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Subscription type is required")
    private String subscriptionType;

    @NotNull(message = "Subscription ID is required")
    private Long subscriptionId;
}
