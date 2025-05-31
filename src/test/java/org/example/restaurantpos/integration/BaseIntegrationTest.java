package org.example.restaurantpos.integration;

import org.example.restaurantpos.RestaurantPosApplication;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = RestaurantPosApplication.class)
@ActiveProfiles("integration-test")
@Testcontainers
public abstract class BaseIntegrationTest {

    static {
        // Start container when class is loaded
        PostgresTestContainer.getInstance().start();
        System.out.println("PostgreSQL container started at: " +
                PostgresTestContainer.getInstance().getJdbcUrl());
    }

    @DynamicPropertySource
    static void registerPgProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
                PostgresTestContainer.getInstance()::getJdbcUrl);
        registry.add("spring.datasource.username",
                PostgresTestContainer.getInstance()::getUsername);
        registry.add("spring.datasource.password",
                PostgresTestContainer.getInstance()::getPassword);
        registry.add("spring.datasource.driver-class-name",
                () -> "org.postgresql.Driver");

        // Hikari config - fix the max lifetime warning
        registry.add("spring.datasource.hikari.maximum-pool-size", () -> "5");
        registry.add("spring.datasource.hikari.minimum-idle", () -> "1");
        registry.add("spring.datasource.hikari.idle-timeout", () -> "30000");
        registry.add("spring.datasource.hikari.max-lifetime", () -> "20000"); // Lower this value
        registry.add("spring.datasource.hikari.connection-timeout", () -> "20000");
        registry.add("spring.datasource.hikari.validation-timeout", () -> "5000");
    }
}