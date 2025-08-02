package com.example.bankcards.core.dto.card;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.UUID;

public record CardPayload(
        @NotNull
        UUID userId,
        @NotBlank
        @Pattern(regexp = "^\\d{16}$")
        String number,
        @Future
        LocalDate expirationDate,
        String brand
) {
}
