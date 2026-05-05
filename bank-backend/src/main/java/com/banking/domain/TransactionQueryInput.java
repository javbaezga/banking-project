package com.banking.domain;

import com.banking.domain.enums.TransactionQuerySortByFieldEnum;
import java.util.Objects;
import reactor.util.annotation.NonNull;

public class TransactionQueryInput extends QueryInput<TransactionQuerySortByFieldEnum> {
    @NonNull
    @Override
    public String sortBy() {
        return Objects.requireNonNullElse(getSortBy(), TransactionQuerySortByFieldEnum.ID).value();
    }
}
