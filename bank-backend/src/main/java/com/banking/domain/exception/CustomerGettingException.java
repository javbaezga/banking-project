package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class CustomerGettingException extends CodeException {
    public CustomerGettingException() {
        super(Constants.ERROR_CODE_CUSTOMER_GETTING, "The customer could not be gotten");
    }
}
