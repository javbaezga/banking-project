package com.banking.domain.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.lang.NonNull;

class MaskUtilsTest {
    @ParameterizedTest
    @ValueSource(strings = {
        "1707943039",
        "1309537262",
        "1802584068",
        "1712664570"
    })
    void givenIdNumberWhenMaskThenReturnMaskedIdNumber(@NonNull final String idNumber) {
        final var maskedDocumentNumber = assertDoesNotThrow(() -> MaskUtils.maskIdNumber(idNumber));
        assertThat(maskedDocumentNumber, matchesRegex("^\\d\\*+\\d{5}$"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void givenInvalidIdNumberWhenMaskThenReturnNull(final String idNumber) {
        final var maskedDocumentNumber = assertDoesNotThrow(() -> MaskUtils.maskIdNumber(idNumber));
        assertThat(maskedDocumentNumber, nullValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2203979994", "21134545678", "2100211900"})
    void givenAccountNumberWhenMaskThenReturnMaskedClaim(final String accountNumber) {
        final var maskedAccountNumber = assertDoesNotThrow(() -> MaskUtils.maskAccountNumber(accountNumber));
        assertThat(maskedAccountNumber, matchesRegex("^\\d\\*+\\d{4}$"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void givenInvalidAccountNumberWhenMaskThenReturnNull(final String accountNumber) {
        final var maskedAccountNumber = assertDoesNotThrow(() -> MaskUtils.maskAccountNumber(accountNumber));
        assertThat(maskedAccountNumber, nullValue());
    }
}
