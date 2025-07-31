package com.example.bankcards.core.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserUpdatePayload(
        @NotBlank(message = "The username cannot be empty or contain backspace characters.")
        @Size(min = 6, max = 256, message = "The username must be between 6 and 256 characters long.")
        String username,

        @NotEmpty
        List<Integer> roleIds
) {
}
