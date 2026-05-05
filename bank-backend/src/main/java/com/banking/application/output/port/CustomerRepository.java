package com.banking.application.output.port;

import com.banking.domain.Customer;
import com.banking.domain.CustomerQueryInput;
import com.banking.domain.validation.constraints.IdNumber;
import com.banking.domain.validation.group.Create;
import com.banking.domain.validation.group.Update;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

@Validated
public interface CustomerRepository {
    @NonNull
    Mono<Page<Customer>> queryCustomers(@NotNull @Valid CustomerQueryInput queryInput);

    @NonNull
    Mono<Customer> getCustomerById(@NotNull @Min(1L) Long customerId);

    @NonNull
    Mono<Customer> getCustomerByIdNumber(@NotBlank @IdNumber String idNumber);

    @NonNull
    Mono<Customer> createCustomer(@NotNull @Validated(Create.class) Customer customer);

    @NonNull
    Mono<Customer> updateCustomer(@NotNull @Validated(Update.class) Customer customer);

    @NonNull
    Mono<Long> deleteCustomer(@NotNull @Min(1L) Long customerId);
}
