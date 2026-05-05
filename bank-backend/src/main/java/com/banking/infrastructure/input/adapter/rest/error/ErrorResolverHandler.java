package com.banking.infrastructure.input.adapter.rest.error;

import com.banking.domain.exception.AccountNotFoundException;
import com.banking.domain.exception.AccountsNotFoundException;
import com.banking.domain.exception.CodeException;
import com.banking.domain.exception.CustomerNotFoundException;
import com.banking.domain.exception.TransactionNotFoundException;
import com.banking.infrastructure.exception.AccountsQueryBadRequestException;
import com.banking.infrastructure.input.adapter.rest.error.resolver.BadRequestErrorResolver;
import com.banking.infrastructure.input.adapter.rest.error.resolver.CodeExceptionResolver;
import com.banking.infrastructure.input.adapter.rest.error.resolver.ConstraintViolationExceptionResolver;
import com.banking.infrastructure.input.adapter.rest.error.resolver.ErrorResolver;
import com.banking.infrastructure.input.adapter.rest.error.resolver.IllegalArgumentExceptionResolver;
import com.banking.infrastructure.input.adapter.rest.error.resolver.MissingRequestValueExceptionResolver;
import com.banking.infrastructure.input.adapter.rest.error.resolver.NoResourceFoundExceptionResolver;
import com.banking.infrastructure.input.adapter.rest.error.resolver.NotFoundErrorResolver;
import com.banking.infrastructure.input.adapter.rest.error.resolver.ResponseStatusExceptionResolver;
import com.banking.infrastructure.input.adapter.rest.error.resolver.ServerWebInputExceptionResolver;
import com.banking.infrastructure.input.adapter.rest.error.resolver.UnexpectedErrorResolver;
import com.banking.infrastructure.input.adapter.rest.error.resolver.UnsupportedMediaTypeStatusExceptionResolver;
import com.banking.infrastructure.input.adapter.rest.error.resolver.WebExchangeBindExceptionResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.MissingRequestValueException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class ErrorResolverHandler implements ErrorWebExceptionHandler {
    final ObjectMapper objectMapper;
    final UnexpectedErrorResolver unexpectedErrorResolver = new UnexpectedErrorResolver();
    final NotFoundErrorResolver notFoundErrorResolver = new NotFoundErrorResolver();
    final Map<Class<? extends Throwable>, ErrorResolver<?>> errorResolvers;
    @Value("${info.project.version}")
    String version;

    @PostConstruct
    private void initializeErrorResolvers() {
        errorResolvers.put(ConstraintViolationException.class, new ConstraintViolationExceptionResolver());
        errorResolvers.put(MissingRequestValueException.class, new MissingRequestValueExceptionResolver());
        errorResolvers.put(ResponseStatusException.class, new ResponseStatusExceptionResolver());
        errorResolvers.put(ServerWebInputException.class, new ServerWebInputExceptionResolver());
        errorResolvers.put(UnsupportedMediaTypeStatusException.class,
            new UnsupportedMediaTypeStatusExceptionResolver());
        errorResolvers.put(WebExchangeBindException.class, new WebExchangeBindExceptionResolver());
        errorResolvers.put(IllegalArgumentException.class, new IllegalArgumentExceptionResolver());
        errorResolvers.put(NoResourceFoundException.class, new NoResourceFoundExceptionResolver());
        errorResolvers.put(CodeException.class, new CodeExceptionResolver());
        errorResolvers.put(CustomerNotFoundException.class, notFoundErrorResolver);
        errorResolvers.put(AccountNotFoundException.class, notFoundErrorResolver);
        errorResolvers.put(AccountsNotFoundException.class, notFoundErrorResolver);
        errorResolvers.put(TransactionNotFoundException.class, notFoundErrorResolver);
        errorResolvers.put(AccountsQueryBadRequestException.class, new BadRequestErrorResolver());
    }

    @SafeVarargs
    @NonNull
    @SuppressWarnings({"unchecked", "rawtypes"})
    private ErrorResolver<?> getFallbackErrorResolver(@NonNull final Throwable throwable,
        @NonNull final Class<? extends Throwable>... classes) {
        return Stream.of(classes)
            .filter(theClass -> theClass.isInstance(throwable))
            .findFirst()
            .map(errorResolvers::get)
            .orElse((ErrorResolver) unexpectedErrorResolver);
    }

    @NonNull
    @Override
    public Mono<Void> handle(@NonNull final ServerWebExchange serverWebExchange, @NonNull final Throwable throwable) {
        log.error("Error message: {}", throwable.getMessage());
        return Mono.just(serverWebExchange.getResponse())
            .doOnNext(response -> response.getHeaders().setContentType(MediaType.APPLICATION_JSON))
            .flatMap(response ->
                Mono.just(errorResolvers.getOrDefault(throwable.getClass(),
                        getFallbackErrorResolver(throwable, CodeException.class)))
                    .flatMap(errorResolver ->
                        response.writeWith(
                            Mono.fromCallable(() -> objectMapper.writeValueAsBytes(
                                    errorResolver.apply(serverWebExchange, throwable, version)))
                                .doOnNext(error ->
                                    log.error("Error response: {}", new String(error, StandardCharsets.UTF_8)))
                                .map(response.bufferFactory()::wrap)
                        )
                    )

            );
    }
}
