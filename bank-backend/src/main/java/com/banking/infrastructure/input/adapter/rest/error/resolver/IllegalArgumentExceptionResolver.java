package com.banking.infrastructure.input.adapter.rest.error.resolver;

import com.banking.infrastructure.input.adapter.rest.error.bean.ErrorList;
import com.banking.infrastructure.input.adapter.rest.error.bean.ErrorModel;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

public class IllegalArgumentExceptionResolver extends ErrorResolver<ErrorModel> {
    @Override
    protected int status() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @NonNull
    @Override
    protected ErrorModel buildError(@NonNull final String requestPath, @NonNull final Throwable throwable,
        @NonNull final String version) {
        final var illegalArgumentException = (IllegalArgumentException) throwable;
        final var error = new ErrorList()
            .message("Bad Request")
            .businessMessage(illegalArgumentException.getMessage());
        final var instance = String.valueOf(status());
        return new ErrorModel()
            .title("Bad input")
            .detail("The input data is invalid")
            .errors(List.of(error))
            .instance(instance)
            .type(requestPath);
    }
}
