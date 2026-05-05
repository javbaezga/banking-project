package com.banking.infrastructure.input.adapter.rest.error.resolver;

import com.banking.domain.exception.CodeException;
import com.banking.infrastructure.input.adapter.rest.error.bean.ErrorModel;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

public class CodeExceptionResolver extends ErrorResolver<ErrorModel> {
    @Override
    protected int status() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    @NonNull
    @Override
    protected ErrorModel buildError(
        @NonNull final String requestPath,
        @NonNull final Throwable throwable,
        @NonNull final String version
    ) {
        final var codeException = (CodeException) throwable;
        final var detail = codeException.getMessage();
        final var instance = codeException.getCode();
        return new ErrorModel()
            .title("Unexpected error")
            .detail(detail)
            .instance(instance)
            .type(requestPath);
    }
}
