package com.example.bankcards.service;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.card.CardDto;
import com.example.bankcards.core.dto.card.CardPayload;
import com.example.bankcards.core.dto.card.CardUpdatePayload;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CardService {
    void create(CardPayload payload);

    CardDto read(UUID cardId);

    void update(UUID cardId, CardUpdatePayload payload);

    void delete(UUID cardId);

    PageDto<CardDto> readAll(Pageable pageable);
}
