package com.example.bankcards.mapper.impl;

import com.example.bankcards.core.annotation.Mapper;
import com.example.bankcards.entity.cache.RefreshToken;
import com.example.bankcards.mapper.RefreshTokenMapper;

import java.time.LocalDateTime;
import java.util.UUID;

@Mapper
public class RefreshTokenMapperImpl implements RefreshTokenMapper {
    @Override
    public RefreshToken create(String signedRefreshToken, UUID userId, LocalDateTime expiresAt) {
        //todo: tests
        return RefreshToken.builder()
                .id(userId)
                .value(signedRefreshToken)
                .expiresAt(expiresAt)
                .build();
    }
}
