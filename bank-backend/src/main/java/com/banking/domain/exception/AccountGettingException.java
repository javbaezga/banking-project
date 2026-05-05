package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class AccountGettingException extends CodeException {
    public AccountGettingException() {
        super(Constants.ERROR_CODE_ACCOUNT_GETTING, "The account could not be gotten");
    }
}
