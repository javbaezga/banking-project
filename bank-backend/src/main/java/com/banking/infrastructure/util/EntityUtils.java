package com.banking.infrastructure.util;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@UtilityClass
@SuppressWarnings("java:S1118")
public final class EntityUtils {
    @NonNull
    public static <T> Mono<T> entityToMono(@NonNull final Supplier<Optional<T>> entitySupplier) {
        return Mono.fromCallable(entitySupplier::get)
            .flatMap(Mono::justOrEmpty);
    }

    @NonNull
    public static <T, C extends Collection<T>> Flux<T> entitiesToFlux(
        @NonNull final Supplier<Optional<C>> entitiesSupplier) {
        return Mono.fromCallable(entitiesSupplier::get)
            .flatMap(Mono::justOrEmpty)
            .flatMapMany(Flux::fromIterable);
    }
}
