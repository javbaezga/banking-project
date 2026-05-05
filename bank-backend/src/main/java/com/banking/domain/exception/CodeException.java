package com.banking.domain.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CodeException extends RuntimeException {
    @SuppressWarnings("java:S1068")
    String code;

    public CodeException(@NonNull final String code, @NonNull final String message) {
        super(message);
        this.code = code;
    }
}
