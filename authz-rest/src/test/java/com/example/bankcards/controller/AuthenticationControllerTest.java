package com.example.bankcards.controller;

import com.example.bankcards.core.dto.auth.JwtPair;
import com.example.bankcards.core.dto.auth.LoginRequest;
import com.example.bankcards.core.dto.auth.RegisterRequest;
import com.example.bankcards.core.exception.BadCredentialsException;
import com.example.bankcards.core.exception.UserAlreadyExistsException;
import com.example.bankcards.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private AuthService service;

    private static final String URI = "/api/v1/auth";

    @Test
    void register__ReturnsOk() throws Exception {
        UUID userId = UUID.randomUUID();
        RegisterRequest request = new RegisterRequest("username", "password");

        when(service.register(request)).thenReturn(userId);

        mvc.perform(post(URI + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(service, times(1)).register(request);
    }

    @Test
    void register__UsernameIsOccupied__ReturnsConflict() throws Exception {
        RegisterRequest request = new RegisterRequest("username", "password");

        when(service.register(request)).thenThrow(UserAlreadyExistsException.class);

        mvc.perform(post(URI + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(service, times(1)).register(request);
    }

    @Test
    void login__ReturnsJWTAndOk() throws Exception {
        LoginRequest request = new LoginRequest("username", "password");
        JwtPair expected = new JwtPair("accessToken", "value");

        when(service.login(request)).thenReturn(new JwtPair("accessToken", "value"));

        mvc.perform(post(URI + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));

        verify(service, times(1)).login(request);
    }

    @Test
    void login__BadCredentials__ReturnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest("username", "password");

        when(service.login(request)).thenThrow(BadCredentialsException.class);

        mvc.perform(post(URI + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(service, times(1)).login(request);
    }
}
