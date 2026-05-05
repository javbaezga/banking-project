package com.banking.domain.validation.constraints;

import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class IdNumberValidator extends AbstractValidator<IdNumber, String> {
    private static final Pattern ID_NUMBER_PATTERN = Pattern.compile("^(?!0{10})\\d{10}$");

    private static boolean validateIdNumber(@NonNull final String idNumber) {
        return ID_NUMBER_PATTERN.matcher(idNumber).matches();
    }

    @Override
    public boolean isValid(@Nullable final String idNumber, @NonNull final ConstraintValidatorContext context) {
        return idNumber == null || validateIdNumber(idNumber);
    }
}
