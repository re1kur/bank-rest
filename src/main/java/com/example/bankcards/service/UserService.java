package com.example.bankcards.service;

import com.example.bankcards.core.dto.user.UserPayload;

public interface UserService {
    void create(UserPayload payload);
}
