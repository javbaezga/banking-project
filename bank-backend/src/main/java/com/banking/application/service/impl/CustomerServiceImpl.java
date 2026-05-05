package com.banking.application.service.impl;

import com.banking.application.input.port.CustomerService;
import com.banking.application.output.port.CustomerRepository;
import com.banking.domain.Customer;
import com.banking.domain.CustomerInput;
import com.banking.domain.CustomerOutput;
import com.banking.domain.CustomerQueryInput;
import com.banking.domain.enums.UpdateTypeEnum;
import com.banking.domain.exception.CodeException;
import com.banking.domain.exception.CustomerCreationException;
import com.banking.domain.exception.CustomerDeletionException;
import com.banking.domain.exception.CustomerGettingException;
import com.banking.domain.exception.CustomerNotFoundException;
import com.banking.domain.exception.CustomerUpdatingException;
import com.banking.domain.exception.CustomersQueryingException;
import com.banking.domain.mapper.CustomerMapper;
import com.banking.domain.util.MaskUtils;
import com.banking.domain.validation.constraints.IdNumber;
import com.banking.domain.validation.group.Create;
import com.banking.domain.validation.group.PartialUpdate;
import com.banking.domain.validation.group.Update;
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
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    CustomerRepository customerRepository;
    CustomerMapper customerMapper;

    @NonNull
    @Override
    public Mono<Page<CustomerOutput>> queryCustomers(@NonNull @NotNull @Valid final CustomerQueryInput queryInput) {
        final var queryInputString = queryInput.toString();
        log.info("|--> Querying customers: input={}", queryInputString);
        return customerRepository.queryCustomers(queryInput)
            .map(customersPage -> customersPage.map(customerMapper::toCustomerOutput))
            .doOnSuccess(customerOutputsPage -> log.info("<--| Customers were queried: input={}", queryInputString))
            .doOnError(error -> log.error("<--| Error querying customers: input={}, error={}", queryInputString,
                error.getMessage()))
            .onErrorMap(error -> !(error instanceof CodeException), error -> new CustomersQueryingException());
    }

    @NonNull
    private static Mono<Customer> getCustomerAndValidateIfEmpty(@NonNull final Mono<Customer> getCustomerInputMono) {
        return getCustomerInputMono.switchIfEmpty(Mono.error(CustomerNotFoundException::new));
    }

    @NonNull
    private Mono<Customer> getCustomerByIdAndValidateIfEmpty(@NonNull final Long customerId) {
        return getCustomerAndValidateIfEmpty(customerRepository.getCustomerById(customerId));
    }

    @NonNull
    @Override
    public Mono<CustomerOutput> getCustomerById(@NotNull @Min(1L) final Long customerId) {
        log.info("|--> Getting customer: ID={}", customerId);
        return getCustomerByIdAndValidateIfEmpty(customerId)
            .map(customerMapper::toCustomerOutput)
            .doOnSuccess(customerOutput -> log.info("<--| Customer was gotten: ID={}", customerId))
            .doOnError(error -> log.error("<--| Error getting customer: ID={}, error={}", customerId,
                error.getMessage()))
            .onErrorMap(error -> !(error instanceof CodeException), error -> new CustomerGettingException());
    }

    @NonNull
    @Override
    public Mono<CustomerOutput> getCustomerByIdNumber(@NotBlank @IdNumber final String idNumber) {
        final var maskedIdNumber = MaskUtils.maskIdNumber(idNumber);
        log.info("|--> Getting customer: ID number={}", maskedIdNumber);
        return getCustomerAndValidateIfEmpty(customerRepository.getCustomerByIdNumber(idNumber))
            .map(customerMapper::toCustomerOutput)
            .doOnSuccess(customerOutput -> log.info("<--| Customer was gotten: ID number={}",
                maskedIdNumber))
            .doOnError(error -> log.error("<--| Error getting customer: ID number={}, error={}",
                maskedIdNumber, error.getMessage()))
            .onErrorMap(error -> !(error instanceof CodeException), error -> new CustomerGettingException());
    }

    @NonNull
    @Override
    public Mono<CustomerOutput> createCustomer(
        @NonNull @NotNull @Validated(Create.class) final CustomerInput customerInput) {
        final var maskedIdNumber = MaskUtils.maskIdNumber(customerInput.getIdNumber());
        log.info("|--> Creating customer: ID number={}", maskedIdNumber);
        return Mono.fromCallable(() -> customerMapper.toCustomer(customerInput))
            .flatMap(customerRepository::createCustomer)
            .map(customerMapper::toCustomerOutput)
            .doOnSuccess(customerOutput -> log.info("<--| Customer was created: ID number={}, ID={}",
                maskedIdNumber, customerOutput.getId()))
            .doOnError(error -> log.error("<--| Error creating customer: ID number={}, error={}",
                maskedIdNumber, error.getMessage()))
            .onErrorMap(error -> new CustomerCreationException());
    }

    @NonNull
    public Mono<CustomerOutput> updateCustomer(@NonNull final CustomerInput customerInput,
        @NonNull final UpdateTypeEnum updateType) {
        final var customerId = customerInput.getId();
        log.info("|--> Updating customer: ID={}, update type={}", customerId, updateType);
        return getCustomerByIdAndValidateIfEmpty(customerId)
            .flatMap(currentCustomer ->
                Mono.just(customerMapper.toCustomer(customerInput))
                    .flatMap(customer ->
                        Mono.just(customerMapper.mergeCustomers(currentCustomer, customer))
                            .flatMap(customerRepository::updateCustomer)
                    )
            )
            .map(customerMapper::toCustomerOutput)
            .doOnSuccess(customerOutput -> log.info("<--| Customer was updated: ID={}, update type={}", customerId,
                updateType))
            .doOnError(error -> log.error("<--| Error updating customer: ID={}, update type={}, error={}", customerId,
                updateType, error.getMessage()))
            .onErrorMap(error -> !(error instanceof CodeException), error -> new CustomerUpdatingException());
    }

    @NonNull
    @Override
    public Mono<CustomerOutput> updateCustomer(
        @NonNull @NotNull @Validated(Update.class) final CustomerInput customerInput) {
        return updateCustomer(customerInput, UpdateTypeEnum.FULL);
    }

    @NonNull
    @Override
    public Mono<CustomerOutput> updateCustomerPartially(
        @NonNull @NotNull @Validated(PartialUpdate.class) final CustomerInput customerInput) {
        return updateCustomer(customerInput, UpdateTypeEnum.PARTIAL);
    }

    @NonNull
    @Override
    public Mono<CustomerOutput> deleteCustomer(@NotNull @Min(1L) final Long customerId) {
        log.info("|--> Deleting customer: ID={}", customerId);
        return getCustomerByIdAndValidateIfEmpty(customerId)
            .flatMap(customer ->
                customerRepository.deleteCustomer(customer.getId())
                    .thenReturn(customer)
            )
            .map(customerMapper::toCustomerOutput)
            .doOnSuccess(customerOutput -> log.info("<--| Customer was deleted: ID={}", customerId))
            .doOnError(error -> log.error("<--| Error deleting customer: ID={}, error={}", customerId,
                error.getMessage()))
            .onErrorMap(error -> !(error instanceof CodeException), error -> new CustomerDeletionException());
    }
}
