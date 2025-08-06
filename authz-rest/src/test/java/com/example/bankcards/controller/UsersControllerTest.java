package com.example.bankcards.controller;

import com.example.bankcards.core.dto.PageDto;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        UserPayload payload = new UserPayload("username", "password", List.of(1));

        UUID userId = UUID.randomUUID();
        when(service.create(payload)).thenReturn(userId);

        mvc.perform(post(URI).contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userId)));

        verify(service, times(1)).create(payload);
    }

    @Test
    void create__BlankUsername__ReturnsBadRequest() throws Exception {
        UserPayload payload = new UserPayload("", "password", List.of(1));

        mvc.perform(post(URI).contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void create__BlankPassword__ReturnsBadRequest() throws Exception {
        UserPayload payload = new UserPayload("username", "", List.of(1));

        mvc.perform(post(URI).contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void create__UsernameExists__ThrowsConflict() throws Exception {
        UserPayload payload = new UserPayload("username", "password", List.of(1));

        doThrow(UserAlreadyExistsException.class).when(service).create(payload);

        mvc.perform(post(URI).contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isConflict());

        verify(service, times(1)).create(payload);
    }

    @Test
    void read__ReturnsOkWithUserDto() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDto expected = UserDto.builder().username("username").roles(List.of("USER")).build();

        when(service.read(userId)).thenReturn(UserDto.builder().username("username").roles(List.of("USER")).build());

        mvc.perform(get(URI + "/%s".formatted(userId)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));


        verify(service, times(1)).read(userId);
    }

    @Test
    void read__UserNotFound__ReturnsBadRequest() throws Exception {
        UUID userId = UUID.randomUUID();

        when(service.read(userId)).thenThrow(UserNotFoundException.class);

        mvc.perform(get(URI + "/%s".formatted(userId)))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).read(userId);
    }

    @Test
    void update__ReturnsNoContent() throws Exception {
        UUID userId = UUID.randomUUID();
        UserUpdatePayload payload = new UserUpdatePayload("username", List.of(1, 2));

        doNothing().when(service).update(userId, new UserUpdatePayload("username", List.of(1, 2)));

        mvc.perform(put(URI + "/%s".formatted(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isNoContent());

        verify(service, times(1)).update(userId, payload);
    }

    @Test
    void update__BlankUsername__ReturnsBadRequest() throws Exception {
        UUID userId = UUID.randomUUID();
        UserUpdatePayload payload = new UserUpdatePayload("", List.of(1, 2));

        mvc.perform(put(URI + "/%s".formatted(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void update__EmptyList__ReturnsBadRequest() throws Exception {
        UUID userId = UUID.randomUUID();
        UserUpdatePayload payload = new UserUpdatePayload("username", List.of());

        mvc.perform(put(URI + "/%s".formatted(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void update__UserNotFound__ReturnsBadRequest() throws Exception {
        UUID userId = UUID.randomUUID();
        UserUpdatePayload payload = new UserUpdatePayload("username", List.of(1, 2));

        doThrow(UserNotFoundException.class).when(service).update(userId, new UserUpdatePayload("username",
                List.of(1, 2)));

        mvc.perform(put(URI + "/%s".formatted(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).update(userId, payload);
    }

    @Test
    void update__UsernameIsOccupied__ReturnsConflict() throws Exception {
        UUID userId = UUID.randomUUID();
        UserUpdatePayload payload = new UserUpdatePayload("username", List.of(1, 2));

        doThrow(UserAlreadyExistsException.class).when(service).update(userId, new UserUpdatePayload("username",
                List.of(1, 2)));

        mvc.perform(put(URI + "/%s".formatted(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isConflict());

        verify(service, times(1)).update(userId, payload);
    }

    @Test
    void delete__ReturnsNoContent() throws Exception {
        UUID userId = UUID.randomUUID();

        doNothing().when(service).delete(userId);

        mvc.perform(delete(URI + "/%s".formatted(userId)))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(userId);
    }

    @Test
    void delete__UserNotFound__ReturnsBadRequest() throws Exception {
        UUID userId = UUID.randomUUID();

        doThrow(UserNotFoundException.class).when(service).delete(userId);

        mvc.perform(delete(URI + "/%s".formatted(userId)))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).delete(userId);
    }

    @Test
    void readList__ReturnsListAndOk() throws Exception {
        Integer page = 0;
        Integer size = 5;
        PageDto<UserDto> expected = new PageDto<>(List.of(UserDto.builder().username("username1").build(),
                UserDto.builder().username("username2").build()), 0, 5, 1, false, false);

        Pageable pageable = PageRequest.of(page, size);

        when(service.readAll(PageRequest.of(page, size))).thenReturn(new PageDto<>(List.of(UserDto.builder().username("username1").build(),
                UserDto.builder().username("username2").build()), 0, 5, 1, false, false));

        mvc.perform(get(URI)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));

        verify(service, times(1)).readAll(pageable);
    }
}
