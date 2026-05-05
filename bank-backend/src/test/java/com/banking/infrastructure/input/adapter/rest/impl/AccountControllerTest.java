package com.banking.infrastructure.input.adapter.rest.impl;

import static com.banking.util.MockDataUtils.ACCOUNT_ID;
import static com.banking.util.MockDataUtils.ACCOUNT_NUMBER;
import static com.banking.util.MockDataUtils.CUSTOMER_ID;
import static com.banking.util.MockDataUtils.PAGE_QUERY_PARAM_NAME;
import static com.banking.util.MockDataUtils.PAGE_QUERY_PARAM_VALUE;
import static com.banking.util.MockDataUtils.SIZE_QUERY_PARAM_NAME;
import static com.banking.util.MockDataUtils.SIZE_QUERY_PARAM_VALUE;
import static com.banking.util.MockDataUtils.getAccountInput;
import static com.banking.util.MockDataUtils.getAccountOutput;
import static com.banking.util.MockDataUtils.getRequestHeaders;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.banking.application.input.port.AccountService;
import com.banking.domain.AccountInput;
import com.banking.domain.AccountOutput;
import com.banking.domain.exception.AccountNotFoundException;
import com.banking.domain.exception.AccountsNotFoundException;
import com.banking.infrastructure.input.adapter.rest.configuration.SecurityConfiguration;
import com.banking.infrastructure.input.adapter.rest.error.ErrorResolverHandler;
import com.banking.infrastructure.input.adapter.rest.error.bean.ErrorModel;
import com.banking.infrastructure.input.adapter.rest.mapper.QueryInputMapper;
import com.banking.infrastructure.input.adapter.rest.mapper.QueryInputMapperImpl;
import com.banking.infrastructure.input.adapter.rest.strategy.AccountsQueryStrategy;
import com.banking.infrastructure.input.adapter.rest.strategy.AccountsQueryStrategyResolver;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(value = AccountController.class)
@ExtendWith(MockitoExtension.class)
@ImportAutoConfiguration({
    SecurityConfiguration.class,
    ErrorResolverHandler.class,
    QueryInputMapperImpl.class
})
class AccountControllerTest {
    private static final String ACCOUNTS_PATH = "/accounts";

    @MockitoBean
    private AccountService accountService;
    @MockitoBean
    private AccountsQueryStrategyResolver accountsQueryStrategyResolver;
    @MockitoSpyBean
    private QueryInputMapper queryInputMapper;
    @Autowired
    private WebTestClient webTestClient;

    private void mockAccountServiceQueryAccounts(
        @NonNull final Mono<org.springframework.data.domain.Page<AccountOutput>> result) {
        when(accountService.queryAccounts(any())).thenReturn(result);
    }

    private void verifyAccountServiceQueryAccounts() {
        verify(accountService).queryAccounts(any());
    }

    private void verifyQueryInputMapperToAccountQueryInput() {
        verify(queryInputMapper).toAccountQueryInput(any(), any(), any(), any(), any());
    }

