package com.example.bankcards.mapper;

import com.example.bankcards.core.dto.user.RoleEnum;
import com.example.bankcards.core.dto.user.UserDto;
import com.example.bankcards.core.dto.user.UserPayload;
import com.example.bankcards.core.dto.user.UserUpdatePayload;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.impl.UserMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {
    @InjectMocks
    private UserMapperImpl userMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void create_ShouldMapPayloadToUserEntity() {
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";
        UserPayload payload = new UserPayload("testuser", rawPassword);

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        User user = userMapper.create(payload);

        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPassword()).isEqualTo(encodedPassword);
        assertThat(user.getRoles()).containsExactly(RoleEnum.USER);
    }

    @Test
    void read_ShouldMapUserEntityToUserDto() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .id(id)
                .username("john_doe")
                .roles(List.of(RoleEnum.USER, RoleEnum.ADMIN))
                .build();

        UserDto dto = userMapper.read(user);

        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.username()).isEqualTo("john_doe");
        assertThat(dto.roles()).containsExactly(RoleEnum.USER, RoleEnum.ADMIN);
    }

    @Test
    void update_ShouldModifyUserFields() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("old_username")
                .roles(List.of(RoleEnum.USER))
                .build();

        UserUpdatePayload payload = new UserUpdatePayload("new_username", List.of(RoleEnum.ADMIN));

        User updatedUser = userMapper.update(user, payload);

        assertThat(updatedUser.getUsername()).isEqualTo("new_username");
        assertThat(updatedUser.getRoles()).containsExactly(RoleEnum.ADMIN);
        assertThat(updatedUser).isSameAs(user);
    }
}