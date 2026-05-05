package com.banking.domain.mapper;

import com.banking.domain.Account;
import com.banking.domain.AccountInput;
import com.banking.domain.AccountOutput;
import com.banking.domain.Customer;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(target = "id", source = "accountInput.id")
    @Mapping(target = "customer", source = "customer")
    @Mapping(target = "number", source = "accountInput.number")
    @Mapping(target = "type", source = "accountInput.type")
    @Mapping(target = "initialBalance", source = "accountInput.initialBalance")
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "dailyBalance", ignore = true)
    @Mapping(target = "dailyBalanceResetDate", ignore = true)
    @Mapping(target = "status", source = "accountInput.status")
    Account toAccount(AccountInput accountInput, Customer customer);

    AccountOutput toAccountOutput(Account account);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initializeBalances", ignore = true)
    Account mergeAccounts(@MappingTarget Account targetAccount, Account sourceAccount);
}
