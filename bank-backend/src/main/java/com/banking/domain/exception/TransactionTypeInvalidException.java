package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class TransactionTypeInvalidException extends CodeException {
    public TransactionTypeInvalidException() {
        super(Constants.ERROR_CODE_TRANSACTION_TYPE_INVALID, "The transaction type is invalid");
    }
}
