package com.example.bankcards.controller;

import com.example.bankcards.core.dto.auth.JwtPair;
import com.example.bankcards.core.dto.auth.LoginRequest;
import com.example.bankcards.core.dto.auth.RefreshTokenPayload;
import com.example.bankcards.core.dto.auth.RegisterRequest;
import com.example.bankcards.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody @Valid RegisterRequest request
            ) {
        UUID responseBody = service.register(request);
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestBody @Valid LoginRequest request
    ) {
        JwtPair responseBody = service.login(request);
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(
            @RequestBody @Valid RefreshTokenPayload request
    ) {
        service.logout(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(
            @RequestBody @Valid RefreshTokenPayload payload
    ) {
        JwtPair responseBody = service.refresh(payload);
        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/.well-known/jwks")
    public ResponseEntity<?> jwksEndpoint() {
        Map<String, Object> responseBody = service.getJwks();
        return ResponseEntity.ok(responseBody);
    }
}