    private void queryAccountsThenExpectStatusAndBodyClass(
        @NonNull final HttpHeaders headers,
        @NonNull final HttpStatus expectedHttpStatus,
        final boolean expectPageContentArray,
        @Nullable final Class<?> expectedBodyClass
    ) {
        final var responseSpec = webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path(ACCOUNTS_PATH + "/query")
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
    void givenQueryParamsWhenQueryAccountsThenExpectOkStatusAndAccountOutputsPage() {
        final var pageContent = List.of(getAccountOutput());
        final var pageable = PageRequest.of(0, pageContent.size());
        final var accountOutputsPage = new PageImpl<>(pageContent, pageable, pageContent.size());
        mockAccountServiceQueryAccounts(Mono.just(accountOutputsPage));
        queryAccountsThenExpectStatusAndBodyClass(getRequestHeaders(), HttpStatus.OK, true, null);
        verifyQueryInputMapperToAccountQueryInput();
        verifyAccountServiceQueryAccounts();
    }

    @Test
    void givenQueryParamsWhenQueryAccountsAndAccountServiceQueryAccountsReturnsExceptionThenExpectInternalServerErrorStatusAndErrorModel() {
        mockAccountServiceQueryAccounts(Mono.error(new RuntimeException("Query accounts test error")));
        queryAccountsThenExpectStatusAndBodyClass(getRequestHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, false,
            ErrorModel.class);
        verifyQueryInputMapperToAccountQueryInput();
        verifyAccountServiceQueryAccounts();
    }

    private void mockAccountServiceGetAccountById(@NonNull final Mono<AccountOutput> result) {
        when(accountService.getAccountById(any())).thenReturn(result);
    }

    private void verifyAccountServiceGetAccountById() {
        verify(accountService).getAccountById(any());
    }

    private void getAccountByIdThenExpectStatusAndBodyClass(
        @NonNull final Long id,
        @NonNull final HttpHeaders headers,
        @NonNull final HttpStatus expectedHttpStatus,
        @NonNull final Class<?> expectedBodyClass
    ) {
        webTestClient.get()
            .uri(ACCOUNTS_PATH + "/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .headers(httpHeaders -> httpHeaders.addAll(headers))
            .exchange()
            .expectStatus().isEqualTo(expectedHttpStatus)
            .expectBody(expectedBodyClass);
    }

    @Test
    void givenAccountIdWhenGetAccountByIdThenExpectOkStatusAndAccountOutput() {
        mockAccountServiceGetAccountById(Mono.just(getAccountOutput()));
        getAccountByIdThenExpectStatusAndBodyClass(ACCOUNT_ID, getRequestHeaders(), HttpStatus.OK, AccountOutput.class);
        verifyAccountServiceGetAccountById();
    }

    @Test
    void givenAccountIdWhenGetAccountByIdAndAccountServiceGetAccountByIdReturnsAccountNotFoundExceptionThenExpectNotFoundStatusAndErrorModel() {
        mockAccountServiceGetAccountById(Mono.error(new AccountNotFoundException()));
        getAccountByIdThenExpectStatusAndBodyClass(ACCOUNT_ID, getRequestHeaders(), HttpStatus.NOT_FOUND,
            ErrorModel.class);
        verifyAccountServiceGetAccountById();
    }

    private void mockAccountsQueryStrategyResolver(@NonNull final Mono<AccountsQueryStrategy> result) {
        when(accountsQueryStrategyResolver.resolve(any(), any())).thenReturn(result);
    }

    private void verifyAccountsQueryStrategyResolver() {
        verify(accountsQueryStrategyResolver).resolve(any(), any());
    }

    private void getAccountsByAccountNumberOrCustomerIdThenExpectStatusAndBodyClass(
        @Nullable final String accountNumber,
        @Nullable final Long customerId,
        @NonNull final HttpHeaders headers,
        @NonNull final HttpStatus expectedHttpStatus,
        @NonNull final Class<?> expectedBodyClass
    ) {
        webTestClient.get()
            .uri(uriBuilder -> {
                final var builder = uriBuilder.path(ACCOUNTS_PATH);
                if (accountNumber != null) {
                    builder.queryParam("account_number", accountNumber);
                }
                if (customerId != null) {
                    builder.queryParam("customer_id", customerId);
                }
                return builder.build();
            })
            .accept(MediaType.APPLICATION_JSON)
            .headers(httpHeaders -> httpHeaders.addAll(headers))
            .exchange()
            .expectStatus().isEqualTo(expectedHttpStatus)
            .expectBody(expectedBodyClass);
    }

    @Test
    void givenAccountNumberWhenGetAccountsByAccountNumberOrCustomerIdThenExpectOkStatusAndAccountOutputList() {
        final var accountsQueryStrategy = mockAccountsQueryStrategyWithAccounts(List.of(getAccountOutput()));
        mockAccountsQueryStrategyResolver(Mono.just(accountsQueryStrategy));
        getAccountsByAccountNumberOrCustomerIdThenExpectStatusAndBodyClass(ACCOUNT_NUMBER, null, getRequestHeaders(),
            HttpStatus.OK, List.class);
        verifyAccountsQueryStrategyResolver();
    }

    @Test
    void givenCustomerIdWhenGetAccountsByAccountNumberOrCustomerIdThenExpectOkStatusAndAccountOutputList() {
        final var accountsQueryStrategy = mockAccountsQueryStrategyWithAccounts(List.of(getAccountOutput()));
        mockAccountsQueryStrategyResolver(Mono.just(accountsQueryStrategy));
        getAccountsByAccountNumberOrCustomerIdThenExpectStatusAndBodyClass(null, CUSTOMER_ID, getRequestHeaders(),
            HttpStatus.OK, List.class);
        verifyAccountsQueryStrategyResolver();
    }

    @Test
    void givenAccountNumberWhenGetAccountsByAccountNumberOrCustomerIdAndAccountsQueryStrategyResolverReturnsAccountsNotFoundExceptionThenExpectNotFoundStatusAndErrorModel() {
        mockAccountsQueryStrategyResolver(Mono.error(new AccountsNotFoundException()));
        getAccountsByAccountNumberOrCustomerIdThenExpectStatusAndBodyClass(ACCOUNT_NUMBER, null, getRequestHeaders(),
            HttpStatus.NOT_FOUND, ErrorModel.class);
        verifyAccountsQueryStrategyResolver();
    }

    @Test
    void givenAccountNumberWhenGetAccountsByAccountNumberOrCustomerIdAndAccountsQueryStrategyResolverReturnsExceptionThenExpectInternalServerErrorStatusAndErrorModel() {
        mockAccountsQueryStrategyResolver(
            Mono.error(new RuntimeException("Get accounts by account number or customer id test error")));
        getAccountsByAccountNumberOrCustomerIdThenExpectStatusAndBodyClass(ACCOUNT_NUMBER, null, getRequestHeaders(),
            HttpStatus.INTERNAL_SERVER_ERROR, ErrorModel.class);
        verifyAccountsQueryStrategyResolver();
    }

    private void mockAccountServiceCreateAccount(@NonNull final Mono<AccountOutput> result) {
        when(accountService.createAccount(any())).thenReturn(result);
    }

    private void verifyAccountServiceCreateAccount() {
        verify(accountService).createAccount(any());
    }

    private void createAccountThenExpectStatusAndBodyClass(
        @NonNull final HttpHeaders headers,
        @NonNull final AccountInput body,
        @NonNull final HttpStatus expectedHttpStatus,
        @NonNull final Class<?> expectedBodyClass
    ) {
        webTestClient.post()
            .uri(ACCOUNTS_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .headers(httpHeaders -> httpHeaders.addAll(headers))
            .body(Mono.just(body), body.getClass())
            .exchange()
            .expectStatus().isEqualTo(expectedHttpStatus)
            .expectBody(expectedBodyClass);
    }

    @Test
    void givenAccountInputWhenCreateAccountThenExpectCreatedStatusAndAccountOutput() {
        mockAccountServiceCreateAccount(Mono.just(getAccountOutput()));
        createAccountThenExpectStatusAndBodyClass(getRequestHeaders(), getAccountInput(), HttpStatus.CREATED,
            AccountOutput.class);
        verifyAccountServiceCreateAccount();
    }

    @Test
    void givenAccountInputWhenCreateAccountAndAccountServiceCreateAccountReturnsExceptionThenExpectInternalServerErrorStatusAndErrorModel() {
        mockAccountServiceCreateAccount(Mono.error(new RuntimeException("Create account test error")));
        createAccountThenExpectStatusAndBodyClass(getRequestHeaders(), getAccountInput(),
            HttpStatus.INTERNAL_SERVER_ERROR, ErrorModel.class);
        verifyAccountServiceCreateAccount();
    }

    private void mockAccountServiceUpdateAccount(@NonNull final Mono<AccountOutput> result) {
        when(accountService.updateAccount(any())).thenReturn(result);
    }

    private void verifyAccountServiceUpdateAccount() {
        verify(accountService).updateAccount(any());
    }

    private void updateAccountThenExpectStatusAndBodyClass(
        @NonNull final Long id,
        @NonNull final HttpHeaders headers,
        @NonNull final AccountInput body,
        @NonNull final HttpStatus expectedHttpStatus,
        @NonNull final Class<?> expectedBodyClass
    ) {
        webTestClient.put()
            .uri(ACCOUNTS_PATH + "/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .headers(httpHeaders -> httpHeaders.addAll(headers))
            .body(Mono.just(body), body.getClass())
            .exchange()
            .expectStatus().isEqualTo(expectedHttpStatus)
            .expectBody(expectedBodyClass);
    }

    @Test
    void givenAccountInputWhenUpdateAccountThenExpectOkStatusAndAccountOutput() {
        mockAccountServiceUpdateAccount(Mono.just(getAccountOutput()));
        updateAccountThenExpectStatusAndBodyClass(ACCOUNT_ID, getRequestHeaders(), getAccountInput(), HttpStatus.OK,
            AccountOutput.class);
        verifyAccountServiceUpdateAccount();
    }

    @Test
    void givenAccountInputWhenUpdateAccountAndAccountServiceUpdateAccountReturnsExceptionThenExpectInternalServerErrorStatusAndErrorModel() {
        mockAccountServiceUpdateAccount(Mono.error(new RuntimeException("Update account test error")));
        updateAccountThenExpectStatusAndBodyClass(ACCOUNT_ID, getRequestHeaders(), getAccountInput(),
            HttpStatus.INTERNAL_SERVER_ERROR, ErrorModel.class);
        verifyAccountServiceUpdateAccount();
    }

    private void mockAccountServiceUpdateAccountPartially(@NonNull final Mono<AccountOutput> result) {
        when(accountService.updateAccountPartially(any())).thenReturn(result);
    }

    private void verifyAccountServiceUpdateAccountPartially() {
        verify(accountService).updateAccountPartially(any());
    }

    private void updateAccountPartiallyThenExpectStatusAndBodyClass(
        @NonNull final Long id,
        @NonNull final HttpHeaders headers,
        @NonNull final AccountInput body,
        @NonNull final HttpStatus expectedHttpStatus,
        @NonNull final Class<?> expectedBodyClass
    ) {
        webTestClient.patch()
            .uri(ACCOUNTS_PATH + "/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .headers(httpHeaders -> httpHeaders.addAll(headers))
            .body(Mono.just(body), body.getClass())
            .exchange()
            .expectStatus().isEqualTo(expectedHttpStatus)
            .expectBody(expectedBodyClass);
    }

    @Test
    void givenAccountInputWhenUpdateAccountPartiallyThenExpectOkStatusAndAccountOutput() {
        mockAccountServiceUpdateAccountPartially(Mono.just(getAccountOutput()));
        updateAccountPartiallyThenExpectStatusAndBodyClass(ACCOUNT_ID, getRequestHeaders(), getAccountInput(),
            HttpStatus.OK, AccountOutput.class);
        verifyAccountServiceUpdateAccountPartially();
    }

    @Test
    void givenAccountInputWhenUpdateAccountPartiallyAndAccountServiceUpdateAccountPartiallyReturnsExceptionThenExpectInternalServerErrorStatusAndErrorModel() {
        mockAccountServiceUpdateAccountPartially(
            Mono.error(new RuntimeException("Update account partially test error")));
        updateAccountPartiallyThenExpectStatusAndBodyClass(ACCOUNT_ID, getRequestHeaders(), getAccountInput(),
            HttpStatus.INTERNAL_SERVER_ERROR, ErrorModel.class);
        verifyAccountServiceUpdateAccountPartially();
    }

    private void mockAccountServiceDeleteAccount(@NonNull final Mono<AccountOutput> result) {
        when(accountService.deleteAccount(any())).thenReturn(result);
    }

    private void verifyAccountServiceDeleteAccount() {
        verify(accountService).deleteAccount(any());
    }

    private void deleteAccountThenExpectStatusAndBodyClass(
        @NonNull final Long id,
        @NonNull final HttpHeaders headers,
        @NonNull final HttpStatus expectedHttpStatus,
        @NonNull final Class<?> expectedBodyClass
    ) {
        webTestClient.delete()
            .uri(ACCOUNTS_PATH + "/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .headers(httpHeaders -> httpHeaders.addAll(headers))
            .exchange()
            .expectStatus().isEqualTo(expectedHttpStatus)
            .expectBody(expectedBodyClass);
    }

    @Test
    void givenAccountIdWhenDeleteAccountThenExpectOkStatusAndAccountOutput() {
        mockAccountServiceDeleteAccount(Mono.just(getAccountOutput()));
        deleteAccountThenExpectStatusAndBodyClass(ACCOUNT_ID, getRequestHeaders(), HttpStatus.OK, AccountOutput.class);
        verifyAccountServiceDeleteAccount();
    }

    @Test
    void givenAccountIdWhenDeleteAccountAndAccountServiceDeleteAccountReturnsAccountNotFoundExceptionThenExpectNotFoundStatusAndErrorModel() {
        mockAccountServiceDeleteAccount(Mono.error(new AccountNotFoundException()));
        deleteAccountThenExpectStatusAndBodyClass(ACCOUNT_ID, getRequestHeaders(), HttpStatus.NOT_FOUND,
            ErrorModel.class);
        verifyAccountServiceDeleteAccount();
    }

    @NonNull
    private AccountsQueryStrategy mockAccountsQueryStrategyWithAccounts(@NonNull final List<AccountOutput> accounts) {
        return new DummyAccountsQueryStrategy(accounts);
    }

    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static class DummyAccountsQueryStrategy implements AccountsQueryStrategy {
        List<AccountOutput> accounts;

        @Override
        public boolean supports(@Nullable final String accountNumber, @Nullable final Long customerId) {
            return true;
        }

        @NonNull
        @Override
        public Flux<AccountOutput> getAccounts(@Nullable final String accountNumber, @Nullable final Long customerId) {
            return Flux.fromIterable(accounts);
        }
    }
}
