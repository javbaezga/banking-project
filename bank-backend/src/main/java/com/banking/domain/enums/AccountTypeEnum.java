package com.banking.domain.enums;

import com.banking.domain.exception.AccountTypeInvalidException;
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
public enum AccountTypeEnum {
    SAVINGS("S", "Ahorros"),
    CURRENT("C", "Corriente");

    private static final Map<String, AccountTypeEnum> ACCOUNT_TYPES_MAP = Stream.of(AccountTypeEnum.values())
        .collect(Collectors.toMap(
            AccountTypeEnum::value,
            Function.identity(),
            CollectorUtils.useMergeFunctionNewValue(),
            HashMap::new
        ));

    String value;
    String description;

    @NonNull
    public static AccountTypeEnum fromValue(@Nullable final String accountType) throws AccountTypeInvalidException {
        return Optional.ofNullable(ACCOUNT_TYPES_MAP.get(accountType))
            .orElseThrow(AccountTypeInvalidException::new);
    }
}
