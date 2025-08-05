package com.example.bankcards.mapper;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.user.UserDto;
import com.example.bankcards.core.dto.user.UserPayload;
import com.example.bankcards.core.dto.user.UserUpdatePayload;
import com.example.bankcards.entity.sql.Role;
import com.example.bankcards.entity.sql.User;
import com.example.bankcards.mapper.impl.UserMapperImpl;
import com.example.bankcards.service.RoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {
    @InjectMocks
    private UserMapperImpl userMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private RoleService roleService;

    @Test
    void create_ShouldMapPayloadToUserEntity() {
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";
        UserPayload payload = new UserPayload("testuser", rawPassword, List.of(1));
        Role expectedRole = Role.builder().name("USER").build();

        when(roleService.getByName("USER")).thenReturn(Role.builder().name("USER").build());
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        User user = userMapper.create(payload);

        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPassword()).isEqualTo(encodedPassword);
        assertThat(user.getRoles()).containsExactly(expectedRole);
    }

    @Test
    void read_ShouldMapUserEntityToUserDto() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .id(id)
                .username("john_doe")
                .roles(List.of(Role.builder().name("USER").build(), Role.builder().name("ADMIN").build()))
                .build();

        UserDto dto = userMapper.read(user);

        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.username()).isEqualTo("john_doe");
        assertThat(dto.roles()).containsExactly("USER", "ADMIN");
    }

    @Test
    void update_ShouldModifyUserFields() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("old_username")
                .roles(List.of(Role.builder().name("USER").build()))
                .build();
        Role admin = Role.builder().name("ADMIN").build();

        UserUpdatePayload payload = new UserUpdatePayload("new_username", List.of(2));

        when(roleService.getById(2)).thenReturn(Role.builder().name("ADMIN").build());
        User updatedUser = userMapper.update(user, payload);

        assertThat(updatedUser.getUsername()).isEqualTo("new_username");
        assertThat(updatedUser.getRoles()).containsExactly(admin);
        assertThat(updatedUser).isSameAs(user);
    }

    @Test
    void readPage__ShouldMapListEntitiesToPageDto() {
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);

        List<UserDto> expectedDtos = List.of(
                UserDto.builder().username("username1").roles(List.of("USER")).build(),
                UserDto.builder().username("username2").roles(List.of("USER")).build()
        );

        PageDto<UserDto> expected = new PageDto<>(expectedDtos, 0, 5, 1, false, false);

        User user1 = User.builder().username("username1").roles(List.of(Role.builder().name("USER").build())).build();
        User user2 = User.builder().username("username2").roles(List.of(Role.builder().name("USER").build())).build();

        List<User> users = List.of(user1, user2);
        Page<User> pageUsers = new PageImpl<>(users, pageable, 2);

        PageDto<UserDto> result = userMapper.readPage(pageUsers);

        assertEquals(expected, result);
    }
}