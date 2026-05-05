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

class CustomerQuerySortByFieldEnumTest {
    @NonNull
    @SuppressWarnings("java:S1144")
    private static Stream<String> customerQuerySortByFieldValues() {
        return Stream.of(CustomerQuerySortByFieldEnum.values()).map(CustomerQuerySortByFieldEnum::value);
    }

    @ParameterizedTest
    @MethodSource("customerQuerySortByFieldValues")
    void givenCustomerQuerySortByFieldValueWhenConvertFromValueThenReturnCustomerQuerySortByFieldEnum(
        final String sortByFieldValue) {
        assertDoesNotThrow(() -> CustomerQuerySortByFieldEnum.fromValue(sortByFieldValue));
    }

    @Test
    void givenNullValueWhenConvertFromValueThenReturnNull() {
        final var customerQuerySortByFieldEnum = CustomerQuerySortByFieldEnum.fromValue(null);
        assertThat(customerQuerySortByFieldEnum, nullValue());
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "?"})
    void givenInvalidCustomerQuerySortByFieldValueWhenConvertFromValueThenThrowQuerySortByFieldInvalidException(
        final String sortByFieldValue) {
        assertThrows(QuerySortByFieldInvalidException.class,
            () -> CustomerQuerySortByFieldEnum.fromValue(sortByFieldValue));
    }
}
