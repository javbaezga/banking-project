package com.banking.infrastructure.output.adapter;

import com.banking.application.output.port.AccountRepository;
import com.banking.domain.Account;
import com.banking.domain.AccountQueryInput;
import com.banking.domain.ResetDailyBalancesInput;
import com.banking.domain.ResetDailyBalancesOutput;
import com.banking.domain.util.MaskUtils;
import com.banking.domain.validation.constraints.AccountNumber;
import com.banking.domain.validation.group.Create;
import com.banking.domain.validation.group.Update;
import com.banking.infrastructure.output.adapter.configuration.AccountConfigurationProperties;
import com.banking.infrastructure.output.adapter.mapper.AccountEntityMapper;
import com.banking.infrastructure.output.repository.AccountJpaRepository;
import com.banking.infrastructure.output.repository.entity.AccountEntity;
import com.banking.infrastructure.output.repository.specification.AccountSpecification;
import com.banking.infrastructure.util.EntityUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AccountRepositoryAdapter implements AccountRepository {
    AccountJpaRepository accountJpaRepository;
    AccountEntityMapper accountEntityMapper;
    AccountConfigurationProperties accountConfigurationProperties;

    @NonNull
    @Override
    public Mono<Page<Account>> queryAccounts(@NonNull @NotNull @Valid final AccountQueryInput queryInput) {
        final var queryInputString = queryInput.toString();
        log.info("Querying account entities: input={}", queryInputString);
        return Mono.fromCallable(() ->
                accountJpaRepository.findAll(
                    AccountSpecification.search(queryInput.getSearchTerm()),
                    queryInput.toPageable()
                )
            )
            .map(accountEntitiesPage -> accountEntitiesPage.map(accountEntityMapper::toAccount))
            .doOnSuccess(accountsPage -> log.info("Account entities were queried: input={}", queryInputString))
            .doOnError(error -> log.error("Error querying account entities: input={}, error={}", queryInputString,
                error.getMessage()));
    }

    @NonNull
    private Mono<AccountEntity> findAccountEntityById(@NonNull final Long accountId) {
        return EntityUtils.entityToMono(() -> accountJpaRepository.findById(accountId));
    }

    @NonNull
    @Override
    public Mono<Account> getAccountById(@NotNull @Min(1L) final Long accountId) {
        log.info("Getting account entity: ID={}", accountId);
        return findAccountEntityById(accountId)
            .map(accountEntityMapper::toAccount)
            .doOnSuccess(account -> log.info("Account entity was gotten and mapped: ID={}", accountId))
            .doOnError(error -> log.error("Error getting account entity: ID={}, error={}", accountId,
                error.getMessage()));
    }

    @NonNull
    @Override
    public Mono<Account> getAccountByNumber(@NotBlank @AccountNumber final String accountNumber) {
        final var maskedAccountNumber = MaskUtils.maskAccountNumber(accountNumber);
        log.info("Getting account entity: account number={}", maskedAccountNumber);
        return EntityUtils.entityToMono(() -> accountJpaRepository.findByNumber(accountNumber))
            .map(accountEntityMapper::toAccount)
            .doOnSuccess(account -> log.info("Account entity was gotten and mapped: account number={}",
                maskedAccountNumber))
            .doOnError(error -> log.error("Error getting account entity: account number={}, error={}",
                maskedAccountNumber, error.getMessage()));
    }

    @NonNull
    @Override
    public Flux<Account> getAccountsByCustomerId(@NotNull @Min(1L) final Long customerId) {
        log.info("Getting accounts entities: customer ID={}", customerId);
        return EntityUtils.entitiesToFlux(() -> accountJpaRepository.findByCustomerId(customerId))
            .map(accountEntityMapper::toAccount)
            .doOnComplete(() -> log.info("Accounts entities were gotten and mapped: customer ID={}", customerId))
            .doOnError(error -> log.error("Error getting accounts entities: customer ID={}, error={}", customerId,
                error.getMessage()));
    }

    @NonNull
    private Mono<Account> saveAccount(@NonNull final Account account) {
        return Mono.fromCallable(() -> accountEntityMapper.toAccountEntity(account))
            .map(accountJpaRepository::save)
            .map(accountEntityMapper::toAccount);
    }

    @NonNull
    @Override
    public Mono<Account> createAccount(@NonNull @NotNull @Validated(Create.class) final Account account) {
        final var maskedAccountNumber = MaskUtils.maskAccountNumber(account.getNumber());
        final var accountConfiguration = accountConfigurationProperties.getAccount();
        log.info("Creating account entity: account number={}", maskedAccountNumber);
        return saveAccount(account.initializeBalances(accountConfiguration.getDailyQuota()))
            .doOnSuccess(createdAccount -> log.info("Account entity was created and mapped: account number={}, ID={}",
                maskedAccountNumber, createdAccount.getId()))
            .doOnError(error -> log.error("Error creating account entity: account number={}, error={}",
                maskedAccountNumber, error.getMessage()));
    }

    @NonNull
    @Override
    public Mono<Account> updateAccount(@NonNull @NotNull @Validated(Update.class) final Account account) {
        final var accountId = account.getId();
        log.info("Updating account entity: ID={}", accountId);
        return saveAccount(account)
            .doOnSuccess(updatedAccount -> log.info("Account entity was updated and mapped: ID={}", accountId))
            .doOnError(error -> log.error("Error updating account entity: ID={}, error={}", accountId,
                error.getMessage()));
    }

    @NonNull
    @Override
    public Mono<Long> deleteAccount(@NotNull @Min(1L) final Long accountId) {
        log.info("Deleting account entity: ID={}", accountId);
        return Mono.fromRunnable(() -> accountJpaRepository.deleteById(accountId))
            .thenReturn(accountId)
            .doOnSuccess(theAccountId -> log.info("Account entity was deleted: ID={}", theAccountId))
            .doOnError(error -> log.error("Error deleting account entity: ID={}, error={}", accountId,
                error.getMessage()));
    }

    @NonNull
    @Override
    public Mono<ResetDailyBalancesOutput> resetDailyBalances(
        @NonNull @NotNull @Valid final ResetDailyBalancesInput resetDailyBalancesInput) {
        final var resetDailyBalancesInputString = resetDailyBalancesInput.toString();
        log.info("Resetting daily balances: input={}", resetDailyBalancesInputString);
        return Mono.fromCallable(() ->
                accountJpaRepository.resetDailyBalances(
                    resetDailyBalancesInput.getDailyBalance(),
                    resetDailyBalancesInput.getResetDate()
                )
            )
            .map(ResetDailyBalancesOutput::new)
            .doOnSuccess(resetDailyBalancesOutput -> log.info("Daily balances were reset: input={}, output={}",
                resetDailyBalancesInputString, resetDailyBalancesOutput))
            .doOnError(error -> log.error("Error resetting daily balances: input={}, error={}",
                resetDailyBalancesInputString, error.getMessage()));
    }
}
