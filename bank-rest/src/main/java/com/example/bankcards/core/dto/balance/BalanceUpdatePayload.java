package com.example.bankcards.core.dto.balance;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record BalanceUpdatePayload(
        @Positive
        BigDecimal value,
        Boolean blocked
) {
}
