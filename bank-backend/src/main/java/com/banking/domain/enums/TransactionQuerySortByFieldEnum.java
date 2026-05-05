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
public enum TransactionQuerySortByFieldEnum {
    ID("id"),
    DATE("date");

    private static final Map<String, TransactionQuerySortByFieldEnum> TRANSACTION_QUERY_SORT_BY_FIELDS_MAP = Stream.of(
            TransactionQuerySortByFieldEnum.values())
        .collect(Collectors.toMap(
            TransactionQuerySortByFieldEnum::value,
            Function.identity(),
            CollectorUtils.useMergeFunctionNewValue(),
            HashMap::new
        ));

    String value;

    @Nullable
    public static TransactionQuerySortByFieldEnum fromValue(@Nullable final String transactionSortByField)
        throws QuerySortByFieldInvalidException {
        return Optional.ofNullable(transactionSortByField)
            .map(theTransactionSortByField ->
                Optional.ofNullable(TRANSACTION_QUERY_SORT_BY_FIELDS_MAP.get(theTransactionSortByField))
                    .orElseThrow(QuerySortByFieldInvalidException::new)
            )
            .orElse(null);
    }
}
