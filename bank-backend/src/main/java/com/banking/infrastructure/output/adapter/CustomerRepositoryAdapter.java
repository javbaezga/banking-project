package com.banking.infrastructure.output.adapter;

import com.banking.application.output.port.CustomerRepository;
import com.banking.domain.Customer;
import com.banking.domain.CustomerQueryInput;
import com.banking.domain.util.MaskUtils;
import com.banking.domain.validation.constraints.IdNumber;
import com.banking.domain.validation.group.Create;
import com.banking.domain.validation.group.Update;
import com.banking.infrastructure.output.adapter.mapper.CustomerEntityMapper;
import com.banking.infrastructure.output.repository.CustomerJpaRepository;
import com.banking.infrastructure.output.repository.entity.CustomerEntity;
import com.banking.infrastructure.output.repository.specification.CustomerSpecification;
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
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CustomerRepositoryAdapter implements CustomerRepository {
    CustomerJpaRepository customerJpaRepository;
    CustomerEntityMapper customerEntityMapper;

    @NonNull
    @Override
    public Mono<Page<Customer>> queryCustomers(@NonNull @NotNull @Valid final CustomerQueryInput queryInput) {
        final var queryInputString = queryInput.toString();
        log.info("Querying customer entities: input={}", queryInputString);
        return Mono.fromCallable(() ->
                customerJpaRepository.findAll(
                    CustomerSpecification.search(queryInput.getSearchTerm()),
                    queryInput.toPageable()
                )
            )
            .map(customerEntitiesPage -> customerEntitiesPage.map(customerEntityMapper::toCustomer))
            .doOnSuccess(customersPage -> log.info("Customer entities were queried: input={}", queryInputString))
            .doOnError(error -> log.error("Error querying customer entities: input={}, error={}", queryInputString,
                error.getMessage()));
    }

    @NonNull
    private Mono<CustomerEntity> findCustomerEntityById(@NonNull final Long customerId) {
        return EntityUtils.entityToMono(() -> customerJpaRepository.findById(customerId));
    }

    @NonNull
    @Override
    public Mono<Customer> getCustomerById(@NotNull @Min(1L) final Long customerId) {
        log.info("Getting customer entity: ID={}", customerId);
        return findCustomerEntityById(customerId)
            .map(customerEntityMapper::toCustomer)
            .doOnSuccess(customer -> log.info("Customer entity was gotten and mapped: ID={}", customerId))
            .doOnError(error -> log.error("Error getting customer entity: ID={}, error={}", customerId,
                error.getMessage()));
    }

    @NonNull
    @Override
    public Mono<Customer> getCustomerByIdNumber(@NotBlank @IdNumber final String idNumber) {
        final var maskedIdNumber = MaskUtils.maskIdNumber(idNumber);
        log.info("Getting customer entity: ID number={}", maskedIdNumber);
        return EntityUtils.entityToMono(() -> customerJpaRepository.findByIdNumber(idNumber))
            .map(customerEntityMapper::toCustomer)
            .doOnSuccess(customer -> log.info("Customer entity was gotten and mapped: ID number={}", maskedIdNumber))
            .doOnError(error -> log.error("Error getting customer entity: ID number={}, error={}",
                maskedIdNumber, error.getMessage()));
    }

    @NonNull
    private Mono<Customer> saveCustomer(@NonNull final Customer customer) {
        return Mono.fromCallable(() -> customerEntityMapper.toCustomerEntity(customer))
            .map(customerJpaRepository::save)
            .map(customerEntityMapper::toCustomer);
    }

    @NonNull
    @Override
    public Mono<Customer> createCustomer(@NonNull @NotNull @Validated(Create.class) final Customer customer) {
        final var maskedIdNumber = MaskUtils.maskIdNumber(customer.getIdNumber());
        log.info("Creating customer entity: ID number={}", maskedIdNumber);
        return saveCustomer(customer)
            .doOnSuccess(createdCustomer -> log.info("Customer entity was created and mapped: ID number={}, ID={}",
                maskedIdNumber, createdCustomer.getId()))
            .doOnError(error -> log.error("Error creating customer: ID number={}, error={}", maskedIdNumber,
                error.getMessage()));
    }

    @NonNull
    @Override
    public Mono<Customer> updateCustomer(@NonNull @NotNull @Validated(Update.class) final Customer customer) {
        final var customerId = customer.getId();
        log.info("Updating customer entity: ID={}", customerId);
        return saveCustomer(customer)
            .doOnSuccess(updatedCustomer -> log.info("Customer entity was updated and mapped: ID={}", customerId))
            .doOnError(error -> log.error("Error updating customer entity: ID={}, error={}", customerId,
                error.getMessage()));
    }

    @NonNull
    @Override
    public Mono<Long> deleteCustomer(@NotNull @Min(1L) final Long customerId) {
        log.info("Deleting customer entity: ID={}", customerId);
        return Mono.fromRunnable(() -> customerJpaRepository.deleteById(customerId))
            .thenReturn(customerId)
            .doOnSuccess(theCustomerId -> log.info("Customer entity was deleted: ID={}", theCustomerId))
            .doOnError(error -> log.error("Error deleting customer entity: ID={}, error={}", customerId,
                error.getMessage()));
    }
}
