package com.romashka.api.products.impl;

import com.romashka.api.products.Product;
import com.romashka.api.products.ProductRepository;
import com.romashka.api.products.ProductService;
import com.romashka.api.products.dtos.CreateProductRequest;
import com.romashka.api.products.dtos.ProductResponse;
import com.romashka.api.products.dtos.UpdateProductRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        product.setCost(request.cost());
        product.setAvailable(request.isAvailable());
        productRepository.save(product);
        return product.getId();
    }

    @Override
    public ProductResponse findById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product with id %s is not found".formatted(id)
                ));
        return responseMapper.apply(product);
    }

    @Override
    public List<ProductResponse> findAll() {
        return productRepository.findAll()
                .stream()
                .map(responseMapper)
                .toList();
    }

    @Override
    public ProductResponse update(UUID id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product with id %s is not found".formatted(id)
                ));
        product.setName(request.name());
        product.setDescription(request.description());
        product.setCost(request.cost());
        product.setAvailable(request.isAvailable());
        productRepository.save(product);
        return responseMapper.apply(product);
    }

    @Override
    public void deleteById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product with id %s is not found".formatted(id)
                ));
        productRepository.delete(product);
    }
}
