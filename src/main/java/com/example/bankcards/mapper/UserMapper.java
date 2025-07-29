package com.example.bankcards.mapper;

import com.example.bankcards.core.dto.user.UserPayload;
import com.example.bankcards.entity.User;

public interface UserMapper {
    User create(UserPayload payload);
}
