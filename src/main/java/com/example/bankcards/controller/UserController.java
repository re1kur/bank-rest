package com.example.bankcards.controller;

import com.example.bankcards.core.dto.user.UserPayload;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public ResponseEntity<?> createUser(
            @RequestBody @Valid UserPayload payload
    ) {
        service.create(payload);
        return ResponseEntity.ok(Void.class);
    }
}
