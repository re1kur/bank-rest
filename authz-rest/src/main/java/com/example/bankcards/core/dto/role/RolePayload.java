package com.example.bankcards.core.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RolePayload(
        @NotBlank(message = "Role name cannot be empty or contain backspace characters.")
        @Size(min = 3, max = 32, message = "Role name must be between 3 and 32 characters long.")
        String name
) {
}
