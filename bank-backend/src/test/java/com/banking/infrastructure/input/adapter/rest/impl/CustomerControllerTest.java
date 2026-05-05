package com.banking.infrastructure.input.adapter.rest.impl;

import static com.banking.util.MockDataUtils.CUSTOMER_ID;
import static com.banking.util.MockDataUtils.CUSTOMER_ID_NUMBER;
import static com.banking.util.MockDataUtils.PAGE_QUERY_PARAM_NAME;
import static com.banking.util.MockDataUtils.PAGE_QUERY_PARAM_VALUE;
import static com.banking.util.MockDataUtils.SIZE_QUERY_PARAM_NAME;
import static com.banking.util.MockDataUtils.SIZE_QUERY_PARAM_VALUE;
import static com.banking.util.MockDataUtils.getCustomerInput;
import static com.banking.util.MockDataUtils.getCustomerOutput;
import static com.banking.util.MockDataUtils.getRequestHeaders;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.banking.application.input.port.CustomerService;
import com.banking.domain.CustomerInput;
import com.banking.domain.CustomerOutput;
import com.banking.domain.exception.CustomerNotFoundException;
import com.banking.infrastructure.input.adapter.rest.configuration.SecurityConfiguration;
import com.banking.infrastructure.input.adapter.rest.error.ErrorResolverHandler;
import com.banking.infrastructure.input.adapter.rest.error.bean.ErrorModel;
import com.banking.infrastructure.input.adapter.rest.mapper.QueryInputMapper;
import com.banking.infrastructure.input.adapter.rest.mapper.QueryInputMapperImpl;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(value = CustomerController.class)
@ExtendWith(MockitoExtension.class)
@ImportAutoConfiguration({
    SecurityConfiguration.class,
    ErrorResolverHandler.class,
    QueryInputMapperImpl.class
})
class CustomerControllerTest {
    private static final String CUSTOMERS_PATH = "/customers";

    @MockitoBean
    private CustomerService customerService;
    @MockitoSpyBean
    private QueryInputMapper queryInputMapper;
    @Autowired
    private WebTestClient webTestClient;

    private void mockCustomerServiceQueryCustomers(@NonNull final Mono<Page<CustomerOutput>> result) {
        when(customerService.queryCustomers(any())).thenReturn(result);
    }

    private void verifyCustomerServiceQueryCustomers() {
        verify(customerService).queryCustomers(any());
    }

    private void verifyQueryInputMapperToCustomerQueryInput() {
        verify(queryInputMapper).toCustomerQueryInput(any(), any(), any(), any(), any());
    }

