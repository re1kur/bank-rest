package com.example.bankcards.outbox;

import com.example.bankcards.entity.OutboxEvent;
import com.example.bankcards.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {
    private final OutboxService outboxService;
    private final TransactionService transactionService;

    @Value("${spring.custom.outbox.type.transaction}")
    private String transactionTypeName;

    @Scheduled(fixedDelay = 10_000)
    private void checkEvents() {
        log.info("CHECK OUTBOX EVENTS.");

        List<OutboxEvent> events = outboxService.getAll();
        for (OutboxEvent event : events) {
            try {
                boolean locked = outboxService.markEventAsProcessing(event);
                if (!locked) continue;

                if (event.getType().equals(transactionTypeName)) {
                    log.info("GOT EVENT TRANSACTION. START PERFORMING.");
                    transactionService.perform(UUID.fromString(event.getPayload()));

                    outboxService.delete(event);
                    log.info("TRANSACTION EVENT HAS BEEN PERFORMED.");
                }
            } catch (Exception e) {
                log.info("ERROR PERFORMING EVENT: {}", e.getMessage());
                event.setStatus("ERROR");
                outboxService.markEventAsError(event);
            }
        }
        log.info("OUTBOX EVENTS HAVE BEEN CHECKED.");
    }
}
