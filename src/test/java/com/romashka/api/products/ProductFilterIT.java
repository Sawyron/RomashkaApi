package com.romashka.api.products;

import com.romashka.api.products.config.PostgresqlContainerTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PostgresqlContainerTestConfiguration.class)
class ProductFilterIT {
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void populate() {
        List<Product> products = IntStream.range(0, 50).mapToObj(i -> {
                    var product = new Product();
                    UUID id = UUID.randomUUID();
                    product.setId(id);
                    product.setName(i + "product" + i);
                    product.setDescription("info" + i);
                    product.setQuantity(i % 2 == 0 ? 0 : i * 10);
                    product.setPrice(i * 100);
                    return product;
                })
                .toList();
        productRepository.saveAll(products);
    }

    @ParameterizedTest
    @CsvSource(
            value = {
                    "null,null,null,null",
                    "1Product1,null,null,null",
                    "null,1000,null,null",
                    "null,1000,true,true",
                    "null,1000,false,true",
                    "null,null,null,true",
                    "null,null,null,false",
                    "1product1,1000,null,null",
                    "1product1,1000,true,null",
                    "1product1,1000,false,null",
                    "1product1,1000,true,true",
                    "1product1,1000,true,false",
            },
            nullValues = "null")
    void whenAllParametersInFilter_thenApplyThemAll(
            final String name,
            final Integer price,
            final Boolean isPriceBottom,
            final Boolean isAvailable
    ) {
        var filter = new ProductFilter(name, price, isPriceBottom, isAvailable);
        Predicate<Product> productPredicate = p -> true;
        if (name != null) {
            productPredicate = productPredicate.and(p -> p.getName().toLowerCase().contains(name.toLowerCase()));
        }
        final boolean actualIsPriceBottom = isPriceBottom != null && isPriceBottom;
        if (price != null) {
            Predicate<Product> pricePredicate = actualIsPriceBottom
                    ? p -> p.getPrice() >= price
                    : p -> p.getPrice() <= price;
            productPredicate = productPredicate.and(pricePredicate);
        }
        if (isAvailable != null) {
            productPredicate = productPredicate.and(isAvailable
                    ? p -> p.getQuantity() > 0
                    : p -> p.getQuantity() == 0);
        }
        List<Product> actualProducts = productRepository.findAll(filter.toSpecification());
        for (Product product : actualProducts) {
            assertTrue(productPredicate.test(product));
        }
        var expectedCount = productRepository.findAll()
                .stream()
                .filter(productPredicate)
                .count();
        assertEquals(expectedCount, actualProducts.size());
    }
}