package com.banking.domain.enums;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.banking.domain.exception.TransactionTypeInvalidException;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.lang.NonNull;

class TransactionTypeEnumTest {
    @NonNull
    @SuppressWarnings("java:S1144")
    private static Stream<String> transactionTypeValues() {
        return Stream.of(TransactionTypeEnum.values()).map(TransactionTypeEnum::value);
    }

    @ParameterizedTest
    @MethodSource("transactionTypeValues")
    void givenTransactionTypeValueWhenConvertFromValueThenReturnTransactionTypeEnum(
        final String transactionTypeValue) {
        assertDoesNotThrow(() -> TransactionTypeEnum.fromValue(transactionTypeValue));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "?"})
    void givenInvalidTransactionTypeValueWhenConvertFromValueThenThrowTransactionTypeInvalidException(
        final String transactionTypeValue) {
        assertThrows(TransactionTypeInvalidException.class, () -> TransactionTypeEnum.fromValue(transactionTypeValue));
    }
}
