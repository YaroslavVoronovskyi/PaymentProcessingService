package com.gmail.voronovskyi.yaroslav.client.impl;

import com.gmail.voronovskyi.yaroslav.client.BankClient;
import com.gmail.voronovskyi.yaroslav.dto.request.BankPaymentRequest;
import com.gmail.voronovskyi.yaroslav.dto.response.BankPaymentResponse;
import com.gmail.voronovskyi.yaroslav.model.BankPaymentStatus;
import com.gmail.voronovskyi.yaroslav.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class FakeBankClientImpl implements BankClient {

    @Override
    public BankPaymentResponse process(BankPaymentRequest request) {
        log.debug("Sending payment {} to fake bank.", request.paymentId());
        try {
            Thread.sleep(Constants.BANK_DELAY.toMillis());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(exception);
        }
        int chance = ThreadLocalRandom.current().nextInt(100);
        if (chance < 20) {
            String transactionId = UUID.randomUUID().toString();
            log.debug("Bank still processing payment with id={}, transactionId={}", request.paymentId(), transactionId);
            return new BankPaymentResponse(BankPaymentStatus.PROCESSING, transactionId, null);
        }
        if (chance < 90) {
            String transactionId = UUID.randomUUID().toString();
            log.debug("Bank approved payment with id={}, transactionId={}", request.paymentId(), transactionId);
            return new BankPaymentResponse(BankPaymentStatus.SUCCESS, transactionId, null);
        }
        log.warn("Bank rejected payment with id={}", request.paymentId());
        return new BankPaymentResponse(BankPaymentStatus.FAILED, null, "Insufficient funds");
    }
}
