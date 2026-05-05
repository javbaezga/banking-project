package com.banking.infrastructure.input.adapter.rest.error.resolver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest(classes = {ResponseStatusExceptionResolver.class})
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S2699")
class ResponseStatusExceptionResolverTest extends AbstractErrorResolverTest<ResponseStatusExceptionResolver> {
    @ParameterizedTest
    @ValueSource(ints = {400, 404, 500})
    void givenResponseStatusExceptionWhenResponseStatusExceptionResolverIsCalledThenExpectError(
        final int httpStatusCode) {
        final var exception = new ResponseStatusException(HttpStatusCode.valueOf(httpStatusCode));
        callToErrorResolverAndExpectError(
            exception,
            "Response error",
            exception.getMessage(),
            String.valueOf(httpStatusCode),
            validateFilePath()
        );
    }

    @Test
    void givenWrongExceptionWhenResponseStatusExceptionResolverIsCalledThenThrowClassCastException() {
        callToErrorResolverAndExpectClassCastException(new Exception("Response status exception test"));
    }
}
