package com.example.bankcards.core.dto.card;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record CardDto(
        UUID id,
        UUID userId,
        String last4,
        LocalDate expirationDate,
        String status
) {
}
