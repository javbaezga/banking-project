package com.banking.infrastructure.input.adapter.rest.strategy.impl;

import com.banking.infrastructure.exception.AccountsQueryBadRequestException;
import com.banking.infrastructure.input.adapter.rest.strategy.AccountsQueryStrategy;
import com.banking.infrastructure.input.adapter.rest.strategy.AccountsQueryStrategyResolver;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountsQueryStrategyResolverImpl implements AccountsQueryStrategyResolver {
    List<AccountsQueryStrategy> accountsQueryStrategies;

    @NonNull
    @Override
    public Mono<AccountsQueryStrategy> resolve(@Nullable final String accountNumber, @Nullable final Long customerId) {
        return Flux.fromIterable(accountsQueryStrategies)
            .filter(accountsQueryStrategy -> accountsQueryStrategy.supports(accountNumber, customerId))
            .next()
            .switchIfEmpty(Mono.error(AccountsQueryBadRequestException::new));
    }
}
