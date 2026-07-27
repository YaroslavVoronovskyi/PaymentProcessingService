package com.gmail.voronovskyi.yaroslav.service.impl;

import com.gmail.voronovskyi.yaroslav.dto.mapper.PaymentMapper;
import com.gmail.voronovskyi.yaroslav.dto.request.CreatePaymentRequest;
import com.gmail.voronovskyi.yaroslav.dto.response.PaymentResponse;
import com.gmail.voronovskyi.yaroslav.exception.PaymentNotFoundException;
import com.gmail.voronovskyi.yaroslav.model.Payment;
import com.gmail.voronovskyi.yaroslav.repository.PaymentRepository;
import com.gmail.voronovskyi.yaroslav.service.PaymentCreateService;
import com.gmail.voronovskyi.yaroslav.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentCreateService paymentCreateService;
    private final PaymentMapper paymentMapper;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              PaymentCreateService paymentCreateService,
                              PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentCreateService = paymentCreateService;
        this.paymentMapper = paymentMapper;
    }

    @Override
    @Transactional
    public PaymentResponse create(CreatePaymentRequest request) {
        log.debug("Creating payment with externalId={}", request.externalId());
        PaymentResponse response = paymentRepository.findByExternalId(request.externalId())
                .map(paymentMapper::paymentToPaymentResponse)
                .orElseGet(() -> {
                    try {
                        return paymentCreateService.createPayment(request);
                    } catch (DataIntegrityViolationException exception) {
                        log.warn("Payment with externalId={} was created concurrently.", request.externalId());
                        return paymentRepository.findByExternalId(request.externalId())
                                .map(paymentMapper::paymentToPaymentResponse)
                                .orElseThrow(() -> exception);
                    }
                });
        log.debug("Payment was successfully created with externalId={}", request.externalId());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse get(UUID id) {
        log.debug("Retrieving payment by id={}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Payment with id={} not found.", id);
                    return new PaymentNotFoundException(id);
                });
        log.debug("Payment was successfully retrieved by id={}", id);
        return paymentMapper.paymentToPaymentResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getAll(Pageable pageable) {
        log.debug("Retrieving all payments");
        Page<Payment> payments = paymentRepository.findAll(pageable);
        if (payments.isEmpty()) {
            return Page.empty();
        }
        log.debug("{} - payments were successfully retrieved", payments.getTotalElements());
        return paymentMapper.paymentPageToPaymentResponsePage(payments);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getByExternalId(UUID externalId) {
        log.debug("Retrieving payment by externalId={}", externalId);
        Payment payment = paymentRepository.findByExternalId(externalId)
                .orElseThrow(() -> {
                    log.warn("Payment with externalId={} not found.", externalId);
                    return new PaymentNotFoundException(externalId);
                });
        log.debug("Payment was successfully retrieved by externalId={}", externalId);
        return paymentMapper.paymentToPaymentResponse(payment);
    }
}
