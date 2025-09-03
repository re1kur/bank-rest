package com.example.bankcards.outbox.impl;

import com.example.bankcards.entity.OutboxEvent;
import com.example.bankcards.outbox.OutboxService;
import com.example.bankcards.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {
    private final OutboxRepository repo;

    @Value("${spring.custom.outbox.type.transaction}")
    private String transactionTypeName;


    @Override
    @Transactional
    public void createTransactionEvent(UUID transactionId) {
        log.info("TRANSACTION EVENT INIT: [{}]", transactionId);

        OutboxEvent event = OutboxEvent.builder()
                .type(transactionTypeName)
                .payload(transactionId.toString())
                .build();
        repo.save(event);

        log.info("TRANSACTION EVENT CREATED: [{}]", transactionId);
    }

    @Override
    public List<OutboxEvent> getAll() {
        return (List<OutboxEvent>) repo.findAll();
    }

    @Override
    @Transactional
    public void delete(OutboxEvent event) {
        UUID id = event.getId();
        log.info("OUTBOX EVENT DELETE REQUEST [{}]", id);

        repo.delete(event);

        log.info("OUTBOX EVENT DELETED [{}]", id);
    }

    @Override
    @Transactional
    public boolean markEventAsProcessing(OutboxEvent event) {
        return repo.markAsProcessing(event.getId()) > 0;
    }

    @Override
    @Transactional
    public void markEventAsError(OutboxEvent event) {
        repo.markAsError(event.getId());
    }
}
