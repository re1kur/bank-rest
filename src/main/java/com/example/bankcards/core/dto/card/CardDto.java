package com.example.bankcards.core.dto.card;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CardDto(
        UUID id,
        UUID userId,
        String lastNumbers,
        LocalDateTime expirationDate,
        String status
) {
}
