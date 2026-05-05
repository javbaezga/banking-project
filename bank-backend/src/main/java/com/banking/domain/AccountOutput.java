package com.banking.domain;

import com.banking.domain.enums.AccountTypeEnum;
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
public class AccountOutput {
    @EqualsAndHashCode.Include
    Long id;
    CustomerOutput customer;
    String number;
    AccountTypeEnum type;
    BigDecimal initialBalance;
    BigDecimal balance;
    BigDecimal dailyBalance;
    LocalDate dailyBalanceResetDate;
    Boolean status;
}
