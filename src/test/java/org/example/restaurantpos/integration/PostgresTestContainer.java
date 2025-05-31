package org.example.restaurantpos.integration;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresTestContainer {
    private static final PostgresTestContainer instance = new PostgresTestContainer();

    private final PostgreSQLContainer<?> container;

    private PostgresTestContainer() {
        container = new PostgreSQLContainer<>("postgres:15")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                // Use command to enable easy cleanup
                .withCommand("postgres -c fsync=off -c synchronous_commit=off -c full_page_writes=off")
                .withReuse(true);
    }

    public static PostgresTestContainer getInstance() {
        return instance;
    }

    public void start() {
        if (!container.isRunning()) {
            container.start();
        }
    }

    public String getJdbcUrl() {
        return container.getJdbcUrl();
    }

    public String getUsername() {
        return container.getUsername();
    }

    public String getPassword() {
        return container.getPassword();
    }
}