package com.romashka.api.sales;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.romashka.api.products.Product;
import com.romashka.api.products.ProductRepository;
import com.romashka.api.products.config.PostgresqlContainerTestConfiguration;
import com.romashka.api.sales.dtos.CreateSaleRequest;
import com.romashka.api.sales.dtos.UpdateSaleRequest;
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
class SaleControllerIT {
    private static final String BASE_URL = "/api/v1/sales";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ProductRepository productRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Test
    void whenCreate_thenSaveAndReduceProductQuantity() throws Exception {
        Product product = createProduct();
        productRepository.save(product);
        int initialQuantity = product.getQuantity();
        var createRequest = new CreateSaleRequest("doc", 10, product.getId());

        MvcResult mvcResult = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        UUID id = UUID.fromString(mvcResult.getResponse()
                .getContentAsString()
                .replaceAll("\"", ""));

        Sale sale = saleRepository.findByIdWithProduct(id).orElseThrow();
        assertAll(
                () -> assertEquals(createRequest.document(), sale.getDocument()),
                () -> assertEquals(createRequest.quantity(), sale.getQuantity()),
                () -> assertEquals(createRequest.productId(), sale.getProduct().getId()),
                () -> assertEquals(createRequest.quantity() * product.getPrice(), sale.getTotalPrice())
        );
        assertEquals(initialQuantity - createRequest.quantity(), sale.getProduct().getQuantity());
    }

    @Test
    void whenCreateWithInvalidQuantity_thenStatus409() throws Exception {
        Product product = createProduct();
        product.setQuantity(5);
        productRepository.save(product);
        var createRequest = new CreateSaleRequest("doc", 10, product.getId());

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void whenCreateWithProductNotExisting_thanStatus404() throws Exception {
        var createRequest = new CreateSaleRequest(
                "doc",
                10,
                UUID.fromString("e7120fe4-c7b2-4351-ae1a-f4ca1057169d")
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetById_thenStatus200() throws Exception {
        var product = createProduct();
        var sale = createSale(product);
        productRepository.save(product);
        saleRepository.save(sale);

        mockMvc.perform(get(BASE_URL + "/{id}", sale.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(sale.getId().toString()))
                .andExpect(jsonPath("$.document").value(sale.getDocument()))
                .andExpect(jsonPath("$.productId").value(product.getId().toString()))
                .andExpect(jsonPath("$.quantity").value(sale.getQuantity()))
                .andExpect(jsonPath(".totalPrice").value(sale.getTotalPrice()));
    }

    @Test
    void whenUpdateRequestIsOk_thenUpdate() throws Exception {
        var product = createProduct();
        var sale = createSale(product);
        sale.setQuantity(1);
        sale.setTotalPrice(product.getPrice() * sale.getQuantity());
        int oldSaleQuantity = sale.getQuantity();
        int oldProductQuantity = product.getQuantity();
        productRepository.save(product);
        saleRepository.save(sale);
        var updateRequest = new UpdateSaleRequest("doc2", 20, product.getId());

        mockMvc.perform(put(BASE_URL + "/{id}", sale.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(sale.getId().toString()))
                .andExpect(jsonPath("$.document").value(updateRequest.document()))
                .andExpect(jsonPath("$.productId").value(product.getId().toString()))
                .andExpect(jsonPath("$.quantity").value(updateRequest.quantity()));
        product = productRepository.getReferenceById(product.getId());

        assertEquals(
                oldProductQuantity - (updateRequest.quantity() - oldSaleQuantity),
                product.getQuantity()
        );
    }

    @Test
    void whenUpdateWithAnotherProduct_thenChangeBothQuantity() throws Exception {
        var initialProduct = createProduct();
        var sale = createSale(initialProduct);
        var updateProduct = createProduct();
        productRepository.saveAll(List.of(initialProduct, updateProduct));
        saleRepository.save(sale);
        var updateRequest = new UpdateSaleRequest("doc2", 20, updateProduct.getId());
        int expectedInitialProductQuantity = initialProduct.getQuantity() + sale.getQuantity();
        int expectedUpdateProductQuantity = updateProduct.getQuantity() - updateRequest.quantity();

        mockMvc.perform(put(BASE_URL + "/{id}", sale.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(sale.getId().toString()))
                .andExpect(jsonPath("$.document").value(updateRequest.document()))
                .andExpect(jsonPath("$.productId").value(updateProduct.getId().toString()))
                .andExpect(jsonPath("$.quantity").value(updateRequest.quantity()));
        initialProduct = productRepository.getReferenceById(initialProduct.getId());
        updateProduct = productRepository.getReferenceById(updateProduct.getId());
        assertEquals(expectedInitialProductQuantity, initialProduct.getQuantity());
        assertEquals(expectedUpdateProductQuantity, updateProduct.getQuantity());
    }

    @Test
    void whenUpdateAnotherProductWithInvalidQuantity_thenStatus409() throws Exception {
        var initialProduct = createProduct();
        var sale = createSale(initialProduct);
        sale.setQuantity(100);
        var updateProduct = createProduct();
        updateProduct.setQuantity(50);
        productRepository.saveAll(List.of(initialProduct, updateProduct));
        saleRepository.save(sale);
        var updateRequest = new UpdateSaleRequest("doc2", 200, updateProduct.getId());
        mockMvc.perform(put(BASE_URL + "/{id}", sale.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void whenUpdateSaleNotFound_theStatus404() throws Exception {
        UUID id = UUID.fromString("e7120fe4-c7b2-4351-ae1a-f4ca1057169d");
        var updateRequest = new UpdateSaleRequest("doc2", 20, id);

        mockMvc.perform(put(BASE_URL + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenUpdateProductNotFound_thenStatus404() throws Exception {
        var product = createProduct();
        var sale = createSale(product);
        productRepository.save(product);
        saleRepository.save(sale);
        saleRepository.flush();
        UUID id = UUID.fromString("e7120fe4-c7b2-4351-ae1a-f4ca1057169d");
        var updateRequest = new UpdateSaleRequest("doc2", 20, id);
        mockMvc.perform(put(BASE_URL + "/{id}", sale.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenDelete_thenIncreaseProductQuantity() throws Exception {
        Product product = createProduct();
        Sale sale = createSale(product);
        productRepository.save(product);
        saleRepository.save(sale);
        int expectedQuantity = product.getQuantity() + sale.getQuantity();
        UUID saleId = sale.getId();

        mockMvc.perform(delete(BASE_URL + "/{id}", saleId))
                .andExpect(status().isNoContent());
        assertFalse(saleRepository.existsById(saleId));
        product = productRepository.findById(product.getId()).orElseThrow();
        assertEquals(expectedQuantity, product.getQuantity());
    }

    @Test
    void whenDeleteSaleNotExists_thenStatus404() throws Exception {
        UUID id = UUID.fromString("e7120fe4-c7b2-4351-ae1a-f4ca1057169d");
        mockMvc.perform(delete(BASE_URL + "/{id}", id))
                .andExpect(status().isNotFound());
    }

    private Product createProduct() {
        var product = new Product();
        product.setPrice(100);
        product.setName("product");
        product.setDescription("info");
        product.setQuantity(1000);
        return product;
    }

    private Sale createSale(Product product) {
        var sale = new Sale();
        sale.setProduct(product);
        sale.setDocument("doc");
        sale.setQuantity(1);
        sale.setTotalPrice(product.getPrice() * sale.getQuantity());
        return sale;
    }
}