package com.example.bankcards.service;

import com.example.bankcards.core.dto.auth.LoginRequest;
import com.example.bankcards.core.dto.auth.JwtPair;
import com.example.bankcards.core.dto.auth.RefreshTokenPayload;
import com.example.bankcards.core.dto.auth.RegisterRequest;

import java.util.Map;
import java.util.UUID;

public interface AuthService {
    UUID register(RegisterRequest request);

    JwtPair login(LoginRequest request);

    void logout(RefreshTokenPayload request);

    JwtPair refresh(RefreshTokenPayload payload);

    Map<String, Object> getJwks();
}
