package com.gmail.voronovskyi.yaroslav.service.impl;

import com.gmail.voronovskyi.yaroslav.dto.request.BankPaymentRequest;
import com.gmail.voronovskyi.yaroslav.dto.response.BankPaymentResponse;
import com.gmail.voronovskyi.yaroslav.dto.response.PaymentProcessingData;
import com.gmail.voronovskyi.yaroslav.exception.BankClientException;
import com.gmail.voronovskyi.yaroslav.client.BankClient;
import com.gmail.voronovskyi.yaroslav.service.PaymentLifecycleService;
import com.gmail.voronovskyi.yaroslav.service.PaymentProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class PaymentProcessingServiceImpl implements PaymentProcessingService {

    private final PaymentLifecycleService paymentLifecycleService;
    private final BankClient bankClient;

    @Autowired
    public PaymentProcessingServiceImpl(PaymentLifecycleService paymentLifecycleService,
                                        BankClient bankClient) {
        this.paymentLifecycleService = paymentLifecycleService;
        this.bankClient = bankClient;
    }

    @Override
    public void process(UUID paymentId) {
        log.debug("Starting payment processing, paymentId={}", paymentId);
        PaymentProcessingData data = paymentLifecycleService.startProcessing(paymentId);
        try {
            BankPaymentRequest request = new BankPaymentRequest(data.paymentId(), data.amount(), data.currency());
            BankPaymentResponse response = bankClient.process(request);
            switch (response.status()) {
                case PROCESSING -> log.debug("Payment with id={} is still processing.", paymentId);
                case SUCCESS -> handleSuccess(data, response);
                case FAILED -> handleFailure(data, response.failureReason());
            }
        } catch (BankClientException exception) {
            handleFailure(data, exception.getMessage());
        }
        log.debug("Payment processing completed, paymentId={}", paymentId);
    }

    private void handleSuccess(PaymentProcessingData data, BankPaymentResponse response) {
        paymentLifecycleService.completeSuccessfully(data.paymentId(), response.transactionId());
        log.debug("Payment with id={} successfully processed, transactionId={}", data.paymentId(), response.transactionId());
    }

    private void handleFailure(PaymentProcessingData data, String reason) {
        paymentLifecycleService.completeWithFailure(data.paymentId(), reason);
        log.debug("Payment with id={} failed, reason={}", data.paymentId(), reason);
    }
}
