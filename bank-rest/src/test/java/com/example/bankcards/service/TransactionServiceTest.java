package com.example.bankcards.service;

import com.example.bankcards.core.dto.TransactionDto;
import com.example.bankcards.core.dto.transaction.TransactionPayload;
import com.example.bankcards.core.exception.CardNotFoundException;
import com.example.bankcards.core.exception.TransactionNotFoundException;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.mapper.TransactionMapper;
import com.example.bankcards.mq.EventPublisher;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @InjectMocks
    private TransactionServiceImpl service;

    @Mock
    private TransactionRepository repo;

    @Mock
    private TransactionMapper mapper;

    @Mock
    private CardService cardService;

    @Mock
    private EventPublisher publisher;


    @Test
    void create__DoesNotThrowsException() {
        UUID senderCardId = UUID.randomUUID();
        UUID receiverCardId = UUID.randomUUID();

        TransactionPayload payload = new TransactionPayload(senderCardId, receiverCardId, new BigDecimal(200));
        Card senderCard = Card.builder().id(senderCardId).build();
        Card receiverCard = Card.builder().id(receiverCardId).build();
        Transaction expectedMapped = Transaction.builder().senderCard(senderCard).receiverCard(receiverCard).amount(payload.amount()).build();

        Transaction mockMapped = Transaction.builder().senderCard(senderCard).receiverCard(receiverCard).amount(payload.amount()).build();
        Transaction mockSaved = Transaction.builder().senderCard(senderCard).receiverCard(receiverCard).amount(payload.amount()).build();

        when(cardService.getById(senderCardId)).thenReturn(senderCard);
        when(cardService.getById(receiverCardId)).thenReturn(receiverCard);
        when(mapper.create(payload, senderCard, receiverCard)).thenReturn(mockMapped);
        when(repo.save(mockMapped)).thenReturn(mockSaved);

        assertDoesNotThrow(() -> service.create(payload));

        verify(cardService, times(1)).getById(senderCardId);
        verify(cardService, times(1)).getById(receiverCardId);
        verify(mapper, times(1)).create(payload, senderCard, receiverCard);
        verify(repo, times(1)).save(expectedMapped);
    }

    @Test
    void create__CardNotFound__ThrowsException() {
        UUID senderCardId = UUID.randomUUID();
        UUID receiverCardId = UUID.randomUUID();

        TransactionPayload payload = new TransactionPayload(senderCardId, receiverCardId, new BigDecimal(200));

        when(cardService.getById(senderCardId)).thenThrow(CardNotFoundException.class);

        assertThrows(CardNotFoundException.class, () -> service.create(payload));

        verify(cardService, times(1)).getById(senderCardId);
        verifyNoMoreInteractions(cardService);
        verifyNoInteractions(mapper);
        verifyNoInteractions(repo);
    }

    @Test
    void read__DoesNotThrowsException() {
        UUID transactionId = UUID.randomUUID();

        Transaction expectedFound = Transaction.builder().id(transactionId).build();
        TransactionDto expected = TransactionDto.builder().id(transactionId).build();

        when(repo.findById(transactionId)).thenReturn(Optional.of(Transaction.builder().id(transactionId).build()));
        when(mapper.read(Transaction.builder().id(transactionId).build())).thenReturn(TransactionDto.builder().id(transactionId).build());

        TransactionDto result = assertDoesNotThrow(() -> service.read(transactionId));
        assertEquals(expected, result);

        verify(repo, times(1)).findById(transactionId);
        verify(mapper, times(1)).read(expectedFound);
    }

    @Test
    void read__TransactionNotFound__ThrowsException() {
        UUID transactionId = UUID.randomUUID();

        when(repo.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> service.read(transactionId));

        verify(repo, times(1)).findById(transactionId);
        verifyNoInteractions(mapper);
    }
}