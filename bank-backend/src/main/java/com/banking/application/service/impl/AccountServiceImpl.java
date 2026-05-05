package com.banking.application.service.impl;

import com.banking.application.input.port.AccountService;
import com.banking.application.output.port.AccountRepository;
import com.banking.application.output.port.CustomerRepository;
import com.banking.domain.Account;
import com.banking.domain.AccountInput;
import com.banking.domain.AccountOutput;
import com.banking.domain.AccountQueryInput;
import com.banking.domain.Customer;
import com.banking.domain.ResetDailyBalancesInput;
import com.banking.domain.ResetDailyBalancesOutput;
import com.banking.domain.enums.UpdateTypeEnum;
import com.banking.domain.exception.AccountCreationException;
import com.banking.domain.exception.AccountDeletionException;
import com.banking.domain.exception.AccountGettingException;
import com.banking.domain.exception.AccountNotFoundException;
import com.banking.domain.exception.AccountUpdatingException;
import com.banking.domain.exception.AccountsNotFoundException;
import com.banking.domain.exception.AccountsQueryingException;
import com.banking.domain.exception.CodeException;
import com.banking.domain.exception.CustomerNotFoundException;
import com.banking.domain.exception.DailyBalancesResettingException;
import com.banking.domain.mapper.AccountMapper;
import com.banking.domain.util.MaskUtils;
import com.banking.domain.validation.constraints.AccountNumber;
import com.banking.domain.validation.group.Create;
import com.banking.domain.validation.group.PartialUpdate;
import com.banking.domain.validation.group.Update;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AccountServiceImpl implements AccountService {
    AccountRepository accountRepository;
    CustomerRepository customerRepository;
    AccountMapper accountMapper;

    @NonNull
    @Override
    public Mono<Page<AccountOutput>> queryAccounts(@NonNull @NotNull @Valid final AccountQueryInput queryInput) {
        final var queryInputString = queryInput.toString();
        log.info("|--> Querying accounts: input={}", queryInputString);
        return accountRepository.queryAccounts(queryInput)
            .map(accountsPage -> accountsPage.map(accountMapper::toAccountOutput))
            .doOnSuccess(accountOutputsPage -> log.info("<--| Accounts were queried: input={}", queryInputString))
            .doOnError(error -> log.error("<--| Error querying accounts: input={}, error={}", queryInputString,
                error.getMessage()))
            .onErrorMap(error -> !(error instanceof CodeException), error -> new AccountsQueryingException());
    }

    @NonNull
    private static Mono<Account> getAccountAndValidateIfEmpty(@NonNull final Mono<Account> getAccountMono) {
        return getAccountMono.switchIfEmpty(Mono.error(AccountNotFoundException::new));
    }

    @NonNull
    private Mono<Account> getAccountByIdAndValidateIfEmpty(@NonNull final Long accountId) {
        return getAccountAndValidateIfEmpty(accountRepository.getAccountById(accountId));
    }

    @NonNull
    @Override
    public Mono<AccountOutput> getAccountById(@NotNull @Min(1L) final Long accountId) {
        log.info("|--> Getting account: ID={}", accountId);
        return getAccountByIdAndValidateIfEmpty(accountId)
            .map(accountMapper::toAccountOutput)
            .doOnSuccess(accountOutput -> log.info("<--| Account was gotten: ID={}", accountId))
            .doOnError(error -> log.error("<--| Error getting account: ID={}, error={}", accountId, error.getMessage()))
            .onErrorMap(error -> !(error instanceof CodeException), error -> new AccountGettingException());
    }

    @NonNull
    @Override
    public Mono<AccountOutput> getAccountByNumber(@NotBlank @AccountNumber final String accountNumber) {
        final var maskedAccountNumber = MaskUtils.maskAccountNumber(accountNumber);
        log.info("|--> Getting account: account number={}", maskedAccountNumber);
        return getAccountAndValidateIfEmpty(accountRepository.getAccountByNumber(accountNumber))
            .map(accountMapper::toAccountOutput)
            .doOnSuccess(accountOutput -> log.info("<--| Account was gotten: account number={}",
                maskedAccountNumber))
            .doOnError(error -> log.error("<--| Error getting account: account number={}, error={}",
                maskedAccountNumber, error.getMessage()))
            .onErrorMap(error -> !(error instanceof CodeException), error -> new AccountGettingException());
    }

    @NonNull
    @Override
    public Flux<AccountOutput> getAccountsByCustomerId(@NotNull @Min(1L) final Long customerId) {
        log.info("|--> Getting accounts: customer ID={}", customerId);
        return accountRepository.getAccountsByCustomerId(customerId)
            .switchIfEmpty(Flux.error(AccountsNotFoundException::new))
            .map(accountMapper::toAccountOutput)
            .doOnComplete(() -> log.info("<--| Accounts were gotten: customer ID={}", customerId))
            .doOnError(error -> log.error("<--| Error getting accounts: customer ID={}, error={}", customerId,
                error.getMessage()))
            .onErrorMap(error -> !(error instanceof CodeException), error -> new AccountGettingException());
    }

    @NonNull
    private Mono<Customer> getCustomerById(@NonNull final Long customerId) {
        return customerRepository.getCustomerById(customerId)
            .switchIfEmpty(Mono.error(CustomerNotFoundException::new));
    }

    @NonNull
    @Override
    public Mono<AccountOutput> createAccount(
        @NonNull @NotNull @Validated(Create.class) final AccountInput accountInput) {
        final var maskedAccountNumber = MaskUtils.maskAccountNumber(accountInput.getNumber());
        log.info("|--> Creating account: account number={}", maskedAccountNumber);
        return getCustomerById(accountInput.getCustomerId())
            .map(customer -> accountMapper.toAccount(accountInput, customer))
            .flatMap(accountRepository::createAccount)
            .map(accountMapper::toAccountOutput)
            .doOnSuccess(accountOutput -> log.info("<--| Account was created: account number={}, ID={}",
                maskedAccountNumber, accountOutput.getId()))
            .doOnError(error -> log.error("<--| Error creating account: account number={}, error={}",
                maskedAccountNumber, error.getMessage()))
            .onErrorMap(error -> !(error instanceof CodeException), error -> new AccountCreationException());
    }

    @NonNull
    private Mono<Customer> resolveCustomerIfChanged(@NonNull final Account currentAccount,
        @NonNull final AccountInput updatedAccountInput) {
        final var accountCustomerId = updatedAccountInput.getCustomerId();
        return Mono.just(accountCustomerId != null
                && !accountCustomerId.equals(currentAccount.getCustomer().getId()))
            .filter(Boolean::booleanValue)
            .flatMap(isCustomerDifferent -> getCustomerById(Objects.requireNonNull(accountCustomerId)))
            .switchIfEmpty(Mono.just(currentAccount.getCustomer()));
    }

    @NonNull
    private Mono<AccountOutput> updateAccount(@NonNull final AccountInput accountInput,
        @NonNull final UpdateTypeEnum updateType) {
        final var accountId = accountInput.getId();
        log.info("|--> Updating account: ID={}, update type={}", accountId, updateType);
        return getAccountByIdAndValidateIfEmpty(accountId)
            .flatMap(currentAccount -> resolveCustomerIfChanged(currentAccount, accountInput)
                .flatMap(resolvedCustomer ->
                    Mono.just(accountMapper.toAccount(accountInput, resolvedCustomer))
                        .flatMap(account ->
                            Mono.just(accountMapper.mergeAccounts(currentAccount, account))
                                .flatMap(accountRepository::updateAccount)
                        )
                )
            )
            .map(accountMapper::toAccountOutput)
            .doOnSuccess(accountOutput ->
                log.info("<--| Account was updated: ID={}, update type={}", accountId, updateType))
            .doOnError(error ->
                log.error("<--| Error updating account: ID={}, partial={}, update type={}", accountId, updateType,
                    error.getMessage()))
            .onErrorMap(error -> !(error instanceof CodeException), error -> new AccountUpdatingException());
    }

    @NonNull
    @Override
    public Mono<AccountOutput> updateAccount(
        @NonNull @NotNull @Validated(Update.class) final AccountInput accountInput) {
        return updateAccount(accountInput, UpdateTypeEnum.FULL);
    }

    @NonNull
    @Override
    public Mono<AccountOutput> updateAccountPartially(
        @NonNull @NotNull @Validated(PartialUpdate.class) final AccountInput accountInput) {
        return updateAccount(accountInput, UpdateTypeEnum.PARTIAL);
    }

    @NonNull
    @Override
    public Mono<AccountOutput> deleteAccount(@NotNull @Min(1L) final Long accountId) {
        log.info("|--> Deleting account: ID={}", accountId);
        return getAccountByIdAndValidateIfEmpty(accountId)
            .flatMap(account ->
                accountRepository.deleteAccount(accountId)
                    .thenReturn(account)
            )
            .map(accountMapper::toAccountOutput)
            .doOnSuccess(accountOutput -> log.info("<--| Account was deleted: ID={}", accountId))
            .doOnError(error ->
                log.error("<--| Error deleting account: ID={}, error={}", accountId, error.getMessage()))
            .onErrorMap(error -> !(error instanceof CodeException), error -> new AccountDeletionException());
    }

    @NonNull
    @Override
    public Mono<ResetDailyBalancesOutput> resetDailyBalances(
        @NonNull @NotNull @Valid final ResetDailyBalancesInput resetDailyBalancesInput) {
        final var resetDailyBalancesInputString = resetDailyBalancesInput.toString();
        log.info("|--> Resetting daily balances: input={}", resetDailyBalancesInputString);
        return accountRepository.resetDailyBalances(resetDailyBalancesInput)
            .doOnSuccess(updateDailyBalancesOutput -> log.info("<--| Daily balances were reset: input={}, output={}",
                resetDailyBalancesInputString, updateDailyBalancesOutput))
            .doOnError(error -> log.error("<--| Error resetting daily balances: input={}, error={}",
                resetDailyBalancesInputString, error.getMessage()))
            .onErrorMap(error -> !(error instanceof CodeException), error -> new DailyBalancesResettingException());
    }
}
