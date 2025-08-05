package com.example.bankcards.mapper.impl;

import com.example.bankcards.core.annotation.Mapper;
import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.card.CardDto;
import com.example.bankcards.core.dto.card.CardFullDto;
import com.example.bankcards.core.dto.card.CardPayload;
import com.example.bankcards.core.dto.card.CardUpdatePayload;
import com.example.bankcards.entity.Balance;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardInformation;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.util.EncryptUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.domain.Page;

import java.util.List;

@Slf4j
@Mapper
@RequiredArgsConstructor
public class CardMapperImpl implements CardMapper {
    private final EncryptUtils encryptUtils;

    @Override
    public Card create(CardPayload payload) {
        String number = payload.number();
        String last4 = number.substring(number.length() - 4);
        String encryptedNumber = encryptUtils.encrypt(number);
        String numberHash = DigestUtils.sha256Hex(number);

        Card build = Card.builder()
                .userId(payload.userId())
                .number(encryptedNumber)
                .numberHash(numberHash)
                .expirationDate(payload.expirationDate())
                .last4(last4)
                .build();

        CardInformation information = CardInformation.builder()
                .card(build)
                .brand(payload.brand())
                .build();
        build.setInformation(information);

        Balance balance = Balance.builder()
                .card(build)
                .build();
        build.setBalance(balance);

        return build;
    }

    @Override
    public CardDto read(Card card) {
        return CardDto.builder()
                .id(card.getId())
                .userId(card.getUserId())
                .status(card.getStatus().name())
                .expirationDate(card.getExpirationDate())
                .last4(card.getLast4())
                .build();
    }

    @Override
    public Card update(Card card, CardUpdatePayload payload) {
        card.setStatus(payload.status());
        card.setExpirationDate(payload.expirationDate());

        return card;
    }

    @Override
    public PageDto<CardDto> readPage(Page<Card> pageCards) {
        List<CardDto> content = pageCards.getContent().stream().map(this::read).toList();

        return new PageDto<>(content,
                pageCards.getNumber(),
                pageCards.getSize(),
                pageCards.getTotalPages(),
                pageCards.hasNext(),
                pageCards.hasPrevious());
    }

    @Override
    public CardFullDto readFull(Card card) {
        return CardFullDto.builder()
                .id(card.getId())
                .userId(card.getUserId())
                .status(card.getStatus().name())
                .expirationDate(card.getExpirationDate())
                .last4(card.getLast4())
                .balance(card.getBalance().getValue())
                .build();
    }
}
