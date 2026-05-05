package com.banking.domain.validation.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import lombok.AccessLevel;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.lang.NonNull;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Getter(AccessLevel.PROTECTED)
abstract class AbstractValidatorTest<A extends Annotation, T, V extends ConstraintValidator<A, T>> {
    @MockitoBean
    private A annotation;
    @MockitoBean
    private ConstraintValidatorContext constraintValidatorContext;
    @SuppressWarnings("java:S1450")
    private V validator;

    @BeforeEach
    void setup() {
        validator = createValidator();
        mockAnnotation(annotation);
        validator.initialize(annotation);
    }

    @NonNull
    protected abstract V createValidator();

    protected void mockAnnotation(@NonNull final A annotation) {
    }
}
