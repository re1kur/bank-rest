package com.example.bankcards.service;

import com.example.bankcards.core.dto.auth.LoginRequest;
import com.example.bankcards.core.dto.auth.JwtPair;
import com.example.bankcards.core.dto.auth.RegisterRequest;
import com.example.bankcards.core.exception.BadCredentialsException;
import com.example.bankcards.core.exception.UserAlreadyExistsException;
import com.example.bankcards.core.exception.UserNotFoundException;
import com.example.bankcards.entity.sql.User;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.sql.UserRepository;
import com.example.bankcards.service.impl.AuthServiceImpl;
import com.example.bankcards.util.JwtProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    private AuthServiceImpl service;

    @Mock
    private UserRepository repo;

    @Mock
    private UserMapper mapper;

    @Mock
    private JwtProvider jwtProvider;

    @Test
    void register__DoesNotThrowsException() {
        RegisterRequest request = new RegisterRequest("username", "password");
        User expected = User.builder().username("username").password("password").build();
        UUID id = UUID.randomUUID();


        User mockMapped = User.builder().username("username").password("password").build();

        when(repo.existsByUsername("username")).thenReturn(false);
        when(mapper.register(request)).thenReturn(mockMapped);
        when(repo.save(mockMapped)).thenReturn(
                User.builder().id(id).username("username").password("password").build());

        assertDoesNotThrow(() -> service.register(request));

        verify(repo, times(1)).existsByUsername(request.username());
        verify(mapper, times(1)).register(request);
        verify(repo, times(1)).save(expected);
    }

    @Test
    void register__UsernameIsOccupied__ThrowsException() {
        RegisterRequest request = new RegisterRequest("username", "password");

        when(repo.existsByUsername("username")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> service.register(request));

        verify(repo, times(1)).existsByUsername(request.username());
        verifyNoMoreInteractions(repo);
        verifyNoInteractions(mapper);
    }

    @Test
    void login__DoesNotThrowsException() {
        UUID userId = UUID.randomUUID();
        LoginRequest request = new LoginRequest("username", "password");
        User expectedFound = User.builder().id(userId).username("username").build();
        JwtPair expected = new JwtPair("accessToken", "value");

        User mockFound = User.builder().id(userId).username("username").build();

        when(repo.findByUsername("username")).thenReturn(Optional.of(mockFound));
        doNothing().when(mapper).login(mockFound, request);
        when(jwtProvider.provide(mockFound)).thenReturn(new JwtPair("accessToken", "value"));

        JwtPair result = assertDoesNotThrow(() -> service.login(request));
        assertEquals(expected, result);

        verify(repo, times(1)).findByUsername(request.username());
        verify(mapper, times(1)).login(expectedFound, request);
        verify(jwtProvider, times(1)).provide(expectedFound);
    }

    @Test
    void login__BadCredentials__ThrowsException() {
        UUID userId = UUID.randomUUID();
        LoginRequest request = new LoginRequest("username", "password");
        User expectedFound = User.builder().id(userId).username("username").build();

        User mockFound = User.builder().id(userId).username("username").build();

        when(repo.findByUsername("username")).thenReturn(Optional.of(mockFound));
        doThrow(BadCredentialsException.class).when(mapper).login(mockFound, request);

        assertThrows(BadCredentialsException.class, () -> service.login(request));

        verify(repo, times(1)).findByUsername(request.username());
        verify(mapper, times(1)).login(expectedFound, request);
        verifyNoInteractions(jwtProvider);
    }

    @Test
    void login__UserNotFound__ThrowsException() {
        LoginRequest request = new LoginRequest("username", "password");

        when(repo.findByUsername("username")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.login(request));

        verify(repo, times(1)).findByUsername(request.username());
        verifyNoInteractions(mapper, jwtProvider);
    }
}
