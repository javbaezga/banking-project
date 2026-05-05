package com.banking.domain.mapper;

import com.banking.domain.Customer;
import com.banking.domain.CustomerInput;
import com.banking.domain.CustomerOutput;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer toCustomer(CustomerInput customerInput);

    CustomerOutput toCustomerOutput(Customer customer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    Customer mergeCustomers(@MappingTarget Customer targetCustomer, Customer sourceCustomer);
}
