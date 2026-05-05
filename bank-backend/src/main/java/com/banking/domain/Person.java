package com.banking.domain;

import com.banking.domain.enums.GenderEnum;
import com.banking.domain.validation.constraints.Age;
import com.banking.domain.validation.constraints.IdNumber;
import com.banking.domain.validation.constraints.Phone;
import com.banking.domain.validation.group.Create;
import com.banking.domain.validation.group.Update;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Person {
    @Null(groups = {Create.class})
    @NotNull(groups = {Update.class})
    @Min(1L)
    @EqualsAndHashCode.Include
    Long id;
    @NotBlank
    @Size(min = 1, max = 100)
    String fullName;
    @NotNull
    GenderEnum gender;
    @NotNull
    @Age
    Byte age;
    @NotBlank
    @IdNumber
    String idNumber;
    @NotBlank
    @Size(min = 1, max = 255)
    String address;
    @NotBlank
    @Phone
    String phone;
}
