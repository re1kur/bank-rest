package com.example.bankcards.mapper;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.TransactionDto;
import com.example.bankcards.core.dto.transaction.TransactionPayload;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import org.springframework.data.domain.Page;

public interface TransactionMapper {
    Transaction create(TransactionPayload payload, Card senderCard, Card receiverCard);

    TransactionDto read(Transaction transaction);

    PageDto<TransactionDto> readPage(Page<Transaction> page);
}
