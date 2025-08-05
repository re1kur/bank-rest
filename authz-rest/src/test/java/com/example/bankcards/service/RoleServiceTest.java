package com.example.bankcards.service;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.role.RoleDto;
import com.example.bankcards.core.dto.role.RolePayload;
import com.example.bankcards.core.dto.role.RoleUpdatePayload;
import com.example.bankcards.core.exception.RoleAlreadyExistsException;
import com.example.bankcards.core.exception.RoleNotFoundException;
import com.example.bankcards.entity.sql.Role;
import com.example.bankcards.mapper.RoleMapper;
import com.example.bankcards.repository.sql.RoleRepository;
import com.example.bankcards.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleServiceImpl service;

    @Mock
    private RoleRepository repo;

    @Mock
    private RoleMapper mapper;


    @Test
    void get__ById__DoesNotThrowsException() {
        String roleName = "roleName";
        Role expected = Role.builder().name("roleName").build();

        when(repo.findByName("roleName")).thenReturn(Optional.of(Role.builder().name("roleName").build()));

        Role result = assertDoesNotThrow(() -> service.getByName(roleName));

        assertEquals(expected, result);

        verify(repo, times(1)).findByName(roleName);
    }

    @Test
    void get__ById__RoleNotFound__ThrowsException() {
        String roleName = "roleName";

        when(repo.findByName("roleName")).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> service.getByName(roleName));

        verify(repo, times(1)).findByName(roleName);
    }

    @Test
    void create__DoesNotThrowException() {
        RolePayload payload = new RolePayload("name");
        Role expectedMapped = Role.builder().name("name").build();
        Role expected  = Role.builder().id(1).name("name").build();

        when(repo.existsByName(payload.name())).thenReturn(false);
        when(mapper.create(new RolePayload("name"))).thenReturn(Role.builder().name("name").build());
        when(repo.save(Role.builder().name("name").build())).thenReturn(Role.builder().id(1).name("name").build());

        Integer result = assertDoesNotThrow(() -> service.create(payload));
        assertEquals(expected.getId(), result);

        verify(repo, times(1)).existsByName(payload.name());
        verify(mapper, times(1)).create(payload);
        verify(repo, times(1)).save(expectedMapped);
    }

    @Test
    void read__DoesNotThrowsExceptionAndReturnsDto() {
        Integer roleId = 1;
        Role expectedFound = Role.builder().id(1).name("name").build();
        RoleDto expected = RoleDto.builder().id(1).name("name").build();

        when(repo.findById(roleId)).thenReturn(Optional.of(Role.builder().id(1).name("name").build()));

        when(mapper.read(Role.builder().id(1).name("name").build())).thenReturn(RoleDto.builder().id(1).name("name").build());

        RoleDto result = assertDoesNotThrow(() -> service.read(roleId));
        assertEquals(expected, result);

        verify(repo, times(1)).findById(roleId);

        verify(mapper, times(1)).read(expectedFound);
    }

    @Test
    void read__RoleNotFound__ThrowsException() {
        Integer roleId = 1;

        when(repo.findById(roleId)).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> service.read(roleId));

        verify(repo, times(1)).findById(roleId);

        verifyNoInteractions(mapper);
    }

    @Test
    void update__DoesNotThrowsException() {
        int roleId = 1;
        RoleUpdatePayload payload = new RoleUpdatePayload("newName");
        Role expectedFound = Role.builder().id(1).name("name").build();
        Role expectedMapped = Role.builder().id(1).name("newName").build();

        when(repo.findById(1)).thenReturn(Optional.of(Role.builder().id(1).name("name").build()));
        when(repo.existsByName("newName")).thenReturn(false);
        when(mapper.update(Role.builder().id(1).name("name").build(), payload)).thenReturn(Role.builder().id(1).name("newName").build());
        when(repo.save(Role.builder().id(1).name("newName").build())).thenReturn(Role.builder().id(1).name("newName").build());

        assertDoesNotThrow(() -> service.update(roleId, payload));

        verify(repo, times(1)).findById(roleId);
        verify(mapper, times(1)).update(expectedFound, payload);
        verify(repo, times(1)).save(expectedMapped);
    }

    @Test
    void update__RoleNotFound__ThrowsException() {
        int roleId = 1;
        RoleUpdatePayload payload = new RoleUpdatePayload("newName");

        when(repo.findById(1)).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> service.update(roleId, payload));

        verify(repo, times(1)).findById(roleId);
        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void update__NewRoleNameIsOccupied__ThrowsException() {
        int roleId = 1;
        RoleUpdatePayload payload = new RoleUpdatePayload("newName");

        when(repo.findById(1)).thenReturn(Optional.of(Role.builder().id(1).name("name").build()));
        when(repo.existsByName("newName")).thenReturn(true);

        assertThrows(RoleAlreadyExistsException.class, () -> service.update(roleId, payload));

        verify(repo, times(1)).findById(roleId);
        verify(repo, times(1)).existsByName(payload.name());
        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void delete__DoesNotThrowsException() {
        int roleId = 1;
        Role expectedFound = Role.builder().id(1).name("name").build();

        when(repo.findById(1)).thenReturn(Optional.of(Role.builder().id(1).name("name").build()));
        doNothing().when(repo).delete(Role.builder().id(1).name("name").build());

        assertDoesNotThrow(() -> service.delete(roleId));

        verify(repo, times(1)).findById(roleId);
        verify(repo, times(1)).delete(expectedFound);
    }

    @Test
    void delete__RoleNotFound__ThrowsException() {
        int roleId = 1;

        when(repo.findById(1)).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> service.delete(roleId));

        verify(repo, times(1)).findById(roleId);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void readAll__ReturnsPageDto() {
        int page = 0;
        int size = 5;
        PageDto<RoleDto> expected = new PageDto<>(List.of(RoleDto.builder().id(1).name("name1").build(),
                RoleDto.builder().id(2).name("name2").build()), 0, 5, 1, false, false);
        Pageable pageable = PageRequest.of(page, size);
        Page<Role> expectedFound = new PageImpl<>(List.of(Role.builder().id(1).name("name1").build(),
                Role.builder().id(2).name("name2").build()), pageable, 1);

        when(repo.findAll(PageRequest.of(page, size)))
                .thenReturn(new PageImpl<>(List.of(Role.builder().id(1).name("name1").build(),
                        Role.builder().id(2).name("name2").build()), pageable, 1));

        when(mapper.readPage(new PageImpl<>(List.of(Role.builder().id(1).name("name1").build(),
                Role.builder().id(2).name("name2").build()), pageable, 1)))
                .thenReturn(new PageDto<>(List.of(RoleDto.builder().id(1).name("name1").build(),
                        RoleDto.builder().id(2).name("name2").build()), 0, 5, 1, false, false));

        PageDto<RoleDto> result = assertDoesNotThrow(() -> service.readAll(pageable));

        assertEquals(expected, result);

        verify(repo, times(1)).findAll(pageable);
        verify(mapper, times(1)).readPage(expectedFound);
    }
}