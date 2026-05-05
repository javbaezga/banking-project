package com.banking.infrastructure.output.adapter.mapper;

import com.banking.domain.Customer;
import com.banking.domain.enums.GenderEnum;
import com.banking.infrastructure.output.repository.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Mapper(componentModel = "spring")
public interface CustomerEntityMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "gender", source = "gender", qualifiedByName = "toGenderValue")
    @Mapping(target = "age", source = "age")
    @Mapping(target = "idNumber", source = "idNumber")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "status", source = "status")
    @Named("toCustomerEntity")
    CustomerEntity toCustomerEntity(Customer customer);

    @Nullable
    @Named("toGenderValue")
    default String toGenderValue(@Nullable final GenderEnum genderEnum) {
        if (genderEnum == null) {
            return null;
        }
        return genderEnum.value();
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "gender", source = "gender", qualifiedByName = "toGenderEnum")
    @Mapping(target = "age", source = "age")
    @Mapping(target = "idNumber", source = "idNumber")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "status", source = "status")
    @Named("toCustomer")
    Customer toCustomer(CustomerEntity customerEntity);

    @NonNull
    @Named("toGenderEnum")
    default GenderEnum toGenderEnum(@Nullable final String gender) {
        return GenderEnum.fromValue(gender);
    }
}
