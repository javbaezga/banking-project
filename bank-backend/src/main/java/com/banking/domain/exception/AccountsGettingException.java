package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class AccountsGettingException extends CodeException {
    public AccountsGettingException() {
        super(Constants.ERROR_CODE_ACCOUNTS_GETTING, "The accounts could not be gotten");
    }
}
