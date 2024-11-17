package com.romashka.api.products;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final InMemoryProductService inMemoryProductService;

    public ProductController(InMemoryProductService inMemoryProductService) {
        this.inMemoryProductService = inMemoryProductService;
    }

    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody @Valid Product product) {
        UUID id = inMemoryProductService.createProduct(product);
        URI url = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(url).body(id);
    }

    @GetMapping("{id}")
    public ResponseEntity<Product> getById(@PathVariable UUID id) {
        Product product = inMemoryProductService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        List<Product> products = inMemoryProductService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @PutMapping("{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody @Valid Product product) {
        inMemoryProductService.updateProduct(id, product);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        inMemoryProductService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }
}
