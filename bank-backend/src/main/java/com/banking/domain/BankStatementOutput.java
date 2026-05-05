package com.banking.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BankStatementOutput {
    @EqualsAndHashCode.Include
    Long id;
    LocalDate date;
    String customer;
    String accountNumber;
    String type;
    BigDecimal initialBalance;
    Boolean status;
    BigDecimal value;
    BigDecimal balance;
}
