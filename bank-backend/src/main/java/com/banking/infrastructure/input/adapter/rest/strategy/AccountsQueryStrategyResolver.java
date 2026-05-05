package com.banking.infrastructure.input.adapter.rest.strategy;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Mono;

public interface AccountsQueryStrategyResolver {
    @NonNull
    Mono<AccountsQueryStrategy> resolve(@Nullable String accountNumber, @Nullable Long customerId);
}
