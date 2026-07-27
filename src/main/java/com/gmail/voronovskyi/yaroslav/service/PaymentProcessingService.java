package com.gmail.voronovskyi.yaroslav.service;

import java.util.UUID;

public interface PaymentProcessingService {

    void process(UUID paymentId);
}
