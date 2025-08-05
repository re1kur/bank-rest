package com.example.bankcards.controller;

import com.example.bankcards.core.dto.balance.BalanceDto;
import com.example.bankcards.core.dto.balance.BalanceUpdatePayload;
import com.example.bankcards.core.exception.BalanceNotFoundException;
import com.example.bankcards.service.BalanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BalanceController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BalanceControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    BalanceService service;

    private static final String URI = "/api/v1/balances";

    @Test
    void read__ReturnsDtoAndOk() throws Exception {
        UUID cardId = UUID.randomUUID();
        BalanceDto expected = new BalanceDto(cardId, new BigDecimal("200.1021"), false);

        when(service.read(cardId)).thenReturn(new BalanceDto(cardId, new BigDecimal("200.1021"), false));

        mvc.perform(get(URI + "/%s".formatted(cardId)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));


        verify(service, times(1)).read(cardId);
    }

    @Test
    void read__BalanceNotFound__ReturnsBadRequest() throws Exception {
        UUID cardId = UUID.randomUUID();

        when(service.read(cardId)).thenThrow(BalanceNotFoundException.class);

        mvc.perform(get(URI + "/%s".formatted(cardId)))
                .andExpect(status().isBadRequest());


        verify(service, times(1)).read(cardId);
    }

    @Test
    void update__ReturnsNoContent() throws Exception {
        UUID cardId = UUID.randomUUID();
        BalanceUpdatePayload payload = new BalanceUpdatePayload(new BigDecimal("200"), false);

        doNothing().when(service).update(cardId, new BalanceUpdatePayload(new BigDecimal("200"), false));

        mvc.perform(put(URI + "/%s".formatted(cardId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isNoContent());

        verify(service, times(1)).update(cardId, payload);
    }

    @Test
    void update__BalanceNotFound__ReturnsBadRequest() throws Exception {
        UUID cardId = UUID.randomUUID();
        BalanceUpdatePayload payload = new BalanceUpdatePayload(new BigDecimal("200"), false);

        doThrow(BalanceNotFoundException.class).when(service).update(cardId, new BalanceUpdatePayload(new BigDecimal("200"), false));

        mvc.perform(put(URI + "/%s".formatted(cardId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).update(cardId, payload);
    }
}
