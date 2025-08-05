package com.example.bankcards.controller;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.TransactionDto;
import com.example.bankcards.core.dto.card.CardDto;
import com.example.bankcards.core.dto.card.CardFullDto;
import com.example.bankcards.core.dto.transaction.TransactionPayload;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileController {
    private final ProfileService service;

    @GetMapping("/cards")
    public ResponseEntity<?> getUserCards(
            @AuthenticationPrincipal JwtAuthenticationToken bearerToken,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "5") Integer size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PageDto<CardDto> responseBody = service.readCards(bearerToken.getToken().getSubject(), pageable);
        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/cards/{id}")
    public ResponseEntity<?> getUserCard(
            @AuthenticationPrincipal JwtAuthenticationToken bearerToken,
            @PathVariable(name = "id") UUID cardId) {
        CardFullDto responseBody = service.readCard(bearerToken.getToken().getSubject(), cardId);
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/transactions")
    public ResponseEntity<?> createTransaction(
            @AuthenticationPrincipal JwtAuthenticationToken bearerToken,
            @RequestBody @Valid TransactionPayload payload
    ) {
        service.createTransaction(bearerToken.getToken().getSubject(), payload);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/cards/{id}/block")
    public ResponseEntity<?> blockCard(
            @AuthenticationPrincipal JwtAuthenticationToken bearerToken,
            @PathVariable(name = "id") UUID cardId
    ) {
        service.blockCard(bearerToken.getToken().getSubject(), cardId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> readTransactions(
            @AuthenticationPrincipal JwtAuthenticationToken bearerToken,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "5") Integer size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PageDto<TransactionDto> responseBody = service.readTransactions(bearerToken, pageable);
        return ResponseEntity.ok(responseBody);
    }
}
