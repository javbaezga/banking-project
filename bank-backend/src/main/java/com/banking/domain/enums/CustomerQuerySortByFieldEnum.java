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
public enum CustomerQuerySortByFieldEnum {
    ID("id"),
    FULL_NAME("fullName"),
    ID_NUMBER("idNumber"),
    USERNAME("username");

    private static final Map<String, CustomerQuerySortByFieldEnum> CUSTOMER_QUERY_SORT_BY_FIELDS_MAP = Stream.of(
            CustomerQuerySortByFieldEnum.values())
        .collect(Collectors.toMap(
            CustomerQuerySortByFieldEnum::value,
            Function.identity(),
            CollectorUtils.useMergeFunctionNewValue(),
            HashMap::new
        ));

    String value;

    @Nullable
    public static CustomerQuerySortByFieldEnum fromValue(@Nullable final String customerSortByField)
        throws QuerySortByFieldInvalidException {
        return Optional.ofNullable(customerSortByField)
            .map(theCustomerSortByField ->
                Optional.ofNullable(CUSTOMER_QUERY_SORT_BY_FIELDS_MAP.get(theCustomerSortByField))
                    .orElseThrow(QuerySortByFieldInvalidException::new)
            )
            .orElse(null);
    }
}
