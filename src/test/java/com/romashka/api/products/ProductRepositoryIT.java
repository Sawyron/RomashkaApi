package com.romashka.api.products;

import com.romashka.api.products.config.PostgresqlContainerTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PostgresqlContainerTestConfiguration.class)
class ProductRepositoryIT {
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void populate() {
        List<Product> products = IntStream.range(0, 50).mapToObj(i -> {
                    var product = new Product();
                    UUID id = UUID.randomUUID();
                    product.setId(id);
                    product.setName("product_" + id);
                    product.setDescription("info_" + new StringBuilder(id.toString()).reverse());
                    product.setAvailable(i % 2 == 0);
                    product.setPrice(i * 100);
                    return product;
                })
                .toList();
        productRepository.saveAll(products);
    }

    private record SortData(String field, Sort.Direction direction) {
    }

    @ParameterizedTest
    @MethodSource("provideSortData")
    void testSorting(List<SortData> sorts) {
        Sort sort = null;
        for (SortData sortData : sorts) {
            Sort currentSort = Sort.by(sortData.direction, sortData.field);
            sort = sort == null
                    ? currentSort
                    : sort.and(currentSort);
        }
        assertNotNull(sort);
        List<Product> actualProducts = productRepository.findAll(sort);
        Comparator<Product> productComparator = null;
        for (SortData sortData : sorts) {
            Comparator<Product> currentComparator = createComparator(sortData.field, sortData.direction);
            productComparator = productComparator != null
                    ? productComparator.thenComparing(currentComparator)
                    : currentComparator;
        }
        assertNotNull(productComparator);
        for (int i = 1; i < actualProducts.size() - 1; i++) {
            int diff = productComparator.compare(actualProducts.get(i), actualProducts.get(i - 1));
            assertTrue(diff >= 0);
        }
    }

    private static Stream<List<SortData>> provideSortData() {
        return Stream.of(
                List.of(
                        new SortData("price", Sort.Direction.ASC)
                ),
                List.of(
                        new SortData("price", Sort.Direction.DESC)
                ),
                List.of(
                        new SortData("name", Sort.Direction.ASC)
                ),
                List.of(
                        new SortData("name", Sort.Direction.DESC)
                ),
                List.of(
                        new SortData("price", Sort.Direction.ASC),
                        new SortData("name", Sort.Direction.ASC)
                ),
                List.of(
                        new SortData("price", Sort.Direction.ASC),
                        new SortData("name", Sort.Direction.DESC)
                ),
                List.of(
                        new SortData("price", Sort.Direction.DESC),
                        new SortData("name", Sort.Direction.ASC)
                ),
                List.of(
                        new SortData("name", Sort.Direction.DESC),
                        new SortData("price", Sort.Direction.DESC)
                ),
                List.of(
                        new SortData("name", Sort.Direction.ASC),
                        new SortData("price", Sort.Direction.ASC)
                ),
                List.of(
                        new SortData("name", Sort.Direction.ASC),
                        new SortData("price", Sort.Direction.DESC)
                ),
                List.of(
                        new SortData("name", Sort.Direction.DESC),
                        new SortData("price", Sort.Direction.ASC)
                ),
                List.of(
                        new SortData("name", Sort.Direction.DESC),
                        new SortData("price", Sort.Direction.DESC)
                )
        );
    }

    private static Comparator<Product> createComparator(String field, Sort.Direction direction) {
        Comparator<Product> comparator = switch (field) {
            case "price" -> Comparator.comparing(Product::getPrice);
            case "name" -> Comparator.comparing(Product::getName);
            default -> throw new IllegalStateException("Unknown field");
        };
        if (direction.equals(Sort.Direction.DESC)) {
            comparator = comparator.reversed();
        }
        return comparator;
    }
}