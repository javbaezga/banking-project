package com.banking.infrastructure.output.adapter.mapper;

import com.banking.domain.Transaction;
import com.banking.domain.enums.TransactionTypeEnum;
import com.banking.infrastructure.output.repository.entity.TransactionEntity;
import java.time.LocalDate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.lang.Nullable;

@Mapper(
    componentModel = "spring",
    imports = LocalDate.class,
    uses = AccountEntityMapper.class
)
public interface TransactionEntityMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "account", source = "account", qualifiedByName = "toAccount")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "type", source = "type", qualifiedByName = "toTransactionTypeEnum")
    @Mapping(target = "value", source = "value")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "status", source = "status")
    Transaction toTransaction(TransactionEntity transactionEntity);

    @Nullable
    @Named("toTransactionTypeEnum")
    default TransactionTypeEnum toTransactionTypeEnum(@Nullable final String transactionType) {
        return TransactionTypeEnum.fromValue(transactionType);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", source = "account", qualifiedByName = "toAccountEntity")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "type", source = "type", qualifiedByName = "toTransactionTypeValue")
    @Mapping(target = "value", source = "value")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "status", source = "status")
    TransactionEntity toTransactionEntity(Transaction transaction);

    @Nullable
    @Named("toTransactionTypeValue")
    default String toTransactionTypeValue(@Nullable final TransactionTypeEnum transactionTypeEnum) {
        if (transactionTypeEnum == null) {
            return null;
        }
        return transactionTypeEnum.value();
    }
}
