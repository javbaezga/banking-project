package com.banking.application.service.impl;

import com.banking.application.input.port.TransactionService;
import com.banking.application.output.port.AccountRepository;
import com.banking.application.output.port.TransactionRepository;
import com.banking.domain.Account;
import com.banking.domain.Transaction;
import com.banking.domain.TransactionInput;
import com.banking.domain.TransactionOutput;
import com.banking.domain.TransactionQueryInput;
import com.banking.domain.exception.AccountDisabledException;
import com.banking.domain.exception.AccountNotFoundException;
import com.banking.domain.exception.BalanceNotAvailableException;
import com.banking.domain.exception.CodeException;
import com.banking.domain.exception.CustomerDisabledException;
import com.banking.domain.exception.DailyQuotaExceededException;
import com.banking.domain.exception.TransactionCreationException;
import com.banking.domain.exception.TransactionGettingException;
import com.banking.domain.exception.TransactionNotFoundException;
import com.banking.domain.exception.TransactionsQueryingException;
import com.banking.domain.mapper.TransactionMapper;
import com.banking.domain.util.MaskUtils;
import com.banking.domain.validation.group.Create;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    TransactionRepository transactionRepository;
    AccountRepository accountRepository;
    TransactionMapper transactionMapper;

    @NonNull
    @Override
    public Mono<Page<TransactionOutput>> queryTransactions(
        @NonNull @NotNull @Valid final TransactionQueryInput queryInput) {
        final var queryInputString = queryInput.toString();
        log.info("|--> Querying transactions: input={}", queryInputString);
        return transactionRepository.queryTransactions(queryInput)
            .map(transactionsPage -> transactionsPage.map(transactionMapper::toTransactionOutput))
            .doOnSuccess(transactionOutputsPage -> log.info("<--| Transactions were queried: input={}",
                queryInputString))
            .doOnError(error -> log.error("<--| Error querying transactions: input={}, error={}",
                queryInputString, error.getMessage()))
            .onErrorMap(error -> !(error instanceof CodeException), error -> new TransactionsQueryingException());
    }

    @NonNull
    @Override
    public Mono<TransactionOutput> getTransactionById(@NotNull @Min(1L) final Long transactionId) {
        log.info("|--> Getting transaction: ID={}", transactionId);
        return transactionRepository.getTransactionById(transactionId)
            .switchIfEmpty(Mono.error(TransactionNotFoundException::new))
            .map(transactionMapper::toTransactionOutput)
            .doOnSuccess(transactionOutput -> log.info("<--| Transaction was gotten: ID={}", transactionId))
            .doOnError(error -> log.error("<--| Error getting transaction: ID={}, error={}", transactionId,
                error.getMessage()))
            .onErrorMap(error -> !(error instanceof CodeException), error -> new TransactionGettingException());
    }

    @NonNull
    private Mono<Account> getAccountByNumberAndValidateIfEmpty(@NonNull final String accountNumber) {
        return accountRepository.getAccountByNumber(accountNumber)
            .switchIfEmpty(Mono.error(AccountNotFoundException::new));
    }

    @NonNull
    private static Mono<Account> validateTransactionAccount(@NonNull final Account account) {
        return Mono.just(account)
            .filter(theAccount -> theAccount.getCustomer().getStatus())
            .switchIfEmpty(Mono.error(CustomerDisabledException::new))
            .filter(Account::getStatus)
            .switchIfEmpty(Mono.error(AccountDisabledException::new));
    }

    @NonNull
    private static Mono<Transaction> validateTransactionAccountDailyQuota(@NonNull final Transaction transaction) {
        final var account = transaction.getAccount();
        return Mono.just(transaction.getValue())
            .filter(transactionValue -> transactionValue.compareTo(BigDecimal.ZERO) > 0)
            .map(account.getDailyBalance()::subtract)
            .filter(newDailyBalance -> newDailyBalance.compareTo(BigDecimal.ZERO) < 0)
            .flatMap(newDailyBalance -> Mono.error(new DailyQuotaExceededException(account.getDailyBalance())))
            .thenReturn(transaction);
    }

    @NonNull
    private static Mono<Transaction> validateTransactionBalance(@NonNull final Transaction transaction) {
        return Mono.just(transaction.getValue())
            .map(transaction.getBalance()::add)
            .filter(newBalance -> newBalance.compareTo(BigDecimal.ZERO) < 0)
            .flatMap(newBalance -> Mono.error(new BalanceNotAvailableException(transaction.getBalance())))
            .thenReturn(transaction);
    }

    @NonNull
    @Override
    public Mono<TransactionOutput> createTransaction(
        @NonNull @NotNull @Validated(Create.class) final TransactionInput transactionInput) {
        final var accountNumber = transactionInput.getAccountNumber();
        final var maskedAccountNumber = MaskUtils.maskAccountNumber(accountNumber);
        log.info("|--> Creating transaction: account number={}", maskedAccountNumber);
        return getAccountByNumberAndValidateIfEmpty(transactionInput.getAccountNumber())
            .flatMap(TransactionServiceImpl::validateTransactionAccount)
            .map(account -> transactionMapper.toTransaction(transactionInput, account))
            .flatMap(TransactionServiceImpl::validateTransactionAccountDailyQuota)
            .flatMap(TransactionServiceImpl::validateTransactionBalance)
            .map(Transaction::updateBalances)
            .flatMap(transactionRepository::createTransaction)
            .map(transactionMapper::toTransactionOutput)
            .doOnSuccess(createdTransaction -> log.info("<--| Transaction was created: account number={}, ID={}",
                maskedAccountNumber, createdTransaction.getId()))
            .doOnError(error -> log.error("<--| Error creating transaction: account number={}, error={}",
                maskedAccountNumber, error.getMessage()))
            .onErrorMap(error -> !(error instanceof CodeException), error -> new TransactionCreationException());
    }
}
