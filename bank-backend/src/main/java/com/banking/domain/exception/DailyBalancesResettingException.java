package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class DailyBalancesResettingException extends CodeException {
    public DailyBalancesResettingException() {
        super(Constants.ERROR_CODE_DAILY_BALANCES_RESETTING, "The daily balances could not be reset");
    }
}
