package com.banking.infrastructure.input.adapter.rest.error.resolver;

import com.banking.infrastructure.input.adapter.rest.error.bean.ErrorModel;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ResponseStatusException;

public class ResponseStatusExceptionResolver extends ErrorResolver<ErrorModel> {
    @Override
    protected int status() {
        return HttpStatus.NOT_FOUND.value();
    }

    @NonNull
    @Override
    protected ErrorModel buildError(@NonNull final String requestPath, @NonNull final Throwable throwable,
        @NonNull final String version) {
        final var responseStatusException = (ResponseStatusException) throwable;
        final var detail = responseStatusException.getMessage();
        final var instance = String.valueOf(responseStatusException.getStatusCode().value());
        return new ErrorModel()
            .title("Response error")
            .detail(detail)
            .instance(instance)
            .type(requestPath);
    }
}
