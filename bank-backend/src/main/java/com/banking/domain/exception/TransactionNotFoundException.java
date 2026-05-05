package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class TransactionNotFoundException extends CodeException {
    public TransactionNotFoundException() {
        super(Constants.ERROR_CODE_TRANSACTION_NOT_FOUND, "The transaction was not found");
    }
}
