package com.example.bankcards.service.impl;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.TransactionDto;
import com.example.bankcards.core.dto.card.CardDto;
import com.example.bankcards.core.dto.card.CardFullDto;
import com.example.bankcards.core.dto.transaction.TransactionPayload;
import com.example.bankcards.core.exception.UserDoesNotHavePermission;
import com.example.bankcards.core.other.CardFilter;
import com.example.bankcards.core.other.TransactionFilter;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.ProfileService;
import com.example.bankcards.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final CardService cardService;
    private final TransactionService transactionService;

    @Override
    public PageDto<CardDto> readCards(String bearerToken, Pageable pageable) {
        return cardService.readAll(pageable, CardFilter.builder().userId(UUID.fromString(bearerToken)).build());
    }

    @Override
    public CardFullDto readCard(String subject, UUID cardId) {
        CardFullDto card = cardService.readFull(cardId);
        if (!card.userId().equals(UUID.fromString(subject)))
            throw new UserDoesNotHavePermission("User [%s] does not have permissions to read card of other user.".formatted(subject));
        return card;
    }

    @Override
    public void createTransaction(String subject, TransactionPayload payload) {
        log.info("CREATE TRANSACTION BY [{}]: {}", subject, payload);

        UUID transactionId = transactionService.create(UUID.fromString(subject), payload);

        log.info("TRANSACTION CREATED [{}] by user [{}]", transactionId, subject);
    }

    @Override
    public void blockCard(String subject, UUID cardId) {
        log.info("BLOCK CARD REQUEST by user [{}]: card [{}]", subject, cardId);

       cardService.blockCard(UUID.fromString(subject), cardId);

       log.info("BLOCK 200.");
    }

    @Override
    public PageDto<TransactionDto> readTransactions(String userId, Pageable pageable) {
        UUID userUuid = UUID.fromString(userId);
        List<Card> cards = cardService.getByUserId(userUuid);
        List<UUID> cardIds = cards.stream().map(Card::getId).toList();

        return transactionService.readAll(pageable, TransactionFilter.builder().cardIds(cardIds).build());
    }
}
