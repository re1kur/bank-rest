package com.example.bankcards.service.impl;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.TransactionDto;
import com.example.bankcards.core.dto.transaction.TransactionPayload;
import com.example.bankcards.core.exception.TransactionNotFoundException;
import com.example.bankcards.core.exception.UserDoesNotHavePermission;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.mapper.TransactionMapper;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionMapper mapper;
    private final TransactionRepository repo;
    private final CardService cardService;

    @Override
    @Transactional
    public void create(TransactionPayload payload) {
        log.info("CREATE TRANSACTION REQUEST: [sender: {}]", payload.senderCardId());

        Card senderCard = cardService.getById(payload.senderCardId());
        Card receiverCard = cardService.getById(payload.receiverCardId());

        Transaction mapped = mapper.create(payload, senderCard, receiverCard);

        Transaction saved = repo.save(mapped);

        log.info("TRANSACTION CREATED: [{}]", saved.getId());
    }

    @Override
    public TransactionDto read(UUID transactionId) {
        return repo.findById(transactionId).map(mapper::read)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction [%s] was not found.".formatted(transactionId)));
    }

    @Override
    @Transactional
    public UUID create(UUID userId, TransactionPayload payload) {
        log.info("CREATE TRANSACTION REQUEST by user [{}]: [receiver: {}]", userId, payload.receiverCardId());

        Card senderCard = cardService.getById(payload.senderCardId());
        Card receiverCard = cardService.getById(payload.receiverCardId());

        if (!senderCard.getUserId().equals(userId))
            throw new UserDoesNotHavePermission("User [%s] does not have permissions to create transaction from other card.".formatted(userId));

        Transaction mapped = mapper.create(payload, senderCard, receiverCard);

        Transaction saved = repo.save(mapped);

        log.info("TRANSACTION CREATED by user [{}]: [{}]", userId, saved.getId());
        return saved.getId();
    }

    @Override
    public PageDto<TransactionDto> readAll(Pageable pageable) {
        return mapper.readPage(repo.findAll(pageable));
    }
}
