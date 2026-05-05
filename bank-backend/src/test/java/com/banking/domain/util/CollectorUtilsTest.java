package com.banking.domain.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.function.BinaryOperator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.lang.NonNull;

class CollectorUtilsTest {
    @Test
    void shouldReturnBinaryOperatorWhenUseMergeFunctionNewValueIsCalled() {
        assertThat(CollectorUtils.useMergeFunctionNewValue(), instanceOf(BinaryOperator.class));
    }

    @ParameterizedTest
    @CsvSource({"1,2", "3,4"})
    void givenCurrentValueAndNewValueWhenApplyUseMergeFunctionNewValueThenReturnNewValue(
        @NonNull final String currentValue, @NonNull final String newValue) {
        final var mergedValue = assertDoesNotThrow(
            () -> CollectorUtils.useMergeFunctionNewValue().apply(currentValue, newValue));
        assertThat(mergedValue, is(newValue));
    }
}
