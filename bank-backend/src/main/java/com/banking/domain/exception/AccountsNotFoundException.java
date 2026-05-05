package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class AccountsNotFoundException extends CodeException {
    public AccountsNotFoundException() {
        super(Constants.ERROR_CODE_ACCOUNTS_NOT_FOUND, "The accounts were not found");
    }
}
