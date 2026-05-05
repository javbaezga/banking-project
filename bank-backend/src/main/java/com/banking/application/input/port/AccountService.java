package com.banking.application.input.port;

import com.banking.domain.AccountInput;
import com.banking.domain.AccountOutput;
import com.banking.domain.AccountQueryInput;
import com.banking.domain.ResetDailyBalancesInput;
import com.banking.domain.ResetDailyBalancesOutput;
import com.banking.domain.validation.constraints.AccountNumber;
import com.banking.domain.validation.group.Create;
import com.banking.domain.validation.group.PartialUpdate;
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
public interface AccountService {
    @NonNull
    Mono<Page<AccountOutput>> queryAccounts(@NotNull @Valid AccountQueryInput queryInput);

    @NonNull
    Mono<AccountOutput> getAccountById(@NotNull @Min(1L) Long accountId);

    @NonNull
    Mono<AccountOutput> getAccountByNumber(@NotBlank @AccountNumber String accountNumber);

    @NonNull
    Flux<AccountOutput> getAccountsByCustomerId(@NotNull @Min(1L) Long customerId);

    @NonNull
    Mono<AccountOutput> createAccount(@NotNull @Validated(Create.class) AccountInput account);

    @NonNull
    Mono<AccountOutput> updateAccount(@NotNull @Validated(Update.class) AccountInput account);

    @NonNull
    Mono<AccountOutput> updateAccountPartially(@NotNull @Validated(PartialUpdate.class) AccountInput account);

    @NonNull
    Mono<AccountOutput> deleteAccount(@NotNull @Min(1L) Long accountId);

    @NonNull
    Mono<ResetDailyBalancesOutput> resetDailyBalances(@NotNull @Valid ResetDailyBalancesInput resetDailyBalancesInput);
}
