package com.example.bankcards.mapper;

import com.example.bankcards.core.dto.card.CardDto;
import com.example.bankcards.core.dto.card.CardPayload;
import com.example.bankcards.core.dto.card.CardUpdatePayload;
import com.example.bankcards.entity.Card;

public interface CardMapper {
    Card create(CardPayload payload);

    CardDto read(Card card);

    Card update(Card card, CardUpdatePayload payload);
}
