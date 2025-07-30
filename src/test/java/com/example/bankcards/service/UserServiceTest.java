package com.example.bankcards.service;

import com.example.bankcards.core.dto.user.RoleEnum;
import com.example.bankcards.core.dto.user.UserDto;
import com.example.bankcards.core.dto.user.UserPayload;
import com.example.bankcards.core.dto.user.UserUpdatePayload;
import com.example.bankcards.core.exception.UserAlreadyExistsException;
import com.example.bankcards.core.exception.UserNotFoundException;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void create__UsernameNotExists__ThrowsException() {
        UserPayload payload = new UserPayload("username", "password");

        when(repo.existsByUsername("username")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> service.create(payload));

        verify(repo, times(1)).existsByUsername(payload.username());
        verifyNoMoreInteractions(repo);
        verifyNoInteractions(mapper);
    }

    @Test
    void get__ReturnsUserDto() {
        UUID userId = UUID.randomUUID();
        User found = User.builder().id(userId).username("username").roles(List.of(RoleEnum.USER)).build();
        UserDto expected = UserDto.builder().username("username").roles(List.of(RoleEnum.USER)).build();

        when(repo.findById(userId)).thenReturn(
                Optional.of(
                        User.builder()
                                .id(userId)
                                .username("username")
                                .roles(List.of(RoleEnum.USER))
                                .build()));
        when(mapper.read(found)).thenReturn(
                UserDto.builder()
                        .username("username")
                        .roles(List.of(RoleEnum.USER))
                        .build());

        UserDto result = assertDoesNotThrow(() -> service.read(userId));
        assertEquals(expected, result);

        verify(repo, times(1)).findById(userId);
        verify(mapper, times(1)).read(found);
    }

    @Test
    void get__UserNotFound__ThrowsException() {
        UUID userId = UUID.randomUUID();

        when(repo.findById(userId)).thenReturn(
                Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.read(userId));

        verify(repo, times(1)).findById(userId);
        verifyNoInteractions(mapper);
    }

    @Test
    void update__DoesNotThrowException() {
        UUID userId = UUID.randomUUID();
        UserUpdatePayload payload = new UserUpdatePayload("username", List.of(RoleEnum.USER, RoleEnum.ADMIN));
        User expected = User.builder()
                .id(userId)
                .username("username")
                .roles(List.of(RoleEnum.USER, RoleEnum.ADMIN))
                .build();

        when(repo.findById(userId)).thenReturn(
                Optional.of(User.builder()
                        .id(userId)
                        .username("username")
                        .roles(List.of(RoleEnum.USER, RoleEnum.ADMIN))
                        .build()));

        when(mapper.update(
                User.builder()
                        .id(userId)
                        .username("username")
                        .roles(List.of(RoleEnum.USER, RoleEnum.ADMIN))
                        .build(),
                new UserUpdatePayload("username", List.of(RoleEnum.USER, RoleEnum.ADMIN))))
                .thenReturn(User.builder()
                        .id(userId)
                        .username("username")
                        .roles(List.of(RoleEnum.USER, RoleEnum.ADMIN))
                        .build());

        assertDoesNotThrow(() -> service.update(userId, payload));

        verify(repo, times(1)).findById(userId);
        verify(mapper, times(1)).update(expected, payload);
        verify(repo, times(1)).save(expected);
    }

    @Test
    void update__NewUsername__DoesNotThrowException() {
        UUID userId = UUID.randomUUID();
        UserUpdatePayload payload = new UserUpdatePayload("newUsername", List.of(RoleEnum.USER, RoleEnum.ADMIN));
        User expectedFound = User.builder()
                .id(userId)
                .username("username")
                .roles(List.of(RoleEnum.USER, RoleEnum.ADMIN))
                .build();
        User expectedMapped = User.builder()
                .id(userId)
                .username("newUsername")
                .roles(List.of(RoleEnum.USER, RoleEnum.ADMIN))
                .build();

        when(repo.findById(userId)).thenReturn(
                Optional.of(User.builder()
                        .id(userId)
                        .username("username")
                        .roles(List.of(RoleEnum.USER, RoleEnum.ADMIN))
                        .build()));

        when(repo.existsByUsername("newUsername")).thenReturn(false);

        when(mapper.update(
                User.builder()
                        .id(userId)
                        .username("username")
                        .roles(List.of(RoleEnum.USER, RoleEnum.ADMIN))
                        .build(),
                new UserUpdatePayload("newUsername", List.of(RoleEnum.USER, RoleEnum.ADMIN))))
                .thenReturn(User.builder()
                        .id(userId)
                        .username("newUsername")
                        .roles(List.of(RoleEnum.USER, RoleEnum.ADMIN))
                        .build());

        assertDoesNotThrow(() -> service.update(userId, payload));

        verify(repo, times(1)).findById(userId);
        verify(repo, times(1)).existsByUsername(payload.username());
        verify(mapper, times(1)).update(expectedFound, payload);
        verify(repo, times(1)).save(expectedMapped);
    }

    @Test
    void update__UserNotFound__ThrowsException() {
        UUID userId = UUID.randomUUID();
        UserUpdatePayload payload = new UserUpdatePayload("username", List.of(RoleEnum.USER, RoleEnum.ADMIN));

        when(repo.findById(userId)).thenReturn(
                Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.update(userId, payload));

        verify(repo, times(1)).findById(userId);
        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void update__UsernameOccupied__ThrowsException() {
        UUID userId = UUID.randomUUID();
        UserUpdatePayload payload = new UserUpdatePayload("newUsername", List.of(RoleEnum.USER, RoleEnum.ADMIN));

        when(repo.findById(userId)).thenReturn(
                Optional.of(User.builder()
                        .id(userId)
                        .username("username")
                        .roles(List.of(RoleEnum.USER, RoleEnum.ADMIN))
                        .build()));

        when(repo.existsByUsername("newUsername")).thenReturn(true);


        assertThrows(UserAlreadyExistsException.class, () -> service.update(userId, payload));


        verify(repo, times(1)).findById(userId);
        verify(repo, times(1)).existsByUsername(payload.username());
        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(repo);
    }
}
