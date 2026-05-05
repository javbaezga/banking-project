package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class CustomerCreationException extends CodeException {
    public CustomerCreationException() {
        super(Constants.ERROR_CODE_CUSTOMER_CREATION, "The customer could not be created");
    }
}
