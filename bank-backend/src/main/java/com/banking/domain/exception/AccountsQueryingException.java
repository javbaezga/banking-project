package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class AccountsQueryingException extends CodeException {
    public AccountsQueryingException() {
        super(Constants.ERROR_CODE_ACCOUNTS_QUERYING, "The accounts could not be queried");
    }
}
