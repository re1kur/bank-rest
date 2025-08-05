package com.example.bankcards.controller;

import com.example.bankcards.core.dto.user.UserDto;
import com.example.bankcards.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileController {
    private final ProfileService service;

    @GetMapping
    public ResponseEntity<?> getProfile(
            @AuthenticationPrincipal Jwt jwt
            ) {
        UserDto responseBody = service.getProfile(jwt.getSubject());
        return ResponseEntity.ok(responseBody);
    }
}
