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
@Constraint(validatedBy = AgeValidator.class)
public @interface Age {
    String message() default "must be a number between {min} to {max}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    byte min() default 13;

    byte max() default 120;
}
