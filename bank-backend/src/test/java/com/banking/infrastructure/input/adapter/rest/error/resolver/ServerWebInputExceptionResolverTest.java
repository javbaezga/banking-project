package com.banking.infrastructure.input.adapter.rest.error.resolver;

import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebInputException;

@SpringBootTest(classes = {ServerWebInputExceptionResolver.class})
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S2699")
class ServerWebInputExceptionResolverTest extends AbstractErrorBadRequestResolverTest<ServerWebInputExceptionResolver> {
    private static final String EXCEPTION_MESSAGE = "Server web input test exception";
    private static final String CAUSE_ERROR = "Bad input request";

    @Test
    void givenServerWebInputExceptionWhenServerWebInputExceptionResolverIsCalledThenExpectError() {
        final var exception = new ServerWebInputException(EXCEPTION_MESSAGE, null, new Throwable(CAUSE_ERROR));
        callToErrorResolverAndExpectError(
            exception,
            "Bad input",
            Objects.requireNonNull(exception.getReason()),
            String.valueOf(HttpStatus.BAD_REQUEST.value()),
            validateFilePath()
        );
    }

    @Test
    void givenWrongExceptionWhenServerWebInputExceptionResolverIsCalledThenThrowClassCastException() {
        callToErrorResolverAndExpectClassCastException(new Exception(EXCEPTION_MESSAGE));
    }
}
