package com.example.bankcards.core.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "The username cannot be empty or contain backspace characters.")
        @Size(min = 6, max = 256, message = "The username must be between 6 and 256 characters long.")
        String username,

        @NotBlank(message = "The password cannot be empty or contain backspace characters.")
        String password
) {
}
