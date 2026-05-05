package com.banking.infrastructure.input.adapter.rest.impl;

import com.banking.application.input.port.CustomerService;
import com.banking.domain.CustomerInput;
import com.banking.domain.CustomerOutput;
import com.banking.domain.util.Constants;
import com.banking.domain.util.MaskUtils;
import com.banking.domain.validation.constraints.IdNumber;
import com.banking.domain.validation.group.Create;
import com.banking.domain.validation.group.PartialUpdate;
import com.banking.domain.validation.group.Update;
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
@RequestMapping("/customers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
@Slf4j
public class CustomerController {
    CustomerService customerService;
    QueryInputMapper queryInputMapper;

    @NonNull
    @GetMapping("/query")
    public Mono<ResponseEntity<Page<CustomerOutput>>> queryCustomers(
        @RequestParam(value = "search", required = false) @Nullable @Size(max = Constants.QUERY_SEARCH_TERM_MAXIMUM_SIZE) final String searchTerm,
        @RequestParam(value = "sort_by", required = false) @Nullable @Size(max = Constants.QUERY_SORT_BY_FIELD_MAXIMUM_SIZE) final String sortBy,
        @RequestParam(value = "sort_direction", required = false) @Nullable @Size(max = Constants.QUERY_SORT_DIRECTION_MAXIMUM_SIZE) final String sortDirection,
        @RequestParam("page") @NotNull @Min(Constants.QUERY_MINIMUM_PAGE) final Integer page,
        @RequestParam("size") @NotNull @Min(Constants.QUERY_PAGE_MINIMUM_SIZE) @Max(Constants.QUERY_PAGE_MAXIMUM_SIZE) final Integer size
    ) {
        log.info(
            "|-> Request received for queryCustomers: search term={}, sort by={}, sort direction={}, page={}, size={}",
            searchTerm, sortBy, sortDirection, page, size);
        return Mono.fromCallable(
                () -> queryInputMapper.toCustomerQueryInput(searchTerm, sortBy, sortDirection, page, size))
            .flatMap(customerService::queryCustomers)
            .doOnSuccess(customerOutputsPage -> log.info(
                "<-| Response ready for queryCustomers: search term={}, sort by={}, sort direction={}, page={}, size={}",
                searchTerm, sortBy, sortDirection, page, size))
            .doOnError(error -> log.error(
                "<-| Error in queryCustomers: search term={}, sort by={}, sort direction={}, page={}, size={}, error={}",
                searchTerm, sortBy, sortDirection, page, size, error.getMessage(), error))
            .map(ResponseEntity::ok);
    }

    @NonNull
    @GetMapping("/{id}")
    public Mono<ResponseEntity<CustomerOutput>> getCustomerById(@PathVariable("id") @NotNull @Min(1) final Long id) {
        log.info("|-> Request received for getCustomerById: ID={}", id);
        return customerService.getCustomerById(id)
            .doOnSuccess(customerOutput -> log.info("<-| Response ready for getCustomerById: ID={}", id))
            .doOnError(error -> log.error("<-| Error in getCustomerById: ID={}, error={}", id, error.getMessage(),
                error))
            .map(ResponseEntity::ok);
    }

    @NonNull
    @GetMapping
    public Mono<ResponseEntity<CustomerOutput>> getCustomerByIdNumber(
        @RequestParam("id_number") @NotNull @IdNumber final String idNumber) {
        log.info("|-> Request received for getCustomerByIdNumber: ID number={}", idNumber);
        return customerService.getCustomerByIdNumber(idNumber)
            .doOnSuccess(customerOutput ->
                log.info("<-| Response ready for getCustomerByIdNumber: ID number={}", idNumber))
            .doOnError(error ->
                log.error("<-| Error in getCustomerByIdNumber: ID number={}, error={}", idNumber, error.getMessage(),
                    error))
            .map(ResponseEntity::ok);
    }

    @NonNull
    @PostMapping
    public Mono<ResponseEntity<CustomerOutput>> createCustomer(
        @RequestBody @NonNull @NotNull @Validated(Create.class) final CustomerInput customer) {
        final var idNumber = customer.getIdNumber();
        final var maskedIdNumber = MaskUtils.maskIdNumber(idNumber);
        log.info("|-> Request received for createCustomer: ID number={}", maskedIdNumber);
        return customerService.createCustomer(customer)
            .doOnSuccess(customerOutput -> log.info("<-| Response ready for createCustomer: ID number={}",
                maskedIdNumber))
            .doOnError(error -> log.error("<-| Error in createCustomer: ID number={}, error={}",
                maskedIdNumber, error.getMessage(), error))
            .map(ResponseEntity.status(HttpStatus.CREATED)::body);
    }

    @NonNull
    @PutMapping("/{id}")
    public Mono<ResponseEntity<CustomerOutput>> updateCustomer(
        @PathVariable("id") @NonNull @NotNull @Min(1) final Long id,
        @RequestBody @NonNull @NotNull @Validated(Update.class) final CustomerInput customer
    ) {
        log.info("|-> Request received for updateCustomer: ID={}", id);
        return customerService.updateCustomer(customer.withId(id))
            .doOnSuccess(customerOutput -> log.info("<-| Response ready for updateCustomer: ID={}", id))
            .doOnError(error -> log.error("<-| Error in updateCustomer: ID={}, error={}", id, error.getMessage(),
                error))
            .map(ResponseEntity::ok);
    }

    @NonNull
    @PatchMapping("/{id}")
    public Mono<ResponseEntity<CustomerOutput>> updateCustomerPartially(
        @PathVariable("id") @NonNull @NotNull @Min(1) final Long id,
        @RequestBody @NonNull @NotNull @Validated(PartialUpdate.class) final CustomerInput customer
    ) {
        log.info("|-> Request received for updateCustomerPartially: ID={}", id);
        return customerService.updateCustomerPartially(customer.withId(id))
            .doOnSuccess(customerOutput -> log.info("<-| Response ready for updateCustomerPartially: ID={}", id))
            .doOnError(error -> log.error("<-| Error in updateCustomerPartially: ID={}, error={}", id,
                error.getMessage(), error))
            .map(ResponseEntity::ok);
    }

    @NonNull
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<CustomerOutput>> deleteCustomer(@PathVariable("id") @NotNull @Min(1) final Long id) {
        log.info("|-> Request received for deleteCustomer: ID={}", id);
        return customerService.deleteCustomer(id)
            .doOnSuccess(customerOutput -> log.info("<-| Response ready for deleteCustomer: ID={}", id))
            .doOnError(error -> log.error("<-| Error in deleteCustomer: ID={}, error={}", id, error.getMessage(),
                error))
            .map(ResponseEntity::ok);
    }
}
