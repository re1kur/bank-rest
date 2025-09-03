package com.example.bankcards.outbox;

import com.example.bankcards.entity.OutboxEvent;

import java.util.List;
import java.util.UUID;

public interface OutboxService {
    void createTransactionEvent(UUID transactionId);

    List<OutboxEvent> getAll();

    void delete(OutboxEvent event);

    boolean markEventAsProcessing(OutboxEvent event);

    void markEventAsError(OutboxEvent event);
}
