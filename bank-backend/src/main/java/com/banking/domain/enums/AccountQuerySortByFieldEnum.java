package com.banking.domain.enums;

import com.banking.domain.exception.QuerySortByFieldInvalidException;
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
import org.springframework.lang.Nullable;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum AccountQuerySortByFieldEnum {
    ID("id"),
    NUMBER("number");

    private static final Map<String, AccountQuerySortByFieldEnum> ACCOUNT_QUERY_SORT_BY_FIELDS_MAP = Stream.of(
            AccountQuerySortByFieldEnum.values())
        .collect(Collectors.toMap(
            AccountQuerySortByFieldEnum::value,
            Function.identity(),
            CollectorUtils.useMergeFunctionNewValue(),
            HashMap::new
        ));

    String value;

    @Nullable
    public static AccountQuerySortByFieldEnum fromValue(@Nullable final String accountSortByField)
        throws QuerySortByFieldInvalidException {
        return Optional.ofNullable(accountSortByField)
            .map(theAccountSortByField ->
                Optional.ofNullable(ACCOUNT_QUERY_SORT_BY_FIELDS_MAP.get(theAccountSortByField))
                    .orElseThrow(QuerySortByFieldInvalidException::new)
            )
            .orElse(null);
    }
}
