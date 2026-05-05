package com.banking.domain.enums;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.banking.domain.exception.QuerySortDirectionInvalidException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.lang.NonNull;

class QuerySortDirectionEnumTest {
    @NonNull
    @SuppressWarnings("java:S1144")
    private static Stream<String> querySortDirectionValues() {
        return Stream.of(QuerySortDirectionEnum.values()).map(QuerySortDirectionEnum::value);
    }

    @ParameterizedTest
    @MethodSource("querySortDirectionValues")
    void givenQuerySortDirectionValueWhenConvertFromValueThenReturnQuerySortDirectionEnum(
        final String sortDirectionValue) {
        assertDoesNotThrow(() -> QuerySortDirectionEnum.fromValue(sortDirectionValue));
    }

    @Test
    void givenNullValueWhenConvertFromValueThenReturnNull() {
        final var querySortDirectionEnum = QuerySortDirectionEnum.fromValue(null);
        assertThat(querySortDirectionEnum, nullValue());
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "?"})
    void givenInvalidQuerySortDirectionValueWhenConvertFromValueThenThrowQuerySortDirectionInvalidException(
        final String sortDirectionValue) {
        assertThrows(QuerySortDirectionInvalidException.class,
            () -> QuerySortDirectionEnum.fromValue(sortDirectionValue));
    }
}
