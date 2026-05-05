package com.banking.infrastructure.input.adapter.rest.strategy.impl;

import com.banking.application.input.port.AccountService;
import com.banking.domain.AccountOutput;
import com.banking.infrastructure.input.adapter.rest.strategy.AccountsQueryStrategy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountsQueryByCustomerIdStrategyImpl implements AccountsQueryStrategy {
    AccountService accountService;

    @Override
    public boolean supports(@Nullable final String accountNumber, @Nullable final Long customerId) {
        return customerId != null && accountNumber == null;
    }

    @NonNull
    @Override
    public Flux<AccountOutput> getAccounts(final String accountNumber, final Long customerId) {
        return accountService.getAccountsByCustomerId(customerId);
    }
}
