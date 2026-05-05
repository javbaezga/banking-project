package com.banking.infrastructure.input.adapter.rest.error.resolver;

import jakarta.validation.ConstraintViolationException;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest(classes = {ConstraintViolationExceptionResolver.class})
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S2699")
class ConstraintViolationExceptionResolverTest extends
    AbstractErrorBadRequestResolverTest<ConstraintViolationExceptionResolver> {
    private static final String EXCEPTION_MESSAGE = "Constraint violation test exception";

    @Test
    void givenConstraintViolationExceptionWhenConstraintViolationExceptionResolverIsCalledThenExpectError() {
        final var exception = new ConstraintViolationException(EXCEPTION_MESSAGE, Set.of());
        callToErrorResolverAndExpectError(
            exception,
            "Bad input",
            "The input data is invalid",
            String.valueOf(HttpStatus.BAD_REQUEST.value()),
            validateFilePath()
        );
    }

    @Test
    void givenWrongExceptionWhenConstraintViolationExceptionResolverIsCalledThenThrowClassCastException() {
        callToErrorResolverAndExpectClassCastException(new Exception(EXCEPTION_MESSAGE));
    }
}
