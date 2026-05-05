package com.banking.infrastructure.input.adapter.rest.strategy;

import com.banking.domain.AccountOutput;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;

public interface AccountsQueryStrategy {
    @NonNull
    boolean supports(@Nullable String accountNumber, @Nullable Long customerId);

    @NonNull
    Flux<AccountOutput> getAccounts(String accountNumber, Long customerId);
}
