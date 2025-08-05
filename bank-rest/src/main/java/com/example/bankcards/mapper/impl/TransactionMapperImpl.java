package com.example.bankcards.mapper.impl;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.TransactionDto;
import com.example.bankcards.core.annotation.Mapper;
import com.example.bankcards.core.dto.transaction.TransactionPayload;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.mapper.TransactionMapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper
public class TransactionMapperImpl implements TransactionMapper {
    @Override
    public Transaction create(TransactionPayload payload, Card senderCard, Card receiverCard) {
        return Transaction.builder()
                .senderCard(senderCard)
                .receiverCard(receiverCard)
                .amount(payload.amount())
                .build();
    }

    @Override
    public TransactionDto read(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .issueTimestamp(transaction.getIssueTimestamp())
                .processedTimestamp(transaction.getProcessedTimestamp())
                .receiverCardId(transaction.getReceiverCard().getId())
                .senderCardId(transaction.getSenderCard().getId())
                .status(transaction.getStatus().name())
                .build();
    }

    @Override
    public PageDto<TransactionDto> readPage(Page<Transaction> page) {
        List<TransactionDto> content = page.getContent().stream().map(this::read).toList();
        int number = page.getNumber();
        int size = page.getSize();
        int totalPages = page.getTotalPages();
        boolean hasNext = page.hasNext();
        boolean hasPrevious = page.hasPrevious();
        return new PageDto<>(content, number, size, totalPages, hasNext, hasPrevious);
    }
}
