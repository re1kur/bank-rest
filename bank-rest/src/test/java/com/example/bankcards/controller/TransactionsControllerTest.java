package com.example.bankcards.controller;

import com.example.bankcards.core.dto.TransactionDto;
import com.example.bankcards.core.dto.transaction.TransactionPayload;
import com.example.bankcards.core.exception.CardNotFoundException;
import com.example.bankcards.core.exception.TransactionNotFoundException;
import com.example.bankcards.service.TransactionService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransactionsController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TransactionsControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    TransactionService service;

    private static final String URI = "/api/v1/transactions";

    @Test
    void create__ReturnsOk() throws Exception {
        UUID senderCardId = UUID.randomUUID();
        UUID receiverCardId = UUID.randomUUID();

        TransactionPayload payload = new TransactionPayload(senderCardId, receiverCardId, new BigDecimal(200));

        mvc.perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(service, times(1)).create(payload);
    }

    @Test
    void create__CardNotFound__Exception() throws Exception {
        UUID senderCardId = UUID.randomUUID();
        UUID receiverCardId = UUID.randomUUID();

        TransactionPayload payload = new TransactionPayload(senderCardId, receiverCardId, new BigDecimal(200));

        doThrow(CardNotFoundException.class).when(service).create(payload);

        mvc.perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).create(payload);
    }

    @Test
    void create__NullReceiver__ReturnsBadRequest() throws Exception {
        UUID senderCardId = UUID.randomUUID();

        TransactionPayload payload = new TransactionPayload(senderCardId, null, new BigDecimal(200));

        mvc.perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void create__NullSender__ReturnsBadRequest() throws Exception {
        UUID receiverCardId = UUID.randomUUID();

        TransactionPayload payload = new TransactionPayload(null, receiverCardId, new BigDecimal(200));

        mvc.perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void create__NegativeAmount__ReturnsBadRequest() throws Exception {
        UUID receiverCardId = UUID.randomUUID();
        UUID senderCardId = UUID.randomUUID();

        TransactionPayload payload = new TransactionPayload(senderCardId, receiverCardId, new BigDecimal(-100));

        mvc.perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void read__ReturnsDtoAndOk() throws Exception {
        UUID transactionId = UUID.randomUUID();
        TransactionDto expected = TransactionDto.builder().id(transactionId).build();

        when(service.read(transactionId)).thenReturn(TransactionDto.builder().id(transactionId).build());

        mvc.perform(get(URI + "/%s".formatted(transactionId)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));

        verify(service, times(1)).read(transactionId);
    }

    @Test
    void read__TransactionNotFound__ReturnsBadRequest() throws Exception {
        UUID transactionId = UUID.randomUUID();

        when(service.read(transactionId)).thenThrow(TransactionNotFoundException.class);

        mvc.perform(get(URI + "/%s".formatted(transactionId)))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).read(transactionId);
    }

    @Test
    void update__ReturnsOk() {

    }
}
