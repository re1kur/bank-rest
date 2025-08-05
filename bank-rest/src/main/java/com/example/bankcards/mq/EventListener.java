package com.example.bankcards.mq;

import com.example.bankcards.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventListener {
    private final TransactionService transactionService;

    @RabbitListener(queues = "${mq.queue.transaction}")
    private void listenTransactions(UUID transactionId) {
        log.info("LISTEN TRANSACTION EVENT: {}", transactionId);

        transactionService.perform(transactionId);

        log.info("TRANSACTION [{}] EVENT IS PROCESSED.", transactionId);
    }
}
