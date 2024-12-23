package com.romashka.api.products.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

public class ProductNotFoundException extends ResponseStatusException {
    public ProductNotFoundException(UUID id) {
        super(
                HttpStatus.NOT_FOUND,
                "Product with id %s is not found".formatted(id)
        );
    }
}
