package com.example.bankcards.core.dto.user;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record UserDto (
        UUID id,
        String username,
        List<RoleEnum> roles
) {
}
