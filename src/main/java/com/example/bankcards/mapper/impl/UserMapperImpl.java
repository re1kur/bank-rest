package com.example.bankcards.mapper.impl;

import com.example.bankcards.core.annotation.Mapper;
import com.example.bankcards.core.dto.user.RoleEnum;
import com.example.bankcards.core.dto.user.UserDto;
import com.example.bankcards.core.dto.user.UserPayload;
import com.example.bankcards.core.dto.user.UserUpdatePayload;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Mapper
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {
    private final BCryptPasswordEncoder encoder;

    @Override
    public User create(UserPayload payload) {
        return User.builder()
                .username(payload.username())
                .roles(List.of(RoleEnum.USER))
                .password(encoder.encode(payload.password()))
                .build();
    }

    @Override
    public UserDto read(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roles(user.getRoles())
                .build();
    }

    @Override
    public User update(User user, UserUpdatePayload payload) {
        user.setUsername(payload.username());
        user.setRoles(payload.roles());

        return user;
    }
}
