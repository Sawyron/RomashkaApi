package com.romashka.api.products.impl;

import com.romashka.api.products.Product;
import com.romashka.api.products.ProductRepository;
import com.romashka.api.products.dtos.CreateProductRequest;
import com.romashka.api.products.dtos.ProductResponse;
import com.romashka.api.products.dtos.UpdateProductRequest;
import com.romashka.api.products.mappers.ProductResponseMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Spy
    private Function<Product, ProductResponse> responseMapper = new ProductResponseMapper();

    @Test
    void whenCreate_thenSaveProductInDb() {
        var request = new CreateProductRequest(
                "product",
                "info",
                100,
                true
        );
        var productCaptor = ArgumentCaptor.forClass(Product.class);

        productService.create(request);

        verify(productRepository).save(productCaptor.capture());
        Product product = productCaptor.getValue();
        assertAll(
                () -> assertEquals(request.name(), product.getName()),
                () -> assertEquals(request.description(), product.getDescription()),
                () -> assertEquals(request.price(), product.getPrice()),
                () -> assertEquals(request.isAvailable(), product.isAvailable())
        );
    }

    @Test
    void whenFindByIdWithExistingId_thenReturnResponse() {
        UUID id = UUID.fromString("e7120fe4-c7b2-4351-ae1a-f4ca1057169d");
        var product = new Product();
        product.setId(id);
        product.setName("product");
        product.setDescription("info");
        product.setPrice(100);
        product.setAvailable(true);
        when(productRepository.findById(eq(id))).thenReturn(Optional.of(product));

        ProductResponse response = productService.findById(id);

        assertAll(
                () -> assertEquals(id, response.id()),
                () -> assertEquals(product.getName(), response.name()),
                () -> assertEquals(product.getDescription(), response.description()),
                () -> assertEquals(product.getPrice(), response.price()),
                () -> assertEquals(product.isAvailable(), response.isAvailable())
        );
    }

    @Test
    void whenFindByIdWithNotExistingId_thenThrowException() {
        UUID id = UUID.fromString("e7120fe4-c7b2-4351-ae1a-f4ca1057169d");
        when(productRepository.findById(eq(id))).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> productService.findById(id));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void whenUpdateWithExistingId_thenUpdate() {
        UUID id = UUID.fromString("e7120fe4-c7b2-4351-ae1a-f4ca1057169d");
        var product = new Product();
        product.setId(id);
        product.setName("product");
        product.setDescription("info");
        product.setPrice(100);
        product.setAvailable(true);
        when(productRepository.findById(eq(id))).thenReturn(Optional.of(product));

        UpdateProductRequest updateRequest = new UpdateProductRequest(
                "product2",
                "info2",
                200,
                false
        );
        ProductResponse updated = productService.update(id, updateRequest);
        verify(productRepository).save(product);
        assertAll(
                () -> assertEquals(product.getId(), updated.id()),
                () -> assertEquals(product.getName(), updated.name()),
                () -> assertEquals(product.getDescription(), updated.description()),
                () -> assertEquals(product.getPrice(), updated.price()),
                () -> assertEquals(product.isAvailable(), updated.isAvailable()),
                () -> assertEquals(product.getName(), updateRequest.name()),
                () -> assertEquals(product.getDescription(), updateRequest.description()),
                () -> assertEquals(product.getPrice(), updateRequest.price()),
                () -> assertEquals(product.isAvailable(), updateRequest.isAvailable())
        );
    }

    @Test
    void whenUpdateWithNotExistingId_thenThrowException() {
        UUID id = UUID.fromString("e7120fe4-c7b2-4351-ae1a-f4ca1057169d");
        when(productRepository.findById(eq(id))).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> productService.update(id, new UpdateProductRequest(
                        "product2",
                        "info2",
                        200,
                        false
                )));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void whenDeleteWithExistingId_thenRemove() {
        UUID id = UUID.fromString("e7120fe4-c7b2-4351-ae1a-f4ca1057169d");
        var product = new Product();
        product.setId(id);
        product.setName("product");
        product.setDescription("info");
        product.setPrice(100);
        product.setAvailable(true);
        when(productRepository.findById(eq(id))).thenReturn(Optional.of(product));

        productService.deleteById(id);
        verify(productRepository).delete(product);
    }

    @Test
    void whenDeleteWithNotExistingId_thenThrowException() {
        UUID id = UUID.fromString("e7120fe4-c7b2-4351-ae1a-f4ca1057169d");
        when(productRepository.findById(eq(id))).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> productService.deleteById(id));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}