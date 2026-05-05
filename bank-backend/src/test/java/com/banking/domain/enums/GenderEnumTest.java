package com.banking.domain.enums;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.banking.domain.exception.GenderInvalidException;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.lang.NonNull;

class GenderEnumTest {
    @NonNull
    @SuppressWarnings("java:S1144")
    private static Stream<String> genderValues() {
        return Stream.of(GenderEnum.values()).map(GenderEnum::value);
    }

    @ParameterizedTest
    @MethodSource("genderValues")
    void givenGenderValueWhenConvertFromValueThenReturnGenderEnum(final String genderValue) {
        assertDoesNotThrow(() -> GenderEnum.fromValue(genderValue));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "?"})
    void givenInvalidGenderValueWhenConvertFromValueThenThrowGenderInvalidException(final String genderValue) {
        assertThrows(GenderInvalidException.class, () -> GenderEnum.fromValue(genderValue));
    }
}
