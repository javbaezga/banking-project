package com.banking.domain.enums;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.banking.domain.exception.QuerySortByFieldInvalidException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.lang.NonNull;

class AccountQuerySortByFieldEnumTest {
    @NonNull
    @SuppressWarnings("java:S1144")
    private static Stream<String> accountQuerySortByFieldValues() {
        return Stream.of(AccountQuerySortByFieldEnum.values()).map(AccountQuerySortByFieldEnum::value);
    }

    @ParameterizedTest
    @MethodSource("accountQuerySortByFieldValues")
    void givenAccountQuerySortByFieldValueWhenConvertFromValueThenReturnAccountQuerySortByFieldEnum(
        final String sortByFieldValue) {
        assertDoesNotThrow(() -> AccountQuerySortByFieldEnum.fromValue(sortByFieldValue));
    }

    @Test
    void givenNullValueWhenConvertFromValueThenReturnNull() {
        final var accountQuerySortByFieldEnum = AccountQuerySortByFieldEnum.fromValue(null);
        assertThat(accountQuerySortByFieldEnum, nullValue());
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "?"})
    void givenInvalidAccountQuerySortByFieldValueWhenConvertFromValueThenThrowQuerySortByFieldInvalidException(
        final String sortByFieldValue) {
        assertThrows(QuerySortByFieldInvalidException.class,
            () -> AccountQuerySortByFieldEnum.fromValue(sortByFieldValue));
    }
}
