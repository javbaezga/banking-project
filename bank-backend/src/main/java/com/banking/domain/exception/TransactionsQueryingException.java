package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class TransactionsQueryingException extends CodeException {
    public TransactionsQueryingException() {
        super(Constants.ERROR_CODE_TRANSACTIONS_QUERYING, "The transactions could not be queried");
    }
}
