package com.banking.infrastructure.input.adapter.rest.error.resolver;

import com.banking.infrastructure.input.adapter.rest.error.bean.ErrorModel;
import com.banking.infrastructure.util.ErrorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.support.WebExchangeBindException;

public class WebExchangeBindExceptionResolver extends ErrorResolver<ErrorModel> {
    @Override
    protected int status() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @NonNull
    @Override
    protected ErrorModel buildError(@NonNull final String requestPath, @NonNull final Throwable throwable,
        @NonNull final String version) {
        final var webExchangeBindException = (WebExchangeBindException) throwable;
        final var detail = webExchangeBindException.getReason();
        final var errors = ErrorUtils.buildErrors(webExchangeBindException);
        final var instance = String.valueOf(status());
        return new ErrorModel()
            .title("Bad input")
            .detail(detail)
            .errors(errors)
            .instance(instance)
            .type(requestPath);
    }
}
