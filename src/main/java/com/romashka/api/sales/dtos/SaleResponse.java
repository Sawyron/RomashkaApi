package com.romashka.api.sales.dtos;

import java.util.UUID;

public record SaleResponse(
        UUID id,
        String document,
        int quantity,
        UUID productId,
        long totalPrice
) {
}
