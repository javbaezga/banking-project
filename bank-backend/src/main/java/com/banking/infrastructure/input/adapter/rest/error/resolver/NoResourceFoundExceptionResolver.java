package com.banking.infrastructure.input.adapter.rest.error.resolver;


import com.banking.infrastructure.input.adapter.rest.error.bean.ErrorModel;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.resource.NoResourceFoundException;

public class NoResourceFoundExceptionResolver extends ErrorResolver<ErrorModel> {
    @Override
    protected int status() {
        return HttpStatus.NOT_FOUND.value();
    }

    @NonNull
    @Override
    protected ErrorModel buildError(@NonNull final String requestPath, @NonNull final Throwable throwable,
        @NonNull final String version) {
        final var noResourceFoundException = (NoResourceFoundException) throwable;
        final var detail = noResourceFoundException.getMessage();
        final var instance = String.valueOf(status());
        return new ErrorModel()
            .title("Not found")
            .detail(detail)
            .instance(instance)
            .type(requestPath);
    }
}
