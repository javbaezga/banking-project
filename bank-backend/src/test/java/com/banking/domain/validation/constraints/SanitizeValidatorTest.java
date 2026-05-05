package com.banking.domain.validation.constraints;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;

@SpringBootTest(classes = {SanitizeValidator.class})
class SanitizeValidatorTest extends AbstractValidatorTest<Sanitize, String, SanitizeValidator> {
    @NonNull
    @Override
    protected SanitizeValidator createValidator() {
        return new SanitizeValidator();
    }

    @Override
    protected void mockAnnotation(@NonNull final Sanitize annotation) {
        when(annotation.includeChars()).thenReturn("-");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
        "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ-",
        "abcdefghijklmnñopqrstuvwxyz-",
        "0123456789-",
        "ñÑ-",
        "áéíóú-",
        "ÁÉÍÓÚ-",
        "äëïöü-",
        "ÄËÏÖÜ-",
        "çÇ-",
    })
    void givenSanitizedStringWhenValidateThenValidationPasses(final String sanitizedString) {
        assertThat(getValidator().isValid(sanitizedString, getConstraintValidatorContext()), is(true));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "<script>",
        "LIKE '%'",
        "# SELECT",
        "~`!¡@#$%^&*()_+={}[]:;\"'<>,.\\¿?*"
    })
    void givenNotSanitizedStringWhenValidateThenValidationFails(final String notSanitizedString) {
        assertThat(getValidator().isValid(notSanitizedString, getConstraintValidatorContext()), is(false));
    }
}
