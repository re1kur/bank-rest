package com.example.bankcards.mapper;

import com.example.bankcards.entity.cache.RefreshToken;

import java.time.LocalDateTime;
import java.util.UUID;

public interface RefreshTokenMapper {
    RefreshToken create(String signedRefreshToken, UUID userId, LocalDateTime expiresAt);
}
