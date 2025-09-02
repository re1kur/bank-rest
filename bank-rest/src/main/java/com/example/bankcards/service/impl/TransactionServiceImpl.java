package com.example.bankcards.service.impl;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.TransactionDto;
import com.example.bankcards.core.dto.card.CardStatus;
import com.example.bankcards.core.dto.transaction.TransactionPayload;
import com.example.bankcards.core.dto.transaction.TransactionStatus;
import com.example.bankcards.core.exception.TransactionNotFoundException;
import com.example.bankcards.core.exception.UserDoesNotHavePermission;
import com.example.bankcards.core.other.TransactionFilter;
import com.example.bankcards.entity.Balance;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.mapper.TransactionMapper;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.mq.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionMapper mapper;
    private final TransactionRepository repo;
    private final CardService cardService;
    private final EventPublisher eventPublisher;

    @Override
    @Transactional
    public UUID create(TransactionPayload payload) {
        log.info("CREATE TRANSACTION REQUEST: [sender: {}]", payload.senderCardId());

        Card senderCard = cardService.getById(payload.senderCardId());
        Card receiverCard = cardService.getById(payload.receiverCardId());

        Transaction mapped = mapper.create(payload, senderCard, receiverCard);

        Transaction saved = repo.save(mapped);
        UUID transactionId = saved.getId();

        eventPublisher.publishTransaction(transactionId);

        log.info("TRANSACTION CREATED: [{}]", transactionId);
        return transactionId;
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

        if (!senderCard.getUser().getId().equals(userId))
            throw new UserDoesNotHavePermission("User [%s] does not have permissions to create transaction from other card.".formatted(userId));

        Transaction mapped = mapper.create(payload, senderCard, receiverCard);

        Transaction saved = repo.save(mapped);


        log.info("TRANSACTION CREATED by user [{}]: [{}]", userId, saved.getId());
        return saved.getId();
    }

    @Override
    public PageDto<TransactionDto> readAll(Pageable pageable, TransactionFilter filter) {
        List<UUID> cardIds = filter.cardIds();
        BigDecimal amount = filter.amount();
        UUID receiverCardId = filter.receiverCardId();
        UUID senderCardId = filter.senderCardId();

        return mapper.readPage(repo.findAll(pageable, amount, receiverCardId, senderCardId, cardIds));
    }

    @Override
    @Transactional
    public void perform(UUID transactionId) {
        log.info("PERFORMING TRANSACTION [{}]", transactionId);

        Transaction transaction = repo.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction [%s] was not found.".formatted(transactionId)));

        Card senderCard = transaction.getSenderCard();
        Card receiverCard = transaction.getReceiverCard();

        try {
            if (senderCard.getStatus() != CardStatus.active) {
                throw new IllegalStateException("Sender card is not active");
            }

            if (receiverCard.getStatus() != CardStatus.active) {
                throw new IllegalStateException("Receiver card is not active");
            }

            Balance senderBalance = senderCard.getBalance();
            Balance receiverBalance = receiverCard.getBalance();

            BigDecimal amount = transaction.getAmount();

            if (senderBalance.getValue().compareTo(amount) < 0) {
                throw new IllegalStateException("Insufficient funds on sender card");
            }

            senderBalance.setValue(senderBalance.getValue().subtract(amount));

            receiverBalance.setValue(receiverBalance.getValue().add(amount));

            transaction.setStatus(TransactionStatus.completed);
            transaction.setProcessedTimestamp(LocalDateTime.now());

            repo.save(transaction);
        } catch (Exception e) {
            log.error("Transaction [{}] failed: {}", transactionId, e.getMessage(), e);
            transaction.setStatus(TransactionStatus.failed);
            transaction.setProcessedTimestamp(LocalDateTime.now());
            repo.save(transaction);
        }
    }
}
