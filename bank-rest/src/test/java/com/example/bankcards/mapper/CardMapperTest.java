package com.example.bankcards.mapper;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.card.CardDto;
import com.example.bankcards.core.dto.card.CardPayload;
import com.example.bankcards.core.dto.card.CardStatus;
import com.example.bankcards.core.dto.card.CardUpdatePayload;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.impl.CardMapperImpl;
import com.example.bankcards.util.EncryptUtils;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardMapperTest {
    @InjectMocks
    private CardMapperImpl mapper;

    @Mock
    private EncryptUtils encryptUtils;

    @Test
    void create__ShouldMapPayloadToCardEntity() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).build();
        LocalDate date = LocalDate.now().plusDays(30);
        String hash = DigestUtils.sha256Hex("1234123141231231");

        CardPayload payload = new CardPayload(userId, "1234123141231231",
                date, "visa");
        Card expected = Card.builder()
                .user(user)
                .number("encryptedNumber")
                .numberHash(hash)
                .expirationDate(date)
                .last4("1231").build();

        when(encryptUtils.encrypt("1234123141231231")).thenReturn("encryptedNumber");

        Card result = mapper.create(payload, user);

        assertEquals(expected.getUser().getId(), result.getUser().getId());
        assertEquals(expected.getNumber(), result.getNumber());
        assertEquals(expected.getLast4(), result.getLast4());
        assertEquals(expected.getNumberHash(), result.getNumberHash());
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
        LocalDate toUpdateTime = LocalDate.now().minusDays(1);
        LocalDate updatedTime = LocalDate.now().plusDays(30);

        CardUpdatePayload payload = new CardUpdatePayload(CardStatus.active,
                updatedTime);

        Card expected = Card.builder()
                .status(payload.status())
                .expirationDate(updatedTime)
                .build();

        Card entity = Card.builder()
                .status(CardStatus.blocked)
                .expirationDate(toUpdateTime)
                .build();

        Card result = mapper.update(entity, payload);

        assertEquals(expected.getStatus(), result.getStatus());
        assertEquals(expected.getExpirationDate(), result.getExpirationDate());
    }

    @Test
    void readPage__ShouldMapListEntitiesToPageDto() {
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);

        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        User user1 = User.builder().id(userId1).build();
        User user2 = User.builder().id(userId2).build();

        UUID cardId1 = UUID.randomUUID();
        UUID cardId2 = UUID.randomUUID();

        LocalDate date1 = LocalDate.now().plusDays(30);
        LocalDate date2 = LocalDate.now().plusDays(30);

        List<CardDto> expectedDtos = List.of(
                CardDto.builder().id(cardId1).last4("1234").expirationDate(date1).status("active").userId(userId1).build(),
                CardDto.builder().id(cardId2).last4("1234").expirationDate(date2).status("active").userId(userId2).build()
        );

        PageDto<CardDto> expected = new PageDto<>(expectedDtos, 0, 5, 1, false, false);


        Card card1 = Card.builder().id(cardId1).user(user1).last4("1234").status(CardStatus.active).expirationDate(date1).build();
        Card card2 = Card.builder().id(cardId2).user(user2).last4("1234").status(CardStatus.active).expirationDate(date2).build();

        List<Card> cards = List.of(card1, card2);
        Page<Card> pageCards = new PageImpl<>(cards, pageable, 2);

        PageDto<CardDto> result = mapper.readPage(pageCards);

        assertEquals(expected, result);
    }
}