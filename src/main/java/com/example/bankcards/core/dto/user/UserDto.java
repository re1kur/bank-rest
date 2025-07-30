package com.example.bankcards.core.dto.user;

import lombok.Builder;

import java.util.List;

@Builder
public record UserDto (
        String username,
        List<RoleEnum> roles
) {
}
