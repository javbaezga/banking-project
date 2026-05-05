package com.banking.domain;

import com.banking.domain.enums.TransactionTypeEnum;
import com.banking.domain.validation.constraints.DecimalNonZero;
import com.banking.domain.validation.group.Create;
import com.banking.domain.validation.group.Update;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Transaction {
    @Null(groups = {Create.class})
    @NotNull(groups = {Update.class})
    @Min(1L)
    @EqualsAndHashCode.Include
    Long id;
    @NotNull
    @Valid
    Account account;
    @NotNull
    LocalDate date;
    @NotNull
    TransactionTypeEnum type;
    @NotNull
    @DecimalNonZero
    @DecimalMin("-999999999999.99")
    @DecimalMax("999999999999.99")
    @Digits(integer = 12, fraction = 2)
    BigDecimal value;
    @NotBlank
    @Size(max = 50)
    String description;
    @NotNull
    @DecimalNonZero
    @DecimalMin("-999999999999.99")
    @DecimalMax("999999999999.99")
    @Digits(integer = 12, fraction = 2)
    BigDecimal balance;
    @NotNull
    Boolean status;

    @NonNull
    public Transaction updateBalances() {
        final var transactionValue = getValue();
        final var newBalance = getBalance().add(transactionValue);
        final var transactionAccount = getAccount();
        final var accountBuilder = transactionAccount.toBuilder().balance(newBalance);
        if (transactionValue.compareTo(BigDecimal.ZERO) > 0) {
            final var newDailyBalance = transactionAccount.getDailyBalance().subtract(transactionValue);
            accountBuilder.dailyBalance(newDailyBalance);
        }
        return toBuilder().balance(newBalance)
            .account(accountBuilder.build())
            .build();
    }
}
