package com.example.bankcards.mapper.impl;

import com.example.bankcards.core.annotation.Mapper;
import com.example.bankcards.core.dto.card.CardDto;
import com.example.bankcards.core.dto.card.CardPayload;
import com.example.bankcards.core.dto.card.CardUpdatePayload;
import com.example.bankcards.entity.Card;
import com.example.bankcards.mapper.CardMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper
public class CardMapperImpl implements CardMapper {

    @Override
    public Card create(CardPayload payload) {
        return null;
    }

    @Override
    public CardDto read(Card card) {
        return null;
    }

    @Override
    public Card update(Card card, CardUpdatePayload payload) {
        return null;
    }
}
