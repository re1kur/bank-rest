package com.example.bankcards.service;

import com.example.bankcards.core.dto.user.UserDto;

public interface ProfileService {
    UserDto getProfile(String subject);
}
