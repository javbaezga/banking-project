package com.banking.application.output.port;

import com.banking.domain.Transaction;
import com.banking.domain.TransactionQueryInput;
import com.banking.domain.validation.group.Create;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

@Validated
public interface TransactionRepository {
    @NonNull
    Mono<Page<Transaction>> queryTransactions(@NotNull @Valid TransactionQueryInput queryInput);

    @NonNull
    Mono<Transaction> getTransactionById(@NotNull @Min(1L) Long transactionId);

    @NonNull
    Mono<Transaction> createTransaction(@NotNull @Validated(Create.class) Transaction transaction);
}
