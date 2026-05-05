package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class QuerySortDirectionInvalidException extends CodeException {
    public QuerySortDirectionInvalidException() {
        super(Constants.ERROR_CODE_QUERY_SORT_DIRECTION_INVALID, "The sort direction is invalid");
    }
}
