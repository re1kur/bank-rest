package com.example.bankcards.controller;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.role.RoleDto;
import com.example.bankcards.core.dto.role.RolePayload;
import com.example.bankcards.core.dto.role.RoleUpdatePayload;
import com.example.bankcards.core.exception.RoleAlreadyExistsException;
import com.example.bankcards.core.exception.RoleNotFoundException;
import com.example.bankcards.service.RoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RolesController.class)
public class RolesControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private RoleService service;

    private static final String URI = "/api/v1/roles";

    @Test
    void create__ReturnsOk() throws Exception {
        RolePayload payload = new RolePayload("name");

        when(service.create(new RolePayload("name"))).thenReturn(1);

        mvc.perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(service, times(1)).create(payload);
    }

    @Test
    void create__RoleNameIsOccupied__ReturnsConflict() throws Exception {
        RolePayload payload = new RolePayload("name");

        when(service.create(new RolePayload("name"))).thenThrow(RoleAlreadyExistsException.class);

        mvc.perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status()
                        .isConflict());

        verify(service, times(1)).create(payload);
    }

    @Test
    void read__ReturnsDtoAndOk() throws Exception {
        int roleId = 1;
        RoleDto expected = RoleDto.builder().id(1).name("name").build();

        when(service.read(1)).thenReturn(RoleDto.builder().id(1).name("name").build());

        mvc.perform(get(URI + "/%d".formatted(roleId)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));

        verify(service, times(1)).read(roleId);
    }

    @Test
    void read__RoleNotFound__ReturnsBadRequest() throws Exception {
        int roleId = 1;

        when(service.read(1)).thenThrow(RoleNotFoundException.class);

        mvc.perform(get(URI + "/%d".formatted(roleId)))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).read(roleId);
    }

    @Test
    void update__ReturnsNoContent() throws Exception {
        int roleId = 1;
        RoleUpdatePayload payload = new RoleUpdatePayload("newName");

        doNothing().when(service).update(1, new RoleUpdatePayload("newName"));

        mvc.perform(put(URI + "/%d".formatted(roleId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isNoContent());

        verify(service, times(1)).update(roleId, payload);
    }

    @Test
    void update__RoleNotFound__ReturnsBadRequest() throws Exception {
        int roleId = 1;
        RoleUpdatePayload payload = new RoleUpdatePayload("newName");

        doThrow(RoleNotFoundException.class).when(service).update(1, new RoleUpdatePayload("newName"));

        mvc.perform(put(URI + "/%d".formatted(roleId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).update(roleId, payload);
    }

    @Test
    void update__NewRoleNameIsOccupied__ReturnsConflict() throws Exception {
        int roleId = 1;
        RoleUpdatePayload payload = new RoleUpdatePayload("newName");

        doThrow(RoleAlreadyExistsException.class).when(service).update(1, new RoleUpdatePayload("newName"));

        mvc.perform(put(URI + "/%d".formatted(roleId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isConflict());

        verify(service, times(1)).update(roleId, payload);
    }

    @Test
    void delete__ReturnsNoContent() throws Exception {
        int roleId = 1;

        doNothing().when(service).delete(1);

        mvc.perform(delete(URI + "/%d".formatted(roleId)))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(roleId);
    }

    @Test
    void delete__RoleNotFound__ReturnsBadRequest() throws Exception {
        int roleId = 1;

        doThrow(RoleNotFoundException.class).when(service).delete(1);

        mvc.perform(delete(URI + "/%d".formatted(roleId)))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).delete(roleId);
    }

    @Test
    void getList__ReturnsPageDtoAndOk() throws Exception {
        Integer page = 0;
        Integer size = 5;
        PageDto<RoleDto> expected = new PageDto<>(List.of(RoleDto.builder().id(1).name("name1").build(),
                RoleDto.builder().id(2).name("name2").build()), 0, 5, 1, false, false);

        Pageable pageable = PageRequest.of(page, size);

        when(service.readAll(PageRequest.of(page, size))).thenReturn(new PageDto<>(List.of(RoleDto.builder().id(1).name("name1").build(),
                RoleDto.builder().id(2).name("name2").build()), 0, 5, 1, false, false));

        mvc.perform(get(URI)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));

        verify(service, times(1)).readAll(pageable);
    }
}
