package com.banking.domain.validation.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = SanitizeValidator.class)
public @interface Sanitize {
    String message() default "must be a string with no special characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String includeChars() default "";
}
