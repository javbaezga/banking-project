package com.banking.infrastructure.input.adapter.rest.error.resolver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest(classes = {IllegalArgumentExceptionResolver.class})
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S2699")
class IllegalArgumentExceptionResolverTest extends
    AbstractErrorBadRequestResolverTest<IllegalArgumentExceptionResolver> {
    private static final String EXCEPTION_MESSAGE = "Illegal argument test exception";

    @Test
    void givenIllegalArgumentExceptionWhenIllegalArgumentExceptionResolverIsCalledThenExpectError() {
        final var exception = new IllegalArgumentException(EXCEPTION_MESSAGE);
        callToErrorResolverAndExpectError(
            exception,
            "Bad input",
            "The input data is invalid",
            String.valueOf(HttpStatus.BAD_REQUEST.value()),
            validateFilePath()
        );
    }

    @Test
    void givenWrongExceptionWhenIllegalArgumentExceptionResolverIsCalledThenThrowClassCastException() {
        callToErrorResolverAndExpectClassCastException(new Exception(EXCEPTION_MESSAGE));
    }
}
