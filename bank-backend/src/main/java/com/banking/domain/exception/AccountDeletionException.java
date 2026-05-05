package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class AccountDeletionException extends CodeException {
    public AccountDeletionException() {
        super(Constants.ERROR_CODE_ACCOUNT_DELETION, "The account could not be deleted");
    }
}
