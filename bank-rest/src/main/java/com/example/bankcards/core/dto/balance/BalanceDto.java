package com.example.bankcards.core.dto.balance;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record BalanceDto(
        UUID cardId,
        BigDecimal value,
        boolean blocked
) {
}
