package com.example.bankcards.core.other;

import com.example.bankcards.core.dto.card.CardStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CardFilter(
        UUID userId,
        CardStatus status,
        Boolean amountDesc,
        BigDecimal amount,
        LocalDateTime expirationDate,
        Boolean dateDesc
) {
}
