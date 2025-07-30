package com.example.bankcards.service;

import com.example.bankcards.core.dto.user.UserDto;
import com.example.bankcards.core.dto.user.UserPayload;
import com.example.bankcards.core.dto.user.UserUpdatePayload;

import java.util.UUID;

public interface UserService {
    void create(UserPayload payload);

    UserDto read(UUID userId);

    void update(UUID userId, UserUpdatePayload payload);
}
