package com.romashka.api.supplies;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.romashka.api.products.Product;
import com.romashka.api.products.ProductRepository;
import com.romashka.api.products.config.PostgresqlContainerTestConfiguration;
import com.romashka.api.supplies.dtos.CreateSupplyRequest;
import com.romashka.api.supplies.dtos.UpdateSupplyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(PostgresqlContainerTestConfiguration.class)
class SupplyControllerIT {
    private final static String BASE_URL = "/api/v1/supplies";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SupplyRepository supplyRepository;

    @Autowired
    private ProductRepository productRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Test
    void whenCreate_thenSaveAndIncreaseProductQuantity() throws Exception {
        Product product = createProduct();
        productRepository.save(product);
        var createRequest = new CreateSupplyRequest("doc", product.getId(), 100);
        int expectedQuantity = createRequest.quantity() + product.getQuantity();

        MvcResult mvcResult = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        UUID id = UUID.fromString(mvcResult.getResponse()
                .getContentAsString()
                .replaceAll("\"", ""));
        Supply supply = supplyRepository.findByIdWithProduct(id).orElseThrow();
        assertAll(
                () -> assertEquals(createRequest.quantity(), supply.getQuantity()),
                () -> assertEquals(createRequest.document(), supply.getDocument()),
                () -> assertEquals(createRequest.productId(), supply.getProduct().getId()),
                () -> assertEquals(expectedQuantity, supply.getProduct().getQuantity())
        );
    }

    @Test
    void whenCreteWithProductNotExisting_thenStatus404() throws Exception {
        UUID id = UUID.fromString("5e72c9d9-8695-4420-98b4-39e3245c1f98");
        var createRequest = new CreateSupplyRequest("doc", id, 100);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetById_thenStatus200() throws Exception {
        Product product = createProduct();
        Supply supply = createSupply(product);
        productRepository.save(product);
        supplyRepository.save(supply);

        mockMvc.perform(get(BASE_URL + "/{id}", supply.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(supply.getId().toString()))
                .andExpect(jsonPath("$.quantity").value(supply.getQuantity()))
                .andExpect(jsonPath("$.productId").value(product.getId().toString()))
                .andExpect(jsonPath("document").value(supply.getDocument()));
    }

    @Test
    void whenGetByIdWithSupplyNotExisting_thenStatus404() throws Exception {
        UUID id = UUID.fromString("5e72c9d9-8695-4420-98b4-39e3245c1f98");
        mockMvc.perform(get(BASE_URL + "/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenUpdate_thenUpdateSupplyAndProductQuantity() throws Exception {
        Product product = createProduct();
        Supply supply = createSupply(product);
        supply.setQuantity(20);
        product.setQuantity(supply.getQuantity());
        productRepository.save(product);
        supplyRepository.save(supply);
        var updateRequest = new UpdateSupplyRequest("doc2", product.getId(), 50);
        int expectedProductQuantity = product.getQuantity() + (updateRequest.quantity() - supply.getQuantity());

        mockMvc.perform(put(BASE_URL + "/{id}", supply.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(supply.getId().toString()))
                .andExpect(jsonPath("$.quantity").value(updateRequest.quantity()))
                .andExpect(jsonPath("$.productId").value(product.getId().toString()))
                .andExpect(jsonPath("document").value(updateRequest.document()));

        Supply updatedSupply = supplyRepository.findByIdWithProduct(supply.getId()).orElseThrow();
        assertAll(
                () -> assertEquals(updateRequest.document(), updatedSupply.getDocument()),
                () -> assertEquals(updateRequest.quantity(), updatedSupply.getQuantity()),
                () -> assertEquals(updateRequest.productId(), updatedSupply.getProduct().getId()),
                () -> assertEquals(expectedProductQuantity, updatedSupply.getProduct().getQuantity())
        );
    }

    @Test
    void whenUpdateWithProduct_thenUpdateBothQuantity() throws Exception {
        Product initialProduct = createProduct();
        Product updatedProduct = createProduct();
        Supply supply = createSupply(initialProduct);
        productRepository.saveAll(List.of(initialProduct, updatedProduct));
        supplyRepository.save(supply);
        var updateRequest = new UpdateSupplyRequest("doc2", updatedProduct.getId(), 20);
        int expectedInitialProductQuantity = initialProduct.getQuantity() - supply.getQuantity();
        int expectedUpdatedProductQuantity = updatedProduct.getQuantity() + updateRequest.quantity();

        mockMvc.perform(put(BASE_URL + "/{id}", supply.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(supply.getId().toString()))
                .andExpect(jsonPath("$.quantity").value(updateRequest.quantity()))
                .andExpect(jsonPath("$.productId").value(updatedProduct.getId().toString()))
                .andExpect(jsonPath("document").value(updateRequest.document()));

        initialProduct = productRepository.findById(initialProduct.getId()).orElseThrow();
        updatedProduct = productRepository.findById(updatedProduct.getId()).orElseThrow();
        assertEquals(expectedInitialProductQuantity, initialProduct.getQuantity());
        assertEquals(expectedUpdatedProductQuantity, updatedProduct.getQuantity());
    }

    @Test
    void whenUpdateWithNotExistingProduct_thenStatus404() throws Exception {
        Product product = createProduct();
        Supply supply = createSupply(product);
        product.setQuantity(supply.getQuantity());
        productRepository.save(product);
        supplyRepository.save(supply);
        UUID id = UUID.fromString("5e72c9d9-8695-4420-98b4-39e3245c1f98");
        var updateRequest = new UpdateSupplyRequest("doc2", id, 100);

        mockMvc.perform(put(BASE_URL + "{id}", supply.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenDelete_thenStatus404() throws Exception {
        Product product = createProduct();
        Supply supply = createSupply(product);
        product.setQuantity(supply.getQuantity());
        productRepository.save(product);
        supplyRepository.save(supply);

        mockMvc.perform(delete(BASE_URL + "/{id}", supply.getId()))
                .andExpect(status().isNoContent());
        assertFalse(supplyRepository.existsById(supply.getId()));
    }

    @Test
    void whenDeleteWithNoExistingSupply_thenStatus404() throws Exception {
        UUID id = UUID.fromString("5e72c9d9-8695-4420-98b4-39e3245c1f98");

        mockMvc.perform(delete(BASE_URL + "/{id}", id))
                .andExpect(status().isNotFound());
    }

    private Product createProduct() {
        var product = new Product();
        product.setName("product");
        product.setPrice(1000);
        product.setDescription("info");
        product.setQuantity(100);
        return product;
    }

    private Supply createSupply(Product product) {
        var supply = new Supply();
        supply.setDocument("doc");
        supply.setQuantity(10);
        supply.setProduct(product);
        return supply;
    }
}