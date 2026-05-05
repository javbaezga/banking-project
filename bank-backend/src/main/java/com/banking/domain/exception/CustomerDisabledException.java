package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class CustomerDisabledException extends CodeException {
    public CustomerDisabledException() {
        super(Constants.ERROR_CODE_CUSTOMER_DISABLED, "The customer is disabled");
    }
}
