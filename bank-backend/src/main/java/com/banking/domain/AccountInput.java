package com.banking.domain;

import com.banking.domain.enums.AccountTypeEnum;
import com.banking.domain.validation.constraints.AccountNumber;
import com.banking.domain.validation.group.Create;
import com.banking.domain.validation.group.PartialUpdate;
import com.banking.domain.validation.group.Update;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.experimental.FieldDefaults;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AccountInput {
    @Min(value = 1L, groups = {Update.class, PartialUpdate.class})
    @With
    @EqualsAndHashCode.Include
    Long id;
    @NotNull(groups = {Create.class, Update.class})
    @Min(value = 1L, groups = {Create.class, Update.class, PartialUpdate.class})
    Long customerId;
    @NotBlank(groups = {Create.class, Update.class})
    @AccountNumber(groups = {Create.class, Update.class, PartialUpdate.class})
    String number;
    @NotNull(groups = {Create.class, Update.class})
    AccountTypeEnum type;
    @NotNull(groups = {Create.class, Update.class})
    @DecimalMin(value = "-999999999999.99", groups = {Create.class, Update.class, PartialUpdate.class})
    @DecimalMax(value = "999999999999.99", groups = {Create.class, Update.class, PartialUpdate.class})
    @Digits(integer = 12, fraction = 2, groups = {Create.class, Update.class, PartialUpdate.class})
    BigDecimal initialBalance;
    @NotNull(groups = {Create.class, Update.class})
    Boolean status;
}
