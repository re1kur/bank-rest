package com.example.bankcards.controller;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.card.CardDto;
import com.example.bankcards.core.dto.card.CardPayload;
import com.example.bankcards.core.dto.card.CardUpdatePayload;
import com.example.bankcards.core.other.CardFilter;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cards")
public class CardsController {
    private final CardService service;

    @PostMapping
    public ResponseEntity<?> createCard(
            @RequestBody @Valid CardPayload payload
            ) {
        service.create(payload);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> readCard(
            @PathVariable(name = "id") UUID cardId
            ) {
        CardDto responseBody = service.read(cardId);
        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCard(
            @PathVariable(name = "id") UUID cardId,
            @RequestBody @Valid CardUpdatePayload payload
            ) {
        service.update(cardId, payload);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCard(
            @PathVariable(name = "id") UUID cardId
    ) {
        service.delete(cardId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> getCards(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
            @ModelAttribute CardFilter filter
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PageDto<CardDto> responseBody = service.readAll(pageable, filter);

        return ResponseEntity.ok(responseBody);
    }
}
