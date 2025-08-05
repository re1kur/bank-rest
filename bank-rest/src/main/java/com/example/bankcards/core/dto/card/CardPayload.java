package com.example.bankcards.core.dto.card;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

public record CardPayload(
        @NotNull(message = "User id must be.")
        UUID userId,
        @NotBlank(message = "Number cannot be empty or contain backspace characters.")
        @Pattern(regexp = "^\\d{16}$")
        String number,
        @Future(message = "Expiration date must be in future.")
        LocalDate expirationDate,
        @NotBlank(message = "Brand name cannot be empty or contain backspace characters.")
        @Size(max = 32, message = "Brand name must be less than 32 characters long.")
        String brand
) {
}
