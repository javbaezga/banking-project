package com.banking.application.input.port;

import com.banking.domain.TransactionInput;
import com.banking.domain.TransactionOutput;
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
public interface TransactionService {
    @NonNull
    Mono<Page<TransactionOutput>> queryTransactions(@NotNull @Valid TransactionQueryInput queryInput);

    @NonNull
    Mono<TransactionOutput> getTransactionById(@NotNull @Min(1L) Long transactionId);

    @NonNull
    Mono<TransactionOutput> createTransaction(@NotNull @Validated(Create.class) TransactionInput transactionInput);
}
