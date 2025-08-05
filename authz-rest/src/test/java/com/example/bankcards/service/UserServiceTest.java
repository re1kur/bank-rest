package com.example.bankcards.service;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.user.UserDto;
import com.example.bankcards.core.dto.user.UserPayload;
import com.example.bankcards.core.dto.user.UserUpdatePayload;
import com.example.bankcards.core.exception.UserAlreadyExistsException;
import com.example.bankcards.core.exception.UserNotFoundException;
import com.example.bankcards.entity.sql.Role;
import com.example.bankcards.entity.sql.User;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.sql.UserRepository;
import com.example.bankcards.service.impl.UserServiceImpl;
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

    static Role role(String name) {
        return Role.builder().name(name).build();
    }

    @Test
    void create__DoesNotThrowsException() {
        UserPayload payload = new UserPayload("username", "password", List.of(1));
        User expected = User.builder().username("username").password("password").build();
        UUID id = UUID.randomUUID();

        when(repo.existsByUsername("username")).thenReturn(false);
        when(mapper.create(payload)).thenReturn(expected);
        when(repo.save(expected)).thenReturn(
                User.builder().id(id).username("username").password("password").build());

        assertDoesNotThrow(() -> service.create(payload));

        verify(repo, times(1)).existsByUsername(payload.username());
        verify(mapper, times(1)).create(payload);
        verify(repo, times(1)).save(expected);
    }

    @Test
    void create__UsernameIsOccupied__ThrowsException() {
        UserPayload payload = new UserPayload("username", "password", List.of(1));

        when(repo.existsByUsername("username")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> service.create(payload));

        verify(repo, times(1)).existsByUsername(payload.username());
        verifyNoMoreInteractions(repo);
        verifyNoInteractions(mapper);
    }

    @Test
    void get__ReturnsUserDto() {
        UUID userId = UUID.randomUUID();
        Role userRole = role("USER");
        User found = User.builder().id(userId).username("username").roles(List.of(userRole)).build();
        UserDto expected = UserDto.builder().id(userId).username("username").roles(List.of("USER")).build();

        when(repo.findById(userId)).thenReturn(Optional.of(found));
        when(mapper.read(found)).thenReturn(expected);

        UserDto result = assertDoesNotThrow(() -> service.read(userId));
        assertEquals(expected, result);

        verify(repo, times(1)).findById(userId);
        verify(mapper, times(1)).read(found);
    }

    @Test
    void get__UserNotFound__ThrowsException() {
        UUID userId = UUID.randomUUID();

        when(repo.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.read(userId));

        verify(repo, times(1)).findById(userId);
        verifyNoInteractions(mapper);
    }

    @Test
    void update__DoesNotThrowException() {
        UUID userId = UUID.randomUUID();
        Role userRole = role("USER");
        Role adminRole = role("ADMIN");

        UserUpdatePayload payload = new UserUpdatePayload("username", List.of(1, 2));
        User found = User.builder()
                .id(userId)
                .username("username")
                .roles(List.of(userRole, adminRole))
                .build();

        User updated = User.builder()
                .id(userId)
                .username("username")
                .roles(List.of(userRole, adminRole))
                .build();

        when(repo.findById(userId)).thenReturn(Optional.of(found));
        when(mapper.update(found, payload)).thenReturn(updated);

        assertDoesNotThrow(() -> service.update(userId, payload));

        verify(repo, times(1)).findById(userId);
        verify(mapper, times(1)).update(found, payload);
        verify(repo, times(1)).save(updated);
    }

    @Test
    void update__NewUsername__DoesNotThrowException() {
        UUID userId = UUID.randomUUID();
        Role userRole = role("USER");
        Role adminRole = role("ADMIN");

        UserUpdatePayload payload = new UserUpdatePayload("newUsername", List.of(1, 2));
        User existing = User.builder()
                .id(userId)
                .username("username")
                .roles(List.of(userRole, adminRole))
                .build();

        User updated = User.builder()
                .id(userId)
                .username("newUsername")
                .roles(List.of(userRole, adminRole))
                .build();

        when(repo.findById(userId)).thenReturn(Optional.of(existing));
        when(repo.existsByUsername("newUsername")).thenReturn(false);
        when(mapper.update(existing, payload)).thenReturn(updated);

        assertDoesNotThrow(() -> service.update(userId, payload));

        verify(repo, times(1)).findById(userId);
        verify(repo, times(1)).existsByUsername(payload.username());
        verify(mapper, times(1)).update(existing, payload);
        verify(repo, times(1)).save(updated);
    }

    @Test
    void update__UserNotFound__ThrowsException() {
        UUID userId = UUID.randomUUID();
        UserUpdatePayload payload = new UserUpdatePayload("username", List.of(1, 2));

        when(repo.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.update(userId, payload));

        verify(repo, times(1)).findById(userId);
        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void update__UsernameOccupied__ThrowsException() {
        UUID userId = UUID.randomUUID();
        Role userRole = role("USER");

        UserUpdatePayload payload = new UserUpdatePayload("newUsername", List.of(1));
        User found = User.builder().id(userId).username("username").roles(List.of(userRole)).build();

        when(repo.findById(userId)).thenReturn(Optional.of(found));
        when(repo.existsByUsername("newUsername")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> service.update(userId, payload));

        verify(repo, times(1)).findById(userId);
        verify(repo, times(1)).existsByUsername(payload.username());
        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void delete__DoesNotThrowException() {
        UUID userId = UUID.randomUUID();
        User expected = User.builder().id(userId).build();

        when(repo.findById(userId)).thenReturn(Optional.of(expected));
        doNothing().when(repo).delete(expected);

        assertDoesNotThrow(() -> service.delete(userId));

        verify(repo, times(1)).findById(userId);
        verify(repo, times(1)).delete(expected);
    }

    @Test
    void delete__UserNotFound__ThrowsException() {
        UUID userId = UUID.randomUUID();

        when(repo.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.delete(userId));

        verify(repo, times(1)).findById(userId);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void readAll__ReturnsPageDto() {
        int page = 0;
        int size = 5;
        PageDto<UserDto> expected = new PageDto<>(List.of(UserDto.builder().username("username1").build(),
                UserDto.builder().username("username2").build()), 0, 5, 1, false, false);
        Pageable pageable = PageRequest.of(page, size);
        Page<User> expectedFound = new PageImpl<>(List.of(User.builder().username("username1").build(),
                User.builder().username("username2").build()), pageable, 1);

        when(repo.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(User.builder().username("username1").build(),
                        User.builder().username("username2").build()), pageable, 1));

        when(mapper.readPage(new PageImpl<>(List.of(User.builder().username("username1").build(),
                User.builder().username("username2").build()), pageable, 1)))
                .thenReturn(new PageDto<>(List.of(UserDto.builder().username("username1").build(),
                        UserDto.builder().username("username2").build()), 0, 5, 1, false, false));

        PageDto<UserDto> result = assertDoesNotThrow(() -> service.readAll(pageable));

        assertEquals(expected, result);

        verify(repo, times(1)).findAll(pageable);
        verify(mapper, times(1)).readPage(expectedFound);
    }
}
