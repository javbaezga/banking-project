package com.banking.domain;

import com.banking.domain.enums.TransactionTypeEnum;
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
public class TransactionOutput {
    @EqualsAndHashCode.Include
    Long id;
    AccountOutput account;
    LocalDate date;
    TransactionTypeEnum type;
    BigDecimal value;
    String description;
    BigDecimal balance;
    Boolean status;
}
