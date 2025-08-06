package com.example.bankcards.service;

import com.example.bankcards.core.dto.balance.BalanceDto;
import com.example.bankcards.core.dto.balance.BalanceUpdatePayload;
import com.example.bankcards.core.exception.BalanceNotFoundException;
import com.example.bankcards.entity.Balance;
import com.example.bankcards.entity.Card;
import com.example.bankcards.mapper.BalanceMapper;
import com.example.bankcards.repository.BalanceRepository;
import com.example.bankcards.service.impl.BalanceServiceImpl;
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
public class BalanceServiceTest {
    @InjectMocks
    private BalanceServiceImpl service;

    @Mock
    private BalanceMapper mapper;

    @Mock
    private BalanceRepository repo;

    @Test
    void read__ReturnsDto() {
        UUID cardId = UUID.randomUUID();
        Balance expectedFound = Balance.builder().card(Card.builder().id(cardId).build()).build();
        BalanceDto expected = BalanceDto.builder().cardId(cardId).build();

        when(repo.findById(cardId)).thenReturn(Optional.of(Balance.builder().card(Card.builder().id(cardId).build()).build()));
        when(mapper.read(Balance.builder().card(Card.builder().id(cardId).build()).build())).thenReturn(BalanceDto.builder().cardId(cardId).build());

        BalanceDto result = assertDoesNotThrow(() -> service.read(cardId));
        assertEquals(expected, result);

        verify(repo, times(1)).findById(cardId);
        verify(mapper, times(1)).read(expectedFound);
    }

    @Test
    void read__BalanceNotFound__ThrowsException() {
        UUID cardId = UUID.randomUUID();

        when(repo.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(BalanceNotFoundException.class, () -> service.read(cardId));

        verify(repo, times(1)).findById(cardId);
        verifyNoInteractions(mapper);
    }

    @Test
    void update__DoesNotThrowsException() {
        UUID cardId = UUID.randomUUID();
        BalanceUpdatePayload payload = new BalanceUpdatePayload(new BigDecimal(200), null);
        Balance expectedFound = Balance.builder().value(new BigDecimal(0)).blocked(false).cardId(cardId).card(Card.builder().id(cardId).build()).build();
        Balance expectedMapped = Balance.builder().value(new BigDecimal(200)).blocked(false).cardId(cardId).card(Card.builder().id(cardId).build()).build();


        Balance mockFound = Balance.builder().value(new BigDecimal(0)).blocked(false).cardId(cardId).card(Card.builder().id(cardId).build()).build();
        BalanceUpdatePayload mockPayload = new BalanceUpdatePayload(new BigDecimal(200), null);
        Balance mockMapped = Balance.builder().value(new BigDecimal(200)).blocked(false).cardId(cardId).card(Card.builder().id(cardId).build()).build();
        Balance mockSaved = Balance.builder().value(new BigDecimal(200)).blocked(false).cardId(cardId).card(Card.builder().id(cardId).build()).build();

        when(repo.findById(cardId)).thenReturn(Optional.of(mockFound));
        when(mapper.update(mockFound, mockPayload)).thenReturn(mockMapped);
        when(repo.save(mockMapped)).thenReturn(mockSaved);

        assertDoesNotThrow(() -> service.update(cardId, payload));

        verify(repo, times(1)).findById(cardId);
        verify(mapper, times(1)).update(expectedFound, payload);
        verify(repo, times(1)).save(expectedMapped);
    }

    @Test
    void update__BalanceNotFound__ThrowsException() {
        UUID cardId = UUID.randomUUID();
        BalanceUpdatePayload payload = new BalanceUpdatePayload(new BigDecimal(200), false);

        when(repo.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(BalanceNotFoundException.class, () -> service.update(cardId, payload));

        verify(repo, times(1)).findById(cardId);
        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(repo);
    }
}
