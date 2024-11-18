package com.romashka.api.validation.sort;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SortValidator.class)
public @interface ValidSort {
    String message() default "Sort contains invalid fields";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] allowedFields();
}
