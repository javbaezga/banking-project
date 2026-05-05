package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class CustomersQueryingException extends CodeException {
    public CustomersQueryingException() {
        super(Constants.ERROR_CODE_CUSTOMERS_QUERYING, "The customers could not be queried");
    }
}
