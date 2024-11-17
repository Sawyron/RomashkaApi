package com.romashka.api.products.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateProductRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 4096) String description,
        @Positive int cost,
        boolean isAvailable
) {
}
