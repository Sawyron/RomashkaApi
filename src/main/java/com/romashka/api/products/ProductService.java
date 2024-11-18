package com.romashka.api.products;

import com.romashka.api.products.dtos.CreateProductRequest;
import com.romashka.api.products.dtos.ProductResponse;
import com.romashka.api.products.dtos.UpdateProductRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    UUID create(CreateProductRequest request);

    ProductResponse findById(UUID id);

    List<ProductResponse> findAll();

    List<ProductResponse> findAll(Specification<Product> specification, Sort sort, int size);

    ProductResponse update(UUID id, UpdateProductRequest request);

    void deleteById(UUID id);
}
