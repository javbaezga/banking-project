package com.banking.domain.mapper;

import com.banking.domain.Account;
import com.banking.domain.Transaction;
import com.banking.domain.TransactionInput;
import com.banking.domain.TransactionOutput;
import com.banking.domain.enums.TransactionTypeEnum;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.lang.NonNull;

@Mapper(componentModel = "spring", imports = LocalDate.class)
public interface TransactionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", source = "account")
    @Mapping(target = "date", expression = "java(LocalDate.now())")
    @Mapping(target = "type", source = "transactionInput.value", qualifiedByName = "toTransactionTypeEnum")
    @Mapping(target = "value", source = "transactionInput.value")
    @Mapping(target = "description", source = "transactionInput.description")
    @Mapping(target = "balance", source = "account.balance")
    @Mapping(target = "status", constant = "true")
    Transaction toTransaction(TransactionInput transactionInput, Account account);

    @NonNull
    @Named("toTransactionTypeEnum")
    default TransactionTypeEnum toTransactionTypeEnum(@NonNull final BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) >= 0) {
            return TransactionTypeEnum.CREDIT;
        }
        return TransactionTypeEnum.DEBIT;
    }

    TransactionOutput toTransactionOutput(Transaction transaction);
}
