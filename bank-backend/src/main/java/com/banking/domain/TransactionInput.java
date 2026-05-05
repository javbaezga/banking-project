package com.banking.domain;

import com.banking.domain.validation.constraints.AccountNumber;
import com.banking.domain.validation.constraints.DecimalNonZero;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
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
public class TransactionInput {
    @NotBlank
    @AccountNumber
    @EqualsAndHashCode.Include
    String accountNumber;
    @NotNull
    @DecimalNonZero
    @DecimalMin("-999999999999.99")
    @DecimalMax("999999999999.99")
    @Digits(integer = 12, fraction = 2)
    BigDecimal value;
    @NotBlank
    @Size(max = 50)
    String description;
}
