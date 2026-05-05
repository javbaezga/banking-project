package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class CustomerNotFoundException extends CodeException {
    public CustomerNotFoundException() {
        super(Constants.ERROR_CODE_CUSTOMER_NOT_FOUND, "The customer was not found");
    }
}
