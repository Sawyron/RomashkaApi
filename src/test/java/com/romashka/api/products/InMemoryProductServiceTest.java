package com.romashka.api.products;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryProductServiceTest {
    @Test
    void whenCreate_thenProductIsSavedWithGeneratedId() {
        var service = new InMemoryProductService();
        var product = new Product(
                null,
                "product",
                "product info",
                100,
                true
        );

        UUID id = service.createProduct(product);

        var products = service.getAllProducts();
        assertEquals(1, products.size());
        Product createdProduct = products.getFirst();
        assertAll(
                () -> assertNotNull(id),
                () -> assertEquals(product.name(), createdProduct.name()),
                () -> assertEquals(product.description(), createdProduct.description()),
                () -> assertEquals(product.cost(), createdProduct.cost()),
                () -> assertEquals(product.isAvailable(), createdProduct.isAvailable())
        );
    }

    @Test
    void whenGetProductById_thenReturnsProductById() {
        var service = new InMemoryProductService();
        var product = new Product(
                null,
                "product",
                "product info",
                100,
                true
        );
        UUID id = service.createProduct(product);
        Product actualProduct = service.getProductById(id);

        assertAll(
                () -> assertEquals(id, actualProduct.id()),
                () -> assertEquals(product.name(), actualProduct.name()),
                () -> assertEquals(product.description(), actualProduct.description()),
                () -> assertEquals(product.cost(), actualProduct.cost()),
                () -> assertEquals(product.isAvailable(), actualProduct.isAvailable())
        );
    }

    @Test
    void whenGetProductByIdThatDoNotExists_thenThrowException() {
        var service = new InMemoryProductService();
        UUID id = UUID.fromString("e7120fe4-c7b2-4351-ae1a-f4ca1057169d");

        assertThrows(ResponseStatusException.class, () -> service.getProductById(id));
    }

    @Test
    void whenUpdateProduct_thenUpdateData() {
        var service = new InMemoryProductService();
        var product = new Product(
                null,
                "product",
                "product info",
                100,
                true
        );
        UUID id = service.createProduct(product);
        Product updated = new Product(
                null,
                "product2",
                "product info 2",
                200,
                false
        );

        service.updateProduct(id, updated);

        Product actualProduct = service.getProductById(id);

        assertAll(
                () -> assertEquals(id, actualProduct.id()),
                () -> assertEquals(updated.name(), actualProduct.name()),
                () -> assertEquals(updated.description(), actualProduct.description()),
                () -> assertEquals(updated.cost(), actualProduct.cost()),
                () -> assertEquals(updated.isAvailable(), actualProduct.isAvailable())
        );
    }

    @Test
    void whenUpdateProductByIdThenDoNotExists_thenThrowException() {
        var service = new InMemoryProductService();
        UUID id = UUID.fromString("e7120fe4-c7b2-4351-ae1a-f4ca1057169d");

        assertThrows(ResponseStatusException.class, () -> service.updateProduct(id, null));
    }

    @Test
    void whenDelete_thenRemovesFromService() {
        var service = new InMemoryProductService();
        var product = new Product(
                null,
                "product",
                "product info",
                100,
                true
        );
        UUID id = service.createProduct(product);

        service.deleteProductById(id);

        assertEquals(0, service.getAllProducts().size());
        assertThrows(ResponseStatusException.class, () -> service.getProductById(id));
    }

    @Test
    void whenDeleteProductByIdThatDoNotExists_thenThrowException() {
        var service = new InMemoryProductService();
        UUID id = UUID.fromString("e7120fe4-c7b2-4351-ae1a-f4ca1057169d");

        assertThrows(ResponseStatusException.class, () -> service.deleteProductById(id));
    }
}