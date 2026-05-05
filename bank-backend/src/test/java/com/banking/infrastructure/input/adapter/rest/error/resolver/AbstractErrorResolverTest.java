package com.banking.infrastructure.input.adapter.rest.error.resolver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.banking.infrastructure.input.adapter.rest.error.bean.ErrorModel;
import org.springframework.lang.NonNull;

abstract class AbstractErrorResolverTest<R extends ErrorResolver<ErrorModel>> extends ErrorResolverBaseTest<ErrorModel, R> {
    @Override
    protected void callToErrorResolverAndExpectError(
        @NonNull final Throwable throwable,
        @NonNull final String expectedTitle,
        @NonNull final String expectedDetail,
        @NonNull final String expectedInstance,
        @NonNull final String expectedType
    ) {
        final var errorResolver = errorResolver();
        assertThat(errorResolver, notNullValue());
        mockServerWebExchangeGetRequest(validateFilePath());
        mockServerWebExchangeGetResponse();
        final var error = errorResolver.apply(serverWebExchange(), throwable, expectedVersion());
        assertThat(error, notNullValue());
        assertThat(error.getTitle(), is(expectedTitle));
        assertThat(error.getDetail(), is(expectedDetail));
        assertThat(error.getInstance(), is(String.valueOf(expectedInstance)));
        assertThat(error.getType(), is(expectedType));
    }
}
