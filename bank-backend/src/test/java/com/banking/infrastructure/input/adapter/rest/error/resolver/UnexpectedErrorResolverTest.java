package com.banking.infrastructure.input.adapter.rest.error.resolver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest(classes = {UnexpectedErrorResolver.class})
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S2699")
class UnexpectedErrorResolverTest extends AbstractErrorResolverTest<UnexpectedErrorResolver> {
    @Test
    void givenExceptionWhenUnexpectedErrorResolverIsCalledThenExpectError() {
        final var exception = new Exception("Unexpected error test exception");
        callToErrorResolverAndExpectError(
            exception,
            "Unexpected error",
            exception.getMessage(),
            String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
            validateFilePath()
        );
    }
}
