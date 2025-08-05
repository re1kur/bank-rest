package com.example.bankcards.mq;

import java.util.UUID;

public interface EventPublisher {
    void publishTransaction(UUID transactionId);
}
