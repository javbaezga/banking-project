package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class TransactionCreationException extends CodeException {
    public TransactionCreationException() {
        super(Constants.ERROR_CODE_TRANSACTION_CREATION, "The transaction could not be created");
    }
}
