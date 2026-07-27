package com.gmail.voronovskyi.yaroslav.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfiguration {

    private final RabbitPaymentProperties properties;

    @Autowired
    public RabbitConfiguration(RabbitPaymentProperties properties) {
        this.properties = properties;
    }

    @Bean
    public Queue paymentQueue() {
        return QueueBuilder
                .durable(properties.getQueue())
                .build();
    }

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(properties.getExchange());
    }

    @Bean
    public Binding paymentBinding(Queue paymentQueue, TopicExchange paymentExchange) {
        return BindingBuilder
                .bind(paymentQueue)
                .to(paymentExchange)
                .with(properties.getRoutingKey());
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
