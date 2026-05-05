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

class TransactionQuerySortByFieldEnumTest {
    @NonNull
    @SuppressWarnings("java:S1144")
    private static Stream<String> transactionQuerySortByFieldValues() {
        return Stream.of(TransactionQuerySortByFieldEnum.values()).map(TransactionQuerySortByFieldEnum::value);
    }

    @ParameterizedTest
    @MethodSource("transactionQuerySortByFieldValues")
    void givenTransactionQuerySortByFieldValueWhenConvertFromValueThenReturnTransactionQuerySortByFieldEnum(
        final String sortByFieldValue) {
        assertDoesNotThrow(() -> TransactionQuerySortByFieldEnum.fromValue(sortByFieldValue));
    }

    @Test
    void givenNullValueWhenConvertFromValueThenReturnNull() {
        final var transactionQuerySortByFieldEnum = TransactionQuerySortByFieldEnum.fromValue(null);
        assertThat(transactionQuerySortByFieldEnum, nullValue());
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "?"})
    void givenInvalidTransactionQuerySortByFieldValueWhenConvertFromValueThenThrowQuerySortByFieldInvalidException(
        final String sortByFieldValue) {
        assertThrows(QuerySortByFieldInvalidException.class,
            () -> TransactionQuerySortByFieldEnum.fromValue(sortByFieldValue));
    }
}
