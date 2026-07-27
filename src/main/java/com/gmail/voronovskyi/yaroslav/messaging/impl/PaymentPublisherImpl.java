package com.gmail.voronovskyi.yaroslav.messaging.impl;

import com.gmail.voronovskyi.yaroslav.config.RabbitPaymentProperties;
import com.gmail.voronovskyi.yaroslav.dto.request.PaymentCreatedEvent;
import com.gmail.voronovskyi.yaroslav.messaging.PaymentPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class PaymentPublisherImpl implements PaymentPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitPaymentProperties properties;

    @Autowired
    public PaymentPublisherImpl(RabbitTemplate rabbitTemplate,
                                RabbitPaymentProperties properties) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
    }

    @Override
    public void publish(UUID paymentId) {
        PaymentCreatedEvent event = new PaymentCreatedEvent(paymentId);
        log.debug("Publish payment with id={} to RabbitMQ.", paymentId);
        rabbitTemplate.convertAndSend(properties.getExchange(), properties.getRoutingKey(), event);
        log.debug("Payment with id={} published.", paymentId);
    }
}
