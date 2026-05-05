package com.banking.application.output.port;

import com.banking.domain.Account;
import com.banking.domain.AccountQueryInput;
import com.banking.domain.ResetDailyBalancesInput;
import com.banking.domain.ResetDailyBalancesOutput;
import com.banking.domain.validation.constraints.AccountNumber;
import com.banking.domain.validation.group.Create;
import com.banking.domain.validation.group.Update;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Validated
public interface AccountRepository {
    @NonNull
    Mono<Page<Account>> queryAccounts(@NotNull @Valid AccountQueryInput queryInput);

    @NonNull
    Mono<Account> getAccountById(@NotNull @Min(1L) Long accountId);

    @NonNull
    Mono<Account> getAccountByNumber(@NotBlank @AccountNumber String accountNumber);

    @NonNull
    Flux<Account> getAccountsByCustomerId(@NotNull @Min(1L) Long customerId);

    @NonNull
    Mono<Account> createAccount(@NotNull @Validated(Create.class) Account account);

    @NonNull
    Mono<Account> updateAccount(@NotNull @Validated(Update.class) Account account);

    @NonNull
    Mono<Long> deleteAccount(@NotNull @Min(1L) Long accountId);

    @NonNull
    Mono<ResetDailyBalancesOutput> resetDailyBalances(@NotNull @Valid ResetDailyBalancesInput resetDailyBalancesInput);
}
