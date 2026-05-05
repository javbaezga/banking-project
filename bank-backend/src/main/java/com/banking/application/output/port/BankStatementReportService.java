package com.banking.application.output.port;

import com.banking.domain.BankStatementInput;
import com.banking.domain.BankStatementOutput;
import com.banking.domain.BankStatementPdfInput;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

@Validated
public interface BankStatementReportService {
    @NonNull
    Mono<Page<BankStatementOutput>> getBankStatement(@NotNull @Valid BankStatementInput bankStatementInput);

    @NonNull
    Mono<byte[]> generateBankStatementPdf(@NotNull @Valid BankStatementPdfInput bankStatementPdfInput);
}
