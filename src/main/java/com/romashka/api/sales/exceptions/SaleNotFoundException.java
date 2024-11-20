package com.romashka.api.sales.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

public class SaleNotFoundException extends ResponseStatusException {
    public SaleNotFoundException(UUID id) {
        super(
                HttpStatus.NOT_FOUND,
                "Sale with id %s is not found".formatted(id)
        );
    }
}
