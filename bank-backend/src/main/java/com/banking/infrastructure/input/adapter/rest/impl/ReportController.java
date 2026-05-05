package com.banking.infrastructure.input.adapter.rest.impl;

import com.banking.application.input.port.ReportService;
import com.banking.domain.BankStatementOutput;
import com.banking.domain.util.Constants;
import com.banking.infrastructure.input.adapter.rest.mapper.ReportMapper;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
@Slf4j
public class ReportController {
    ReportService reportService;
    ReportMapper reportMapper;

    @NonNull
    @GetMapping("/bank-statements/{customer_id}")
    public Mono<ResponseEntity<Page<BankStatementOutput>>> getBankStatement(
        @PathVariable("customer_id") @NotNull @Min(1L) final Long customerId,
        @RequestParam("start_date") @NotNull final LocalDate startDate,
        @RequestParam("end_date") @NotNull final LocalDate endDate,
        @RequestParam("page") @NotNull @Min(Constants.QUERY_MINIMUM_PAGE) final Integer page,
        @RequestParam("size") @NotNull @Min(Constants.QUERY_PAGE_MINIMUM_SIZE) @Max(Constants.BANK_STATEMENT_MAXIMUM_PAGE_SIZE) final Integer size
    ) {
        log.info(
            "|-> Request received for getBankStatement: customer ID={}, start date={}, end date={}, page={}, size={}",
            customerId, startDate, endDate, page, size);
        return Mono.fromCallable(() -> reportMapper.toBankStatementInput(customerId, startDate, endDate, page, size))
            .flatMap(reportService::getBankStatement)
            .doOnSuccess(bankStatementOutputPage -> log.info(
                "<-| Response ready for getBankStatement: customer ID={}, start date={}, end date={}, page={}, size={}",
                customerId, startDate, endDate, page, size))
            .doOnError(error -> log.error(
                "<-| Error in getBankStatement: customer ID={}, start date={}, end date={}, page={}, size={}, error={}",
                customerId, startDate, endDate, page, size, error.getMessage(), error))
            .map(ResponseEntity::ok);
    }

    @NonNull
    @GetMapping("/bank-statements/{customer_id}/pdf")
    public Mono<ResponseEntity<String>> getBankStatementPdf(
        @PathVariable("customer_id") @NotNull @Min(1L) final Long customerId,
        @RequestParam("start_date") @NotNull final LocalDate startDate,
        @RequestParam("end_date") @NotNull final LocalDate endDate
    ) {
        log.info(
            "|-> Request received for getBankStatementPdf: customer ID={}, start date={}, end date={}",
            customerId, startDate, endDate);
        return Mono.fromCallable(() -> reportMapper.toBankStatementPdfInput(customerId, startDate, endDate))
            .flatMap(reportService::generateBankStatementPdf)
            .doOnSuccess(bankStatementPdfBase64Content -> log.info(
                "<-| Response ready for getBankStatementPdf: customer ID={}, start date={}, end date={}",
                customerId, startDate, endDate))
            .doOnError(error -> log.error(
                "<-| Error in getBankStatementPdf: customer ID={}, start date={}, end date={}, error={}",
                customerId, startDate, endDate, error.getMessage(), error))
            .map(ResponseEntity::ok);
    }
}
