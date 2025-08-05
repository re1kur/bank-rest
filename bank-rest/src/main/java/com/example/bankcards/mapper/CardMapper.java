package com.example.bankcards.mapper;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.card.CardDto;
import com.example.bankcards.core.dto.card.CardFullDto;
import com.example.bankcards.core.dto.card.CardPayload;
import com.example.bankcards.core.dto.card.CardUpdatePayload;
import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;

public interface CardMapper {
    Card create(CardPayload payload);

    CardDto read(Card card);

    Card update(Card card, CardUpdatePayload payload);

    PageDto<CardDto> readPage(Page<Card> pageCards);

    CardFullDto readFull(Card card);
}
