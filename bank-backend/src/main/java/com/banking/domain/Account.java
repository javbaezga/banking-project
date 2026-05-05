package com.banking.domain;

import com.banking.domain.enums.AccountTypeEnum;
import com.banking.domain.validation.constraints.AccountNumber;
import com.banking.domain.validation.group.Create;
import com.banking.domain.validation.group.PartialUpdate;
import com.banking.domain.validation.group.Update;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
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
public class Account {
    @Null(groups = {Create.class})
    @NotNull(groups = {Update.class})
    @Min(1L)
    @EqualsAndHashCode.Include
    Long id;
    @NotNull
    @Valid
    Customer customer;
    @NotBlank
    @AccountNumber
    String number;
    @NotNull
    AccountTypeEnum type;
    @NotNull
    @DecimalMin("-999999999999.99")
    @DecimalMax("999999999999.99")
    @Digits(integer = 12, fraction = 2)
    BigDecimal initialBalance;
    @NotNull(groups = {Update.class, PartialUpdate.class})
    @DecimalMin("-999999999999.99")
    @DecimalMax("999999999999.99")
    @Digits(integer = 12, fraction = 2)
    BigDecimal balance;
    @NotNull(groups = {Update.class, PartialUpdate.class})
    @DecimalMin("-999999999.99")
    @DecimalMax("999999999.99")
    @Digits(integer = 9, fraction = 2)
    BigDecimal dailyBalance;
    @NotNull(groups = {Update.class, PartialUpdate.class})
    LocalDate dailyBalanceResetDate;
    @NotNull
    Boolean status;

    @NonNull
    public Account initializeBalances(@NonNull final BigDecimal dailyBalance) {
        setBalance(getInitialBalance());
        setDailyBalance(dailyBalance);
        setDailyBalanceResetDate(LocalDate.now());
        return this;
    }
}
