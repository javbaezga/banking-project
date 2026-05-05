package com.banking.domain.util;

import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("java:S1118")
public final class Constants {
    public static final int QUERY_SEARCH_TERM_MAXIMUM_SIZE = 50;
    public static final int QUERY_SORT_BY_FIELD_MAXIMUM_SIZE = 20;
    public static final int QUERY_SORT_DIRECTION_MAXIMUM_SIZE = 4;
    public static final int QUERY_MINIMUM_PAGE = 0;
    public static final int QUERY_PAGE_MINIMUM_SIZE = 1;
    public static final int QUERY_PAGE_MAXIMUM_SIZE = 100;

    public static final int BANK_STATEMENT_MAXIMUM_PAGE_SIZE = 5000;

    public static final String ERROR_CODE_CUSTOMERS_QUERYING = "CME-0001";
    public static final String ERROR_CODE_CUSTOMER_GETTING = "CME-0002";
    public static final String ERROR_CODE_CUSTOMER_NOT_FOUND = "CME-0003";
    public static final String ERROR_CODE_CUSTOMER_CREATION = "CME-0004";
    public static final String ERROR_CODE_CUSTOMER_UPDATING = "CME-0005";
    public static final String ERROR_CODE_CUSTOMER_DELETION = "CME-0006";

    public static final String ERROR_CODE_ACCOUNTS_QUERYING = "AME-0001";
    public static final String ERROR_CODE_ACCOUNT_GETTING = "AME-0002";
    public static final String ERROR_CODE_ACCOUNTS_GETTING = "AME-0003";
    public static final String ERROR_CODE_ACCOUNT_NOT_FOUND = "AME-0004";
    public static final String ERROR_CODE_ACCOUNTS_NOT_FOUND = "AME-0005";
    public static final String ERROR_CODE_ACCOUNT_CREATION = "AME-0006";
    public static final String ERROR_CODE_ACCOUNT_UPDATING = "AME-0007";
    public static final String ERROR_CODE_ACCOUNT_DELETION = "AME-0008";

    public static final String ERROR_CODE_DAILY_BALANCES_RESETTING = "AME-0009";

    public static final String ERROR_CODE_TRANSACTIONS_QUERYING = "TME-0001";
    public static final String ERROR_CODE_TRANSACTION_GETTING = "TME-0002";
    public static final String ERROR_CODE_TRANSACTION_NOT_FOUND = "TME-0003";
    public static final String ERROR_CODE_TRANSACTION_CREATION = "TME-0004";

    public static final String ERROR_CODE_CUSTOMER_DISABLED = "TME-0005";
    public static final String ERROR_CODE_ACCOUNT_DISABLED = "TME-0006";
    public static final String ERROR_CODE_DAILY_QUOTA_EXCEEDED = "TME-0007";
    public static final String ERROR_CODE_BALANCE_NOT_AVAILABLE = "TME-0008";

    public static final String ERROR_CODE_BANK_STATEMENT_GETTING = "BSE-0001";
    public static final String ERROR_CODE_BANK_STATEMENT_PDF_GENERATION = "BSE-0002";

    public static final String ERROR_CODE_GENDER_INVALID = "EGE-0001";
    public static final String ERROR_CODE_ACCOUNT_TYPE_INVALID = "EGE-0002";
    public static final String ERROR_CODE_TRANSACTION_TYPE_INVALID = "EGE-0003";
    public static final String ERROR_CODE_QUERY_SORT_DIRECTION_INVALID = "EGE-0004";
    public static final String ERROR_CODE_CUSTOMER_SORT_BY_FIELD_INVALID = "EGE-0005";
}
