package com.example.bankcards.core.dto.auth;

public record JwtPair(
        String accessToken,
        String refreshToken
) {
}
