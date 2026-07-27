package com.gmail.voronovskyi.yaroslav.service.impl;

import com.gmail.voronovskyi.yaroslav.dto.mapper.PaymentMapper;
import com.gmail.voronovskyi.yaroslav.dto.request.CreatePaymentRequest;
import com.gmail.voronovskyi.yaroslav.dto.response.PaymentResponse;
import com.gmail.voronovskyi.yaroslav.exception.PaymentNotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    private static final UUID TEST_PAYMENT_ID = UUID.fromString("dc63c44e-981f-4150-8f42-d851bcea988d");
    private static final UUID TEST_EXTERNAL_ID = UUID.fromString("ba45c639-ab4b-4b23-bae9-3ae42b9c5d1c");

    @Mock
    private PaymentRepository paymentRepositoryMock;

    @Mock
    private PaymentPublisher paymentPublisherMock;

    @Mock
    private PaymentMapper paymentMapperMock;

    @InjectMocks
    private PaymentServiceImpl service;

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
    void shouldReturnExistingPaymentWhenAlreadyExists() {
        when(paymentRepositoryMock.findByExternalId(TEST_EXTERNAL_ID)).thenReturn(Optional.of(payment));
        when(paymentMapperMock.paymentToPaymentResponse(payment)).thenReturn(response);

        PaymentResponse actual = service.create(request);

        assertEquals(response, actual);
        verify(paymentRepositoryMock, never()).saveAndFlush(any());
        verify(paymentPublisherMock, never()).publish(any());
    }

    @Test
    void shouldReturnPaymentById() {
        when(paymentRepositoryMock.findById(TEST_PAYMENT_ID)).thenReturn(Optional.of(payment));
        when(paymentMapperMock.paymentToPaymentResponse(payment)).thenReturn(response);

        PaymentResponse actual = service.get(TEST_PAYMENT_ID);

        assertEquals(response, actual);
    }

    @Test
    void shouldThrowWhenPaymentNotFoundById() {
        when(paymentRepositoryMock.findById(TEST_PAYMENT_ID)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class,
                () -> service.get(TEST_PAYMENT_ID));
    }

    @Test
    void shouldReturnPaymentByExternalId() {
        when(paymentRepositoryMock.findByExternalId(TEST_EXTERNAL_ID)).thenReturn(Optional.of(payment));
        when(paymentMapperMock.paymentToPaymentResponse(payment)).thenReturn(response);

        PaymentResponse actual = service.getByExternalId(TEST_EXTERNAL_ID);

        assertEquals(response, actual);
    }

    @Test
    void shouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(paymentRepositoryMock.findAll(pageable)).thenReturn(Page.empty());

        Page<PaymentResponse> actual = service.getAll(pageable);

        assertTrue(actual.isEmpty());
        verify(paymentMapperMock, never()).paymentPageToPaymentResponsePage(any());
    }

    @Test
    void shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Payment> payments = new PageImpl<>(List.of(payment));
        Page<PaymentResponse> expected = new PageImpl<>(List.of(response));
        when(paymentRepositoryMock.findAll(pageable)).thenReturn(payments);
        when(paymentMapperMock.paymentPageToPaymentResponsePage(payments)).thenReturn(expected);

        Page<PaymentResponse> actual = service.getAll(pageable);

        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnExistingPaymentWhenConcurrentInsertOccurs() {
        when(paymentRepositoryMock.findByExternalId(TEST_EXTERNAL_ID)).thenReturn(Optional.of(payment));
        when(paymentMapperMock.paymentToPaymentResponse(payment)).thenReturn(response);

        PaymentResponse actual = service.create(request);

        assertEquals(response, actual);
        verify(paymentPublisherMock, never()).publish(any());
    }
}
