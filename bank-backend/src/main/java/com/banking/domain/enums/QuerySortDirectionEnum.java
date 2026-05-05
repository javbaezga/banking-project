package com.banking.domain.enums;

import com.banking.domain.exception.QuerySortDirectionInvalidException;
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
import org.springframework.data.domain.Sort.Direction;
import org.springframework.lang.Nullable;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum QuerySortDirectionEnum {
    ASC("asc", Direction.ASC),
    DESC("desc", Direction.DESC);

    private static final Map<String, QuerySortDirectionEnum> QUERY_SORT_DIRECTIONS_MAP = Stream.of(
            QuerySortDirectionEnum.values())
        .collect(Collectors.toMap(
            QuerySortDirectionEnum::value,
            Function.identity(),
            CollectorUtils.useMergeFunctionNewValue(),
            HashMap::new
        ));

    String value;
    Direction direction;

    @Nullable
    public static QuerySortDirectionEnum fromValue(@Nullable final String sortDirection)
        throws QuerySortDirectionInvalidException {
        return Optional.ofNullable(sortDirection)
            .map(theSortDirection ->
                Optional.ofNullable(QUERY_SORT_DIRECTIONS_MAP.get(theSortDirection))
                    .orElseThrow(QuerySortDirectionInvalidException::new)
            )
            .orElse(null);
    }
}
