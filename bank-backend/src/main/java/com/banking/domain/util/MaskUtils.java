package com.banking.domain.util;

import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;

@UtilityClass
@SuppressWarnings("java:S1118")
public final class MaskUtils {
    private static final String MASK_REGULAR_EXPRESSION_FORMAT = "(?!^.?).(?=.{%d})";
    public static final String MASK_STRING = "*";
    public static final int ID_NUMBER_VISIBLE_END_LENGTH = 5;
    public static final int ACCOUNT_NUMBER_VISIBLE_END_LENGTH = 4;

    @Nullable
    private static String maskText(@Nullable final String textToMask, final int visibleEndLength) {
        if (textToMask == null || textToMask.isBlank()) {
            return null;
        }
        return textToMask.replaceAll(String.format(MASK_REGULAR_EXPRESSION_FORMAT, visibleEndLength), MASK_STRING);
    }

    @Nullable
    public static String maskIdNumber(@Nullable final String idNumber) {
        return maskText(idNumber, ID_NUMBER_VISIBLE_END_LENGTH);
    }

    @Nullable
    public static String maskAccountNumber(@Nullable final String accountNumber) {
        return maskText(accountNumber, ACCOUNT_NUMBER_VISIBLE_END_LENGTH);
    }
}
