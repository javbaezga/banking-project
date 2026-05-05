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
@Constraint(validatedBy = IdNumberValidator.class)
public @interface IdNumber {
    String message() default "must be a string of 10 digits long, and greater than 0";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
