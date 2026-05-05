package com.banking.domain;

import com.banking.domain.enums.CustomerQuerySortByFieldEnum;
import java.util.Objects;
import reactor.util.annotation.NonNull;

public class CustomerQueryInput extends QueryInput<CustomerQuerySortByFieldEnum> {
    @NonNull
    @Override
    public String sortBy() {
        return Objects.requireNonNullElse(getSortBy(), CustomerQuerySortByFieldEnum.ID).value();
    }
}
