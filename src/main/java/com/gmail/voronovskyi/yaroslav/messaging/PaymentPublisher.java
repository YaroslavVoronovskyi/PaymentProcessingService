package com.gmail.voronovskyi.yaroslav.messaging;

import java.util.UUID;

public interface PaymentPublisher {

    void publish(UUID paymentId);
}
