package com.banking.domain.validation.constraints;

import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class PhoneValidator extends AbstractValidator<Phone, String> {
    private static final String PHONE_REGULAR_EXPRESSION = "^(?!0{%d,%d})\\d{%d,%d}$";

    private Pattern phonePattern;

    @Override
    public void initialize(@NonNull final Phone phoneAnnotation) {
        super.initialize(phoneAnnotation);
        final var phoneMinLength = phoneAnnotation.min();
        final var phoneMaxLength = phoneAnnotation.max();
        phonePattern = Pattern.compile(
            String.format(PHONE_REGULAR_EXPRESSION, phoneMinLength, phoneMaxLength, phoneMinLength, phoneMaxLength));
    }

    private boolean validatePhone(@NonNull final String phone) {
        return phonePattern.matcher(phone).matches();
    }

    @Override
    public boolean isValid(@Nullable final String phone, @NonNull final ConstraintValidatorContext context) {
        return phone == null || validatePhone(phone);
    }
}
