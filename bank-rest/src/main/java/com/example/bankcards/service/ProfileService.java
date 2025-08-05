package com.example.bankcards.service;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.TransactionDto;
import com.example.bankcards.core.dto.card.CardDto;
import com.example.bankcards.core.dto.card.CardFullDto;
import com.example.bankcards.core.dto.transaction.TransactionPayload;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.UUID;

public interface ProfileService {
    PageDto<CardDto> readCards(String bearerToken, Pageable pageable);

    CardFullDto readCard(String subject, UUID cardId);

    void createTransaction(String subject, TransactionPayload payload);

    void blockCard(String subject, UUID cardId);

    PageDto<TransactionDto> readTransactions(JwtAuthenticationToken bearerToken, Pageable pageable);
}
