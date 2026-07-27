package com.gmail.voronovskyi.yaroslav.service;

import com.gmail.voronovskyi.yaroslav.dto.response.PaymentProcessingData;

import java.util.UUID;

public interface PaymentLifecycleService {

    PaymentProcessingData startProcessing(UUID paymentId);

    void completeSuccessfully(UUID paymentId, String bankTransactionId);

    void completeWithFailure(UUID paymentId, String failureReason);
}
