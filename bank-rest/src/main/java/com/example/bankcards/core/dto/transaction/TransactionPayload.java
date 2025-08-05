package com.example.bankcards.core.dto.transaction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionPayload (
        @NotNull
        UUID senderCardId,
        @NotNull
        UUID receiverCardId,
        @Positive
        @NotNull
        BigDecimal amount
) {
}
