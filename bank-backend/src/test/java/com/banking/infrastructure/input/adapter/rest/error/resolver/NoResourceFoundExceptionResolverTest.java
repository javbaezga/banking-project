package com.banking.infrastructure.input.adapter.rest.error.resolver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.resource.NoResourceFoundException;

@SpringBootTest(classes = {NoResourceFoundExceptionResolver.class})
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S2699")
class NoResourceFoundExceptionResolverTest extends AbstractErrorResolverTest<NoResourceFoundExceptionResolver> {
    @Test
    void givenExceptionWhenNoResourceFoundExceptionResolverIsCalledThenExpectError() {
        final var exception = new NoResourceFoundException("test");
        callToErrorResolverAndExpectError(
            exception,
            "Not found",
            exception.getMessage(),
            String.valueOf(HttpStatus.NOT_FOUND.value()),
            validateFilePath()
        );
    }

    @Test
    void givenWrongExceptionWhenNoResourceFoundExceptionResolverIsCalledThenThrowClassCastException() {
        callToErrorResolverAndExpectClassCastException(new Exception("Not found test exception"));
    }
}
