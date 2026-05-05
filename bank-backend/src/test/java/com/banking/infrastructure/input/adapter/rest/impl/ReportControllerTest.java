package com.banking.infrastructure.input.adapter.rest.impl;

import static com.banking.util.MockDataUtils.CUSTOMER_ID;
import static com.banking.util.MockDataUtils.PAGE_QUERY_PARAM_NAME;
import static com.banking.util.MockDataUtils.PAGE_QUERY_PARAM_VALUE;
import static com.banking.util.MockDataUtils.REPORT_END_DATE;
import static com.banking.util.MockDataUtils.REPORT_START_DATE;
import static com.banking.util.MockDataUtils.SIZE_QUERY_PARAM_NAME;
import static com.banking.util.MockDataUtils.SIZE_QUERY_PARAM_VALUE;
import static com.banking.util.MockDataUtils.getBankStatementOutput;
import static com.banking.util.MockDataUtils.getRequestHeaders;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.banking.application.input.port.ReportService;
import com.banking.domain.BankStatementOutput;
import com.banking.domain.exception.BankStatementGettingException;
import com.banking.domain.exception.BankStatementPdfGenerationException;
import com.banking.infrastructure.input.adapter.rest.configuration.SecurityConfiguration;
import com.banking.infrastructure.input.adapter.rest.error.ErrorResolverHandler;
import com.banking.infrastructure.input.adapter.rest.error.bean.ErrorModel;
import com.banking.infrastructure.input.adapter.rest.mapper.ReportMapper;
import com.banking.infrastructure.input.adapter.rest.mapper.ReportMapperImpl;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(value = ReportController.class)
@ExtendWith(MockitoExtension.class)
@ImportAutoConfiguration({
    SecurityConfiguration.class,
    ErrorResolverHandler.class,
    ReportMapperImpl.class
})
class ReportControllerTest {
    private static final String REPORTS_PATH = "/reports";
    private static final String BANK_STATEMENTS_PATH = REPORTS_PATH + "/bank-statements";

    @MockitoBean
    private ReportService reportService;
    @MockitoSpyBean
    private ReportMapper reportMapper;
    @Autowired
    private WebTestClient webTestClient;

    private void mockReportServiceGetBankStatement(@NonNull final Mono<Page<BankStatementOutput>> result) {
        when(reportService.getBankStatement(any())).thenReturn(result);
    }

    private void verifyReportServiceGetBankStatement() {
        verify(reportService).getBankStatement(any());
    }

    private void verifyReportMapperToBankStatementInput() {
        verify(reportMapper).toBankStatementInput(any(), any(), any(), any(), any());
    }

