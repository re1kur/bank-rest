package com.example.bankcards.mapper.impl;

import com.example.bankcards.core.annotation.Mapper;
import com.example.bankcards.core.dto.card.CardDto;
import com.example.bankcards.core.dto.card.CardPayload;
import com.example.bankcards.core.dto.card.CardUpdatePayload;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper
@RequiredArgsConstructor
public class CardMapperImpl implements CardMapper {
    private final EncryptUtil encryptUtil;

    @Override
    public Card create(CardPayload payload, User user) {
        String number = payload.number();
        String last4 = number.substring(number.length() - 4);

        String encryptedNumber = encryptUtil.encrypt(number);

        return Card.builder()
                .user(user)
                .number(encryptedNumber)
                .expirationDate(payload.date())
                .last4(last4)
                .build();
    }

    @Override
    public CardDto read(Card card) {
        return CardDto.builder()
                .id(card.getId())
                .userId(card.getUser().getId())
                .status(card.getStatus().name())
                .expirationDate(card.getExpirationDate())
                .last4(card.getLast4())
                .build();
    }

    @Override
    public Card update(Card card, CardUpdatePayload payload) {
        card.setStatus(payload.status());

        return card;
    }
}
