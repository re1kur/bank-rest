package com.example.bankcards.controller;

import com.example.bankcards.core.dto.user.UserPayload;
import com.example.bankcards.core.exception.UserAlreadyExistsException;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private UserService service;

    private static final String URI = "/api/v1/users";

    @Test
    void create__ReturnsOk() throws Exception {
        UserPayload payload = new UserPayload("username", "password");

        doNothing().when(service).create(payload);

        mvc.perform(
                        post(URI).contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    @Test
    void create__BlankUsername__ReturnsBadRequest() throws Exception {
        UserPayload payload = new UserPayload("", "password");

        mvc.perform(
                        post(URI).contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create__BlankPassword__ReturnsBadRequest() throws Exception {
        UserPayload payload = new UserPayload("username", "");

        mvc.perform(
                        post(URI).contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create__UsernameExists__ThrowsConflict() throws Exception {
        UserPayload payload = new UserPayload("username", "password");

        doThrow(UserAlreadyExistsException.class).when(service).create(payload);

        mvc.perform(
                        post(URI).contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isConflict());
    }
}
