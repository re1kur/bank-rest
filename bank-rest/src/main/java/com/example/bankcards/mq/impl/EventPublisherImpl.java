package com.example.bankcards.mq.impl;

import com.example.bankcards.mq.EventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisherImpl implements EventPublisher {
    private final RabbitTemplate template;
//    private final EventMapper mapper;
    private final ObjectMapper serializer;

    @Value("${mq.exchange.transaction}")
    private String transactionExchange;

    @Value("${mq.routing-key.transaction}")
    private String transactionRoutKey;


    @Override
    public void publishTransaction(UUID transactionId) {
        log.info("TRANSACTION EVENT INIT: [{}]", transactionId);

        template.convertAndSend(transactionExchange, transactionRoutKey, transactionId);

        log.info("TRANSACTION EVENT PUBLISHED: [{}]", transactionId);
    }
}
