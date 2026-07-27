package com.gmail.voronovskyi.yaroslav.service.impl;

import com.gmail.voronovskyi.yaroslav.client.BankClient;
import com.gmail.voronovskyi.yaroslav.dto.response.BankPaymentResponse;
import com.gmail.voronovskyi.yaroslav.dto.response.PaymentProcessingData;
import com.gmail.voronovskyi.yaroslav.exception.BankClientException;
import com.gmail.voronovskyi.yaroslav.model.BankPaymentStatus;
import com.gmail.voronovskyi.yaroslav.model.Currency;
import com.gmail.voronovskyi.yaroslav.service.PaymentLifecycleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentProcessingServiceImplTest {

    private static final UUID TEST_PAYMENT_ID = UUID.fromString("dc63c44e-981f-4150-8f42-d851bcea988d");

    @Mock
    private PaymentLifecycleService lifecycleServiceMock;

    @Mock
    private BankClient bankClientMock;

    @InjectMocks
    private PaymentProcessingServiceImpl service;

    private PaymentProcessingData processingData;

    @BeforeEach
    void setUp() {
        processingData = new PaymentProcessingData(TEST_PAYMENT_ID, new BigDecimal("100"), Currency.USD);
    }

    @Test
    void shouldCompleteSuccessfully() {
        when(lifecycleServiceMock.startProcessing(TEST_PAYMENT_ID)).thenReturn(processingData);
        when(bankClientMock.process(any())).thenReturn(
                new BankPaymentResponse(BankPaymentStatus.SUCCESS, "BANK-1", null));

        service.process(TEST_PAYMENT_ID);

        verify(lifecycleServiceMock).completeSuccessfully(TEST_PAYMENT_ID, "BANK-1");
        verify(lifecycleServiceMock, never()).completeWithFailure(any(), any());
    }

    @Test
    void shouldCompleteWithFailure() {
        when(lifecycleServiceMock.startProcessing(TEST_PAYMENT_ID)).thenReturn(processingData);
        when(bankClientMock.process(any()))
                .thenReturn(new BankPaymentResponse(BankPaymentStatus.FAILED, null, "Rejected"));

        service.process(TEST_PAYMENT_ID);

        verify(lifecycleServiceMock).completeWithFailure(TEST_PAYMENT_ID, "Rejected");
    }

    @Test
    void shouldHandleBankException() {
        when(lifecycleServiceMock.startProcessing(TEST_PAYMENT_ID)).thenReturn(processingData);
        when(bankClientMock.process(any())).thenThrow(new BankClientException("Timeout"));

        service.process(TEST_PAYMENT_ID);

        verify(lifecycleServiceMock).completeWithFailure(TEST_PAYMENT_ID, "Timeout");
    }

    @Test
    void shouldDoNothingWhenBankStillProcessing() {
        when(lifecycleServiceMock.startProcessing(TEST_PAYMENT_ID)).thenReturn(processingData);
        when(bankClientMock.process(any()))
                .thenReturn(new BankPaymentResponse(BankPaymentStatus.PROCESSING, "BANK-1", null));

        service.process(TEST_PAYMENT_ID);

        verify(lifecycleServiceMock, never()).completeSuccessfully(any(), any());
        verify(lifecycleServiceMock, never()).completeWithFailure(any(), any());
    }
}
