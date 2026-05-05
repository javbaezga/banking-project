package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class QuerySortByFieldInvalidException extends CodeException {
    public QuerySortByFieldInvalidException() {
        super(Constants.ERROR_CODE_CUSTOMER_SORT_BY_FIELD_INVALID, "The sort by field is invalid");
    }
}
