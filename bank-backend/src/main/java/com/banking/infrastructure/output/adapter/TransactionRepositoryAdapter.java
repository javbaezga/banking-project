package com.banking.infrastructure.output.adapter;

import com.banking.application.output.port.TransactionRepository;
import com.banking.domain.Transaction;
import com.banking.domain.TransactionQueryInput;
import com.banking.domain.exception.TransactionNotFoundException;
import com.banking.domain.util.MaskUtils;
import com.banking.domain.validation.group.Create;
import com.banking.infrastructure.output.adapter.mapper.TransactionEntityMapper;
import com.banking.infrastructure.output.repository.AccountJpaRepository;
import com.banking.infrastructure.output.repository.TransactionJpaRepository;
import com.banking.infrastructure.output.repository.specification.TransactionSpecification;
import com.banking.infrastructure.util.EntityUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TransactionRepositoryAdapter implements TransactionRepository {
    TransactionJpaRepository transactionJpaRepository;
    AccountJpaRepository accountJpaRepository;
    TransactionEntityMapper transactionMapper;

    @NonNull
    @Override
    public Mono<Page<Transaction>> queryTransactions(@NonNull @NotNull @Valid final TransactionQueryInput queryInput) {
        final var queryInputString = queryInput.toString();
        log.info("Querying transaction entities: input={}", queryInputString);
        return Mono.fromCallable(() ->
                transactionJpaRepository.findAll(
                    TransactionSpecification.search(queryInput.getSearchTerm()),
                    queryInput.toPageable()
                )
            )
            .map(transactionEntitiesPage -> transactionEntitiesPage.map(transactionMapper::toTransaction))
            .doOnSuccess(transactionsPage -> log.info("Transaction entities were queried: input={}", queryInputString))
            .doOnError(error -> log.error("Error querying transaction entities: input={}, error={}", queryInputString,
                error.getMessage()));
    }

    @NonNull
    @Override
    public Mono<Transaction> getTransactionById(@NotNull @Min(1L) final Long transactionId) {
        log.info("Getting transaction entity: ID={}", transactionId);
        return EntityUtils.entityToMono(() -> transactionJpaRepository.findById(transactionId))
            .switchIfEmpty(Mono.error(TransactionNotFoundException::new))
            .map(transactionMapper::toTransaction)
            .doOnSuccess(transaction -> log.info("Transaction entity was gotten and mapped: ID={}", transactionId))
            .doOnError(error -> log.error("Error getting transaction entity: ID={}, error={}", transactionId,
                error.getMessage()));
    }

    @Transactional
    @NonNull
    @Override
    public Mono<Transaction> createTransaction(
        @NonNull @NotNull @Validated(Create.class) final Transaction transaction) {
        final var accountNumber = transaction.getAccount().getNumber();
        final var maskedAccountNumber = MaskUtils.maskAccountNumber(accountNumber);
        log.info("Creating transaction entity: account number={}", maskedAccountNumber);
        return Mono.fromCallable(() -> {
                final var transactionEntity = transactionMapper.toTransactionEntity(transaction);
                accountJpaRepository.save(transactionEntity.getAccount());
                return transactionJpaRepository.save(transactionEntity);
            })
            .subscribeOn(Schedulers.boundedElastic())
            .map(transactionMapper::toTransaction)
            .doOnSuccess(createdTransaction ->
                log.info("Transaction entity was created and mapped: account number={}, ID={}", maskedAccountNumber,
                    createdTransaction.getId()))
            .doOnError(error ->
                log.error("Error creating transaction entity: account number={}, error={}", maskedAccountNumber,
                    error.getMessage()));
    }
}
