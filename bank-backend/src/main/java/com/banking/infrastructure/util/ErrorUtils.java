package com.banking.infrastructure.util;

import com.banking.infrastructure.input.adapter.rest.error.bean.ErrorList;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebExchangeBindException;

@UtilityClass
@SuppressWarnings("java:S1118")
public final class ErrorUtils {
    private static final String BAD_REQUEST_ERROR_MESSAGE = "Bad Request";

    @NonNull
    private static ErrorList buildErrorList(@Nullable final String businessMessage) {
        return new ErrorList().message(BAD_REQUEST_ERROR_MESSAGE).businessMessage(businessMessage);
    }

    @NonNull
    public static List<ErrorList> buildErrors(
        @NonNull final ConstraintViolationException constraintViolationException) {
        return constraintViolationException.getConstraintViolations()
            .stream()
            .map(constraintViolation -> constraintViolation.getPropertyPath() + " " + constraintViolation.getMessage())
            .distinct()
            .map(ErrorUtils::buildErrorList)
            .toList();
    }

    @NonNull
    public static List<ErrorList> buildErrors(@NonNull final WebExchangeBindException webExchangeBindException) {
        return webExchangeBindException.getFieldErrors()
            .stream()
            .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
            .distinct()
            .map(ErrorUtils::buildErrorList)
            .toList();
    }
}
