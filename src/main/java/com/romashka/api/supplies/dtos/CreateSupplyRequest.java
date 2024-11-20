package com.romashka.api.supplies.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateSupplyRequest(
        @NotBlank @Size(max = 255) String document,
        UUID productId,
        @Positive int quantity
) {
}
