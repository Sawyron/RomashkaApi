package com.romashka.api.sales.mappers;

import com.romashka.api.sales.Sale;
import com.romashka.api.sales.dtos.SaleResponse;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class SaleResponseMapper implements Function<Sale, SaleResponse> {
    @Override
    public SaleResponse apply(Sale sale) {
        return new SaleResponse(
                sale.getId(),
                sale.getDocument(),
                sale.getQuantity(),
                sale.getProduct().getId(),
                sale.getTotalPrice()
        );
    }
}
