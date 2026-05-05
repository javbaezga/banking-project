package com.banking.domain;

import com.banking.domain.enums.GenderEnum;
import com.banking.domain.validation.constraints.Age;
import com.banking.domain.validation.constraints.IdNumber;
import com.banking.domain.validation.constraints.Phone;
import com.banking.domain.validation.group.Create;
import com.banking.domain.validation.group.PartialUpdate;
import com.banking.domain.validation.group.Update;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

@NonFinal
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PersonInput {
    @Min(value = 1L, groups = {Update.class, PartialUpdate.class})
    @EqualsAndHashCode.Include
    Long id;
    @NotBlank(groups = {Create.class, Update.class})
    @Size(min = 1, max = 100, groups = {Create.class, Update.class, PartialUpdate.class})
    String fullName;
    @NotNull(groups = {Create.class, Update.class})
    GenderEnum gender;
    @NotNull(groups = {Create.class, Update.class})
    @Age(groups = {Create.class, Update.class, PartialUpdate.class})
    Byte age;
    @NotBlank(groups = {Create.class, Update.class})
    @IdNumber(groups = {Create.class, Update.class, PartialUpdate.class})
    String idNumber;
    @NotBlank(groups = {Create.class, Update.class})
    @Size(min = 1, max = 255, groups = {Create.class, Update.class, PartialUpdate.class})
    String address;
    @NotBlank(groups = {Create.class, Update.class})
    @Phone(groups = {Create.class, Update.class, PartialUpdate.class})
    String phone;
}
