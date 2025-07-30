package com.example.bankcards.controller;

import com.example.bankcards.core.dto.card.CardDto;
import com.example.bankcards.core.dto.card.CardPayload;
import com.example.bankcards.core.dto.card.CardStatus;
import com.example.bankcards.core.dto.card.CardUpdatePayload;
import com.example.bankcards.core.exception.CardAlreadyExistsException;
import com.example.bankcards.core.exception.CardNotFoundException;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CardsController.class)
public class CardsControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    CardService service;

    private static final String URI = "/api/v1/cards";

    @Test
    void create__ReturnsOk() throws Exception {
        CardPayload payload = new CardPayload(UUID.randomUUID(), "1234123141231231", "visa");

        doNothing().when(service).create(payload);

        mvc.perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(service, times(1)).create(payload);
    }

    @Test
    void create__EmptyNumber__ReturnsBadRequest() throws Exception {
        CardPayload payload = new CardPayload(UUID.randomUUID(), "", "visa");

        doNothing().when(service).create(payload);

        mvc.perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void create__EmptyUserId__ReturnsBadRequest() throws Exception {
        CardPayload payload = new CardPayload(null, "1234123141231231", "visa");

        doNothing().when(service).create(payload);

        mvc.perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void create__CardNumberIsOccupied__ReturnsConflict() throws Exception {
        CardPayload payload = new CardPayload(UUID.randomUUID(), "1234123141231231", "visa");

        doThrow(CardAlreadyExistsException.class).when(service).create(payload);

        mvc.perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isConflict());

        verify(service, times(1)).create(payload);
    }

    @Test
    void read__ReturnsDtoAndOk() throws Exception {
        UUID cardId = UUID.randomUUID();
        CardDto expected = CardDto.builder().id(cardId).build();

        when(service.read(cardId)).thenReturn(CardDto.builder().id(cardId).build());

        mvc.perform(get(URI + "/%s".formatted(cardId)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));

        verify(service, times(1)).read(cardId);
    }

    @Test
    void read__CardNotFound__ReturnsBadRequest() throws Exception {
        UUID cardId = UUID.randomUUID();

        when(service.read(cardId)).thenThrow(CardNotFoundException.class);

        mvc.perform(get(URI + "/%s".formatted(cardId)))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).read(cardId);
    }

    @Test
    void update__ReturnsNoContent() throws Exception {
        UUID cardId = UUID.randomUUID();
        CardUpdatePayload payload = new CardUpdatePayload(CardStatus.active);

        doNothing().when(service).update(cardId, payload);

        mvc.perform(put(URI + "/%s".formatted(cardId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isNoContent());

        verify(service, times(1)).update(cardId, payload);
    }

    @Test
    void update__CardNotFound__ReturnsBadRequest() throws Exception {
        UUID cardId = UUID.randomUUID();
        CardUpdatePayload payload = new CardUpdatePayload(CardStatus.active);

        doThrow(CardNotFoundException.class).when(service).update(cardId, payload);

        mvc.perform(put(URI + "/%s".formatted(cardId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).update(cardId, payload);
    }

    @Test
    void update__StatusIsNull__ReturnsBadRequest() throws Exception {
        UUID cardId = UUID.randomUUID();
        CardUpdatePayload payload = new CardUpdatePayload(null);

        mvc.perform(put(URI + "/%s".formatted(cardId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void delete__ReturnsNoContent() throws Exception {
        UUID cardId = UUID.randomUUID();

        doNothing().when(service).delete(cardId);

        mvc.perform(delete(URI + "/%s".formatted(cardId)))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(cardId);
    }

    @Test
    void delete_CardNotFound__ReturnsBadRequest() throws Exception {
        UUID cardId = UUID.randomUUID();

        doThrow(CardNotFoundException.class).when(service).delete(cardId);

        mvc.perform(delete(URI + "/%s".formatted(cardId)))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).delete(cardId);
    }
}
