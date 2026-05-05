package com.banking.infrastructure.input.adapter.rest.error.resolver;

import com.banking.domain.exception.CodeException;
import com.banking.infrastructure.input.adapter.rest.error.bean.ErrorModel;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

public class NotFoundErrorResolver extends ErrorResolver<com.banking.infrastructure.input.adapter.rest.error.bean.ErrorModel> {
    @Override
    protected int status() {
        return HttpStatus.NOT_FOUND.value();
    }

    @NonNull
    @Override
    protected ErrorModel buildError(@NonNull final String requestPath, @NonNull final Throwable throwable,
        @NonNull final String version) {
        final var notFoundException = (CodeException) throwable;
        final var detail = notFoundException.getMessage();
        final var instance = String.valueOf(notFoundException.getCode());
        return new ErrorModel()
            .title("Not found")
            .detail(detail)
            .instance(instance)
            .type(requestPath);
    }
}
