package com.gmail.voronovskyi.yaroslav.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TestContainersTest extends AbstractTestContainers {

    @Test
    void testDbStart() {
        assertTrue(POSTGRESQL_CONTAINER.isCreated());
        assertTrue(POSTGRESQL_CONTAINER.isRunning());
    }

    @Test
    void testRabbitStart() {
        assertTrue(RABBIT_CONTAINER.isCreated());
        assertTrue(RABBIT_CONTAINER.isRunning());
    }
}
