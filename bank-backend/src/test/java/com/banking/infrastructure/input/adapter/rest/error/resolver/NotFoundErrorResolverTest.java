package com.banking.infrastructure.input.adapter.rest.error.resolver;

import com.banking.domain.exception.CodeException;
import com.banking.domain.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {NotFoundErrorResolver.class})
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S2699")
class NotFoundErrorResolverTest extends AbstractErrorResolverTest<NotFoundErrorResolver> {
    private static final String EXCEPTION_MESSAGE = "Not found test exception";

    @Test
    void givenExceptionWhenNotFoundExceptionResolverIsCalledThenExpectError() {
        final var exception = new CodeException(Constants.ERROR_CODE_TRANSACTION_NOT_FOUND, EXCEPTION_MESSAGE);
        callToErrorResolverAndExpectError(
            exception,
            "Not found",
            exception.getMessage(),
            exception.getCode(),
            validateFilePath()
        );
    }

    @Test
    void givenWrongExceptionWhenNotFoundExceptionResolverIsCalledThenThrowClassCastException() {
        callToErrorResolverAndExpectClassCastException(new Exception(EXCEPTION_MESSAGE));
    }
}
