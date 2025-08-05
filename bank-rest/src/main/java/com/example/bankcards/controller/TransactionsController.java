package com.example.bankcards.controller;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.TransactionDto;
import com.example.bankcards.core.dto.transaction.TransactionPayload;
import com.example.bankcards.core.other.TransactionFilter;
import com.example.bankcards.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        UUID responseBody = service.create(payload);
        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> readTransaction(
            @PathVariable(name = "id") UUID transactionId
            ) {
        TransactionDto responseBody = service.read(transactionId);
        return ResponseEntity.ok(responseBody);
    }

    @GetMapping
    public ResponseEntity<?> readTransactions(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
            @ModelAttribute TransactionFilter filter
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PageDto<TransactionDto> responseBody = service.readAll(pageable, filter);
        return ResponseEntity.ok(responseBody);
    }
}
