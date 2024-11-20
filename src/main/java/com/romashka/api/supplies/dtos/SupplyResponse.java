package com.romashka.api.supplies.dtos;

import java.util.UUID;

public record SupplyResponse(
        UUID id,
        String document,
        UUID productId,
        int quantity
) {
}
