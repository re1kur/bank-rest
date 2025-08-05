package com.example.bankcards.service.impl;

import com.example.bankcards.core.dto.user.UserDto;
import com.example.bankcards.service.ProfileService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserService userService;
    @Override
    public UserDto getProfile(String subject) {
        return userService.read(UUID.fromString(subject));
    }
}
