package com.example.bankcards.controller;

import com.example.bankcards.core.dto.user.UserDto;
import com.example.bankcards.core.dto.user.UserPayload;
import com.example.bankcards.core.dto.user.UserUpdatePayload;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController {
    private final UserService service;

    @PostMapping
    public ResponseEntity<?> createUser(
            @RequestBody @Valid UserPayload payload
    ) {
        service.create(payload);
        return ResponseEntity.ok(Void.class);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> readUser(
            @PathVariable(name = "id") UUID userId
    ) {
        UserDto responseBody = service.read(userId);
        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable(name = "id") UUID userId,
            @RequestBody @Valid UserUpdatePayload payload
    ) {
        service.update(userId, payload);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable(name = "id") UUID userId
    ) {
        service.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
