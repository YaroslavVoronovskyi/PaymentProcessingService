package com.gmail.voronovskyi.yaroslav.service.impl;

import com.gmail.voronovskyi.yaroslav.dto.response.PaymentProcessingData;
import com.gmail.voronovskyi.yaroslav.exception.PaymentNotFoundException;
import com.gmail.voronovskyi.yaroslav.exception.PaymentProcessingException;
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

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentLifecycleServiceImplTest {

    private static final UUID TEST_PAYMENT_ID = UUID.fromString("dc63c44e-981f-4150-8f42-d851bcea988d");

    @Mock
    private PaymentRepository paymentRepositoryMock;

    @InjectMocks
    private PaymentLifecycleServiceImpl service;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.create(UUID.randomUUID(), new BigDecimal("150"), Currency.USD);
        payment.setId(TEST_PAYMENT_ID);
    }

    @Test
    void shouldStartProcessing() {
        when(paymentRepositoryMock.findByIdForUpdate(TEST_PAYMENT_ID)).thenReturn(Optional.of(payment));
        when(paymentRepositoryMock.save(payment)).thenReturn(payment);

        PaymentProcessingData result = service.startProcessing(TEST_PAYMENT_ID);

        assertEquals(PaymentStatus.PROCESSING, payment.getStatus());
        assertEquals(TEST_PAYMENT_ID, result.paymentId());
        verify(paymentRepositoryMock).save(payment);
    }

    @Test
    void shouldThrowWhenStatusIsNotNew() {
        payment.markProcessing();
        when(paymentRepositoryMock.findByIdForUpdate(TEST_PAYMENT_ID)).thenReturn(Optional.of(payment));

        assertThrows(PaymentProcessingException.class,
                () -> service.startProcessing(TEST_PAYMENT_ID));

        verify(paymentRepositoryMock, never()).save(any());
    }

    @Test
    void shouldCompleteSuccessfully() {
        when(paymentRepositoryMock.findByIdForUpdate(TEST_PAYMENT_ID)).thenReturn(Optional.of(payment));

        service.completeSuccessfully(TEST_PAYMENT_ID, "BANK-123");

        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertEquals("BANK-123", payment.getBankTransactionId());
        verify(paymentRepositoryMock).save(payment);
    }

    @Test
    void shouldCompleteWithFailure() {
        when(paymentRepositoryMock.findByIdForUpdate(TEST_PAYMENT_ID)).thenReturn(Optional.of(payment));

        service.completeWithFailure(TEST_PAYMENT_ID, "Not enough money");

        assertEquals(PaymentStatus.FAILED, payment.getStatus());
        assertEquals("Not enough money", payment.getFailureReason());
        verify(paymentRepositoryMock).save(payment);
    }

    @Test
    void shouldThrowWhenPaymentNotFound() {
        when(paymentRepositoryMock.findByIdForUpdate(TEST_PAYMENT_ID)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class,
                () -> service.completeSuccessfully(TEST_PAYMENT_ID, "1"));
    }
}
