package com.romashka.api.products;

import com.romashka.api.products.dtos.CreateProductRequest;
import com.romashka.api.products.dtos.ProductResponse;
import com.romashka.api.products.dtos.UpdateProductRequest;
import com.romashka.api.validation.sort.ValidSort;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody @Valid CreateProductRequest product) {
        UUID id = productService.create(product);
        URI url = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(url).body(id);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable UUID id) {
        var product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll(
            ProductFilter filter,
            @RequestParam(defaultValue = "20") int size,
            @ParameterObject @Valid @ValidSort(allowedFields = {"name", "price"}) Sort sort
    ) {
        var products = productService.findAll(filter.toSpecification(), sort, size);
        return ResponseEntity.ok(products);
    }

    @PutMapping("{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable UUID id, @RequestBody @Valid UpdateProductRequest request) {
        var product = productService.update(id, request);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
