package com.banking.infrastructure.input.adapter.rest.mapper;

import com.banking.domain.BankStatementInput;
import com.banking.domain.BankStatementPdfInput;
import java.time.LocalDate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    BankStatementInput toBankStatementInput(
        Long customerId,
        LocalDate startDate,
        LocalDate endDate,
        Integer page,
        Integer size
    );

    BankStatementPdfInput toBankStatementPdfInput(Long customerId, LocalDate startDate, LocalDate endDate);
}
