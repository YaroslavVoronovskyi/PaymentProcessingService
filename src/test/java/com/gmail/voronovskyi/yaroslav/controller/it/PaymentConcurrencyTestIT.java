package com.gmail.voronovskyi.yaroslav.controller.it;

import com.gmail.voronovskyi.yaroslav.config.AbstractTestContainers;
import com.gmail.voronovskyi.yaroslav.dto.request.CreatePaymentRequest;
import com.gmail.voronovskyi.yaroslav.dto.response.PaymentResponse;
import com.gmail.voronovskyi.yaroslav.model.Currency;
import com.gmail.voronovskyi.yaroslav.model.Payment;
import com.gmail.voronovskyi.yaroslav.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentConcurrencyTestIT extends AbstractTestContainers {

    private static final String PAYMENT_ROOT_PATH = "/api/v1/payments";
    private static final UUID TEST_EXTERNAL_ID = UUID.fromString("ba45c639-ab4b-4b23-bae9-3ae42b9c5d1c");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAllInBatch();
    }

    @Test
    void shouldCreateOnlyOnePaymentWhenRequestsAreConcurrent() throws Exception {
        CreatePaymentRequest request = createPaymentRequest();
        int threads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch finished = new CountDownLatch(threads);
        List<Future<ResponseEntity<PaymentResponse>>> futures = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            futures.add(executor.submit(() -> {
                ready.countDown();
                start.await();
                try {
                    return createPayment(request);
                } finally {
                    finished.countDown();
                }
            }));
        }
        ready.await();
        start.countDown();
        finished.await();
        executor.shutdown();

        List<ResponseEntity<PaymentResponse>> responses = new ArrayList<>();
        for (Future<ResponseEntity<PaymentResponse>> future : futures) {
            responses.add(future.get());
        }
        assertEquals(threads, responses.size());
        assertThat(responses)
                .allMatch(r -> r.getStatusCode() == HttpStatus.CREATED);

        UUID paymentId = Objects.requireNonNull(responses.getFirst().getBody()).id();

        assertThat(responses)
                .allSatisfy(r ->
                        assertThat(Objects.requireNonNull(r.getBody()).id())
                                .isEqualTo(paymentId));

        assertThat(paymentRepository.findAll()).hasSize(1);
        assertThat(paymentRepository.findByExternalId(TEST_EXTERNAL_ID)).isPresent();
    }

    @Test
    void shouldReturnExistingPaymentWhenConcurrentRequestsUseExistingExternalId() throws Exception {
        CreatePaymentRequest request = createPaymentRequest();
        ResponseEntity<PaymentResponse> createdResponse = createPayment(request);

        assertThat(createdResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        UUID paymentId = Objects.requireNonNull(createdResponse.getBody()).id();
        int threads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch finished = new CountDownLatch(threads);
        List<Future<ResponseEntity<PaymentResponse>>> futures = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            futures.add(executor.submit(() -> {
                ready.countDown();
                start.await();
                try {
                    return createPayment(request);
                } finally {
                    finished.countDown();
                }
            }));
        }
        ready.await();
        start.countDown();
        finished.await();
        executor.shutdown();
        for (Future<ResponseEntity<PaymentResponse>> future : futures) {
            ResponseEntity<PaymentResponse> response = future.get();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(Objects.requireNonNull(response.getBody()).id()).isEqualTo(paymentId);
            assertThat(response.getBody().externalId()).isEqualTo(TEST_EXTERNAL_ID);
        }
        assertThat(paymentRepository.count()).isEqualTo(1);

        Payment payment = paymentRepository.findByExternalId(TEST_EXTERNAL_ID).orElseThrow();
        assertThat(payment.getId()).isEqualTo(paymentId);
    }

    private ResponseEntity<PaymentResponse> createPayment(CreatePaymentRequest createPaymentRequest) {
        HttpEntity<CreatePaymentRequest> request = new HttpEntity<>(createPaymentRequest, new HttpHeaders());
        return restTemplate.postForEntity(PAYMENT_ROOT_PATH, request, PaymentResponse.class);
    }

    private CreatePaymentRequest createPaymentRequest() {
        return new CreatePaymentRequest(TEST_EXTERNAL_ID, new BigDecimal("150.00"), Currency.UAH);
    }
}
