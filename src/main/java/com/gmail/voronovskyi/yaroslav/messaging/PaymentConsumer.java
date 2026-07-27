package com.gmail.voronovskyi.yaroslav.messaging;

import com.gmail.voronovskyi.yaroslav.dto.request.PaymentCreatedEvent;

public interface PaymentConsumer {

    void consume(PaymentCreatedEvent event);
}
