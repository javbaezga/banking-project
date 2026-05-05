package com.banking.infrastructure.input.adapter.rest.error.resolver;

import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

@SpringBootTest(classes = {UnsupportedMediaTypeStatusExceptionResolver.class})
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S2699")
class UnsupportedMediaTypeStatusExceptionResolverTest extends
    AbstractErrorResolverTest<UnsupportedMediaTypeStatusExceptionResolver> {
    private static final String EXCEPTION_MESSAGE = "Unsupported media type status test exception";

    @Test
    void givenUnsupportedMediaTypeStatusExceptionWhenUnsupportedMediaTypeStatusExceptionResolverIsCalledThenExpectError() {
        final var exception = new UnsupportedMediaTypeStatusException(EXCEPTION_MESSAGE);
        callToErrorResolverAndExpectError(
            exception,
            "Unsupported media type",
            Objects.requireNonNull(exception.getReason()),
            String.valueOf(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()),
            validateFilePath()
        );
    }

    @Test
    void givenWrongExceptionWhenUnsupportedMediaTypeStatusExceptionResolverIsCalledThenThrowClassCastException() {
        callToErrorResolverAndExpectClassCastException(new Exception(EXCEPTION_MESSAGE));
    }
}
