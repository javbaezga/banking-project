package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class AccountUpdatingException extends CodeException {
    public AccountUpdatingException() {
        super(Constants.ERROR_CODE_ACCOUNT_UPDATING, "The account could not be updated");
    }
}
