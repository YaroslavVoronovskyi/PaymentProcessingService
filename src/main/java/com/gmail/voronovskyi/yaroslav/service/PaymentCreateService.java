package com.gmail.voronovskyi.yaroslav.service;

import com.gmail.voronovskyi.yaroslav.dto.request.CreatePaymentRequest;
import com.gmail.voronovskyi.yaroslav.dto.response.PaymentResponse;

public interface PaymentCreateService {

    PaymentResponse createPayment(CreatePaymentRequest request);
}
