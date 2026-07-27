package com.gmail.voronovskyi.yaroslav.controller.it;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gmail.voronovskyi.yaroslav.config.AbstractTestContainers;
import com.gmail.voronovskyi.yaroslav.dto.request.CreatePaymentRequest;
import com.gmail.voronovskyi.yaroslav.dto.response.PaymentResponse;
import com.gmail.voronovskyi.yaroslav.exception.ApiError;
import com.gmail.voronovskyi.yaroslav.model.Currency;
import com.gmail.voronovskyi.yaroslav.model.Payment;
import com.gmail.voronovskyi.yaroslav.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class PaymentControllerTestIT extends AbstractTestContainers {

    private static final String PAYMENT_ROOT_PATH = "/api/v1/payments";
    private static final String RETRIEVE_PAYMENT_BY_ID_PATH = PAYMENT_ROOT_PATH + "/%s";
    private static final String RETRIEVE_PAYMENT_BY_EXTERNAL_ID_PATH = PAYMENT_ROOT_PATH + "/external/%s";
    private static final String TEST_JSON_NODE_CONTENT = "content";
    private static final UUID TEST_PAYMENT_ID = UUID.fromString("dc63c44e-981f-4150-8f42-d851bcea988d");
    private static final UUID TEST_EXTERNAL_ID = UUID.fromString("ba45c639-ab4b-4b23-bae9-3ae42b9c5d1c");

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAllInBatch();
    }

    @Test
    void shouldCreateNewPayment() {
        CreatePaymentRequest request = createPaymentRequest();

        ResponseEntity<PaymentResponse> response = createPayment(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        PaymentResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(TEST_EXTERNAL_ID, body.externalId());

        Optional<Payment> payment = paymentRepository.findById(body.id());
        assertTrue(payment.isPresent());
    }

    @Test
    void shouldReturnExistingPaymentWhenExternalIdAlreadyExists() {
        CreatePaymentRequest request = createPaymentRequest();

        PaymentResponse first = createPayment(request).getBody();
        PaymentResponse second = createPayment(request).getBody();

        assertNotNull(first);
        assertNotNull(second);
        assertEquals(first.id(), second.id());
    }

    @Test
    void shouldReturnPaymentById() {
        CreatePaymentRequest request = createPaymentRequest();

        PaymentResponse created = createPayment(request).getBody();

        assertNotNull(created);
        ResponseEntity<PaymentResponse> response = retrievePayment(created.id());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(created.id(), response.getBody().id());
    }

    @Test
    void shouldReturn404WhenPaymentDoesNotExistById() {
        ResponseEntity<ApiError> response =
                restTemplate.getForEntity(String.format(RETRIEVE_PAYMENT_BY_ID_PATH, TEST_PAYMENT_ID), ApiError.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldReturnPaymentByExternalId() {
        CreatePaymentRequest request = createPaymentRequest();

        PaymentResponse created = createPayment(request).getBody();

        assertNotNull(created);
        ResponseEntity<PaymentResponse> response = retrievePaymentByExternalId(created.externalId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(created.id(), response.getBody().id());
        assertEquals(created.externalId(), response.getBody().externalId());
    }

    @Test
    void shouldReturn404WhenPaymentDoesNotExistByExternalId() {
        ResponseEntity<ApiError> response = restTemplate.getForEntity(
                String.format(RETRIEVE_PAYMENT_BY_EXTERNAL_ID_PATH, TEST_EXTERNAL_ID), ApiError.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldReturnEmptyPageWheTryGetPayments() throws IOException {
        List<PaymentResponse> result = retrieveAllPayments();

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnPageWheTryGetPayments() throws IOException {
        CreatePaymentRequest request = createPaymentRequest();
        createPayment(request);

        List<PaymentResponse> result = retrieveAllPayments();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(TEST_EXTERNAL_ID, result.getFirst().externalId());
        assertEquals(BigDecimal.valueOf(150.00), result.getFirst().amount());
    }

    private ResponseEntity<PaymentResponse> createPayment(CreatePaymentRequest createPaymentRequest) {
        HttpEntity<CreatePaymentRequest> request = new HttpEntity<>(createPaymentRequest, new HttpHeaders());
        return restTemplate.postForEntity(PAYMENT_ROOT_PATH, request, PaymentResponse.class);
    }

    private ResponseEntity<PaymentResponse> retrievePayment(UUID id) {
        return restTemplate.getForEntity(String.format(RETRIEVE_PAYMENT_BY_ID_PATH, id), PaymentResponse.class);
    }

    private ResponseEntity<PaymentResponse> retrievePaymentByExternalId(UUID externalId) {
        return restTemplate.getForEntity(String.format(RETRIEVE_PAYMENT_BY_EXTERNAL_ID_PATH, externalId), PaymentResponse.class);
    }

    private List<PaymentResponse> retrieveAllPayments() throws IOException {
        HttpEntity<Void> request = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange(PAYMENT_ROOT_PATH, HttpMethod.GET, request, String.class);
        String responseBody = response.getBody();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        JsonNode root = mapper.readTree(responseBody);
        JsonNode contentNode = root.get(TEST_JSON_NODE_CONTENT);
        return mapper.readerForListOf(PaymentResponse.class).readValue(contentNode);
    }

    private CreatePaymentRequest createPaymentRequest() {
        return new CreatePaymentRequest(TEST_EXTERNAL_ID, new BigDecimal("150.00"), Currency.UAH);
    }
}
