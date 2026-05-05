package com.banking.infrastructure.input.adapter.rest.error.resolver;

import com.banking.infrastructure.input.adapter.rest.error.bean.ErrorModel;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

public class UnexpectedErrorResolver extends ErrorResolver<ErrorModel> {
    @Override
    protected int status() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    @NonNull
    @Override
    protected ErrorModel buildError(@NonNull final String requestPath, @NonNull final Throwable throwable,
        @NonNull final String version) {
        final var detail = throwable.getMessage();
        final var instance = String.valueOf(status());
        return new ErrorModel()
            .title("Unexpected error")
            .detail(detail)
            .instance(instance)
            .type(requestPath);
    }
}
