package com.gmail.voronovskyi.yaroslav.controller.it;

import com.gmail.voronovskyi.yaroslav.config.AbstractTestContainers;
import com.gmail.voronovskyi.yaroslav.dto.request.CreatePaymentRequest;
import com.gmail.voronovskyi.yaroslav.dto.response.PaymentResponse;
import com.gmail.voronovskyi.yaroslav.model.Currency;
import com.gmail.voronovskyi.yaroslav.model.Payment;
import com.gmail.voronovskyi.yaroslav.model.PaymentStatus;
import com.gmail.voronovskyi.yaroslav.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RabbitIntegrationTestIT extends AbstractTestContainers {

    private static final String PAYMENT_ROOT_PATH = "/api/v1/payments";
    private static final UUID TEST_EXTERNAL_ID = UUID.fromString("ba45c639-ab4b-4b23-bae9-3ae42b9c5d1c");

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    PaymentRepository paymentRepository;

    @Test
    void shouldProcessPaymentAsynchronously() {
        CreatePaymentRequest request = createPaymentRequest();

        PaymentResponse payment = createPayment(request).getBody();

        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    assertNotNull(payment);
                    Payment entity = paymentRepository.findById(payment.id())
                            .orElseThrow();
                    assertNotEquals(PaymentStatus.NEW, entity.getStatus());
                    assertTrue(entity.getStatus() == PaymentStatus.SUCCESS ||
                            entity.getStatus() == PaymentStatus.FAILED ||
                            entity.getStatus() == PaymentStatus.PROCESSING);
                });
    }

    private ResponseEntity<PaymentResponse> createPayment(CreatePaymentRequest createPaymentRequest) {
        HttpEntity<CreatePaymentRequest> request = new HttpEntity<>(createPaymentRequest, new HttpHeaders());
        return restTemplate.postForEntity(PAYMENT_ROOT_PATH, request, PaymentResponse.class);
    }

    private CreatePaymentRequest createPaymentRequest() {
        return new CreatePaymentRequest(TEST_EXTERNAL_ID, new BigDecimal("150.00"), Currency.UAH);
    }
}
