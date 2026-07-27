package com.gmail.voronovskyi.yaroslav.service.impl;

import com.gmail.voronovskyi.yaroslav.dto.mapper.PaymentMapper;
import com.gmail.voronovskyi.yaroslav.dto.request.CreatePaymentRequest;
import com.gmail.voronovskyi.yaroslav.dto.response.PaymentResponse;
import com.gmail.voronovskyi.yaroslav.messaging.PaymentPublisher;
import com.gmail.voronovskyi.yaroslav.model.Currency;
import com.gmail.voronovskyi.yaroslav.model.Payment;
import com.gmail.voronovskyi.yaroslav.model.PaymentStatus;
import com.gmail.voronovskyi.yaroslav.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentCreateServiceImplTest {

    private static final UUID TEST_PAYMENT_ID = UUID.fromString("dc63c44e-981f-4150-8f42-d851bcea988d");
    private static final UUID TEST_EXTERNAL_ID = UUID.fromString("ba45c639-ab4b-4b23-bae9-3ae42b9c5d1c");

    @Mock
    private PaymentRepository paymentRepositoryMock;

    @Mock
    private PaymentPublisher paymentPublisherMock;

    @Mock
    private PaymentMapper paymentMapperMock;

    @InjectMocks
    private PaymentCreateServiceImpl service;

    private CreatePaymentRequest request;
    private Payment payment;
    private PaymentResponse response;

    @BeforeEach
    void setUp() {
        request = new CreatePaymentRequest(TEST_EXTERNAL_ID, new BigDecimal("100.00"), Currency.USD);
        payment = Payment.create(TEST_EXTERNAL_ID, new BigDecimal("100.00"), Currency.USD);
        payment.setId(TEST_PAYMENT_ID);
        response = new PaymentResponse(TEST_PAYMENT_ID, TEST_EXTERNAL_ID, new BigDecimal("100.00"), Currency.USD,
                PaymentStatus.NEW, null, null, Instant.now(), Instant.now());
    }

    @Test
    void shouldCreatePayment() {
        when(paymentRepositoryMock.saveAndFlush(any(Payment.class))).thenReturn(payment);
        when(paymentMapperMock.paymentToPaymentResponse(payment)).thenReturn(response);

        PaymentResponse actual = service.createPayment(request);

        assertEquals(response, actual);
        verify(paymentRepositoryMock).saveAndFlush(any(Payment.class));
        verify(paymentPublisherMock).publish(TEST_PAYMENT_ID);
    }

    @Test
    void shouldThrowExceptionWhenConcurrentInsertAndPaymentNotFound() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("duplicate");

        when(paymentRepositoryMock.saveAndFlush(any())).thenThrow(exception);
        assertThrows(DataIntegrityViolationException.class,
                () -> service.createPayment(request));
    }
}
