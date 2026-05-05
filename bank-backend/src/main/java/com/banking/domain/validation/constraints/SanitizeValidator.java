package com.banking.domain.validation.constraints;

import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class SanitizeValidator extends AbstractValidator<Sanitize, String> {
    private static final String SANITIZE_EXTENSION_PLACEHOLDER = "{chars}";
    private static final String SANITIZE_REGULAR_EXPRESSION = String.format(
        "^[A-Za-z0-9ñÑáéíóúÁÉÍÓÚäëïöüÄËÏÖÜçÇ., %s]*$",
        SANITIZE_EXTENSION_PLACEHOLDER
    );

    private Pattern sanitizePattern;

    @Override
    public void initialize(@NonNull final Sanitize sanitizeAnnotation) {
        super.initialize(sanitizeAnnotation);
        sanitizePattern = Pattern.compile(
            SANITIZE_REGULAR_EXPRESSION.replace(SANITIZE_EXTENSION_PLACEHOLDER, sanitizeAnnotation.includeChars()));
    }

    @Override
    public boolean isValid(@Nullable final String value, @NonNull final ConstraintValidatorContext context) {
        return value == null || sanitizePattern.matcher(value).matches();
    }
}
