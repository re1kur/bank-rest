package com.example.bankcards.core.dto.card;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CardUpdatePayload(
        @NotNull
        CardStatus status,
        @Future
        LocalDate expirationDate
) {
}
