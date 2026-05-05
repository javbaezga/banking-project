package com.banking.infrastructure.input.adapter.rest.error.resolver;

import com.banking.domain.exception.CodeException;
import com.banking.domain.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {CodeExceptionResolver.class})
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S2699")
class CodeExceptionResolverTest extends AbstractErrorResolverTest<CodeExceptionResolver> {
    private static final String EXCEPTION_MESSAGE = "Error code test exception";

    @Test
    void givenCodeExceptionWhenCodeExceptionResolverIsCalledThenExpectError() {
        final var exception = new CodeException(Constants.ERROR_CODE_TRANSACTION_TYPE_INVALID, EXCEPTION_MESSAGE);
        callToErrorResolverAndExpectError(
            exception,
            "Unexpected error",
            exception.getMessage(),
            exception.getCode(),
            validateFilePath()
        );
    }

    @Test
    void givenWrongExceptionWhenCodeExceptionResolverIsCalledThenThrowClassCastException() {
        callToErrorResolverAndExpectClassCastException(new Exception(EXCEPTION_MESSAGE));
    }
}
