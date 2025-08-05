package com.example.bankcards.core.dto.event;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionEvent(
        UUID id,
        UUID senderCardId,
        UUID receiverCardId,
        BigDecimal amount,
        String status
) {
}
