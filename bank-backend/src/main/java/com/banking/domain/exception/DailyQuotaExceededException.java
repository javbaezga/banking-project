package com.banking.domain.exception;

import com.banking.domain.util.Constants;
import java.math.BigDecimal;
import org.springframework.lang.NonNull;

public class DailyQuotaExceededException extends CodeException {
    public DailyQuotaExceededException(@NonNull final BigDecimal dailyBalance) {
        super(Constants.ERROR_CODE_DAILY_QUOTA_EXCEEDED,
            String.format("The daily quota %s was exceeded", dailyBalance));
    }
}
