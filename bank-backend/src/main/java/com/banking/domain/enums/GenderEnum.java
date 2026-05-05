package com.banking.domain.enums;

import com.banking.domain.exception.GenderInvalidException;
import com.banking.domain.util.CollectorUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum GenderEnum {
    MALE("M"),
    FEMALE("F");

    private static final Map<String, GenderEnum> GENDERS_MAP = Stream.of(GenderEnum.values())
        .collect(Collectors.toMap(
            GenderEnum::value,
            Function.identity(),
            CollectorUtils.useMergeFunctionNewValue(),
            HashMap::new
        ));

    String value;

    @NonNull
    public static GenderEnum fromValue(@Nullable final String gender) throws GenderInvalidException {
        return Optional.ofNullable(GENDERS_MAP.get(gender))
            .orElseThrow(GenderInvalidException::new);
    }
}
