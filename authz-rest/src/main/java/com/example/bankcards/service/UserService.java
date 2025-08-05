package com.example.bankcards.service;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.user.UserDto;
import com.example.bankcards.core.dto.user.UserPayload;
import com.example.bankcards.core.dto.user.UserUpdatePayload;
import com.example.bankcards.entity.sql.User;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    void create(UserPayload payload);

    UserDto read(UUID userId);

    void update(UUID userId, UserUpdatePayload payload);

    void delete(UUID userId);

    User get(UUID userId);

    PageDto<UserDto> readAll(Pageable pageable);
}
