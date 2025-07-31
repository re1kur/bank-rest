package com.example.bankcards.mapper;

import com.example.bankcards.core.dto.card.CardDto;
import com.example.bankcards.core.dto.card.CardPayload;
import com.example.bankcards.core.dto.card.CardStatus;
import com.example.bankcards.core.dto.card.CardUpdatePayload;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.impl.CardMapperImpl;
import com.example.bankcards.util.EncryptUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardMapperTest {
    @InjectMocks
    private CardMapperImpl mapper;

    @Mock
    private EncryptUtil encryptUtil;

    @Test
    void create__ShouldMapPayloadToCardEntity() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).build();
        LocalDate date = LocalDate.now().plusDays(30);

        CardPayload payload = new CardPayload(userId, "1234123141231231",
                date, "visa");
        Card expected = Card.builder()
                .user(user)
                .number("encryptedNumber")
                .expirationDate(date)
                .last4("1231").build();

        when(encryptUtil.encrypt("1234123141231231")).thenReturn("encryptedNumber");

        Card result = mapper.create(payload, user);

        assertEquals(expected.getUser(), result.getUser());
        assertEquals(expected.getNumber(), result.getNumber());
        assertEquals(expected.getLast4(), result.getLast4());
        assertEquals(expected.getExpirationDate(), result.getExpirationDate());
    }

    @Test
    void read__ShouldMapEntityToDto() {

        UUID cardId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).build();
        LocalDate date = LocalDate.now().plusDays(30);

        Card entity = Card.builder()
                .id(cardId)
                .user(user)
                .status(CardStatus.active)
                .expirationDate(date)
                .number("encryptedNumber")
                .last4("1234")
                .build();

        CardDto expected = CardDto.builder()
                .id(cardId)
                .userId(userId)
                .status(CardStatus.active.name())
                .expirationDate(date)
                .last4("1234")
                .build();

        CardDto result = mapper.read(entity);

        assertEquals(expected.id(), result.id());
        assertEquals(expected.userId(), result.userId());
        assertEquals(expected.status(), result.status());
        assertEquals(expected.expirationDate(), result.expirationDate());
        assertEquals(expected.last4(), result.last4());
    }

    @Test
    void update() {
        LocalDate date = LocalDate.now().minusDays(1);

        CardUpdatePayload payload = new CardUpdatePayload(CardStatus.active,
                LocalDate.now().plusDays(30));

        Card expected = Card.builder()
                .status(payload.status())
                .expirationDate(payload.expirationDate())
                .build();

        Card entity = Card.builder()
                .status(CardStatus.blocked)
                .expirationDate(date)
                .build();

        Card result = mapper.update(entity, payload);

        assertEquals(expected.getStatus(), result.getStatus());
        assertEquals(expected.getExpirationDate(), result.getExpirationDate());
    }
}