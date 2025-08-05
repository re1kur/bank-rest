package com.example.bankcards.core.other;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record TransactionFilter(
        List<UUID> cardIds,
        UUID senderCardId,
        UUID receiverCardId,
        BigDecimal amount
) {
}
