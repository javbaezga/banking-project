package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class BankStatementGettingException extends CodeException {
    public BankStatementGettingException() {
        super(Constants.ERROR_CODE_BANK_STATEMENT_GETTING, "The bank statement could not be gotten");
    }
}
