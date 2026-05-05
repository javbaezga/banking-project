package com.banking.infrastructure.output.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface BankStatementProjection {
    Long getId();
    LocalDate getDate();
    String getCustomer();
    String getAccountNumber();
    String getType();
    BigDecimal getInitialBalance();
    Boolean getStatus();
    BigDecimal getValue();
    BigDecimal getBalance();
}
