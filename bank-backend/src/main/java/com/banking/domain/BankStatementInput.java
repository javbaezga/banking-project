package com.banking.domain;

import com.banking.domain.util.Constants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class BankStatementInput extends AbstractBankStatementInput {
    @NotNull
    @Min(Constants.QUERY_MINIMUM_PAGE)
    Integer page;
    @NotNull
    @Min(Constants.QUERY_PAGE_MINIMUM_SIZE)
    @Max(Constants.BANK_STATEMENT_MAXIMUM_PAGE_SIZE)
    Integer size;
}
