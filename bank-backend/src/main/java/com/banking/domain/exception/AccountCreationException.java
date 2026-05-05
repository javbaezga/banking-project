package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class AccountCreationException extends CodeException {
    public AccountCreationException() {
        super(Constants.ERROR_CODE_ACCOUNT_CREATION, "The account could not be created");
    }
}
