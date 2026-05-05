package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class AccountNotFoundException extends CodeException {
    public AccountNotFoundException() {
        super(Constants.ERROR_CODE_ACCOUNT_NOT_FOUND, "The account was not found");
    }
}
