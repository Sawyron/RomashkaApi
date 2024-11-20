package com.romashka.api.supplies.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

public class SupplyNotFoundException extends ResponseStatusException {
    public SupplyNotFoundException(UUID id) {
        super(
                HttpStatus.NOT_FOUND,
                "Supply with id %s is not found".formatted(id)
        );
    }
}
