package com.example.bankcards.service;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.TransactionDto;
import com.example.bankcards.core.dto.transaction.TransactionPayload;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TransactionService {
    void create(TransactionPayload payload);

    TransactionDto read(UUID transactionId);

    UUID create(UUID userId, TransactionPayload payload);

    PageDto<TransactionDto> readAll(Pageable pageable);
}
