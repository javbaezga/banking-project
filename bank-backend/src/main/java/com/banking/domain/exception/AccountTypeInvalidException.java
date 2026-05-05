package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class AccountTypeInvalidException extends CodeException {
    public AccountTypeInvalidException() {
        super(Constants.ERROR_CODE_ACCOUNT_TYPE_INVALID, "The account type is invalid");
    }
}
