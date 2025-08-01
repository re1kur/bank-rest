package com.example.bankcards.mapper.impl;

import com.example.bankcards.core.annotation.Mapper;
import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.user.UserDto;
import com.example.bankcards.core.dto.user.UserPayload;
import com.example.bankcards.core.dto.user.UserUpdatePayload;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;


@Mapper
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {
    private final BCryptPasswordEncoder encoder;
    private final RoleService roleService;

    @Override
    public User create(UserPayload payload) {
        Role userRole = roleService.get("USER");
        return User.builder()
                .username(payload.username())
                .roles(List.of(userRole))
                .password(encoder.encode(payload.password()))
                .build();
    }

    @Override
    public UserDto read(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .build();
    }

    @Override
    public User update(User user, UserUpdatePayload payload) {
        List<Role> roles = payload.roleIds().stream().map(roleService::get).toList();

        user.setUsername(payload.username());
        user.setRoles(new ArrayList<>(roles));

        return user;
    }

    @Override
    public PageDto<UserDto> readPage(Page<User> pageUsers) {
        List<UserDto> content = pageUsers.map(this::read).toList();

        return new PageDto<>(content,
                pageUsers.getNumber(),
                pageUsers.getSize(),
                pageUsers.getTotalPages(),
                pageUsers.hasNext(),
                pageUsers.hasPrevious());
    }

}
