package com.banking.domain.validation.constraints;

import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class AccountNumberValidator extends AbstractValidator<AccountNumber, String> {
    private static final String ACCOUNT_NUMBER_REGULAR_EXPRESSION = "^(?!0{%d})\\d{%d}$";

    private Pattern accountNumberPattern;

    @Override
    public void initialize(@NonNull final AccountNumber accountNumberAnnotation) {
        super.initialize(accountNumberAnnotation);
        final var accountNumberLength = accountNumberAnnotation.length();
        accountNumberPattern = Pattern.compile(
            String.format(ACCOUNT_NUMBER_REGULAR_EXPRESSION, accountNumberLength, accountNumberLength));
    }

    private boolean validateAccountNumber(@NonNull final String accountNumber) {
        return accountNumberPattern.matcher(accountNumber).matches();
    }

    @Override
    public boolean isValid(@Nullable final String accountNumber, @NonNull final ConstraintValidatorContext context) {
        return accountNumber == null || validateAccountNumber(accountNumber);
    }
}
