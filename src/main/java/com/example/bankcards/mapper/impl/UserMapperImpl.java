package com.example.bankcards.mapper.impl;

import com.example.bankcards.core.annotation.Mapper;
import com.example.bankcards.core.dto.user.UserPayload;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.UserMapper;

@Mapper
public class UserMapperImpl implements UserMapper {
    @Override
    public User create(UserPayload payload) {
        return null;
    }
}
