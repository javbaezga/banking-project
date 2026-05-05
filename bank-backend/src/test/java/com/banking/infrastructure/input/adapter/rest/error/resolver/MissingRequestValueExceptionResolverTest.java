package com.banking.infrastructure.input.adapter.rest.error.resolver;

import com.banking.util.MockDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {MissingRequestValueExceptionResolver.class})
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S2699")
class MissingRequestValueExceptionResolverTest extends
    AbstractErrorBadRequestResolverTest<MissingRequestValueExceptionResolver> {
    @Test
    void givenMissingRequestValueExceptionWhenMissingRequestValueExceptionResolverIsCalledThenExpectError()
        throws NoSuchMethodException {
        final var exception = MockDataUtils.getMissingRequestValueException();
        callToErrorResolverAndExpectError(
            exception,
            "Missing input",
            "The input data is missing",
            String.valueOf(exception.getStatusCode().value()),
            validateFilePath()
        );
    }

    @Test
    void givenWrongExceptionWhenMissingRequestValueExceptionResolverIsCalledThenThrowClassCastException() {
        callToErrorResolverAndExpectClassCastException(new Exception("Missing request value test exception"));
    }
}
