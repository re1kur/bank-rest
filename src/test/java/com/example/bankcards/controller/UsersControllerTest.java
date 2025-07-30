package com.example.bankcards.controller;

import com.example.bankcards.core.dto.user.RoleEnum;
import com.example.bankcards.core.dto.user.UserDto;
import com.example.bankcards.core.dto.user.UserPayload;
import com.example.bankcards.core.dto.user.UserUpdatePayload;
import com.example.bankcards.core.exception.UserAlreadyExistsException;
import com.example.bankcards.core.exception.UserNotFoundException;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UsersController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UsersControllerTest {
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

    @Test
    void get__ReturnsOkWithUserDto() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDto expected = UserDto.builder().username("username").roles(List.of(RoleEnum.USER)).build();

        when(service.read(userId)).thenReturn(UserDto.builder().username("username").roles(List.of(RoleEnum.USER)).build());

        mvc.perform(get(URI + "/%s".formatted(userId)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

    @Test
    void get__UserNotFound__ReturnsBadRequest() throws Exception {
        UUID userId = UUID.randomUUID();

        when(service.read(userId)).thenThrow(UserNotFoundException.class);

        mvc.perform(get(URI + "/%s".formatted(userId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update__ReturnsNoContent() throws Exception {
        UUID userId = UUID.randomUUID();
        UserUpdatePayload payload = new UserUpdatePayload("username", List.of(RoleEnum.USER, RoleEnum.ADMIN));

        doNothing().when(service).update(userId, new UserUpdatePayload("username", List.of(RoleEnum.USER, RoleEnum.ADMIN)));

        mvc.perform(put(URI + "/%s".formatted(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isNoContent());
    }

    @Test
    void update__BlankUsername__ReturnsBadRequest() throws Exception {
        UUID userId = UUID.randomUUID();
        UserUpdatePayload payload = new UserUpdatePayload("", List.of(RoleEnum.USER, RoleEnum.ADMIN));

        mvc.perform(put(URI + "/%s".formatted(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update__EmptyList__ReturnsBadRequest() throws Exception {
        UUID userId = UUID.randomUUID();
        UserUpdatePayload payload = new UserUpdatePayload("username", List.of());

        mvc.perform(put(URI + "/%s".formatted(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update__UserNotFound__ReturnsBadRequest() throws Exception {
        UUID userId = UUID.randomUUID();
        UserUpdatePayload payload = new UserUpdatePayload("username", List.of(RoleEnum.USER, RoleEnum.ADMIN));

        doThrow(UserNotFoundException.class).when(service).update(userId, new UserUpdatePayload("username", List.of(RoleEnum.USER, RoleEnum.ADMIN)));

        mvc.perform(put(URI + "/%s".formatted(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update__UsernameIsOccupied__ReturnsConflict() throws Exception {
        UUID userId = UUID.randomUUID();
        UserUpdatePayload payload = new UserUpdatePayload("username", List.of(RoleEnum.USER, RoleEnum.ADMIN));

        doThrow(UserAlreadyExistsException.class).when(service).update(userId, new UserUpdatePayload("username", List.of(RoleEnum.USER, RoleEnum.ADMIN)));

        mvc.perform(put(URI + "/%s".formatted(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isConflict());
    }
}
