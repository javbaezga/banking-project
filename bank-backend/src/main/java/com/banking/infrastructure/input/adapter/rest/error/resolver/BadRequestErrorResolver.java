package com.banking.infrastructure.input.adapter.rest.error.resolver;

import com.banking.domain.exception.CodeException;
import com.banking.infrastructure.input.adapter.rest.error.bean.ErrorModel;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

public class BadRequestErrorResolver extends ErrorResolver<ErrorModel> {
    @Override
    protected int status() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @NonNull
    @Override
    protected ErrorModel buildError(@NonNull final String requestPath, @NonNull final Throwable throwable,
        @NonNull final String version) {
        final var badRequestException = (CodeException) throwable;
        final var detail = badRequestException.getMessage();
        final var instance = badRequestException.getCode();
        return new ErrorModel()
            .title("Bad input")
            .detail(detail)
            .instance(instance)
            .type(requestPath);
    }
}
