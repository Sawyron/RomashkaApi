package com.romashka.api.products.impl;

import com.romashka.api.products.Product;
import com.romashka.api.products.ProductRepository;
import com.romashka.api.products.ProductService;
import com.romashka.api.products.dtos.CreateProductRequest;
import com.romashka.api.products.dtos.ProductResponse;
import com.romashka.api.products.dtos.UpdateProductRequest;
import com.romashka.api.products.exceptions.ProductNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final Function<Product, ProductResponse> responseMapper;

    public ProductServiceImpl(
            ProductRepository productRepository,
            Function<Product, ProductResponse> responseMapper
    ) {
        this.productRepository = productRepository;
        this.responseMapper = responseMapper;
    }

    @Override
    public UUID create(CreateProductRequest request) {
        var product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        productRepository.save(product);
        return product.getId();
    }

    @Override
    public ProductResponse findById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return responseMapper.apply(product);
    }


    @Override
    public List<ProductResponse> findAll(Specification<Product> specification, Sort sort, int size) {
        Pageable pageable = PageRequest.of(0, size, sort);
        return productRepository.findAll(specification, pageable)
                .stream()
                .map(responseMapper)
                .toList();
    }

    @Override
    public ProductResponse update(UUID id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        productRepository.save(product);
        return responseMapper.apply(product);
    }

    @Override
    public void deleteById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        productRepository.delete(product);
    }
}
