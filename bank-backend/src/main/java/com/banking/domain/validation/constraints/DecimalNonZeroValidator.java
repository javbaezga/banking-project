package com.banking.domain.validation.constraints;

import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class DecimalNonZeroValidator extends AbstractValidator<DecimalNonZero, BigDecimal> {
    private boolean validateDecimalNonZero(@NonNull final BigDecimal value) {
        return BigDecimal.ZERO.compareTo(value) != 0;
    }

    @Override
    public boolean isValid(@Nullable final BigDecimal value, @NonNull final ConstraintValidatorContext context) {
        return value == null || validateDecimalNonZero(value);
    }
}
