package com.romashka.api.products;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record Product(
        UUID id,
        @Size(max = 255) String name,
        @Size(max = 4096) String description,
        @Positive int cost,
        boolean isAvailable
) {
}
