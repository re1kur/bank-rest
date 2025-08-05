package com.example.bankcards.mapper;

import com.example.bankcards.core.dto.PageDto;
import com.example.bankcards.core.dto.auth.LoginRequest;
import com.example.bankcards.core.dto.auth.RegisterRequest;
import com.example.bankcards.core.dto.user.UserDto;
import com.example.bankcards.core.dto.user.UserPayload;
import com.example.bankcards.core.dto.user.UserUpdatePayload;
import com.example.bankcards.entity.sql.User;
import org.springframework.data.domain.Page;

public interface UserMapper {
    User create(UserPayload payload);

    UserDto read(User user);

    User update(User user, UserUpdatePayload payload);

    PageDto<UserDto> readPage(Page<User> users);

    User register(RegisterRequest request);

    void login(User user, LoginRequest request);
}
