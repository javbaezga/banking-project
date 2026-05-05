package com.banking.infrastructure.input.adapter.rest.mapper;

import com.banking.domain.AccountQueryInput;
import com.banking.domain.CustomerQueryInput;
import com.banking.domain.TransactionQueryInput;
import com.banking.domain.enums.AccountQuerySortByFieldEnum;
import com.banking.domain.enums.CustomerQuerySortByFieldEnum;
import com.banking.domain.enums.QuerySortDirectionEnum;
import com.banking.domain.enums.TransactionQuerySortByFieldEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.lang.Nullable;

@Mapper(componentModel = "spring")
public interface QueryInputMapper {
    @Mapping(target = "searchTerm", source = "searchTerm")
    @Mapping(target = "sortBy", source = "sortBy", qualifiedByName = "toCustomerQuerySortByFieldEnum")
    @Mapping(target = "sortDirection", source = "sortDirection", qualifiedByName = "toQuerySortDirectionEnum")
    @Mapping(target = "page", source = "page")
    @Mapping(target = "size", source = "size")
    CustomerQueryInput toCustomerQueryInput(String searchTerm, String sortBy, String sortDirection, Integer page,
        Integer size);

    @Nullable
    @Named("toCustomerQuerySortByFieldEnum")
    default CustomerQuerySortByFieldEnum toCustomerQuerySortByFieldEnum(@Nullable final String sortBy) {
        return CustomerQuerySortByFieldEnum.fromValue(sortBy);
    }

    @Nullable
    @Named("toQuerySortDirectionEnum")
    default QuerySortDirectionEnum toQuerySortDirectionEnum(@Nullable final String sortDirection) {
        return QuerySortDirectionEnum.fromValue(sortDirection);
    }

    @Mapping(target = "searchTerm", source = "searchTerm")
    @Mapping(target = "sortBy", source = "sortBy", qualifiedByName = "toAccountQuerySortByFieldEnum")
    @Mapping(target = "sortDirection", source = "sortDirection", qualifiedByName = "toQuerySortDirectionEnum")
    @Mapping(target = "page", source = "page")
    @Mapping(target = "size", source = "size")
    AccountQueryInput toAccountQueryInput(String searchTerm, String sortBy, String sortDirection, Integer page,
        Integer size);

    @Nullable
    @Named("toAccountQuerySortByFieldEnum")
    default AccountQuerySortByFieldEnum toAccountQuerySortByFieldEnum(@Nullable final String sortBy) {
        return AccountQuerySortByFieldEnum.fromValue(sortBy);
    }

    @Mapping(target = "searchTerm", source = "searchTerm")
    @Mapping(target = "sortBy", source = "sortBy", qualifiedByName = "toTransactionQuerySortByFieldEnum")
    @Mapping(target = "sortDirection", source = "sortDirection", qualifiedByName = "toQuerySortDirectionEnum")
    @Mapping(target = "page", source = "page")
    @Mapping(target = "size", source = "size")
    TransactionQueryInput toTransactionQueryInput(String searchTerm, String sortBy, String sortDirection, Integer page,
        Integer size);

    @Nullable
    @Named("toTransactionQuerySortByFieldEnum")
    default TransactionQuerySortByFieldEnum toTransactionQuerySortByFieldEnum(@Nullable final String sortBy) {
        return TransactionQuerySortByFieldEnum.fromValue(sortBy);
    }
}
