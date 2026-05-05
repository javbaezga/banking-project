package com.banking.domain.util;

import java.util.function.BinaryOperator;
import lombok.experimental.UtilityClass;
import org.springframework.lang.NonNull;

@UtilityClass
@SuppressWarnings("java:S1118")
public final class CollectorUtils {
    @NonNull
    public static <T> BinaryOperator<T> useMergeFunctionNewValue() {
        return (existingValue, newValue) -> newValue;
    }
}
