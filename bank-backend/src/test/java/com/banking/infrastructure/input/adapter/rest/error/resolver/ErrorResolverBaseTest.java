package com.banking.infrastructure.input.adapter.rest.error.resolver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

@Getter(AccessLevel.PROTECTED)
@Accessors(fluent = true)
abstract class ErrorResolverBaseTest<E, R extends ErrorResolver<E>> {
    @Value("${info.project.version}")
    private String expectedVersion;
    @Value("${test.path}")
    private String validateFilePath;
    @Mock
    private ServerWebExchange serverWebExchange;
    @Mock
    private ServerHttpResponse serverHttpResponse;
    @Autowired
    private R errorResolver;

    protected void mockServerWebExchangeGetRequest(@NonNull final String result) {
        when(serverWebExchange.getRequest()).thenReturn(MockServerHttpRequest.get(result).build());
    }

    protected void mockServerWebExchangeGetResponse() {
        when(serverWebExchange.getResponse()).thenReturn(serverHttpResponse);
    }

    protected abstract void callToErrorResolverAndExpectError(
        @NonNull final Throwable throwable,
        @NonNull final String expectedTitle,
        @NonNull final String expectedDetail,
        @NonNull final String expectedInstance,
        @NonNull final String expectedType
    );

    protected void callToErrorResolverAndExpectClassCastException(@NonNull final Throwable wrongException) {
        mockServerWebExchangeGetRequest(validateFilePath);
        assertThat(errorResolver, notNullValue());
        assertThat(serverWebExchange, notNullValue());
        assertThat(expectedVersion, notNullValue());
        assertThrows(ClassCastException.class,
            () -> errorResolver.apply(serverWebExchange, wrongException, expectedVersion));
    }
}
