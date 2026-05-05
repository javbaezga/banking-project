package com.banking.infrastructure.input.adapter.rest.error.resolver;

import com.banking.infrastructure.input.adapter.rest.error.bean.ErrorModel;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

public class UnsupportedMediaTypeStatusExceptionResolver extends ErrorResolver<ErrorModel> {
    @Override
    protected int status() {
        return HttpStatus.UNSUPPORTED_MEDIA_TYPE.value();
    }

    @NonNull
    @Override
    protected ErrorModel buildError(@NonNull final String requestPath, @NonNull final Throwable throwable,
        @NonNull final String version) {
        final var unsupportedMediaTypeStatusException = (UnsupportedMediaTypeStatusException) throwable;
        final var detail = unsupportedMediaTypeStatusException.getReason();
        final var instance = String.valueOf(status());
        return new ErrorModel()
            .title("Unsupported media type")
            .detail(detail)
            .instance(instance)
            .type(requestPath);
    }
}
