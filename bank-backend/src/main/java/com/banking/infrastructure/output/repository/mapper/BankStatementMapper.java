package com.banking.infrastructure.output.repository.mapper;

import com.banking.domain.BankStatementOutput;
import com.banking.infrastructure.output.repository.projection.BankStatementProjection;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BankStatementMapper {
    BankStatementOutput toBankStatementOutput(BankStatementProjection bankStatementProjection);
}
