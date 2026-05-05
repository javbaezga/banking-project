package com.banking.domain;

import com.banking.domain.enums.AccountQuerySortByFieldEnum;
import java.util.Objects;
import reactor.util.annotation.NonNull;

public class AccountQueryInput extends QueryInput<AccountQuerySortByFieldEnum> {
    @NonNull
    @Override
    public String sortBy() {
        return Objects.requireNonNullElse(getSortBy(), AccountQuerySortByFieldEnum.ID).value();
    }
}
