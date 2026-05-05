package com.banking.infrastructure.input.adapter.rest.impl;

import com.banking.application.input.port.AccountService;
import com.banking.domain.AccountInput;
import com.banking.domain.AccountOutput;
import com.banking.domain.util.Constants;
import com.banking.domain.util.MaskUtils;
import com.banking.domain.validation.constraints.AccountNumber;
import com.banking.domain.validation.group.Create;
import com.banking.domain.validation.group.PartialUpdate;
import com.banking.domain.validation.group.Update;
import com.banking.infrastructure.input.adapter.rest.mapper.QueryInputMapper;
import com.banking.infrastructure.input.adapter.rest.strategy.AccountsQueryStrategyResolver;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
@Slf4j
public class AccountController {
    AccountService accountService;
    AccountsQueryStrategyResolver accountsQueryStrategyResolver;
    QueryInputMapper queryInputMapper;

    @NonNull
    @GetMapping("/query")
    public Mono<ResponseEntity<Page<AccountOutput>>> queryAccounts(
        @RequestParam(value = "search", required = false) @Nullable @Size(max = Constants.QUERY_SEARCH_TERM_MAXIMUM_SIZE) final String searchTerm,
        @RequestParam(value = "sort_by", required = false) @Nullable @Size(max = Constants.QUERY_SORT_BY_FIELD_MAXIMUM_SIZE) final String sortBy,
        @RequestParam(value = "sort_direction", required = false) @Nullable @Size(max = Constants.QUERY_SORT_DIRECTION_MAXIMUM_SIZE) final String sortDirection,
        @RequestParam("page") @NotNull @Min(Constants.QUERY_MINIMUM_PAGE) final Integer page,
        @RequestParam("size") @NotNull @Min(Constants.QUERY_PAGE_MINIMUM_SIZE) @Max(Constants.QUERY_PAGE_MAXIMUM_SIZE) final Integer size
    ) {
        log.info(
            "|-> Request received for queryAccounts: search term={}, sort by={}, sort direction={}, page={}, size={}",
            searchTerm, sortBy, sortDirection, page, size);
        return Mono.fromCallable(
                () -> queryInputMapper.toAccountQueryInput(searchTerm, sortBy, sortDirection, page, size))
            .flatMap(accountService::queryAccounts)
            .doOnSuccess(accountOutputsPage -> log.info(
                "<-| Response ready for queryAccounts: search term={}, sort by={}, sort direction={}, page={}, size={}",
                searchTerm, sortBy, sortDirection, page, size))
            .doOnError(error -> log.error(
                "<-| Error in queryAccounts: search term={}, sort by={}, sort direction={}, page={}, size={}, error={}",
                searchTerm, sortBy, sortDirection, page, size, error.getMessage(), error))
            .map(ResponseEntity::ok);
    }

    @NonNull
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AccountOutput>> getAccountById(@PathVariable("id") @NotNull @Min(1) final Long id) {
        log.info("|-> Request received for getAccountById: ID={}", id);
        return accountService.getAccountById(id)
            .doOnSuccess(accountOutput -> log.info("<-| Response ready for getAccountById: ID={}", id))
            .doOnError(error -> log.error("<-| Error in getAccountById: ID={}, error={}", id, error.getMessage(),
                error))
            .map(ResponseEntity::ok);
    }

    @NonNull
    @GetMapping
    public Mono<ResponseEntity<List<AccountOutput>>> getAccountsByAccountNumberOrCustomerId(
        @RequestParam(value = "account_number", required = false) @Nullable @AccountNumber final String accountNumber,
        @RequestParam(value = "customer_id", required = false) @Nullable @Min(1) final Long customerId
    ) {
        final var maskedAccountNumber = MaskUtils.maskAccountNumber(accountNumber);
        log.info("|-> Request received for getAccountsByAccountNumberOrCustomerId: account number={}, customer ID={}",
            maskedAccountNumber, customerId);
        return accountsQueryStrategyResolver.resolve(accountNumber, customerId)
            .flatMapMany(accountsQueryStrategy -> accountsQueryStrategy.getAccounts(accountNumber, customerId))
            .collectList()
            .doOnSuccess(accounts -> log.info(
                "<-| Response ready for getAccountsByAccountNumberOrCustomerId: account number={}, customer ID={}, accounts found={}",
                maskedAccountNumber, customerId, accounts.size()))
            .doOnError(error -> log.error(
                "<-| Error in getAccountsByAccountNumberOrCustomerId: account number={}, customer ID={}, error={}",
                maskedAccountNumber, customerId, error.getMessage(), error))
            .map(ResponseEntity::ok);
    }

    @NonNull
    @PostMapping
    public Mono<ResponseEntity<AccountOutput>> createAccount(
        @RequestBody @NonNull @NotNull @Validated(Create.class) final AccountInput account) {
        final var accountNumber = account.getNumber();
        final var maskedAccountNumber = MaskUtils.maskAccountNumber(accountNumber);
        log.info("|-> Request received for createAccount: account number={}", maskedAccountNumber);
        return accountService.createAccount(account)
            .doOnSuccess(accountOutput -> log.info("<-| Response ready for createAccount: account number={}",
                maskedAccountNumber))
            .doOnError(error -> log.error("<-| Error in createAccount: account number={}, error={}",
                maskedAccountNumber, error.getMessage(), error))
            .map(ResponseEntity.status(HttpStatus.CREATED)::body);
    }

    @NonNull
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AccountOutput>> updateAccount(
        @PathVariable("id") @NonNull @NotNull @Min(1) final Long id,
        @RequestBody @NonNull @NotNull @Validated(Update.class) final AccountInput account
    ) {
        log.info("|-> Request received for updateAccount: ID={}", id);
        return accountService.updateAccount(account.withId(id))
            .doOnSuccess(accountOutput -> log.info("<-| Response ready for updateAccount: ID={}", id))
            .doOnError(error -> log.error("<-| Error in updateAccount: ID={}, error={}", id, error.getMessage(), error))
            .map(ResponseEntity::ok);
    }

    @NonNull
    @PatchMapping("/{id}")
    public Mono<ResponseEntity<AccountOutput>> updateAccountPartially(
        @PathVariable("id") @NonNull @NotNull @Min(1) final Long id,
        @RequestBody @NonNull @NotNull @Validated(PartialUpdate.class) final AccountInput account
    ) {
        log.info("|-> Request received for updateAccountPartially: ID={}", id);
        return accountService.updateAccountPartially(account.withId(id))
            .doOnSuccess(accountOutput -> log.info("<-| Response ready for updateAccountPartially: ID={}", id))
            .doOnError(error -> log.error("<-| Error in updateAccountPartially: ID={}, error={}", id,
                error.getMessage(), error))
            .map(ResponseEntity::ok);
    }

    @NonNull
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<AccountOutput>> deleteAccount(@PathVariable("id") @NotNull @Min(1) final Long id) {
        log.info("|-> Request received for deleteAccount: ID={}", id);
        return accountService.deleteAccount(id)
            .doOnSuccess(accountOutput -> log.info("<-| Response ready for deleteAccount: ID={}", id))
            .doOnError(error -> log.error("<-| Error in deleteAccount: ID={}, error={}", id, error.getMessage(), error))
            .map(ResponseEntity::ok);
    }
}
