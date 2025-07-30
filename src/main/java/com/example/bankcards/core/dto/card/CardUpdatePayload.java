package com.example.bankcards.core.dto.card;


import jakarta.validation.constraints.NotNull;

public record CardUpdatePayload(
        @NotNull
        CardStatus status
) {
}
