package com.banking.infrastructure.input.adapter.rest.error.resolver;

import com.banking.infrastructure.input.adapter.rest.error.bean.ErrorModel;
import com.banking.infrastructure.util.ErrorUtils;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

public class ConstraintViolationExceptionResolver extends ErrorResolver<ErrorModel> {
    @Override
    protected int status() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @NonNull
    @Override
    protected ErrorModel buildError(
        @NonNull final String requestPath,
        @NonNull final Throwable throwable,
        @NonNull final String version
    ) {
        final var constraintViolationException = (ConstraintViolationException) throwable;
        final var errors = ErrorUtils.buildErrors(constraintViolationException);
        final var instance = String.valueOf(status());
        return new ErrorModel()
            .title("Bad input")
            .detail("The input data is invalid")
            .errors(errors)
            .instance(instance)
            .type(requestPath);
    }
}
