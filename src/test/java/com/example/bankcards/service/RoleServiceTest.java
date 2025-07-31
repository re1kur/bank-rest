package com.example.bankcards.service;

import com.example.bankcards.core.exception.RoleNotFoundException;
import com.example.bankcards.entity.Role;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleServiceImpl service;

    @Mock
    private RoleRepository repo;


    @Test
    void get__DoesNotThrowsException() {
        String roleName = "roleName";
        Role expected = Role.builder().name("roleName").build();

        when(repo.findByName(roleName)).thenReturn(Optional.of(Role.builder().name("roleName").build()));

        Role result = assertDoesNotThrow(() -> service.get(roleName));

        assertEquals(expected, result);

        verify(repo, times(1)).findByName(roleName);
    }

    @Test
    void get__RoleNotFound__ThrowsException() {
        String roleName = "roleName";

        when(repo.findByName(roleName)).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> service.get(roleName));

        verifyNoInteractions(repo);
    }

}