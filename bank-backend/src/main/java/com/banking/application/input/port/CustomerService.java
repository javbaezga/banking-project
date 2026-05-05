package com.banking.application.input.port;

import com.banking.domain.CustomerInput;
import com.banking.domain.CustomerOutput;
import com.banking.domain.CustomerQueryInput;
import com.banking.domain.validation.constraints.IdNumber;
import com.banking.domain.validation.group.Create;
import com.banking.domain.validation.group.PartialUpdate;
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
public interface CustomerService {
    @NonNull
    Mono<Page<CustomerOutput>> queryCustomers(@NotNull @Valid CustomerQueryInput queryInput);

    @NonNull
    Mono<CustomerOutput> getCustomerById(@NotNull @Min(1L) Long customerId);

    @NonNull
    Mono<CustomerOutput> getCustomerByIdNumber(@NotBlank @IdNumber String idNumber);

    @NonNull
    Mono<CustomerOutput> createCustomer(@NotNull @Validated(Create.class) CustomerInput customer);

    @NonNull
    Mono<CustomerOutput> updateCustomer(@NotNull @Validated(Update.class) CustomerInput customer);

    @NonNull
    Mono<CustomerOutput> updateCustomerPartially(@NotNull @Validated(PartialUpdate.class) CustomerInput customer);

    @NonNull
    Mono<CustomerOutput> deleteCustomer(@NotNull @Min(1L) Long customerId);
}
