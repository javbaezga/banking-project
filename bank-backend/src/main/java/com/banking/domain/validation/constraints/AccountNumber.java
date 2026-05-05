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
@Constraint(validatedBy = AccountNumberValidator.class)
public @interface AccountNumber {
    String message() default "must be a string of {length} digits long and greater than 0";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int length() default 6;
}
