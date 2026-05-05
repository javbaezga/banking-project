package com.banking.domain.exception;

import com.banking.domain.util.Constants;

public class BankStatementPdfGenerationException extends CodeException {
    public BankStatementPdfGenerationException() {
        super(Constants.ERROR_CODE_BANK_STATEMENT_PDF_GENERATION, "The bank statement PDF could not be generated");
    }
}
