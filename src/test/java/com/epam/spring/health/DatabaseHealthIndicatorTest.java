package com.epam.spring.health;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DatabaseHealthIndicatorTest {
    private DataSource dataSource;
    private Connection connection;
    private HealthIndicator healthIndicator;

    @BeforeEach
    void setUp() throws SQLException {
        dataSource = mock(DataSource.class);
        connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);

        healthIndicator = new DatabaseHealthIndicator(dataSource);
    }

    @Test
    void testDatabaseIsUp() throws SQLException {
        when(connection.isValid(anyInt())).thenReturn(true);

        Health health = healthIndicator.health();

        assertEquals(Health.up().withDetail("message", "Database is up").build(), health);
        verify(connection, times(1)).isValid(anyInt());
    }

    @Test
    void testDatabaseIsDown() throws SQLException {
        when(connection.isValid(anyInt())).thenReturn(false);

        Health health = healthIndicator.health();

        assertEquals(Health.down().withDetail("message", "Database is down").build(), health);
        verify(connection, times(1)).isValid(anyInt());
    }

    @Test
    void testDatabaseConnectionFailure() throws SQLException {
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection error"));

        Health health = healthIndicator.health();

        assertEquals("DOWN", health.getStatus().getCode());
        assertEquals("Database connection failed", health.getDetails().get("message"));
    }
}