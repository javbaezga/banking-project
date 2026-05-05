package com.banking.application.service.impl;

import com.banking.application.input.port.ReportService;
import com.banking.application.output.port.BankStatementReportService;
import com.banking.domain.BankStatementInput;
import com.banking.domain.BankStatementOutput;
import com.banking.domain.BankStatementPdfInput;
import com.banking.domain.exception.BankStatementGettingException;
import com.banking.domain.exception.BankStatementPdfGenerationException;
import com.banking.domain.exception.CodeException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Base64;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ReportServiceImpl implements ReportService {
    BankStatementReportService bankStatementRepository;

    @NonNull
    @Override
    public Mono<Page<BankStatementOutput>> getBankStatement(
        @NonNull @NotNull @Valid final BankStatementInput bankStatementInput) {
        final var bankStatementInputString = bankStatementInput.toString();
        log.info("|--> Getting bank statement: input={}", bankStatementInputString);
        return bankStatementRepository.getBankStatement(bankStatementInput)
            .doOnSuccess(paginatedBankStatement -> log.info("<--| Bank statement was gotten: input={}, total items={}",
                bankStatementInputString, paginatedBankStatement.getTotalElements()))
            .doOnError(error -> log.error("<--| Error getting bank statement: input={}, error={}",
                bankStatementInputString, error.getMessage()))
            .onErrorMap(error -> !(error instanceof CodeException), error -> new BankStatementGettingException());
    }

    @NonNull
    @Override
    public Mono<String> generateBankStatementPdf(
        @NonNull @NotNull @Valid final BankStatementPdfInput bankStatementPdfInput) {
        final var bankStatementPdfInputString = bankStatementPdfInput.toString();
        log.info("|--> Generating bank statement PDF: input={}", bankStatementPdfInputString);
        return bankStatementRepository.generateBankStatementPdf(bankStatementPdfInput)
            .map(Base64.getEncoder()::encodeToString)
            .doOnSuccess(bankStatementPdfBase64Content ->
                log.info("<--| Bank statement PDF was generated: input={}, PDF content length={}",
                    bankStatementPdfInputString, bankStatementPdfBase64Content.length()))
            .doOnError(error ->
                log.error("<--| Error generating bank statement PDF: input={}, error={}",
                    bankStatementPdfInputString, error.getMessage()))
            .onErrorMap(error -> !(error instanceof CodeException), error -> new BankStatementPdfGenerationException());
    }
}
