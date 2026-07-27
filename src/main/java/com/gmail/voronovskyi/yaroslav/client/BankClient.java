package com.gmail.voronovskyi.yaroslav.client;

import com.gmail.voronovskyi.yaroslav.dto.request.BankPaymentRequest;
import com.gmail.voronovskyi.yaroslav.dto.response.BankPaymentResponse;

public interface BankClient {

    BankPaymentResponse process(BankPaymentRequest request);
}
