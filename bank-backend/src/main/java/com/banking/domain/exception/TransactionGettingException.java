package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class TransactionGettingException extends CodeException {
    public TransactionGettingException() {
        super(Constants.ERROR_CODE_TRANSACTION_GETTING, "The transaction could not be gotten");
    }
}
