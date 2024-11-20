package com.romashka.api.supplies.mappers;

import com.romashka.api.supplies.Supply;
import com.romashka.api.supplies.dtos.SupplyResponse;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class SupplyResponseMapper implements Function<Supply, SupplyResponse> {
    @Override
    public SupplyResponse apply(Supply supply) {
        return new SupplyResponse(
                supply.getId(),
                supply.getDocument(),
                supply.getProduct().getId(),
                supply.getQuantity()
        );
    }
}
