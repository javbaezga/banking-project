package com.banking.infrastructure.input.adapter.rest.impl;

import com.banking.application.input.port.TransactionService;
import com.banking.domain.TransactionInput;
import com.banking.domain.TransactionOutput;
import com.banking.domain.util.Constants;
import com.banking.domain.util.MaskUtils;
import com.banking.infrastructure.input.adapter.rest.mapper.QueryInputMapper;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
@Slf4j
public class TransactionController {
    TransactionService transactionService;
    QueryInputMapper queryInputMapper;

    @NonNull
    @GetMapping("/query")
    public Mono<ResponseEntity<Page<TransactionOutput>>> queryTransactions(
        @RequestParam(value = "search", required = false) @Nullable @Size(max = Constants.QUERY_SEARCH_TERM_MAXIMUM_SIZE) final String searchTerm,
        @RequestParam(value = "sort_by", required = false) @Nullable @Size(max = Constants.QUERY_SORT_BY_FIELD_MAXIMUM_SIZE) final String sortBy,
        @RequestParam(value = "sort_direction", required = false) @Nullable @Size(max = Constants.QUERY_SORT_DIRECTION_MAXIMUM_SIZE) final String sortDirection,
        @RequestParam("page") @NotNull @Min(Constants.QUERY_MINIMUM_PAGE) final Integer page,
        @RequestParam("size") @NotNull @Min(Constants.QUERY_PAGE_MINIMUM_SIZE) @Max(Constants.QUERY_PAGE_MAXIMUM_SIZE) final Integer size
    ) {
        log.info(
            "|-> Request received for queryTransactions: search term={}, sort by={}, sort direction={}, page={}, size={}",
            searchTerm, sortBy, sortDirection, page, size);
        return Mono.fromCallable(
                () -> queryInputMapper.toTransactionQueryInput(searchTerm, sortBy, sortDirection, page, size))
            .flatMap(transactionService::queryTransactions)
            .doOnSuccess(transactionOutputsPage -> log.info(
                "<-| Response ready for queryTransactions: search term={}, sort by={}, sort direction={}, page={}, size={}",
                searchTerm, sortBy, sortDirection, page, size))
            .doOnError(error -> log.error(
                "<-| Error in queryTransactions: search term={}, sort by={}, sort direction={}, page={}, size={}, error={}",
                searchTerm, sortBy, sortDirection, page, size, error.getMessage(), error))
            .map(ResponseEntity::ok);
    }

    @NonNull
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TransactionOutput>> getTransactionById(
        @PathVariable("id") @NotNull @Min(1) final Long id) {
        log.info("|-> Request received for getTransactionById: ID={}", id);
        return transactionService.getTransactionById(id)
            .doOnSuccess(transactionOutput -> log.info("<-| Response ready for getTransactionById: ID={}", id))
            .doOnError(error -> log.error("<-| Error in getTransactionById: ID={}, error={}", id, error.getMessage(),
                error))
            .map(ResponseEntity::ok);
    }

    @NonNull
    @PostMapping
    public Mono<ResponseEntity<TransactionOutput>> createTransaction(
        @RequestBody @NonNull @NotNull final TransactionInput transaction) {
        final var accountNumber = transaction.getAccountNumber();
        final var maskedAccountNumber = MaskUtils.maskAccountNumber(accountNumber);
        log.info("|-> Request received for createTransaction: account number={}", maskedAccountNumber);
        return transactionService.createTransaction(transaction)
            .doOnSuccess(transactionOutput ->
                log.info("<-| Response ready for createTransaction: account number={}", maskedAccountNumber))
            .doOnError(error ->
                log.error("<-| Error in createTransaction: account number={}, error={}", maskedAccountNumber,
                    error.getMessage(), error))
            .map(ResponseEntity.status(HttpStatus.CREATED)::body);
    }
}
