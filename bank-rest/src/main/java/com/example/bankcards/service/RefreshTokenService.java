package com.example.bankcards.service;


import com.example.bankcards.entity.cache.RefreshToken;

import java.time.LocalDateTime;
import java.util.UUID;

public interface RefreshTokenService {
    void create(String signedRefreshToken, UUID userId, LocalDateTime expiresAt);

    void delete(UUID userId);

    RefreshToken get(UUID userId);
}
