package com.example.bankcards.core.dto.auth;

import jakarta.validation.constraints.NotNull;

public record RefreshTokenPayload(
        @NotNull
        String value
) {
}
