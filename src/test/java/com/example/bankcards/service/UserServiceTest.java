package com.example.bankcards.service;

import com.example.bankcards.core.dto.user.UserPayload;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private UserRepository repo;

    @Mock
    private UserMapper mapper;

    @Test
    void create__DoesNotThrowsException() {
        UserPayload payload = new UserPayload("username", "password");
        User mapped = User.builder().username("username").password("password").build();
        User expected = User.builder().username("username").password("password").build();
        UUID id = UUID.randomUUID();

        when(repo.existsByUsername("username")).thenReturn(false);
        when(mapper.create(payload)).thenReturn(expected);
        when(repo.save(User.builder().username("username").password("password").build())).thenReturn(
                User.builder().id(id).username("username").password("password").build());

        assertDoesNotThrow(() -> service.create(payload));

        verify(repo, times(1)).existsByUsername(payload.username());
        verify(mapper, times(1)).create(payload);
        verify(repo, times(1)).save(mapped);
    }
}
