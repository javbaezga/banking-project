package com.banking.infrastructure.input.adapter.rest.impl;

import static com.banking.util.MockDataUtils.PAGE_QUERY_PARAM_NAME;
import static com.banking.util.MockDataUtils.PAGE_QUERY_PARAM_VALUE;
import static com.banking.util.MockDataUtils.SIZE_QUERY_PARAM_NAME;
import static com.banking.util.MockDataUtils.SIZE_QUERY_PARAM_VALUE;
import static com.banking.util.MockDataUtils.TRANSACTION_ID;
import static com.banking.util.MockDataUtils.getRequestHeaders;
import static com.banking.util.MockDataUtils.getTransactionInput;
import static com.banking.util.MockDataUtils.getTransactionOutput;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.banking.application.input.port.TransactionService;
import com.banking.domain.TransactionInput;
import com.banking.domain.TransactionOutput;
import com.banking.domain.exception.TransactionNotFoundException;
import com.banking.infrastructure.input.adapter.rest.configuration.SecurityConfiguration;
import com.banking.infrastructure.input.adapter.rest.error.ErrorResolverHandler;
import com.banking.infrastructure.input.adapter.rest.error.bean.ErrorModel;
import com.banking.infrastructure.input.adapter.rest.mapper.QueryInputMapper;
import com.banking.infrastructure.input.adapter.rest.mapper.QueryInputMapperImpl;
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

@WebFluxTest(value = TransactionController.class)
@ExtendWith(MockitoExtension.class)
@ImportAutoConfiguration({
    SecurityConfiguration.class,
    ErrorResolverHandler.class,
    QueryInputMapperImpl.class
})
class TransactionControllerTest {
    private static final String TRANSACTIONS_PATH = "/transactions";

    @MockitoBean
    private TransactionService transactionService;
    @MockitoSpyBean
    private QueryInputMapper queryInputMapper;
    @Autowired
    private WebTestClient webTestClient;

    private void mockTransactionServiceQueryTransactions(@NonNull final Mono<Page<TransactionOutput>> result) {
        when(transactionService.queryTransactions(any())).thenReturn(result);
    }

    private void verifyTransactionServiceQueryTransactions() {
        verify(transactionService).queryTransactions(any());
    }

    private void verifyQueryInputMapperToTransactionQueryInput() {
        verify(queryInputMapper).toTransactionQueryInput(any(), any(), any(), any(), any());
    }

    private void queryTransactionsThenExpectStatusAndBodyClass(
        @NonNull final HttpHeaders headers,
        @NonNull final HttpStatus expectedHttpStatus,
        final boolean expectPageContentArray,
        @Nullable final Class<?> expectedBodyClass
    ) {
        final var responseSpec = webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path(TRANSACTIONS_PATH + "/query")
                .queryParam(PAGE_QUERY_PARAM_NAME, PAGE_QUERY_PARAM_VALUE)
                .queryParam(SIZE_QUERY_PARAM_NAME, SIZE_QUERY_PARAM_VALUE)
                .build())
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
    void givenQueryParamsWhenQueryTransactionsThenExpectOkStatusAndTransactionOutputsPage() {
        final var pageContent = List.of(getTransactionOutput());
        final var pageable = PageRequest.of(0, pageContent.size());
        final var transactionOutputsPage = new PageImpl<>(pageContent, pageable, pageContent.size());
        mockTransactionServiceQueryTransactions(Mono.just(transactionOutputsPage));
        queryTransactionsThenExpectStatusAndBodyClass(getRequestHeaders(), HttpStatus.OK, true, null);
        verifyQueryInputMapperToTransactionQueryInput();
        verifyTransactionServiceQueryTransactions();
    }

    @Test
    void givenQueryParamsWhenQueryTransactionsAndTransactionServiceQueryTransactionsReturnsExceptionThenExpectInternalServerErrorStatusAndErrorModel() {
        mockTransactionServiceQueryTransactions(Mono.error(new RuntimeException("Query transactions test error")));
        queryTransactionsThenExpectStatusAndBodyClass(getRequestHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, false,
            ErrorModel.class);
        verifyQueryInputMapperToTransactionQueryInput();
        verifyTransactionServiceQueryTransactions();
    }

