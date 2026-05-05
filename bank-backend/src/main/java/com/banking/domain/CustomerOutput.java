package com.banking.domain;

import com.banking.domain.enums.GenderEnum;
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
public class CustomerOutput {
    @EqualsAndHashCode.Include
    Long id;
    String fullName;
    GenderEnum gender;
    Byte age;
    String idNumber;
    String address;
    String phone;
    String username;
    Boolean status;
}
