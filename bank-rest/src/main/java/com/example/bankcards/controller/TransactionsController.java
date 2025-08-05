package com.example.bankcards.controller;

import com.example.bankcards.core.dto.TransactionDto;
import com.example.bankcards.core.dto.transaction.TransactionPayload;
import com.example.bankcards.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionsController {
    private final TransactionService service;

    @PostMapping
    public ResponseEntity<?> createTransaction(
            @RequestBody @Valid TransactionPayload payload
    ) {
        service.create(payload);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> readTransaction(
            @PathVariable(name = "id") UUID transactionId
            ) {
        TransactionDto responseBody = service.read(transactionId);
        return ResponseEntity.ok(responseBody);
    }
}
