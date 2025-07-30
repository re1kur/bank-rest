package com.example.bankcards.mapper;

import com.example.bankcards.core.dto.user.UserDto;
import com.example.bankcards.core.dto.user.UserPayload;
import com.example.bankcards.core.dto.user.UserUpdatePayload;
import com.example.bankcards.entity.User;

public interface UserMapper {
    User create(UserPayload payload);

    UserDto read(User user);

    User update(User user, UserUpdatePayload payload);
}
