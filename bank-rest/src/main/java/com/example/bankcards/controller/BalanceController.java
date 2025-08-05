package com.example.bankcards.controller;

import com.example.bankcards.core.dto.balance.BalanceDto;
import com.example.bankcards.core.dto.balance.BalanceUpdatePayload;
import com.example.bankcards.service.BalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/balances")
public class BalanceController {
    private final BalanceService service;

    @GetMapping("/{id}")
    public ResponseEntity<?> readBalance(
            @PathVariable(name = "id") UUID cardId
            ) {
        BalanceDto responseBody = service.read(cardId);
        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBalance(
            @PathVariable(name = "id") UUID cardId,
            @RequestBody @Valid BalanceUpdatePayload payload
            ) {
        service.update(cardId, payload);
        return ResponseEntity.noContent().build();
    }
}
