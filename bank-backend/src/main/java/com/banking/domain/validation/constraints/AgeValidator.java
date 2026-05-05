package com.banking.domain.validation.constraints;

import jakarta.validation.ConstraintValidatorContext;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class AgeValidator extends AbstractValidator<Age, Byte> {
    private boolean validateAgeLimits(@NonNull final Byte age) {
        final var ageAnnotation = annotation();
        return age >= ageAnnotation.min() && age <= ageAnnotation.max();
    }

    @Override
    public boolean isValid(@Nullable final Byte age, @NonNull final ConstraintValidatorContext context) {
        return age == null || validateAgeLimits(age);
    }
}
