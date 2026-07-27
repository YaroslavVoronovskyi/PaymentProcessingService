package com.gmail.voronovskyi.yaroslav.service;

import com.gmail.voronovskyi.yaroslav.dto.request.CreatePaymentRequest;
import com.gmail.voronovskyi.yaroslav.dto.response.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse create(CreatePaymentRequest request);

    PaymentResponse get(UUID id);

    Page<PaymentResponse> getAll(Pageable pageable);

    PaymentResponse getByExternalId(UUID externalId);
}