    private void mockTransactionServiceGetTransactionById(@NonNull final Mono<TransactionOutput> result) {
        when(transactionService.getTransactionById(any())).thenReturn(result);
    }

    private void verifyTransactionServiceGetTransactionById() {
        verify(transactionService).getTransactionById(any());
    }

    private void getTransactionByIdThenExpectStatusAndBodyClass(
        @NonNull final Long id,
        @NonNull final HttpHeaders headers,
        @NonNull final HttpStatus expectedHttpStatus,
        @NonNull final Class<?> expectedBodyClass
    ) {
        webTestClient.get()
            .uri(TRANSACTIONS_PATH + "/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .headers(httpHeaders -> httpHeaders.addAll(headers))
            .exchange()
            .expectStatus().isEqualTo(expectedHttpStatus)
            .expectBody(expectedBodyClass);
    }

    @Test
    void givenTransactionIdWhenGetTransactionByIdThenExpectOkStatusAndTransactionOutput() {
        mockTransactionServiceGetTransactionById(Mono.just(getTransactionOutput()));
        getTransactionByIdThenExpectStatusAndBodyClass(TRANSACTION_ID, getRequestHeaders(), HttpStatus.OK,
            TransactionOutput.class);
        verifyTransactionServiceGetTransactionById();
    }

    @Test
    void givenTransactionIdWhenGetTransactionByIdAndTransactionServiceGetTransactionByIdReturnsTransactionNotFoundExceptionThenExpectNotFoundStatusAndErrorModel() {
        mockTransactionServiceGetTransactionById(Mono.error(new TransactionNotFoundException()));
        getTransactionByIdThenExpectStatusAndBodyClass(TRANSACTION_ID, getRequestHeaders(), HttpStatus.NOT_FOUND,
            ErrorModel.class);
        verifyTransactionServiceGetTransactionById();
    }

    private void mockTransactionServiceCreateTransaction(@NonNull final Mono<TransactionOutput> result) {
        when(transactionService.createTransaction(any())).thenReturn(result);
    }

    private void verifyTransactionServiceCreateTransaction() {
        verify(transactionService).createTransaction(any());
    }

    private void createTransactionThenExpectStatusAndBodyClass(
        @NonNull final HttpHeaders headers,
        @NonNull final TransactionInput body,
        @NonNull final HttpStatus expectedHttpStatus,
        @NonNull final Class<?> expectedBodyClass
    ) {
        webTestClient.post()
            .uri(TRANSACTIONS_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .headers(httpHeaders -> httpHeaders.addAll(headers))
            .body(Mono.just(body), body.getClass())
            .exchange()
            .expectStatus().isEqualTo(expectedHttpStatus)
            .expectBody(expectedBodyClass);
    }

    @Test
    void givenTransactionInputWhenCreateTransactionThenExpectCreatedStatusAndTransactionOutput() {
        mockTransactionServiceCreateTransaction(Mono.just(getTransactionOutput()));
        createTransactionThenExpectStatusAndBodyClass(getRequestHeaders(), getTransactionInput(), HttpStatus.CREATED,
            TransactionOutput.class);
        verifyTransactionServiceCreateTransaction();
    }

    @Test
    void givenTransactionInputWhenCreateTransactionAndTransactionServiceCreateTransactionReturnsExceptionThenExpectInternalServerErrorStatusAndErrorModel() {
        mockTransactionServiceCreateTransaction(Mono.error(new RuntimeException("Create transaction test error")));
        createTransactionThenExpectStatusAndBodyClass(getRequestHeaders(), getTransactionInput(),
            HttpStatus.INTERNAL_SERVER_ERROR, ErrorModel.class);
        verifyTransactionServiceCreateTransaction();
    }
}
