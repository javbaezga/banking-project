package com.banking.domain;

import com.banking.domain.validation.group.Create;
import com.banking.domain.validation.group.PartialUpdate;
import com.banking.domain.validation.group.Update;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class CustomerInput extends PersonInput {
    @NotBlank(groups = {Create.class, Update.class})
    @Size(min = 8, max = 25, groups = {Create.class, Update.class, PartialUpdate.class})
    String username;
    @NotBlank(groups = {Create.class, Update.class})
    @Size(min = 8, max = 50, groups = {Create.class, Update.class, PartialUpdate.class})
    String password;
    @NotNull(groups = {Create.class, Update.class})
    Boolean status;

    @NonNull
    public CustomerInput withId(@Nullable final Long id) {
        if (Objects.equals(getId(), id)) {
            return this;
        }
        return toBuilder().id(id).build();
    }
}
