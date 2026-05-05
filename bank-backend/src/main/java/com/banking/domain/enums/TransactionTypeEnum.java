package com.banking.domain.enums;

import com.banking.domain.exception.TransactionTypeInvalidException;
import com.banking.domain.util.CollectorUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum TransactionTypeEnum {
    DEBIT("D", "Débito"),
    CREDIT("C", "Crédito");

    private static final Map<String, TransactionTypeEnum> TRANSACTION_TYPES_MAP = Stream.of(TransactionTypeEnum.values())
        .collect(Collectors.toMap(
            TransactionTypeEnum::value,
            Function.identity(),
            CollectorUtils.useMergeFunctionNewValue(),
            HashMap::new
        ));

    String value;
    String description;

    @NonNull
    public static TransactionTypeEnum fromValue(@Nullable final String transactionType)
        throws TransactionTypeInvalidException {
        return Optional.ofNullable(TRANSACTION_TYPES_MAP.get(transactionType))
            .orElseThrow(TransactionTypeInvalidException::new);
    }
}
