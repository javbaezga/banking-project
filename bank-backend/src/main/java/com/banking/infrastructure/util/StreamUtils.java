package com.banking.infrastructure.util;

import java.io.OutputStream;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.lang.NonNull;

@UtilityClass
@SuppressWarnings("java:S1118")
public final class StreamUtils {
    @NonNull
    @SneakyThrows
    public static void closeOutputStream(@NonNull final OutputStream outputStream) {
        outputStream.close();
    }
}
