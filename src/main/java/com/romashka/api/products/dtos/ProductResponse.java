package com.romashka.api.products.dtos;

import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        int price,
        int quantity
) {
}
