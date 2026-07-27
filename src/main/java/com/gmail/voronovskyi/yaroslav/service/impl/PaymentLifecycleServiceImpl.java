package com.gmail.voronovskyi.yaroslav.service.impl;

import com.gmail.voronovskyi.yaroslav.dto.response.PaymentProcessingData;
import com.gmail.voronovskyi.yaroslav.exception.PaymentNotFoundException;
import com.gmail.voronovskyi.yaroslav.exception.PaymentProcessingException;
import com.gmail.voronovskyi.yaroslav.model.Payment;
import com.gmail.voronovskyi.yaroslav.model.PaymentStatus;
import com.gmail.voronovskyi.yaroslav.repository.PaymentRepository;
import com.gmail.voronovskyi.yaroslav.service.PaymentLifecycleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class PaymentLifecycleServiceImpl implements PaymentLifecycleService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentLifecycleServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PaymentProcessingData startProcessing(UUID paymentId) {
        log.debug("Marking payment with id={} as PROCESSING", paymentId);
        Payment payment = findPayment(paymentId);
        if (payment.getStatus() != PaymentStatus.NEW) {
            throw new PaymentProcessingException(String.format("Payment cannot be processed from status: %s", payment.getStatus()));
        }
        payment.markProcessing();
        Payment savedPayment = paymentRepository.save(payment);
        log.debug("Payment with id={} marked as PROCESSING.", paymentId);
        return new PaymentProcessingData(savedPayment.getId(), savedPayment.getAmount(), savedPayment.getCurrency());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void completeSuccessfully(UUID paymentId, String bankTransactionId) {
        log.debug("Marking payment with id={} as SUCCESS", paymentId);
        Payment payment = findPayment(paymentId);
        payment.markSuccess(bankTransactionId);
        paymentRepository.save(payment);
        log.debug("Payment with id={} marked as SUCCESS.", paymentId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void completeWithFailure(UUID paymentId, String failureReason) {
        log.debug("Marking payment with id={} as FAILED", paymentId);
        Payment payment = findPayment(paymentId);
        payment.markFailed(failureReason);
        paymentRepository.save(payment);
        log.debug("Payment with id={} marked as FAILED.", paymentId);
    }

    private Payment findPayment(UUID paymentId) {
        return paymentRepository.findByIdForUpdate(paymentId)
                .orElseThrow(() -> {
                    log.warn("Payment with id={} not found", paymentId);
                    return new PaymentNotFoundException(paymentId);
                });
    }
}
