package com.romashka.api.products;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class InMemoryProductService {
    private static final String PRODUCT_NOT_FOUND_TEMPLATE = "product with id %s is not found";

    private final Map<UUID, Product> productMap = new HashMap<>();

    public UUID createProduct(Product product) {
        UUID id = UUID.randomUUID();
        productMap.put(
                id,
                new Product(
                        id,
                        product.name(),
                        product.description(),
                        product.cost(),
                        product.isAvailable()
                )
        );
        return id;
    }

    public List<Product> getAllProducts() {
        return productMap.values()
                .stream()
                .toList();
    }

    public Product getProductById(UUID id) {
        Product product = productMap.get(id);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, PRODUCT_NOT_FOUND_TEMPLATE.formatted(id));
        }
        return product;
    }

    public void updateProduct(UUID id, Product product) {
        if (productMap.containsKey(id)) {
            productMap.put(
                    id,
                    new Product(
                            id,
                            product.name(),
                            product.description(),
                            product.cost(),
                            product.isAvailable()
                    ));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, PRODUCT_NOT_FOUND_TEMPLATE.formatted(id));
        }
    }

    public void deleteProductById(UUID id) {
        if (!productMap.containsKey(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, PRODUCT_NOT_FOUND_TEMPLATE.formatted(id));
        }
        productMap.remove(id);
    }
}
