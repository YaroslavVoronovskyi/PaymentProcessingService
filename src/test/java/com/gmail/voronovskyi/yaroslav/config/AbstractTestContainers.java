package com.gmail.voronovskyi.yaroslav.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.rabbitmq.RabbitMQContainer;

@Slf4j
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractTestContainers {

    @Container
    static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:17.0")
                    .withDatabaseName("payments")
                    .withUsername("test")
                    .withPassword("test")
                    .withLogConsumer(new Slf4jLogConsumer(log).withSeparateOutputStreams());

    @Container
    static final RabbitMQContainer RABBIT_CONTAINER =
            new RabbitMQContainer("rabbitmq:4-management");


    @DynamicPropertySource
    private static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRESQL_CONTAINER::getDriverClassName);
        registry.add("spring.liquibase.enabled", () -> "true");
        registry.add("spring.liquibase.contexts", () -> "test");

        registry.add("spring.rabbitmq.host", RABBIT_CONTAINER::getHost);
        registry.add("spring.rabbitmq.port", RABBIT_CONTAINER::getAmqpPort);
        registry.add("spring.rabbitmq.username", RABBIT_CONTAINER::getAdminUsername);
        registry.add("spring.rabbitmq.password", RABBIT_CONTAINER::getAdminPassword);
    }
}
