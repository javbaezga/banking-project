package com.banking.infrastructure.input.adapter.rest.error.resolver;

import com.banking.util.MockDataUtils;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {WebExchangeBindExceptionResolver.class})
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S2699")
class WebExchangeBindExceptionResolverTest extends
    AbstractErrorBadRequestResolverTest<WebExchangeBindExceptionResolver> {
    private static final String EXCEPTION_MESSAGE = "Web exchange bind test exception";

    @Test
    void givenWebExchangeBindExceptionWhenWebExchangeBindExceptionResolverIsCalledThenExpectError()
        throws NoSuchMethodException, SecurityException {
        final var exception = MockDataUtils.buildWebExchangeBindException(EXCEPTION_MESSAGE);
        callToErrorResolverAndExpectError(
            exception,
            "Bad input",
            Objects.requireNonNull(exception.getReason()),
            String.valueOf(exception.getStatusCode().value()),
            validateFilePath()
        );
    }

    @Test
    void givenWrongExceptionWhenWebExchangeBindExceptionResolverIsCalledThenThrowClassCastException() {
        callToErrorResolverAndExpectClassCastException(new Exception(EXCEPTION_MESSAGE));
    }
}
