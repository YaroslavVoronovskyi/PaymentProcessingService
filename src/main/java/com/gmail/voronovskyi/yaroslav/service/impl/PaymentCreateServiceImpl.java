package com.gmail.voronovskyi.yaroslav.service.impl;

import com.gmail.voronovskyi.yaroslav.dto.mapper.PaymentMapper;
import com.gmail.voronovskyi.yaroslav.dto.request.CreatePaymentRequest;
import com.gmail.voronovskyi.yaroslav.dto.response.PaymentResponse;
import com.gmail.voronovskyi.yaroslav.messaging.PaymentPublisher;
import com.gmail.voronovskyi.yaroslav.model.Payment;
import com.gmail.voronovskyi.yaroslav.repository.PaymentRepository;
import com.gmail.voronovskyi.yaroslav.service.PaymentCreateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PaymentCreateServiceImpl implements PaymentCreateService {

    private final PaymentRepository paymentRepository;
    private final PaymentPublisher paymentPublisher;
    private final PaymentMapper paymentMapper;

    @Autowired
    public PaymentCreateServiceImpl(PaymentRepository paymentRepository,
                                    PaymentPublisher paymentPublisher,
                                    PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentPublisher = paymentPublisher;
        this.paymentMapper = paymentMapper;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        log.debug("Saving payment with externalId={} to DB", request.externalId());
        Payment payment = Payment.create(request.externalId(), request.amount(), request.currency());
        Payment saved = paymentRepository.saveAndFlush(payment);
        paymentPublisher.publish(saved.getId());
        log.debug("Payment was successfully saved to DB with externalId={}", request.externalId());
        return paymentMapper.paymentToPaymentResponse(saved);
    }
}
