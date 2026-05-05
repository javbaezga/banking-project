package com.banking.domain.exception;

import com.banking.domain.util.Constants;
import java.math.BigDecimal;
import org.springframework.lang.NonNull;

public class BalanceNotAvailableException extends CodeException {
    public BalanceNotAvailableException(@NonNull final BigDecimal dailyBalance) {
        super(Constants.ERROR_CODE_BALANCE_NOT_AVAILABLE,
            String.format("The daily quota %s was exceeded", dailyBalance));
    }
}
