package com.banking.infrastructure.exception;

import com.banking.domain.exception.CodeException;
import com.banking.infrastructure.util.Constants;

public class AccountsQueryBadRequestException extends CodeException {
    public AccountsQueryBadRequestException() {
        super(Constants.ERROR_CODE_ACCOUNTS_QUERY_BAD_REQUEST, "Accounts query bad request");
    }
}
