package com.example.bankcards.service.impl;

import com.example.bankcards.core.dto.card.CardDto;
import com.example.bankcards.core.dto.card.CardPayload;
import com.example.bankcards.core.dto.card.CardUpdatePayload;
import com.example.bankcards.core.exception.CardAlreadyExistsException;
import com.example.bankcards.core.exception.CardNotFoundException;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardRepository repo;
    private final CardMapper mapper;
    private final UserService userService;

    @Override
    @Transactional
    public void create(CardPayload payload) {
        log.info("CREATE CARD REQUEST: [user: {}]", payload.userId());

        if (repo.existsByNumber(payload.number()))
            throw new CardAlreadyExistsException("Card [%s] already exists.");

        User user = userService.get(payload.userId());

        Card mapped = mapper.create(payload, user);

        Card saved = repo.save(mapped);

        log.info("CARD CREATED: [{}]", saved.getId());
    }

    @Override
    public CardDto read(UUID cardId) {
        return repo.findById(cardId)
                .map(mapper::read)
                .orElseThrow(() -> new CardNotFoundException("Card [%s] was not found.".formatted(cardId)));
    }

    @Override
    @Transactional
    public void update(UUID cardId, CardUpdatePayload payload) {
        log.info("UPDATE CARD REQUEST: [{}]", cardId);

        Card found = repo.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card [%s] was not found.".formatted(cardId)));

        Card mapped = mapper.update(found, payload);

        repo.save(mapped);

        log.info("CARD UPDATED: [{}]", cardId);
    }

    @Override
    @Transactional
    public void delete(UUID cardId) {
        log.info("DELETE CARD REQUEST: [{}]", cardId);

        Card found = repo.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card [%s] was not found.".formatted(cardId)));

        repo.delete(found);

        log.info("DELETED CARD: [{}]", cardId);
    }
}
