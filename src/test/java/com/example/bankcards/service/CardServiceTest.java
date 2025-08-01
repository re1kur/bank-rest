package com.example.bankcards.service;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.card.CardDto;
import com.example.bankcards.core.dto.card.CardPayload;
import com.example.bankcards.core.dto.card.CardStatus;
import com.example.bankcards.core.dto.card.CardUpdatePayload;
import com.example.bankcards.core.exception.CardAlreadyExistsException;
import com.example.bankcards.core.exception.CardNotFoundException;
import com.example.bankcards.core.exception.UserNotFoundException;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.impl.CardServiceImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {
    @InjectMocks
    private CardServiceImpl service;

    @Mock
    private CardMapper mapper;

    @Mock
    private CardRepository repo;

    @Mock
    private UserService userService;

    @Test
    void create__DoesNotThrowsException() {
        UUID cardId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CardPayload payload = new CardPayload(userId, "1234123141231231", LocalDate.now().plusDays(30), "visa");
        Card expected = Card.builder()
                .id(userId)
                .build();
        User user = User.builder().id(userId).build();
        String hash = DigestUtils.sha256Hex("1234123141231231");

        when(repo.existsByNumberHash(hash)).thenReturn(false);
        when(userService.get(userId)).thenReturn(User.builder().id(userId).build());
        when(mapper.create(new CardPayload(userId, "1234123141231231", LocalDate.now().plusDays(30), "visa"), User.builder().id(userId).build()))
                .thenReturn(Card.builder()
                        .id(userId)
                        .build());
        when(repo.save(Card.builder()
                .id(userId)
                .build())).thenReturn(
                Card.builder().id(cardId).build());

        assertDoesNotThrow(() -> service.create(payload));

        verify(repo, times(1)).existsByNumberHash(payload.number());
        verify(userService, times(1)).get(userId);
        verify(mapper, times(1)).create(payload, user);
        verify(repo, times(1)).save(expected);
    }

    @Test
    void create__CardNumberIsOccupied__ThrowsException() {
        UUID userId = UUID.randomUUID();
        CardPayload payload = new CardPayload(userId, "1234123141231231", LocalDate.now().plusDays(30), "visa");

        String hash = DigestUtils.sha256Hex("1234123141231231");

        when(repo.existsByNumberHash(hash)).thenReturn(true);

        assertThrows(CardAlreadyExistsException.class, () -> service.create(payload));

        verify(repo, times(1)).existsByNumberHash(payload.number());
        verifyNoInteractions(mapper, userService);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void create__UserNotFound__ThrowsException() {
        UUID userId = UUID.randomUUID();
        CardPayload payload = new CardPayload(userId, "1234123141231231", LocalDate.now().plusDays(30), "visa");
        String hash = DigestUtils.sha256Hex("1234123141231231");

        when(repo.existsByNumberHash(hash)).thenReturn(false);
        when(userService.get(userId)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> service.create(payload));

        verify(repo, times(1)).existsByNumberHash(payload.number());
        verify(userService, times(1)).get(userId);
        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void read__DoesNotThrowsException() {
        UUID cardId = UUID.randomUUID();
        Card foundExpected = Card.builder().id(cardId).build();
        CardDto expected = CardDto.builder().id(cardId).build();

        when(repo.findById(cardId)).thenReturn(Optional.of(Card.builder().id(cardId).build()));
        when(mapper.read(Card.builder().id(cardId).build()))
                .thenReturn(CardDto.builder().id(cardId).build());

        CardDto result = assertDoesNotThrow(() -> service.read(cardId));
        assertEquals(expected, result);

        verify(repo, times(1)).findById(cardId);
        verify(mapper, times(1)).read(foundExpected);
    }

    @Test
    void read__CardNotFound__ThrowsException() {
        UUID cardId = UUID.randomUUID();

        when(repo.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> service.read(cardId));

        verify(repo, times(1)).findById(cardId);
        verifyNoInteractions(mapper);
    }

    @Test
    void update__DoesNotThrowsException() {
        UUID cardId = UUID.randomUUID();
        CardUpdatePayload payload = new CardUpdatePayload(CardStatus.active, LocalDate.now().plusDays(30));
        Card expectedFound = Card.builder().id(cardId).build();
        Card expected = Card.builder().id(cardId).status(CardStatus.active).build();

        when(repo.findById(cardId)).thenReturn(Optional.of(Card.builder().id(cardId).build()));

        when(mapper.update(Card.builder().id(cardId).build(), new CardUpdatePayload(CardStatus.active, LocalDate.now().plusDays(30))))
                .thenReturn(Card.builder().id(cardId).status(CardStatus.active).build());

        assertDoesNotThrow(() -> service.update(cardId, payload));

        verify(repo, times(1)).findById(cardId);
        verify(mapper, times(1)).update(expectedFound, payload);
        verify(repo, times(1)).save(expected);
    }

    @Test
    void update__CardNotFound__ThrowsException() {
        UUID cardId = UUID.randomUUID();
        CardUpdatePayload payload = new CardUpdatePayload(CardStatus.active, LocalDate.now().plusDays(30));

        when(repo.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> service.update(cardId, payload));

        verify(repo, times(1)).findById(cardId);
        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void delete__DoesNotThrowException() {
        UUID cardId = UUID.randomUUID();
        Card expected = Card.builder().id(cardId).build();

        when(repo.findById(cardId)).thenReturn(Optional.of(Card.builder().id(cardId).build()));
        doNothing().when(repo).delete(Card.builder().id(cardId).build());

        assertDoesNotThrow(() -> service.delete(cardId));

        verify(repo, times(1)).findById(cardId);
        verify(repo, times(1)).delete(expected);
    }

    @Test
    void delete__CardNotFound__ThrowsException() {
        UUID cardId = UUID.randomUUID();

        when(repo.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> service.delete(cardId));

        verify(repo, times(1)).findById(cardId);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void readAll__ReturnsPageDto() {
        int page = 0;
        int size = 5;

        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        PageDto<CardDto> expected = new PageDto<>(List.of(CardDto.builder().userId(userId1).build(),
                CardDto.builder().userId(userId2).build()), 0, 5, 1, false, false);

        Pageable pageable = PageRequest.of(page, size);
        Page<Card> expectedFound = new PageImpl<>(List.of(Card.builder().user(User.builder().id(userId1).build()).build(),
                Card.builder().user(User.builder().id(userId2).build()).build()), pageable, 1);

        when(repo.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(Card.builder().user(User.builder().id(userId1).build()).build(),
                        Card.builder().user(User.builder().id(userId2).build()).build()), pageable, 1));

        when(mapper.readPage(new PageImpl<>(List.of(Card.builder().user(User.builder().id(userId1).build()).build(),
                        Card.builder().user(User.builder().id(userId2).build()).build()), pageable, 1)))
                .thenReturn(new PageDto<>(List.of(CardDto.builder().userId(userId1).build(),
                        CardDto.builder().userId(userId2).build()), 0, 5, 1, false, false));

        PageDto<CardDto> result = assertDoesNotThrow(() -> service.readAll(pageable));

        assertEquals(expected, result);

        verify(repo, times(1)).findAll(pageable);
        verify(mapper, times(1)).readPage(expectedFound);
    }
}