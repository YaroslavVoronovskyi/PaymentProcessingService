package com.gmail.voronovskyi.yaroslav.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "rabbitmq.payment")
public class RabbitPaymentProperties {

    private String queue;
    private String exchange;
    private String routingKey;
}
