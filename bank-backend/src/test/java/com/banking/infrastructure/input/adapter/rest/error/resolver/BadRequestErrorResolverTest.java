package com.banking.infrastructure.input.adapter.rest.error.resolver;

import com.banking.domain.exception.CodeException;
import com.banking.infrastructure.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {BadRequestErrorResolver.class})
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S2699")
class BadRequestErrorResolverTest extends AbstractErrorResolverTest<BadRequestErrorResolver> {
    private static final String BAD_REQUEST_EXCEPTION_MESSAGE = "Bad request test exception";

    @Test
    void givenCodeExceptionWhenBadRequestErrorResolverIsCalledThenExpectError() {
        final var exception = new CodeException(Constants.ERROR_CODE_ACCOUNTS_QUERY_BAD_REQUEST,
            BAD_REQUEST_EXCEPTION_MESSAGE);
        callToErrorResolverAndExpectError(
            exception,
            "Bad input",
            exception.getMessage(),
            exception.getCode(),
            validateFilePath()
        );
    }

    @Test
    void givenWrongExceptionWhenBadRequestErrorResolverThenThrowClassCastException() {
        callToErrorResolverAndExpectClassCastException(new Exception(BAD_REQUEST_EXCEPTION_MESSAGE));
    }
}
