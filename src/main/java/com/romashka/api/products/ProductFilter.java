package com.romashka.api.products;

import jakarta.persistence.criteria.Path;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public record ProductFilter(
        @Size(max = 255) String name,
        Integer price,
        Boolean isPriceBottom,
        Boolean isAvailable
) {
    public ProductFilter {
        isPriceBottom = isPriceBottom != null && isPriceBottom;
    }

    public Specification<Product> toSpecification() {
        return Specification.where(createNameSpec())
                .and(createPriceSpec())
                .and(createAvailableSpec());
    }

    private Specification<Product> createNameSpec() {
        return (root, query, cb) -> StringUtils.hasText(name)
                ? cb.like(cb.lower(root.get("name")), '%' + name.toLowerCase() + '%')
                : null;
    }

    private Specification<Product> createPriceSpec() {
        return (root, query, cb) -> {
            if (price == null) {
                return null;
            }
            Path<Integer> pricePath = root.get("price");
            return isPriceBottom ? cb.greaterThanOrEqualTo(pricePath, price) : cb.lessThanOrEqualTo(pricePath, price);
        };
    }

    private Specification<Product> createAvailableSpec() {
        return (root, query, cb) -> {
            if (isAvailable == null) {
                return null;
            }
            Path<Integer> quantityPath = root.get("quantity");
            return isAvailable
                    ? cb.greaterThan(quantityPath, 0)
                    : cb.lessThanOrEqualTo(quantityPath, 0);
        };
    }
}
