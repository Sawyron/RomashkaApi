package com.romashka.api.products.mappers;

import com.romashka.api.products.Product;
import com.romashka.api.products.dtos.ProductResponse;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ProductResponseMapper implements Function<Product, ProductResponse> {
    @Override
    public ProductResponse apply(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.isAvailable()
        );
    }
}
