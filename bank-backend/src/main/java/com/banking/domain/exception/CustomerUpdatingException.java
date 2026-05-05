package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class CustomerUpdatingException extends CodeException {
    public CustomerUpdatingException() {
        super(Constants.ERROR_CODE_CUSTOMER_UPDATING, "The customer could not be updated");
    }
}
