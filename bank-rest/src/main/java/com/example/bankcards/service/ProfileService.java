package com.example.bankcards.service;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.TransactionDto;
import com.example.bankcards.core.dto.card.CardDto;
import com.example.bankcards.core.dto.card.CardFullDto;
import com.example.bankcards.core.dto.transaction.TransactionPayload;
import com.example.bankcards.core.dto.user.UserDto;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProfileService {
    PageDto<CardDto> readCards(String bearerToken, Pageable pageable);

    CardFullDto readCard(String subject, UUID cardId);

    void createTransaction(String subject, TransactionPayload payload);

    void blockCard(String subject, UUID cardId);

    PageDto<TransactionDto> readTransactions(String userId, Pageable pageable);

    UserDto getProfile(String subject);
}
