package com.banking.domain.enums;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.banking.domain.exception.AccountTypeInvalidException;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.lang.NonNull;

class AccountTypeEnumTest {
    @NonNull
    @SuppressWarnings("java:S1144")
    private static Stream<String> accountTypeValues() {
        return Stream.of(AccountTypeEnum.values()).map(AccountTypeEnum::value);
    }

    @ParameterizedTest
    @MethodSource("accountTypeValues")
    void givenAccountTypeValueWhenConvertFromValueThenReturnAccountTypeEnum(final String accountTypeValue) {
        assertDoesNotThrow(() -> AccountTypeEnum.fromValue(accountTypeValue));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "?"})
    void givenInvalidAccountTypeValueWhenConvertFromValueThenThrowIllegalArgumentException(
        final String accountTypeValue) {
        assertThrows(AccountTypeInvalidException.class, () -> AccountTypeEnum.fromValue(accountTypeValue));
    }
}
