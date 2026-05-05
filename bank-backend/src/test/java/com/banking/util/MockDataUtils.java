package com.banking.util;

import com.banking.domain.AccountInput;
import com.banking.domain.AccountOutput;
import com.banking.domain.BankStatementOutput;
import com.banking.domain.CustomerInput;
import com.banking.domain.CustomerOutput;
import com.banking.domain.TransactionInput;
import com.banking.domain.TransactionOutput;
import com.banking.domain.enums.AccountTypeEnum;
import com.banking.domain.enums.GenderEnum;
import com.banking.domain.enums.TransactionTypeEnum;
import com.banking.infrastructure.input.adapter.rest.error.bean.ErrorModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MissingRequestValueException;

@UtilityClass
@SuppressWarnings("java:S1118")
public final class MockDataUtils {
    private static final String ERROR_CODE_FIELD_NAME = "title";
    private static final String ERROR_CODE_LABEL = "Error code";
    public static final String PAGE_QUERY_PARAM_NAME = "page";
    public static final int PAGE_QUERY_PARAM_VALUE = 0;
    public static final String SIZE_QUERY_PARAM_NAME = "size";
    public static final int SIZE_QUERY_PARAM_VALUE = 10;
    public static final Long CUSTOMER_ID = 1L;
    public static final String CUSTOMER_ID_NUMBER = "1234567890";
    public static final Long ACCOUNT_ID = 1L;
    public static final String ACCOUNT_NUMBER = "123456";
    public static final Long TRANSACTION_ID = 1L;
    public static final BigDecimal TRANSACTION_VALUE = BigDecimal.valueOf(500.0);
    public static final String TRANSACTION_DESCRIPTION = "Test transaction";
    public static final LocalDate REPORT_START_DATE = LocalDate.of(2026, 5, 1);
    public static final LocalDate REPORT_END_DATE = LocalDate.of(2026, 5, 31);

    @NonNull
    public static HttpHeaders getRequestHeaders() {
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @NonNull
    public static MissingRequestValueException getMissingRequestValueException() throws NoSuchMethodException {
        final var fieldName = ERROR_CODE_FIELD_NAME;
        return new MissingRequestValueException(
            fieldName,
            String.class,
            ERROR_CODE_LABEL,
            MethodParameter.forParameter(ErrorModel.class.getMethod(fieldName, String.class).getParameters()[0])
        );
    }

    @NonNull
    public static WebExchangeBindException buildWebExchangeBindException(@NonNull final String errorMessage)
        throws NoSuchMethodException {
        final var target = ERROR_CODE_FIELD_NAME;
        final var objectName = ErrorModel.class.getName();
        final var bindException = new BindException(target, objectName);
        bindException.getBindingResult().addError(new FieldError(objectName, target, errorMessage));
        return new WebExchangeBindException(
            MethodParameter.forParameter(ErrorModel.class.getMethod(target, String.class).getParameters()[0]),
            bindException
        );
    }

    @NonNull
    public static CustomerOutput getCustomerOutput() {
        return CustomerOutput.builder()
            .id(CUSTOMER_ID)
            .fullName("John Doe")
            .gender(GenderEnum.MALE)
            .age((byte) 30)
            .idNumber(CUSTOMER_ID_NUMBER)
            .address("Test address")
            .phone("0987654321")
            .username("johndoe1")
            .status(true)
            .build();
    }

    @NonNull
    public static CustomerInput getCustomerInput() {
        return CustomerInput.builder()
            .fullName("John Doe")
            .gender(GenderEnum.MALE)
            .age((byte) 30)
            .idNumber(CUSTOMER_ID_NUMBER)
            .address("Test address")
            .phone("0987654321")
            .username("johndoe1")
            .password("password1")
            .status(true)
            .build();
    }

    @NonNull
    public static AccountOutput getAccountOutput() {
        return AccountOutput.builder()
            .id(ACCOUNT_ID)
            .customer(getCustomerOutput())
            .number(ACCOUNT_NUMBER)
            .type(AccountTypeEnum.SAVINGS)
            .initialBalance(BigDecimal.valueOf(1000.00))
            .balance(BigDecimal.valueOf(1000.00))
            .status(true)
            .build();
    }

    @NonNull
    public static AccountInput getAccountInput() {
        return AccountInput.builder()
            .customerId(CUSTOMER_ID)
            .number(ACCOUNT_NUMBER)
            .type(AccountTypeEnum.SAVINGS)
            .initialBalance(BigDecimal.valueOf(1000.00))
            .status(true)
            .build();
    }

    @NonNull
    public static TransactionOutput getTransactionOutput() {
        return TransactionOutput.builder()
            .id(TRANSACTION_ID)
            .date(LocalDate.now())
            .type(TransactionTypeEnum.CREDIT)
            .value(TRANSACTION_VALUE)
            .description(TRANSACTION_DESCRIPTION)
            .balance(BigDecimal.valueOf(1500.00))
            .status(true)
            .build();
    }

    @NonNull
    public static TransactionInput getTransactionInput() {
        return TransactionInput.builder()
            .accountNumber(ACCOUNT_NUMBER)
            .value(TRANSACTION_VALUE)
            .description(TRANSACTION_DESCRIPTION)
            .build();
    }

    @NonNull
    public static BankStatementOutput getBankStatementOutput() {
        return BankStatementOutput.builder()
            .id(TRANSACTION_ID)
            .date(REPORT_START_DATE)
            .customer("John Doe")
            .accountNumber(ACCOUNT_NUMBER)
            .type(AccountTypeEnum.SAVINGS.name())
            .initialBalance(BigDecimal.valueOf(1000.00))
            .status(true)
            .value(BigDecimal.valueOf(500.00))
            .balance(BigDecimal.valueOf(1500.00))
            .build();
    }
}
