package com.romashka.api.validation.sort;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SortValidator implements ConstraintValidator<ValidSort, Sort> {
    private Set<String> allowedFields;

    @Override
    public void initialize(ValidSort constraintAnnotation) {
        String[] fields = constraintAnnotation.allowedFields();
        allowedFields = new HashSet<>(fields.length);
        Collections.addAll(allowedFields, fields);
    }

    @Override
    public boolean isValid(Sort sort, ConstraintValidatorContext context) {
        if (sort == null) {
            return true;
        }
        for (Sort.Order order : sort) {
            if (!allowedFields.contains(order.getProperty())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "Invalid sort property '%s'. Allowed properties are: %s"
                                .formatted(order.getProperty(), String.join(", ", allowedFields))
                ).addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
