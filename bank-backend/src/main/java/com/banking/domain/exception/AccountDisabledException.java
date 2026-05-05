package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class AccountDisabledException extends CodeException {
    public AccountDisabledException() {
        super(Constants.ERROR_CODE_ACCOUNT_DISABLED, "The account is disabled");
    }
}
