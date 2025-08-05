package com.example.bankcards.core.dto.auth;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull
        String username,
        @NotNull
        String password
) {
}
