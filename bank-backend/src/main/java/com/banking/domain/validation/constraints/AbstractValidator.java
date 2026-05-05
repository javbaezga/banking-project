package com.banking.domain.validation.constraints;

import jakarta.validation.ConstraintValidator;
import java.lang.annotation.Annotation;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.lang.NonNull;

public abstract class AbstractValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {
    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private A annotation;

    @Override
    public void initialize(@NonNull final A annotation) {
        this.annotation = annotation;
    }
}
