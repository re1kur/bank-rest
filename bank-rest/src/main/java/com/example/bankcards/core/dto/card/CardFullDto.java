package com.example.bankcards.core.dto.card;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record CardFullDto(
        UUID id,
        UUID userId,
        String last4,
        LocalDate expirationDate,
        String status,
        BigDecimal balance
) {
}
