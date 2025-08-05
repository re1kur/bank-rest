package com.example.bankcards.core.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record TransactionDto(
        UUID id,
        UUID senderCardId,
        UUID receiverCardId,
        BigDecimal amount,
        String status,
        LocalDateTime issueTimestamp,
        LocalDateTime processedTimestamp
) {
}
