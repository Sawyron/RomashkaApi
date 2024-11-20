package com.romashka.api.sales.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateSaleRequest(
        @NotBlank @Size(max = 255) String document,
        @Positive int quantity,
        UUID productId
) {
}
