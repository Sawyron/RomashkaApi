package com.romashka.api.products;

import com.romashka.api.products.dtos.CreateProductRequest;
import com.romashka.api.products.dtos.ProductResponse;
import com.romashka.api.products.dtos.UpdateProductRequest;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    UUID create(CreateProductRequest request);

    ProductResponse findById(UUID id);

    List<ProductResponse> findAll();

    ProductResponse update(UUID id, UpdateProductRequest request);

    void deleteById(UUID id);
}
