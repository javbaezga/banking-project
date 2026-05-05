package com.banking.domain;

import com.banking.domain.enums.QuerySortDirectionEnum;
import com.banking.domain.util.Constants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.lang.NonNull;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class QueryInput<S extends Enum<?>> {
    @Size(max = Constants.QUERY_SEARCH_TERM_MAXIMUM_SIZE)
    String searchTerm;
    S sortBy;
    QuerySortDirectionEnum sortDirection;
    @NotNull
    @Min(Constants.QUERY_MINIMUM_PAGE)
    Integer page;
    @NotNull
    @Min(Constants.QUERY_PAGE_MINIMUM_SIZE)
    @Max(Constants.QUERY_PAGE_MAXIMUM_SIZE)
    Integer size;

    @NonNull
    public abstract String sortBy();

    @NonNull
    public Direction sortDirection() {
        if (sortDirection == null) {
            return Direction.ASC;
        }
        return sortDirection.direction();
    }

    @NonNull
    public Pageable toPageable() {
        final var sort = Sort.by(sortDirection(), sortBy());
        return PageRequest.of(getPage(), getSize(), sort);
    }
}
