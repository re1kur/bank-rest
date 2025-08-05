package com.example.bankcards.service.impl;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.card.*;
import com.example.bankcards.core.exception.CardAlreadyExistsException;
import com.example.bankcards.core.exception.CardNotFoundException;
import com.example.bankcards.core.exception.UserDoesNotHavePermission;
import com.example.bankcards.core.other.CardFilter;
import com.example.bankcards.entity.Card;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardRepository repo;
    private final CardMapper mapper;
    private final UserClient userClient;

    @Override
    @Transactional
    public void create(CardPayload payload) {
        UUID userId = payload.userId();
        log.info("CREATE CARD REQUEST: [user: {}]", userId);

        checkConflicts(payload);

        userClient.checkIfExists(payload.userId());

        Card mapped = mapper.create(payload);

        Card saved = repo.save(mapped);

        log.info("CARD CREATED: [user: {}, card: {}]", userId, saved.getId());
    }

    private void checkConflicts(CardPayload payload) {
        String number = payload.number();
        String numberHash = DigestUtils.sha256Hex(number);

        if (repo.existsByNumberHash(numberHash))
            throw new CardAlreadyExistsException("Card [%s] already exists.".formatted(number));
    }

    @Override
    public CardDto read(UUID cardId) {
        log.info("READ CARD REQUEST: [{}]", cardId);

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

        log.info("CARD DELETED: [{}]", cardId);
    }

    @Override
    public PageDto<CardDto> readAll(Pageable pageable, CardFilter filter) {
        BigDecimal amount = filter.amount();
        Boolean amountDesc = filter.amountDesc();
        LocalDateTime expirationDate = filter.expirationDate();
        Boolean dateDesc = filter.dateDesc();
        CardStatus status = filter.status();
        UUID userId = filter.userId();


        return mapper.readPage(repo.findAll(pageable, amount, amountDesc, expirationDate, dateDesc, status.name(), userId));
    }

    @Override
    public Card getById(UUID cardId) {
        return repo.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card [%s] was not found.".formatted(cardId)));
    }

    @Override
    @Transactional
    public CardFullDto readFull(UUID cardId) {
        return repo.findById(cardId)
                .map(mapper::readFull)
                .orElseThrow(() -> new CardNotFoundException("Card [%s] was not found.".formatted(cardId)));
    }

    @Override
    @Transactional
    public void blockCard(UUID userId, UUID cardId) {
        log.info("UPDATE STATUS TO BLOCKED REQUEST BY user [{}], card [{}]", userId, cardId);

        Card found = repo.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card [%s] was not found.".formatted(cardId)));

        if (!found.getUserId().equals(userId))
            throw new UserDoesNotHavePermission("User [%s] does not have permissions to block card that does not belong to him.".formatted(userId));

        checkBlockConflicts(found);

        found.setStatus(CardStatus.blocked);

        repo.save(found);

        log.info("CARD [{}] BLOCKED BY user [{}]", cardId, userId);
    }

    private void checkBlockConflicts(Card found) {
        CardStatus status = found.getStatus();

        if (status.equals(CardStatus.blocked))
            throw new CardAlreadyExistsException("Card [%s] already blocked.".formatted(found.getId()));

        if (status.equals(CardStatus.expired))
            throw new UserDoesNotHavePermission("User [%s] does not have change status to expired card.".formatted(found.getUserId()));
    }
}
