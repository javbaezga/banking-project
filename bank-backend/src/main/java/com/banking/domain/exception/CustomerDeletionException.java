package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class CustomerDeletionException extends CodeException {
    public CustomerDeletionException() {
        super(Constants.ERROR_CODE_CUSTOMER_DELETION, "The customer could not be deleted");
    }
}
