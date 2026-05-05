package com.banking.infrastructure.output.adapter.mapper;

import com.banking.domain.Account;
import com.banking.domain.enums.AccountTypeEnum;
import com.banking.infrastructure.output.repository.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.lang.Nullable;

@Mapper(componentModel = "spring", uses = CustomerEntityMapper.class)
public interface AccountEntityMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "toCustomerEntity")
    @Mapping(target = "number", source = "number")
    @Mapping(target = "type", source = "type", qualifiedByName = "toAccountTypeValue")
    @Mapping(target = "initialBalance", source = "initialBalance")
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "dailyBalance", source = "dailyBalance")
    @Mapping(target = "dailyBalanceResetDate", source = "dailyBalanceResetDate")
    @Mapping(target = "status", source = "status")
    @Named("toAccountEntity")
    AccountEntity toAccountEntity(Account account);

    @Nullable
    @Named("toAccountTypeValue")
    default String toAccountTypeValue(@Nullable final AccountTypeEnum accountTypeEnum) {
        if (accountTypeEnum == null) {
            return null;
        }
        return accountTypeEnum.value();
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "toCustomer")
    @Mapping(target = "number", source = "number")
    @Mapping(target = "type", source = "type", qualifiedByName = "toAccountTypeEnum")
    @Mapping(target = "initialBalance", source = "initialBalance")
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "dailyBalance", source = "dailyBalance")
    @Mapping(target = "dailyBalanceResetDate", source = "dailyBalanceResetDate")
    @Mapping(target = "status", source = "status")
    @Named("toAccount")
    Account toAccount(AccountEntity accountEntity);

    @Nullable
    @Named("toAccountTypeEnum")
    default AccountTypeEnum toAccountTypeEnum(@Nullable final String accountType) {
        return AccountTypeEnum.fromValue(accountType);
    }
}
