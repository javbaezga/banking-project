package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class GenderInvalidException extends CodeException {
    public GenderInvalidException() {
        super(Constants.ERROR_CODE_GENDER_INVALID, "The gender is invalid");
    }
}
