package com.banking.infrastructure.input.adapter.rest.error.resolver;

import com.banking.infrastructure.input.adapter.rest.error.bean.ErrorList;
import com.banking.infrastructure.input.adapter.rest.error.bean.ErrorModel;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.server.MissingRequestValueException;

public class MissingRequestValueExceptionResolver extends ErrorResolver<ErrorModel> {
    @Override
    protected int status() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    protected ErrorModel buildError(@NonNull final String requestPath, @NonNull final Throwable throwable,
        @NonNull final String version) {
        final var missingRequestValueException = (MissingRequestValueException) throwable;
        final var error = new ErrorList()
            .message(String.format("Bad Request: %s", missingRequestValueException.getName()))
            .businessMessage(missingRequestValueException.getReason());
        final var instance = String.valueOf(missingRequestValueException.getStatusCode().value());
        return new ErrorModel()
            .title("Missing input")
            .detail("The input data is missing")
            .errors(List.of(error))
            .instance(instance)
            .type(requestPath);
    }
}
