package com.example.bankcards.core.dto.role;

import lombok.Builder;

@Builder
public record RoleDto(
        Integer id,
        String name
) {
}
