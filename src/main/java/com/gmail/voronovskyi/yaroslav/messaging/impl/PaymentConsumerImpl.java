package com.gmail.voronovskyi.yaroslav.messaging.impl;

import com.gmail.voronovskyi.yaroslav.dto.request.PaymentCreatedEvent;
import com.gmail.voronovskyi.yaroslav.messaging.PaymentConsumer;
import com.gmail.voronovskyi.yaroslav.service.PaymentProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentConsumerImpl implements PaymentConsumer {

    private final PaymentProcessingService paymentProcessingService;

    @Autowired
    public PaymentConsumerImpl(PaymentProcessingService paymentProcessingService) {
        this.paymentProcessingService = paymentProcessingService;
    }

    @Override
    @RabbitListener(queues = "${rabbitmq.payment.queue}")
    public void consume(PaymentCreatedEvent event) {
        log.debug("Payment message received with paymentId={}", event.paymentId());
        paymentProcessingService.process(event.paymentId());
    }
}