    private void getBankStatementThenExpectStatusAndBodyClass(
        @NonNull final Long customerId,
        @NonNull final HttpHeaders headers,
        @NonNull final HttpStatus expectedHttpStatus,
        final boolean expectPageContentArray,
        @Nullable final Class<?> expectedBodyClass
    ) {
        final var responseSpec = webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path(BANK_STATEMENTS_PATH + "/{customer_id}")
                .queryParam("start_date", REPORT_START_DATE)
                .queryParam("end_date", REPORT_END_DATE)
                .queryParam(PAGE_QUERY_PARAM_NAME, PAGE_QUERY_PARAM_VALUE)
                .queryParam(SIZE_QUERY_PARAM_NAME, SIZE_QUERY_PARAM_VALUE)
                .build(customerId))
            .accept(MediaType.APPLICATION_JSON)
            .headers(httpHeaders -> httpHeaders.addAll(headers))
            .exchange()
            .expectStatus()
            .isEqualTo(expectedHttpStatus);
        if (expectPageContentArray) {
            responseSpec.expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.totalElements").exists();
        } else if (expectedBodyClass != null) {
            responseSpec.expectBody(expectedBodyClass);
        } else {
            responseSpec.expectBody();
        }
    }

    @Test
    void givenCustomerIdWhenGetBankStatementThenExpectOkStatusAndBankStatementOutputsPage() {
        final var pageContent = List.of(getBankStatementOutput());
        final var pageable = PageRequest.of(0, pageContent.size());
        final var bankStatementOutputsPage = new PageImpl<>(pageContent, pageable, pageContent.size());
        mockReportServiceGetBankStatement(Mono.just(bankStatementOutputsPage));
        getBankStatementThenExpectStatusAndBodyClass(CUSTOMER_ID, getRequestHeaders(), HttpStatus.OK, true, null);
        verifyReportMapperToBankStatementInput();
        verifyReportServiceGetBankStatement();
    }

    @Test
    void givenCustomerIdWhenGetBankStatementAndReportServiceGetBankStatementReturnsBankStatementGettingExceptionThenExpectInternalServerErrorStatusAndErrorModel() {
        mockReportServiceGetBankStatement(Mono.error(new BankStatementGettingException()));
        getBankStatementThenExpectStatusAndBodyClass(CUSTOMER_ID, getRequestHeaders(), HttpStatus.INTERNAL_SERVER_ERROR,
            false, ErrorModel.class);
        verifyReportMapperToBankStatementInput();
        verifyReportServiceGetBankStatement();
    }

    @Test
    void givenCustomerIdWhenGetBankStatementAndReportServiceGetBankStatementReturnsExceptionThenExpectInternalServerErrorStatusAndErrorModel() {
        mockReportServiceGetBankStatement(Mono.error(new RuntimeException("Get bank statement test error")));
        getBankStatementThenExpectStatusAndBodyClass(CUSTOMER_ID, getRequestHeaders(), HttpStatus.INTERNAL_SERVER_ERROR,
            false, ErrorModel.class);
        verifyReportMapperToBankStatementInput();
        verifyReportServiceGetBankStatement();
    }

    private void mockReportServiceGenerateBankStatementPdf(@NonNull final Mono<String> result) {
        when(reportService.generateBankStatementPdf(any())).thenReturn(result);
    }

    private void verifyReportServiceGenerateBankStatementPdf() {
        verify(reportService).generateBankStatementPdf(any());
    }

    private void verifyReportMapperToBankStatementPdfInput() {
        verify(reportMapper).toBankStatementPdfInput(any(), any(), any());
    }

    private void getBankStatementPdfThenExpectStatusAndBodyClass(
        @NonNull final Long customerId,
        @NonNull final HttpHeaders headers,
        @NonNull final HttpStatus expectedHttpStatus,
        @NonNull final Class<?> expectedBodyClass
    ) {
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path(BANK_STATEMENTS_PATH + "/{customer_id}/pdf")
                .queryParam("start_date", REPORT_START_DATE)
                .queryParam("end_date", REPORT_END_DATE)
                .build(customerId))
            .accept(MediaType.APPLICATION_JSON)
            .headers(httpHeaders -> httpHeaders.addAll(headers))
            .exchange()
            .expectStatus().isEqualTo(expectedHttpStatus)
            .expectBody(expectedBodyClass);
    }

    @Test
    void givenCustomerIdWhenGetBankStatementPdfThenExpectOkStatusAndBase64PdfContent() {
        mockReportServiceGenerateBankStatementPdf(Mono.just("base64PdfContent"));
        getBankStatementPdfThenExpectStatusAndBodyClass(CUSTOMER_ID, getRequestHeaders(), HttpStatus.OK,
            String.class);
        verifyReportMapperToBankStatementPdfInput();
        verifyReportServiceGenerateBankStatementPdf();
    }

    @Test
    void givenCustomerIdWhenGetBankStatementPdfAndReportServiceGenerateBankStatementPdfReturnsBankStatementPdfGenerationExceptionThenExpectInternalServerErrorStatusAndErrorModel() {
        mockReportServiceGenerateBankStatementPdf(Mono.error(new BankStatementPdfGenerationException()));
        getBankStatementPdfThenExpectStatusAndBodyClass(CUSTOMER_ID, getRequestHeaders(),
            HttpStatus.INTERNAL_SERVER_ERROR, ErrorModel.class);
        verifyReportMapperToBankStatementPdfInput();
        verifyReportServiceGenerateBankStatementPdf();
    }

    @Test
    void givenCustomerIdWhenGetBankStatementPdfAndReportServiceGenerateBankStatementPdfReturnsExceptionThenExpectInternalServerErrorStatusAndErrorModel() {
        mockReportServiceGenerateBankStatementPdf(
            Mono.error(new RuntimeException("Generate bank statement PDF test error")));
        getBankStatementPdfThenExpectStatusAndBodyClass(CUSTOMER_ID, getRequestHeaders(),
            HttpStatus.INTERNAL_SERVER_ERROR, ErrorModel.class);
        verifyReportMapperToBankStatementPdfInput();
        verifyReportServiceGenerateBankStatementPdf();
    }
}