    private void queryCustomersThenExpectStatusAndBodyClass(
        @NonNull final HttpHeaders headers,
        @NonNull final HttpStatus expectedHttpStatus,
        final boolean expectPageContentArray,
        @Nullable final Class<?> expectedBodyClass
    ) {
        final var responseSpec = webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path(CUSTOMERS_PATH + "/query")
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
    void givenQueryParamsWhenQueryCustomersThenExpectOkStatusAndCustomerOutputsPage() {
        final var pageContent = List.of(getCustomerOutput());
        final var pageable = PageRequest.of(0, pageContent.size());
        final var customerOutputsPage = new PageImpl<>(pageContent, pageable, pageContent.size());
        mockCustomerServiceQueryCustomers(Mono.just(customerOutputsPage));
        queryCustomersThenExpectStatusAndBodyClass(getRequestHeaders(), HttpStatus.OK, true, null);
        verifyQueryInputMapperToCustomerQueryInput();
        verifyCustomerServiceQueryCustomers();
    }

    @Test
    void givenQueryParamsWhenQueryCustomersAndCustomerServiceQueryCustomersReturnsExceptionThenExpectInternalServerErrorStatusAndErrorModel() {
        mockCustomerServiceQueryCustomers(Mono.error(new RuntimeException("Query customers test error")));
        queryCustomersThenExpectStatusAndBodyClass(getRequestHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, false,
            ErrorModel.class);
        verifyQueryInputMapperToCustomerQueryInput();
        verifyCustomerServiceQueryCustomers();
    }

    private void mockCustomerServiceGetCustomerById(@NonNull final Mono<CustomerOutput> result) {
        when(customerService.getCustomerById(any())).thenReturn(result);
    }

    private void verifyCustomerServiceGetCustomerById() {
        verify(customerService).getCustomerById(any());
    }

    private void getCustomerByIdThenExpectStatusAndBodyClass(
        @NonNull final Long id,
        @NonNull final HttpHeaders headers,
        @NonNull final HttpStatus expectedHttpStatus,
        @NonNull final Class<?> expectedBodyClass
    ) {
        webTestClient.get()
            .uri(CUSTOMERS_PATH + "/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .headers(httpHeaders -> httpHeaders.addAll(headers))
            .exchange()
            .expectStatus().isEqualTo(expectedHttpStatus)
            .expectBody(expectedBodyClass);
    }

    @Test
    void givenCustomerIdWhenGetCustomerByIdThenExpectOkStatusAndCustomerOutput() {
        mockCustomerServiceGetCustomerById(Mono.just(getCustomerOutput()));
        getCustomerByIdThenExpectStatusAndBodyClass(CUSTOMER_ID, getRequestHeaders(), HttpStatus.OK,
            CustomerOutput.class);
        verifyCustomerServiceGetCustomerById();
    }

    @Test
    void givenCustomerIdWhenGetCustomerByIdAndCustomerServiceGetCustomerByIdReturnsCustomerNotFoundExceptionThenExpectNotFoundStatusAndErrorModel() {
        mockCustomerServiceGetCustomerById(Mono.error(new CustomerNotFoundException()));
        getCustomerByIdThenExpectStatusAndBodyClass(CUSTOMER_ID, getRequestHeaders(), HttpStatus.NOT_FOUND,
            ErrorModel.class);
        verifyCustomerServiceGetCustomerById();
    }

    private void mockCustomerServiceGetCustomerByIdNumber(@NonNull final Mono<CustomerOutput> result) {
        when(customerService.getCustomerByIdNumber(any())).thenReturn(result);
    }

    private void verifyCustomerServiceGetCustomerByIdNumber() {
        verify(customerService).getCustomerByIdNumber(any());
    }

    private void getCustomerByIdNumberThenExpectStatusAndBodyClass(
        @NonNull final String idNumber,
        @NonNull final HttpHeaders headers,
        @NonNull final HttpStatus expectedHttpStatus,
        @NonNull final Class<?> expectedBodyClass
    ) {
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path(CUSTOMERS_PATH)
                .queryParam("id_number", idNumber)
                .build())
            .accept(MediaType.APPLICATION_JSON)
            .headers(httpHeaders -> httpHeaders.addAll(headers))
            .exchange()
            .expectStatus().isEqualTo(expectedHttpStatus)
            .expectBody(expectedBodyClass);
    }

    @Test
    void givenIdNumberWhenGetCustomerByIdNumberThenExpectOkStatusAndCustomerOutput() {
        mockCustomerServiceGetCustomerByIdNumber(Mono.just(getCustomerOutput()));
        getCustomerByIdNumberThenExpectStatusAndBodyClass(CUSTOMER_ID_NUMBER, getRequestHeaders(), HttpStatus.OK,
            CustomerOutput.class);
        verifyCustomerServiceGetCustomerByIdNumber();
    }

    @Test
    void givenIdNumberWhenGetCustomerByIdNumberAndCustomerServiceGetCustomerByIdNumberReturnsCustomerNotFoundExceptionThenExpectNotFoundStatusAndErrorModel() {
        mockCustomerServiceGetCustomerByIdNumber(Mono.error(new CustomerNotFoundException()));
        getCustomerByIdNumberThenExpectStatusAndBodyClass(CUSTOMER_ID_NUMBER, getRequestHeaders(), HttpStatus.NOT_FOUND,
            ErrorModel.class);
        verifyCustomerServiceGetCustomerByIdNumber();
    }

    private void mockCustomerServiceCreateCustomer(@NonNull final Mono<CustomerOutput> result) {
        when(customerService.createCustomer(any())).thenReturn(result);
    }

    private void verifyCustomerServiceCreateCustomer() {
        verify(customerService).createCustomer(any());
    }

    private void createCustomerThenExpectStatusAndBodyClass(
        @NonNull final HttpHeaders headers,
        @NonNull final CustomerInput body,
        @NonNull final HttpStatus expectedHttpStatus,
        @NonNull final Class<?> expectedBodyClass
    ) {
        webTestClient.post()
            .uri(CUSTOMERS_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .headers(httpHeaders -> httpHeaders.addAll(headers))
            .body(Mono.just(body), body.getClass())
            .exchange()
            .expectStatus().isEqualTo(expectedHttpStatus)
            .expectBody(expectedBodyClass);
    }

    @Test
    void givenCustomerInputWhenCreateCustomerThenExpectCreatedStatusAndCustomerOutput() {
        mockCustomerServiceCreateCustomer(Mono.just(getCustomerOutput()));
        createCustomerThenExpectStatusAndBodyClass(getRequestHeaders(), getCustomerInput(), HttpStatus.CREATED,
            CustomerOutput.class);
        verifyCustomerServiceCreateCustomer();
    }

    @Test
    void givenCustomerInputWhenCreateCustomerAndCustomerServiceCreateCustomerReturnsExceptionThenExpectInternalServerErrorStatusAndErrorModel() {
        mockCustomerServiceCreateCustomer(Mono.error(new RuntimeException("Create customer test error")));
        createCustomerThenExpectStatusAndBodyClass(getRequestHeaders(), getCustomerInput(),
            HttpStatus.INTERNAL_SERVER_ERROR, ErrorModel.class);
        verifyCustomerServiceCreateCustomer();
    }

    private void mockCustomerServiceUpdateCustomer(@NonNull final Mono<CustomerOutput> result) {
        when(customerService.updateCustomer(any())).thenReturn(result);
    }

    private void verifyCustomerServiceUpdateCustomer() {
        verify(customerService).updateCustomer(any());
    }

    private void updateCustomerThenExpectStatusAndBodyClass(
        @NonNull final Long id,
        @NonNull final HttpHeaders headers,
        @NonNull final CustomerInput body,
        @NonNull final HttpStatus expectedHttpStatus,
        @NonNull final Class<?> expectedBodyClass
    ) {
        webTestClient.put()
            .uri(CUSTOMERS_PATH + "/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .headers(httpHeaders -> httpHeaders.addAll(headers))
            .body(Mono.just(body), body.getClass())
            .exchange()
            .expectStatus().isEqualTo(expectedHttpStatus)
            .expectBody(expectedBodyClass);
    }

    @Test
    void givenCustomerInputWhenUpdateCustomerThenExpectOkStatusAndCustomerOutput() {
        mockCustomerServiceUpdateCustomer(Mono.just(getCustomerOutput()));
        updateCustomerThenExpectStatusAndBodyClass(CUSTOMER_ID, getRequestHeaders(), getCustomerInput(), HttpStatus.OK,
            CustomerOutput.class);
        verifyCustomerServiceUpdateCustomer();
    }

    @Test
    void givenCustomerInputWhenUpdateCustomerAndCustomerServiceUpdateCustomerReturnsExceptionThenExpectInternalServerErrorStatusAndErrorModel() {
        mockCustomerServiceUpdateCustomer(Mono.error(new RuntimeException("Update customer test error")));
        updateCustomerThenExpectStatusAndBodyClass(CUSTOMER_ID, getRequestHeaders(), getCustomerInput(),
            HttpStatus.INTERNAL_SERVER_ERROR, ErrorModel.class);
        verifyCustomerServiceUpdateCustomer();
    }

    private void mockCustomerServiceUpdateCustomerPartially(@NonNull final Mono<CustomerOutput> result) {
        when(customerService.updateCustomerPartially(any())).thenReturn(result);
    }

    private void verifyCustomerServiceUpdateCustomerPartially() {
        verify(customerService).updateCustomerPartially(any());
    }

    private void updateCustomerPartiallyThenExpectStatusAndBodyClass(
        @NonNull final Long id,
        @NonNull final HttpHeaders headers,
        @NonNull final CustomerInput body,
        @NonNull final HttpStatus expectedHttpStatus,
        @NonNull final Class<?> expectedBodyClass
    ) {
        webTestClient.patch()
            .uri(CUSTOMERS_PATH + "/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .headers(httpHeaders -> httpHeaders.addAll(headers))
            .body(Mono.just(body), body.getClass())
            .exchange()
            .expectStatus().isEqualTo(expectedHttpStatus)
            .expectBody(expectedBodyClass);
    }

    @Test
    void givenCustomerInputWhenUpdateCustomerPartiallyThenExpectOkStatusAndCustomerOutput() {
        mockCustomerServiceUpdateCustomerPartially(Mono.just(getCustomerOutput()));
        updateCustomerPartiallyThenExpectStatusAndBodyClass(CUSTOMER_ID, getRequestHeaders(), getCustomerInput(),
            HttpStatus.OK, CustomerOutput.class);
        verifyCustomerServiceUpdateCustomerPartially();
    }

    @Test
    void givenCustomerInputWhenUpdateCustomerPartiallyAndCustomerServiceUpdateCustomerPartiallyReturnsExceptionThenExpectInternalServerErrorStatusAndErrorModel() {
        mockCustomerServiceUpdateCustomerPartially(
            Mono.error(new RuntimeException("Update customer partially test error")));
        updateCustomerPartiallyThenExpectStatusAndBodyClass(CUSTOMER_ID, getRequestHeaders(), getCustomerInput(),
            HttpStatus.INTERNAL_SERVER_ERROR, ErrorModel.class);
        verifyCustomerServiceUpdateCustomerPartially();
    }

    private void mockCustomerServiceDeleteCustomer(@NonNull final Mono<CustomerOutput> result) {
        when(customerService.deleteCustomer(any())).thenReturn(result);
    }

    private void verifyCustomerServiceDeleteCustomer() {
        verify(customerService).deleteCustomer(any());
    }

    private void deleteCustomerThenExpectStatusAndBodyClass(
        @NonNull final Long id,
        @NonNull final HttpHeaders headers,
        @NonNull final HttpStatus expectedHttpStatus,
        @NonNull final Class<?> expectedBodyClass
    ) {
        webTestClient.delete()
            .uri(CUSTOMERS_PATH + "/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .headers(httpHeaders -> httpHeaders.addAll(headers))
            .exchange()
            .expectStatus().isEqualTo(expectedHttpStatus)
            .expectBody(expectedBodyClass);
    }

    @Test
    void givenCustomerIdWhenDeleteCustomerThenExpectOkStatusAndCustomerOutput() {
        mockCustomerServiceDeleteCustomer(Mono.just(getCustomerOutput()));
        deleteCustomerThenExpectStatusAndBodyClass(CUSTOMER_ID, getRequestHeaders(), HttpStatus.OK,
            CustomerOutput.class);
        verifyCustomerServiceDeleteCustomer();
    }

    @Test
    void givenCustomerIdWhenDeleteCustomerAndCustomerServiceDeleteCustomerReturnsCustomerNotFoundExceptionThenExpectNotFoundStatusAndErrorModel() {
        mockCustomerServiceDeleteCustomer(Mono.error(new CustomerNotFoundException()));
        deleteCustomerThenExpectStatusAndBodyClass(CUSTOMER_ID, getRequestHeaders(), HttpStatus.NOT_FOUND,
            ErrorModel.class);
        verifyCustomerServiceDeleteCustomer();
    }

    @ParameterizedTest
    @ValueSource(strings = {"HEAD", "OPTIONS"})
    void givenForbiddenHttpMethodWhenRequestToCustomersPathUsingForbiddenMethodThenExpectUnauthorizedStatus(
        final String httpMethod) {
        webTestClient.method(HttpMethod.valueOf(httpMethod))
            .uri(CUSTOMERS_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .headers(httpHeaders -> httpHeaders.addAll(getRequestHeaders()))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
